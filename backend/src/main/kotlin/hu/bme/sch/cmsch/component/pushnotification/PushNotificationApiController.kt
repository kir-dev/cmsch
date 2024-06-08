package hu.bme.sch.cmsch.component.pushnotification

import hu.bme.sch.cmsch.util.getUserOrNull
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@ConditionalOnBean(PushNotificationComponent::class)
@RequestMapping("/api/pushnotification/")
class PushNotificationApiController(
    private val notificationService: PushNotificationService,
    private val notificationComponent: PushNotificationComponent
) {

    @PostMapping("add-token")
    @Operation(summary = "Subscribe to notifications by submitting the messaging token")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "The messaging token has been recorded successfully"),
            ApiResponse(responseCode = "403", description = "The user has no permission to add a messaging token")
        ]
    )
    fun addToken(auth: Authentication?, @RequestBody request: TokenUpdateRequest): ResponseEntity<Any> {
        val user = auth?.getUserOrNull()
        if (user == null || !notificationComponent.minRole.isAvailableForRole(user.role))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        notificationService.addToken(user.id, request.token)
        return ResponseEntity.ok().build()
    }

    @PostMapping("delete-token")
    @Operation(summary = "Unsubscribe from receiving notifications by submitting the messaging token")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "The messaging token has been deleted successfully"),
            ApiResponse(responseCode = "403", description = "The user has no permission to add a messaging token")
        ]
    )
    fun deleteToken(auth: Authentication?, @RequestBody request: TokenUpdateRequest): ResponseEntity<Any> {
        val user = auth?.getUserOrNull()
        if (user == null || !notificationComponent.minRole.isAvailableForRole(user.role))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        notificationService.deleteToken(user.id, request.token)
        return ResponseEntity.ok().build()
    }
}
