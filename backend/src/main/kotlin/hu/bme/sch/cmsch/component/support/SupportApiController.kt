package hu.bme.sch.cmsch.component.support

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.component.login.asUserEntity
import hu.bme.sch.cmsch.component.support.dto.IncomingEmailDto
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.util.getUserOrNull
import hu.bme.sch.cmsch.util.isAvailableForRole
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import tools.jackson.databind.ObjectMapper

@RestController
@RequestMapping("/api")
@ConditionalOnBean(SupportComponent::class)
class SupportApiController(
    private val supportService: SupportService,
    private val supportComponent: SupportComponent,
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper
) {

    private val log = LoggerFactory.getLogger(SupportApiController::class.java)

    data class CreateThreadRequest(
        val title: String = "",
        val content: String = "",
        val authorName: String = "",
        val authorEmail: String = ""
    )
    data class AddMessageRequest(val content: String = "", val authorName: String = "")

    data class ThreadListView(val threads: List<SupportThreadEntity>)

    data class SupportThreadView(
        val id: Int,
        val uuid: String,
        val title: String,
        val status: SupportThreadStatus,
        val solver: String,
        val createdAt: Long,
        val updatedAt: Long,
        val userEmail: String,
        val userName: String
    ) {
        companion object {
            fun from(t: SupportThreadEntity) = SupportThreadView(
                t.id, t.uuid, t.title, t.status, t.solver, t.createdAt, t.updatedAt, t.userEmail, t.userName
            )
        }
    }

    data class PublicMessageView(
        val id: Int,
        val threadUuid: String,
        val content: String,
        val createdAt: Long,
        val authorName: String,
        val fromAdmin: Boolean
    ) {
        companion object {
            fun from(m: SupportMessageEntity) = PublicMessageView(
                m.id, m.threadUuid, m.content, m.createdAt, m.authorName, m.fromAdmin
            )
        }
    }

    data class ThreadDetailView(val thread: SupportThreadView, val messages: List<PublicMessageView>, val canReply: Boolean)

    @PostMapping("/support/thread")
    @Operation(summary = "Create a new support thread")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Thread created"),
        ApiResponse(responseCode = "400", description = "Missing required fields"),
        ApiResponse(responseCode = "429", description = "Open thread limit reached")
    ])
    fun createThread(@RequestBody request: CreateThreadRequest, auth: Authentication?): ResponseEntity<SupportThreadEntity> {
        if (request.title.isBlank()) return ResponseEntity.badRequest().build()
        val user = auth?.getUserOrNull()
        return if (user != null && supportComponent.minRole.isAvailableForRole(user.role)) {
            val email = getEmail(user)
            if (supportService.isBlockedUser(user.internalId, email))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
            if (supportService.countOpenThreadsForUser(user.internalId, "") >= supportComponent.maxOpenThreads)
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build()
            ResponseEntity.ok(supportService.createThread(request.title, request.content, user.internalId, email, user.userName))
        } else {
            if (request.authorEmail.isBlank()) return ResponseEntity.badRequest().build()
            if (supportService.isBlockedUser("", request.authorEmail))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
            if (supportService.countOpenThreadsForUser("", request.authorEmail) >= supportComponent.maxOpenThreads)
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build()
            ResponseEntity.ok(supportService.createThread(
                request.title, request.content, "", request.authorEmail,
                request.authorName.ifBlank { request.authorEmail }
            ))
        }
    }

    @GetMapping("/support/threads")
    @Operation(summary = "List support threads for the current authenticated user")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "List of threads"),
        ApiResponse(responseCode = "401", description = "Not authenticated"),
        ApiResponse(responseCode = "403", description = "Insufficient role")
    ])
    fun listThreads(auth: Authentication?): ResponseEntity<ThreadListView> {
        val user = auth?.getUserOrNull() ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        if (!supportComponent.minRole.isAvailableForRole(user.role))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        return ResponseEntity.ok(ThreadListView(supportService.getThreadsForUser(user.internalId)))
    }

    @GetMapping("/support/thread/{uuid}")
    @Operation(summary = "Get a support thread by UUID (public)")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Thread with messages"),
        ApiResponse(responseCode = "404", description = "Thread not found")
    ])
    fun getThread(
        @PathVariable uuid: String,
        @RequestParam(required = false) secret: String?,
        auth: Authentication?
    ): ResponseEntity<ThreadDetailView> {
        val thread = supportService.findByUuid(uuid) ?: return ResponseEntity.notFound().build()

        val secretMatches = secret != null && thread.uuidSecret == secret
        val user = auth?.getUserOrNull()
        val isOwner = user != null && thread.userInternalId.isNotBlank() && thread.userInternalId == user.internalId
        val canReply = (secretMatches || isOwner) && thread.status != SupportThreadStatus.DONE

        val messages = supportService.getPublicMessagesForThread(uuid).map { PublicMessageView.from(it) }
        return ResponseEntity.ok(ThreadDetailView(SupportThreadView.from(thread), messages, canReply))
    }

    @PostMapping("/support/thread/{uuid}/message")
    @Operation(summary = "Add a customer message using uuid-secret or authenticated session")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Message added"),
        ApiResponse(responseCode = "400", description = "Content is blank"),
        ApiResponse(responseCode = "403", description = "Not authorized"),
        ApiResponse(responseCode = "404", description = "Thread not found or closed")
    ])
    fun addMessage(
        @PathVariable uuid: String,
        @RequestParam(required = false) secret: String?,
        @RequestBody request: AddMessageRequest,
        auth: Authentication?
    ): ResponseEntity<PublicMessageView> {
        if (request.content.isBlank()) return ResponseEntity.badRequest().build()
        val thread = supportService.findByUuid(uuid) ?: return ResponseEntity.notFound().build()
        if (thread.status == SupportThreadStatus.DONE) return ResponseEntity.notFound().build()

        val user = auth?.getUserOrNull()
        val secretMatches = secret != null && thread.uuidSecret == secret
        val isOwner = user != null && thread.userInternalId.isNotBlank() && thread.userInternalId == user.internalId

        if (!secretMatches && !isOwner)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        val authorEmail = if (user != null) getEmail(user) else thread.userEmail
        if (supportService.isBlockedUser(thread.userInternalId, authorEmail))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        if (supportService.isContentTooLong(request.content))
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build()
        if (supportService.hasTooManyConsecutiveCustomerResponses(uuid))
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build()

        val authorName = when {
            user != null -> user.userName
            request.authorName.isNotBlank() -> request.authorName
            else -> thread.userName
        }

        val message = supportService.addCustomerMessage(uuid, request.content, authorName, authorEmail)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(PublicMessageView.from(message))
    }

    @PostMapping("/support/incoming-email/{secret}")
    @Operation(summary = "Receive an incoming email webhook (secret is part of the URL)")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Email processed"),
        ApiResponse(responseCode = "404", description = "Webhook disabled or invalid secret")
    ])
    fun incomingEmail(
        @PathVariable secret: String,
        @RequestBody body: String
    ): ResponseEntity<String> {
        log.error("incomingEmail")
        if (!supportComponent.emailWebhookEnabled) {
            log.error("emailWebhookEnabled")
            return ResponseEntity.notFound().build()
        }
        if (secret != supportComponent.incomingEmailSecret) {
            log.error("incomingEmailSecret ${secret}")
            return ResponseEntity.notFound().build()
        }

        val dto = try {
            objectMapper.readValue(body, IncomingEmailDto::class.java)
        } catch (e: Exception) {
            log.error("Failed to parse incoming email payload: {} | payload: {}", e.message, body.take(10000), e)
            return ResponseEntity.badRequest().body("invalid payload")
        }

        try {
            supportService.processIncomingEmail(dto)
        } catch (e: Exception) {
            log.error("Failed to process incoming email id='{}' subject='{}'", dto.id, dto.subject, e)
            return ResponseEntity.internalServerError().body("processing failed")
        }
        return ResponseEntity.ok("ok")
    }

    private fun getEmail(user: CmschUser): String {
        return user.asUserEntity(userRepository).email
    }
}
