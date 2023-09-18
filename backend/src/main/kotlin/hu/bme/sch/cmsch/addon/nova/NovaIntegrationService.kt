package hu.bme.sch.cmsch.addon.nova

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import hu.bme.sch.cmsch.component.form.FilledOutFormDto
import hu.bme.sch.cmsch.component.form.FormRepository
import hu.bme.sch.cmsch.component.form.ResponseEntity
import hu.bme.sch.cmsch.component.form.ResponseRepository
import hu.bme.sch.cmsch.component.task.SubmittedTaskRepository
import hu.bme.sch.cmsch.component.task.TaskEntityRepository
import hu.bme.sch.cmsch.component.task.TaskStatus
import hu.bme.sch.cmsch.component.task.resolveTaskStatus
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.TimeService
import org.postgresql.util.PSQLException
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*

const val AVATAR_TAG = "avatar"
const val CV_TAG = "cv"

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.ext",
    name = ["nova"],
    havingValue = "true",
    matchIfMissing = false
)
open class NovaIntegrationService(
    private val responseRepository: ResponseRepository,
    private val formRepository: FormRepository,
    private val userRepository: UserRepository,
    private val taskRepository: Optional<TaskEntityRepository>,
    private val submittedTaskRepository: Optional<SubmittedTaskRepository>,
    private val clock: TimeService,
    private val objectMapper: ObjectMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun updateSubmissions(emails: List<String>): Int {
        val form = formRepository.findAll().firstOrNull { it.selected }

        if (form == null) {
            log.info("[NOVA/VALID-USERS] Form not found with non empty url")
            return 0
        }

        var successful = 0
        val now = clock.getTimeInSeconds()
        responseRepository.findAllByFormId(form.id)
            .filter { !it.rejected && !it.accepted }
            .filter { it.email.isNotBlank() && it.email in emails }
            .forEach {
                it.accepted = true
                it.acceptedAt = now
                it.lastUpdatedDate = now

                responseRepository.save(it)
                log.info("[NOVA/VALID-USERS] User response accepted for {}", it.email)
                ++successful
            }

        return successful
    }

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    open fun fetchSubmissions(): List<FilledOutFormDto> {
        val form = formRepository.findAll().firstOrNull { it.selected }

        if (form == null) {
            log.info("[NOVA/VALID-USERS] Form not found with selected attribute")
            return listOf()
        }

        val readerForSubmission = objectMapper.readerFor(object : TypeReference<Map<String, String>>() {})

        return responseRepository.findAllByFormId(form.id)
            .filter { !it.rejected }
            .map { response ->
                val user = userRepository.findById(response.submitterUserId ?: 0)
                if (user.isEmpty)
                    log.error("User not found in submission list {} {}", response.submitterUserId, response.submitterUserName)
                val avatar = user.map { fetchAvatar(it) }
                val cv = user.map { fetchCv(it) }
                return@map FilledOutFormDto(
                    internalId = user.map { it.internalId }.orElse("n/a"),
                    email = response.email,
                    name = response.submitterUserName,
                    neptun = user.map { it.neptun }.orElse(null),
                    submittedAt = response.creationDate,
                    accepted = response.accepted,
                    rejected = response.rejected,
                    lastUpdatedAt = response.lastUpdatedDate,
                    formSubmission = tryToParseSubmission(readerForSubmission, response),
                    profilePictureUrl = avatar.map { it.first }.orElse(""),
                    profileStatus = avatar.map { it.second }.orElse(TaskStatus.NOT_SUBMITTED),
                    cvUrl = cv.map { it.first }.orElse(""),
                    cvStatus = cv.map { it.second }.orElse(TaskStatus.NOT_SUBMITTED),
                    detailsValidated = response.detailsValidated
                )
            }
    }

    fun fetchAvatar(user: UserEntity): Pair<String, TaskStatus> {
        return taskRepository.map { tasks ->
            tasks.findAllByTag(AVATAR_TAG).firstOrNull()?.let { task ->
                submittedTaskRepository.map { submissions ->
                    submissions.findAllByUserIdAndTask_Id(user.id, task.id).firstOrNull()?.let {
                        Pair(it.imageUrlAnswer, resolveTaskStatus(it))
                    } ?: taskNotSubmitted()
                }.orElse(taskNotSubmitted())
            } ?: taskNotSubmitted()
        }.orElse(taskNotSubmitted())
    }

    fun fetchCv(user: UserEntity): Pair<String, TaskStatus> {
        return taskRepository.map { tasks ->
            tasks.findAllByTag(CV_TAG).firstOrNull()?.let { task ->
                submittedTaskRepository.map { submissions ->
                    submissions.findAllByUserIdAndTask_Id(user.id, task.id).firstOrNull()?.let {
                        Pair(it.fileUrlAnswer, resolveTaskStatus(it))
                    } ?: taskNotSubmitted()
                }.orElse(taskNotSubmitted())
            } ?: taskNotSubmitted()
        }.orElse(taskNotSubmitted())
    }

    private fun taskNotSubmitted() = Pair("", TaskStatus.NOT_SUBMITTED)

    private fun tryToParseSubmission(
        readerForSubmission: ObjectReader,
        response: ResponseEntity
    ): Map<String, String> {
        return try {
            readerForSubmission.readValue(response.submission)
        } catch (e: Throwable) {
            log.error("Failed to map submission: {}", response.submission, e)
            mapOf()
        }
    }

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun setPaymentStatus(email: String, status: Boolean, rejectionMessage: String?) {
        val form = formRepository.findAll().firstOrNull { it.selected }
        if (form == null) {
            log.info("[NOVA/VALID-USERS] Form not found with non empty url")
            return
        }

        val now = clock.getTimeInSeconds()
        responseRepository.findAllByFormIdAndEmail(form.id, email)
            .forEach {
                it.accepted = status
                it.acceptedAt = now
                it.lastUpdatedDate = now
                it.rejectionMessage = rejectionMessage ?: ""

                responseRepository.save(it)
                log.info("[NOVA/VALID-USERS] User response accepted={} for {} rej:{}", status, it.email, rejectionMessage)
            }
    }

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun setDetailsStatus(email: String, status: Boolean, rejectionMessage: String?) {
        val form = formRepository.findAll().firstOrNull { it.selected }
        if (form == null) {
            log.info("[NOVA/VALID-USERS] Form not found with non empty url")
            return
        }

        val now = clock.getTimeInSeconds()
        responseRepository.findAllByFormIdAndEmail(form.id, email)
            .forEach {
                it.detailsValidated = status
                it.detailsValidatedAt = now
                it.lastUpdatedDate = now
                it.rejectionMessage = rejectionMessage ?: ""

                responseRepository.save(it)
                log.info("[NOVA/VALID-USERS] User response validated={} for {} reason: {}", status, it.email, rejectionMessage)
            }
    }

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun setAvatarStatus(email: String, status: Boolean, rejectionMessage: String?) {
        val user = userRepository.findByEmail(email).orElse(null) ?: return

        taskRepository.ifPresent { tasks ->
            tasks.findAllByTag(AVATAR_TAG).forEach { task ->
                submittedTaskRepository.ifPresent { submissions ->
                    submissions.findAllByUserIdAndTask_Id(user.id, task.id).forEach { submission ->
                        submission.approved = status
                        submission.rejected = !status
                        submission.score = if (status) task.maxScore else 0
                        submission.response = rejectionMessage ?: ""

                        log.info("[NOVA/VALID-USERS] User AVATAR ok={} for email:{} sub:{} task:{} rej:{}",
                            status, email, submission.id, task.id, rejectionMessage)
                        submissions.save(submission)
                    }
                }
            }
        }
    }

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun setCvStatus(email: String, status: Boolean, rejectionMessage: String?) {
        val user = userRepository.findByEmail(email).orElse(null) ?: return

        taskRepository.ifPresent { tasks ->
            tasks.findAllByTag(CV_TAG).forEach { task ->
                submittedTaskRepository.ifPresent { submissions ->
                    submissions.findAllByUserIdAndTask_Id(user.id, task.id).forEach { submission ->
                        submission.approved = status
                        submission.rejected = !status
                        submission.score = if (status) task.maxScore else 0
                        submission.response = rejectionMessage ?: ""

                        log.info("[NOVA/VALID-USERS] User CV ok={} for email:{} sub:{} task:{} rej:{}",
                            status, email, submission.id, task.id, rejectionMessage)
                        submissions.save(submission)
                    }
                }
            }
        }
    }


}
