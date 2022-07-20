package hu.bme.sch.cmsch.component.signup

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.TimeService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.ext",
    name = ["nova"],
    havingValue = "true",
    matchIfMissing = false
)
open class NovaIntegrationService(
    private val signupResponseRepository: SignupResponseRepository,
    private val signupFormRepository: SignupFormRepository,
    private val userRepository: UserRepository,
    private val clock: TimeService,
    private val objectMapper: ObjectMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun updateSubmissions(emails: List<String>): Int {
        val form = signupFormRepository.findAll().firstOrNull { it.selected }

        if (form == null) {
            log.info("[NOVA/VALID-USERS] Form not found with non empty url")
            return 0
        }

        var successful = 0
        val now = clock.getTimeInSeconds()
        signupResponseRepository.findAllByFormId(form.id)
            .filter { !it.rejected && !it.accepted }
            .filter { it.email.isNotBlank() && it.email in emails }
            .forEach {
                it.accepted = true
                it.acceptedAt = now
                it.lastUpdatedDate = now

                signupResponseRepository.save(it)
                log.info("[NOVA/VALID-USERS] User response accepted for {}", it.email)
                ++successful
            }

        return successful
    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    open fun fetchSubmissions(): List<FilledOutFormDto> {
        val form = signupFormRepository.findAll().firstOrNull { it.selected }

        if (form == null) {
            log.info("[NOVA/VALID-USERS] Form not found with selected attribute")
            return listOf()
        }

        val readerForSubmission = objectMapper.readerFor(object : TypeReference<Map<String, String>>() {})

        return signupResponseRepository.findAllByFormId(form.id)
            .filter { !it.rejected }
            .map { response ->
                val user = userRepository.findById(response.submitterUserId ?: 0)
                if (user.isEmpty)
                    log.error("User not found in submission list {} {}", response.submitterUserId, response.submitterUserName)
                return@map FilledOutFormDto(
                    internalId = user.map { it.internalId }.orElse("n/a"),
                    email = response.email,
                    name = response.submitterUserName,
                    neptun = user.map { it.neptun }.orElse(null),
                    submittedAt = response.creationDate,
                    accepted = response.accepted,
                    rejected = response.rejected,
                    lastUpdatedAt = response.lastUpdatedDate,
                    formSubmission = tryToParseSubmission(readerForSubmission, response)
                )
            }
    }

    private fun tryToParseSubmission(
        readerForSubmission: ObjectReader,
        response: SignupResponseEntity
    ): Map<String, String> {
        return try {
            readerForSubmission.readValue(response.submission)
        } catch (e: Throwable) {
            log.error("Failed to map submission: {}", response.submission, e)
            mapOf()
        }
    }

    fun setPaymentStatus(email: String, equals: Boolean) {
        TODO("Not yet implemented")
    }

    fun setAvatarStatus(email: String, equals: Boolean) {
        TODO("Not yet implemented")
    }

    fun setCvStatus(email: String, equals: Boolean) {
        TODO("Not yet implemented")
    }

    fun setDetailsStatus(email: String, equals: Boolean) {
        TODO("Not yet implemented")
    }


}
