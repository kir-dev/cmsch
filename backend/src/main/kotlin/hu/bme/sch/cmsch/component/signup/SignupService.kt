package hu.bme.sch.cmsch.component.signup

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.UserRepository
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
    private val userRepository: UserRepository,
    private val clock: TimeService,
    private val objectMapper: ObjectMapper,
    private val applicationComponent: ApplicationComponent
) {

    internal val log = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    open fun getAllForms(role: RoleType): List<SignupFormEntity> {
        val now = clock.getTimeInSeconds()
        return signupFormRepository.findAllByOpenTrueAndAvailableFromLessThanAndAvailableUntilGreaterThan(now, now)
            .filter { (it.minRole.value <= role.value && it.maxRole.value >= role.value) || role.isAdmin }
    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    open fun fetchForm(user: UserEntity, path: String): SignupFormView {
        val form = signupFormRepository.findAllByUrl(path).getOrNull(0)
            ?: return SignupFormView(status = FormStatus.NOT_FOUND)

        if ((form.minRole.value > user.role.value || form.maxRole.value < user.role.value) && !user.role.isAdmin)
            return SignupFormView(status = FormStatus.NOT_FOUND)

        val now = clock.getTimeInSeconds() + (applicationComponent.submitDiff.getValue().toLongOrNull() ?: 0)
        if (!form.open)
            return SignupFormView(status = FormStatus.NOT_ENABLED)
        if (form.availableFrom > now)
            return SignupFormView(status = FormStatus.TOO_EARLY)
        if (form.availableUntil < now)
            return SignupFormView(status = FormStatus.TOO_LATE)

        if (form.allowedGroups.isNotBlank() && userRepository.findById(user.id)
                .map { it.groupName !in form.allowedGroups.split(Regex(",[ ]*")) }
                .orElse(true)) {
            return SignupFormView(status = FormStatus.GROUP_NOT_PERMITTED, message = form.groupRejectedMessage)
        }

        val groupId = user.group?.id
        if (form.ownerIsGroup == true && groupId == null)
            return SignupFormView(status = FormStatus.GROUP_NOT_PERMITTED, message = form.groupRejectedMessage)

        val submission = if (form.ownerIsGroup == true) {
            signupResponseRepository.findByFormIdAndSubmitterGroupId(form.id, groupId ?: 0)
        } else {
            signupResponseRepository.findByFormIdAndSubmitterUserId(form.id, user.id)
        }
        if (submission.isPresent) {
            val entity = submission.orElseThrow()
            val submittedFields = objectMapper.readerFor(object : TypeReference<Map<String, String>>() {})
                .readValue<Map<String, String>>(entity.submission)

            return when {
                entity.rejected -> SignupFormView(
                    form = SignupFormEntityDto(form),
                    submission = submittedFields,
                    detailsValidated = entity.detailsValidated,
                    status = FormStatus.REJECTED,
                    message = entity.rejectionMessage
                )
                entity.accepted -> SignupFormView(
                    form = SignupFormEntityDto(form),
                    submission = submittedFields,
                    detailsValidated = entity.detailsValidated,
                    status = FormStatus.ACCEPTED,
                    message = form.acceptedMessage
                )
                else -> SignupFormView(
                    form = SignupFormEntityDto(form),
                    submission = submittedFields,
                    detailsValidated = entity.detailsValidated,
                    status = FormStatus.SUBMITTED,
                    // TODO: Remove hardcoded string
                    message = form.submittedMessage
                            + (if (!entity.detailsValidated && entity.rejectionMessage.isNotBlank())
                                    ("\n\n**Üzenet a rendezőktől:** " + entity.rejectionMessage) else "")
                )
            }
        }

        if (isFull(form))
            return SignupFormView(status = FormStatus.FULL)

        return SignupFormView(form = SignupFormEntityDto(form), submission = null, status = FormStatus.NO_SUBMISSION, message = null)
    }

    private fun isFull(form: SignupFormEntity): Boolean {
        return form.submissionLimit >= 0 && (signupResponseRepository.countAllByFormIdAndRejectedFalse(form.id) >= form.submissionLimit)
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun submitForm(user: UserEntity, path: String, data: Map<String, String>, update: Boolean): FormSubmissionStatus {
        val form = signupFormRepository.findAllByUrl(path).getOrNull(0)
            ?: return FormSubmissionStatus.FORM_NOT_AVAILABLE

        if ((form.minRole.value > user.role.value || form.maxRole.value < user.role.value) && !user.role.isAdmin)
            return FormSubmissionStatus.FORM_NOT_AVAILABLE
        val now = clock.getTimeInSeconds()
        if (!form.open || !clock.inRange(form.availableFrom, form.availableUntil, now))
            return FormSubmissionStatus.FORM_NOT_AVAILABLE

        val groupId = user.group?.id
        if (form.ownerIsGroup == true && groupId == null)
            return FormSubmissionStatus.FORM_NOT_AVAILABLE

        val submissionEntity = if (form.ownerIsGroup == true) {
            signupResponseRepository.findByFormIdAndSubmitterGroupId(form.id, groupId ?: 0).orElse(null)
        } else {
            signupResponseRepository.findByFormIdAndSubmitterUserId(form.id, user.id).orElse(null)
        }

        if (!update && submissionEntity != null)
            return FormSubmissionStatus.ALREADY_SUBMITTED
        if (update && submissionEntity == null)
            return FormSubmissionStatus.EDIT_SUBMISSION_NOT_FOUND
        if (update && submissionEntity.detailsValidated)
            return FormSubmissionStatus.EDIT_CANNOT_BE_CHANGED
        if (form.allowedGroups.isNotBlank() && user.groupName !in form.allowedGroups.split(Regex(",[ ]*"))) {
            return FormSubmissionStatus.FORM_NOT_AVAILABLE
        }

        if (!update && isFull(form))
            return FormSubmissionStatus.FORM_IS_FULL

        val formStruct = objectMapper.readerFor(object : TypeReference<List<SignupFormElement>>() {})
            .readValue<List<SignupFormElement>>(form.formJson)

        val submission = mutableMapOf<String, String>()
        if (update) {
            objectMapper.readerFor(object : TypeReference<Map<String, String>>() {})
                .readValue<Map<String, String>>(submissionEntity.submission)
                .entries
                .forEach {
                    submission[it.key] = it.value
                }
        }

        for (field in formStruct) {
            if (!field.type.persist)
                continue

            if (update && field.permanent)
                continue

            if (field.type.serverSide) {
                submission[field.fieldName] = field.type.fetchValue(user)
            } else {
                val value = data[field.fieldName]
                if (value == null) {
                    log.info("User {} missing value {}", user.id, field.fieldName)
                    return FormSubmissionStatus.INVALID_VALUES
                }

                when (field.type) {
                    FormElementType.NUMBER -> {
                        if (!value.matches(Regex("[0-9]+"))) {
                            log.info("User {} invalid NUMBER value {} = {}", user.id, field.fieldName, value)
                            return FormSubmissionStatus.INVALID_VALUES
                        }
                    }
                    FormElementType.EMAIL -> {
                        if (!value.matches(Regex(".+@.+\\..+"))) {
                            log.info("User {} invalid EMAIL value {} = {}", user.id, field.fieldName, value)
                            return FormSubmissionStatus.INVALID_VALUES
                        }
                    }
                    FormElementType.CHECKBOX -> {
                        if (!value.equals("true", ignoreCase = true) && !value.equals("false", ignoreCase = true)) {
                            log.info("User {} invalid CHECKBOX value {} = {}", user.id, field.fieldName, value)
                            return FormSubmissionStatus.INVALID_VALUES
                        }
                    }
                    FormElementType.SELECT -> {
                        if (value !in field.values.split(Regex(",[ ]*")).map { it.trim() }) {
                            log.info("User {} invalid SELECT value {} = {}", user.id, field.fieldName, value)
                            return FormSubmissionStatus.INVALID_VALUES
                        }
                    }
                    FormElementType.MUST_AGREE -> {
                        if (value != "true") {
                            log.info("User {} invalid MUST_AGREE value {} = {}", user.id, field.fieldName, value)
                            return FormSubmissionStatus.INVALID_VALUES
                        }
                    }
                    else -> {
                        // more validators not necessary
                    }
                }

                if (!value.matches(Regex(field.formatRegex))) {
                    log.info("User {} invalid REGEX value {} = {}", user.id, field.fieldName, value)
                    return FormSubmissionStatus.INVALID_VALUES
                }

                submission[field.fieldName] = data[field.fieldName]!!
            }
        }

        if (update) {
            log.info("User {} changed form {} successfully", user.id, form.id)
            submissionEntity.submission = objectMapper.writeValueAsString(submission)
            submissionEntity.lastUpdatedDate = now
            signupResponseRepository.save(submissionEntity)
        } else {
            log.info("User {} filled out form {} successfully", user.id, form.id)
            signupResponseRepository.save(SignupResponseEntity(
                submitterUserId = if (form.ownerIsGroup == true) null else user.id,
                submitterUserName = if (form.ownerIsGroup == true) "" else user.fullName,
                submitterGroupId = if (form.ownerIsGroup == true) groupId else null,
                submitterGroupName = if (form.ownerIsGroup == true) (user.group?.name ?: "") else "",
                formId = form.id,
                creationDate = now,
                accepted = false,
                rejected = false,
                email = user.email,
                submission = objectMapper.writeValueAsString(submission)
            ))
        }

        if (form.grantAttendeeRole) {
            if (user.role.value <= RoleType.ATTENDEE.value) {
                user.role = RoleType.ATTENDEE
                userRepository.save(user)
                log.info("Granting ATTENDEE for user {} by filling form {} successfully", user.id, form.id)
            } else {
                log.info("NOT granting ATTENDEE for user {} for filling form {} successfully because higher role", user.id, form.id)
            }
            return FormSubmissionStatus.OK_RELOG_REQUIRED
        }

        return FormSubmissionStatus.OK
    }

    @Transactional(readOnly = true)
    open fun getSelectedForms(): List<SignupFormEntity> {
        return signupFormRepository.findAllBySelectedTrue()
    }

    @Transactional(readOnly = true)
    open fun getSubmissions(form: SignupFormEntity): List<SignupResponseEntity> {
        return signupResponseRepository.findAllByFormId(form.id)
    }

}
