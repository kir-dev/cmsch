package hu.bme.sch.cmsch.component.signup

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.component.login.CmschUserPrincipal
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.TimeService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(SignupComponent::class)
open class SignupService(
    private val signupFormRepository: SignupFormRepository,
    private val signupResponseRepository: SignupResponseRepository,
    private val clock: TimeService,
    private val objectMapper: ObjectMapper
) {

    internal val log = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    open fun getAllForms(role: RoleType): List<SignupFormEntity> {
        val now = clock.getTime()
        return signupFormRepository.findAllByOpenTrueAndAvailableFromLessThanAndAvailableUntilGreaterThan(now, now)
            .filter { (it.minRole.value <= role.value && it.maxRole.value >= role.value) || role.isAdmin }
    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    open fun fetchForm(user: CmschUser, path: String): SignupFormView {
        val form = signupFormRepository.findAllByUrl(path).getOrNull(0)
            ?: return SignupFormView(status = FormStatus.NOT_FOUND)

        if ((form.minRole.value > user.role.value || form.maxRole.value < user.role.value) && !user.role.isAdmin)
            return SignupFormView(status = FormStatus.NOT_FOUND)

        val now = clock.getTime()
        if (!form.open)
            return SignupFormView(status = FormStatus.NOT_ENABLED)
        if (form.availableFrom > now)
            return SignupFormView(status = FormStatus.TOO_EARLY)
        if (form.availableUntil < now)
            return SignupFormView(status = FormStatus.TOO_LATE)

        val submission = signupResponseRepository.findByFormIdAndSubmitterUserId(form.id, user.id)
        if (submission.isPresent) {
            val entity = submission.orElseThrow()
            return when {
                entity.rejected -> SignupFormView(form = form, submission = entity, status = FormStatus.REJECTED, message = entity.rejectionMessage)
                entity.accepted -> SignupFormView(form = form, submission = entity, status = FormStatus.ACCEPTED, message = form.acceptedMessage)
                else -> SignupFormView(form = form, submission = entity, status = FormStatus.SUBMITTED, message = form.submittedMessage)
            }
        }

        if (isFull(form))
            return SignupFormView(status = FormStatus.FULL)

        return SignupFormView(form = form, submission = null, status = FormStatus.NO_SUBMISSION, message = null)
    }

    private fun isFull(form: SignupFormEntity): Boolean {
        return form.submissionLimit >= 0 && (signupResponseRepository.countAllByFormIdAndRejectedFalse(form.id) >= form.submissionLimit)
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun submitForm(user: UserEntity, path: String, data: Map<String, String>): FormSubmissionStatus {
        val form = signupFormRepository.findAllByUrl(path).getOrNull(0)
            ?: return FormSubmissionStatus.FORM_NOT_AVAILABLE

        if ((form.minRole.value > user.role.value || form.maxRole.value < user.role.value) && !user.role.isAdmin)
            return FormSubmissionStatus.FORM_NOT_AVAILABLE

        val now = clock.getTime()
        if (!form.open || form.availableFrom > now || form.availableUntil < now)
            return FormSubmissionStatus.FORM_NOT_AVAILABLE

        if (signupResponseRepository.findByFormIdAndSubmitterUserId(form.id, user.id).isPresent)
            return FormSubmissionStatus.ALREADY_SUBMITTED

        if (isFull(form))
            return FormSubmissionStatus.FORM_IS_FULL

        val formStruct = objectMapper.readerFor(object : TypeReference<List<SignupFormElement>>() {})
            .readValue<List<SignupFormElement>>(form.formJson)

        val submission = mutableMapOf<String, String>()
        for (signupFormElement in formStruct) {
            if (!signupFormElement.type.persist)
                continue

            if (signupFormElement.type.serverSide) {
                submission[signupFormElement.fieldName] = signupFormElement.type.fetchValue(user)
            } else {
                val value = data[signupFormElement.fieldName]
                if (value == null) {
                    log.info("User {} missing value {}", user.id, signupFormElement.fieldName)
                    return FormSubmissionStatus.INVALID_VALUES
                }

                when (signupFormElement.type) {
                    FormElementType.NUMBER -> {
                        if (!value.matches(Regex("[0-9]+"))) {
                            log.info("User {} invalid NUMBER value {} = {}", user.id, signupFormElement.fieldName, value)
                            return FormSubmissionStatus.INVALID_VALUES
                        }
                    }
                    FormElementType.EMAIL -> {
                        if (!value.matches(Regex(".+@.+\\..+"))) {
                            log.info("User {} invalid EMAIL value {} = {}", user.id, signupFormElement.fieldName, value)
                            return FormSubmissionStatus.INVALID_VALUES
                        }
                    }
                    FormElementType.SELECT -> {
                        if (value !in signupFormElement.values.split(Regex(",[ ]*"))) {
                            log.info("User {} invalid SELECT value {} = {}", user.id, signupFormElement.fieldName, value)
                            return FormSubmissionStatus.INVALID_VALUES
                        }
                    }
                    FormElementType.MUST_AGREE -> {
                        if (value != "true") {
                            log.info("User {} invalid MUST_AGREE value {} = {}", user.id, signupFormElement.fieldName, value)
                            return FormSubmissionStatus.INVALID_VALUES
                        }
                    }
                    else -> {
                        // more validators not necessarry
                    }
                }

                if (!value.matches(Regex(signupFormElement.formatRegex))) {
                    log.info("User {} invalid REGEX value {} = {}", user.id, signupFormElement.fieldName, value)
                    return FormSubmissionStatus.INVALID_VALUES
                }

                submission[signupFormElement.fieldName] = data[signupFormElement.fieldName]!!
            }
        }

        log.info("User {} filled out form {} successfully", user.id, form.id)
        signupResponseRepository.save(SignupResponseEntity(
            submitterUserId = user.id,
            submitterUserName = user.fullName,
            formId = form.id,
            creationDate = now,
            accepted = false,
            rejected = false
        ))

        return FormSubmissionStatus.OK
    }

}
