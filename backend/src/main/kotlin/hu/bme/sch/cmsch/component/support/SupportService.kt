package hu.bme.sch.cmsch.component.support

import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.email.EmailService
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.component.support.dto.IncomingEmailDto
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.TimeService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@ConditionalOnBean(SupportComponent::class)
class SupportService(
    private val threadRepository: SupportThreadRepository,
    private val messageRepository: SupportMessageRepository,
    private val clock: TimeService,
    private val supportComponent: SupportComponent,
    private val appComponent: ApplicationComponent,
    private val emailService: EmailService,
    private val userRepository: UserRepository
) {

    private val log = LoggerFactory.getLogger(SupportService::class.java)

    fun normalizeSubject(subject: String): String {
        var s = subject.trim()
        val prefixes = listOf("re:", "fwd:", "fw:")
        var changed = true
        while (changed) {
            changed = false
            for (prefix in prefixes) {
                if (s.lowercase().startsWith(prefix)) {
                    s = s.substring(prefix.length).trim()
                    changed = true
                }
            }
        }
        return s
    }

    @Transactional(readOnly = true)
    fun findMatchingThread(normalizedSubject: String, email: String): SupportThreadEntity? {
        return threadRepository.findByUserEmail(email)
            .filter { it.status != SupportThreadStatus.DONE }
            .firstOrNull { normalizeSubject(it.title).equals(normalizedSubject, ignoreCase = true) }
    }

    @Transactional(readOnly = true)
    fun findByUuid(uuid: String): SupportThreadEntity? = threadRepository.findByUuid(uuid).orElse(null)

    @Transactional(readOnly = true)
    fun findByUuidAndSecret(uuid: String, secret: String): SupportThreadEntity? {
        val thread = threadRepository.findByUuid(uuid).orElse(null) ?: return null
        return if (thread.uuidSecret == secret) thread else null
    }

    @Transactional(readOnly = true)
    fun countOpenThreadsForUser(internalId: String, email: String): Long {
        return if (internalId.isNotBlank())
            threadRepository.countOpenByUserInternalId(internalId)
        else
            threadRepository.countOpenByAnonymousEmail(email)
    }

    private fun isBlocked(userInternalId: String, userEmail: String): Boolean {
        if (supportComponent.blockedEmails.isNotBlank()) {
            val blocked = supportComponent.blockedEmails.split(",").map { it.trim() }
            if (userEmail.isNotBlank() && blocked.any { it.equals(userEmail, ignoreCase = true) }) return true
        }
        if (supportComponent.blockedUserIds.isNotBlank() && userInternalId.isNotBlank()) {
            val blocked = supportComponent.blockedUserIds.split(",").map { it.trim() }
            if (blocked.contains(userInternalId)) return true
        }
        return false
    }

    private fun countTrailingCustomerResponses(threadUuid: String): Int {
        val messages = messageRepository.findByThreadUuidAndInternalOnlyFalseOrderByCreatedAtAsc(threadUuid)
        var count = 0
        for (msg in messages.reversed()) {
            if (msg.fromAdmin) break
            count++
        }
        return count
    }

    fun isBlockedUser(userInternalId: String, userEmail: String): Boolean = isBlocked(userInternalId, userEmail)

    fun isContentTooLong(content: String): Boolean =
        supportComponent.maxResponseLength > 0 && content.length > supportComponent.maxResponseLength

    fun hasTooManyConsecutiveCustomerResponses(threadUuid: String): Boolean {
        val max = supportComponent.maxCustomerResponsesWithoutAnswer
        return max > 0 && countTrailingCustomerResponses(threadUuid) >= max
    }

    @Transactional
    fun createThread(title: String, content: String, userInternalId: String, userEmail: String, userName: String): SupportThreadEntity {
        val now = clock.getTimeInSeconds()
        val hasContent = content.isNotBlank()
        val thread = SupportThreadEntity(
            uuid = UUID.randomUUID().toString(),
            uuidSecret = UUID.randomUUID().toString(),
            title = title,
            status = SupportThreadStatus.WAITING_FOR_ADMIN,
            solver = "",
            createdAt = now,
            updatedAt = now,
            userInternalId = userInternalId,
            userEmail = userEmail,
            userName = userName,
            lastCustomerAnswerAt = if (hasContent) now else 0
        )
        val savedThread = threadRepository.save(thread)
        if (hasContent) {
            messageRepository.save(SupportMessageEntity(
                threadUuid = savedThread.uuid,
                content = content,
                createdAt = now,
                authorName = userName,
                authorEmail = userEmail,
                fromAdmin = false
            ))
        }
        return savedThread
    }

    @Transactional
    fun addCustomerMessage(threadUuid: String, content: String, authorName: String, authorEmail: String): SupportMessageEntity? {
        val thread = threadRepository.findByUuid(threadUuid).orElse(null) ?: return null
        if (thread.status == SupportThreadStatus.DONE) return null
        val now = clock.getTimeInSeconds()
        thread.status = SupportThreadStatus.WAITING_FOR_ADMIN
        thread.updatedAt = now
        thread.lastCustomerAnswerAt = now
        threadRepository.save(thread)
        return messageRepository.save(SupportMessageEntity(
            threadUuid = threadUuid,
            content = content,
            createdAt = now,
            authorName = authorName,
            authorEmail = authorEmail,
            fromAdmin = false
        ))
    }

    @Transactional
    fun addAdminReply(threadId: Int, content: String, adminUser: CmschUser, displayName: String, internalOnly: Boolean) {
        val thread = threadRepository.findById(threadId).orElse(null) ?: return
        if (thread.status == SupportThreadStatus.DONE) return
        val now = clock.getTimeInSeconds()
        if (!internalOnly) {
            thread.status = SupportThreadStatus.WAITING_FOR_CUSTOMER
            thread.solver = displayName
        }
        thread.updatedAt = now
        threadRepository.save(thread)
        messageRepository.save(SupportMessageEntity(
            threadUuid = thread.uuid,
            content = content,
            createdAt = now,
            authorName = displayName,
            realAuthorName = adminUser.userName,
            authorEmail = "",
            fromAdmin = true,
            internalOnly = internalOnly
        ))
        if (!internalOnly && supportComponent.sendEmailOnAdminReply) {
            val threadUrl = buildThreadUrl(thread)
            val template = emailService.getTemplateBySelector(supportComponent.answerEmailTemplateSelector)
            if (template != null) {
                emailService.sendTemplatedEmail(
                    responsible = adminUser,
                    template = template,
                    values = mapOf(
                        "title" to thread.title,
                        "message" to content,
                        "solver" to displayName,
                        "threadUrl" to threadUrl
                    ),
                    to = listOf(thread.userEmail)
                )
            } else {
                log.warn("Email template '{}' not found, skipping notification", supportComponent.answerEmailTemplateSelector)
            }
        }
    }

    @Transactional
    fun claimThread(threadId: Int, adminUser: CmschUser, displayName: String = adminUser.userName) {
        val thread = threadRepository.findById(threadId).orElse(null) ?: return
        thread.solver = displayName.ifBlank { adminUser.userName }
        thread.updatedAt = clock.getTimeInSeconds()
        threadRepository.save(thread)
    }

    @Transactional
    fun closeThread(threadId: Int) {
        val thread = threadRepository.findById(threadId).orElse(null) ?: return
        thread.status = SupportThreadStatus.DONE
        thread.updatedAt = clock.getTimeInSeconds()
        threadRepository.save(thread)
    }

    @Transactional
    fun reopenThread(threadId: Int) {
        val thread = threadRepository.findById(threadId).orElse(null) ?: return
        thread.status = SupportThreadStatus.WAITING_FOR_ADMIN
        thread.updatedAt = clock.getTimeInSeconds()
        threadRepository.save(thread)
    }

    @Transactional
    fun processIncomingEmail(dto: IncomingEmailDto) {
        val fromEmail = dto.addresses.from.address.trim()
        val subject = dto.subject.trim()
        val body = dto.body.text.ifBlank { dto.body.html }
        if (fromEmail.isBlank() || subject.isBlank()) {
            log.warn("Incoming email ignored: from='{}', subject='{}'", fromEmail, subject)
            return
        }
        val hostRegex = supportComponent.allowedSenderHostRegex.trim()
        if (hostRegex.isNotBlank()) {
            val host = fromEmail.substringAfter("@", "")
            if (!Regex(hostRegex).containsMatchIn(host)) {
                log.warn("Incoming email rejected: sender host '{}' did not match regex '{}'", host, hostRegex)
                return
            }
        }
        val normalized = normalizeSubject(subject)
        val existing = findMatchingThread(normalized, fromEmail)
        val user = userRepository.findByEmailIgnoreCase(fromEmail).orElse(null)
        if (existing != null) {
            addCustomerMessage(existing.uuid, body, user?.fullName ?: fromEmail, fromEmail)
        } else {
            createThread(subject, body, user?.internalId ?: "", fromEmail, user?.fullName ?: fromEmail)
        }
    }

    @Transactional(readOnly = true)
    fun getThreadsForUser(userInternalId: String): List<SupportThreadEntity> {
        return threadRepository.findByUserInternalId(userInternalId)
            .sortedWith(compareBy<SupportThreadEntity> { if (it.status == SupportThreadStatus.DONE) 1 else 0 }
                .thenByDescending { it.updatedAt })
    }

    @Transactional(readOnly = true)
    fun getMessagesForThread(threadUuid: String): List<SupportMessageEntity> {
        return messageRepository.findByThreadUuidOrderByCreatedAtAsc(threadUuid)
    }

    @Transactional(readOnly = true)
    fun getPublicMessagesForThread(threadUuid: String): List<SupportMessageEntity> {
        return messageRepository.findByThreadUuidAndInternalOnlyFalseOrderByCreatedAtAsc(threadUuid)
    }

    private fun buildThreadUrl(thread: SupportThreadEntity): String {
        val base = appComponent.siteUrl.trimEnd('/')
        return if (base.isNotBlank()) "$base/support/${thread.uuid}?secret=${thread.uuidSecret}" else ""
    }
}
