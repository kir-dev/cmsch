package hu.bme.sch.cmsch.component.form

import tools.jackson.core.type.TypeReference
import tools.jackson.module.kotlin.jacksonObjectMapper
import hu.bme.sch.cmsch.component.email.EmailService
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.extending.FormSubmissionListener
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.TimeService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.resilience.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.sql.SQLException
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
@ConditionalOnBean(FormComponent::class)
class FormService(
    private val formRepository: FormRepository,
    private val responseRepository: ResponseRepository,
    private val userRepository: UserRepository,
    private val clock: TimeService,
    private val listeners: MutableList<out FormSubmissionListener>,
    private val formComponent: FormComponent,
    private val emailService: Optional<EmailService>
) {

    internal val log = LoggerFactory.getLogger(javaClass)

    private final val objectMapper = jacksonObjectMapper()
    private final val gridReader = objectMapper.readerFor(FormGridValue::class.java)
    private final val choiceGridReader = objectMapper.readerFor(object : TypeReference<MutableMap<String, String>>() {})
    private final val selectionGridReader = objectMapper.readerFor(object : TypeReference<MutableMap<String, Boolean>>() {})
    private final val choiceGridWriter = objectMapper.writerFor(object : TypeReference<MutableMap<String, String>>() {})
    private final val selectionGridWriter = objectMapper.writerFor(object : TypeReference<MutableMap<String, Boolean>>() {})

    @Transactional(readOnly = true)
    fun getAllForms(role: RoleType): List<FormEntity> {
        val now = clock.getTimeInSeconds()
        return formRepository.findAllByOpenTrueAndAvailableFromLessThanAndAvailableUntilGreaterThan(now, now)
            .filter { (it.minRole.value <= role.value && it.maxRole.value >= role.value) || role.isAdmin }
    }

    @Transactional(readOnly = true)
    fun getAllAdvertised(role: RoleType): List<FormEntity> {
        val now = clock.getTimeInSeconds()
        return formRepository.findAllByAdvertizedTrueAndOpenTrueAndAvailableFromLessThanAndAvailableUntilGreaterThan(now, now)
            .filter { (it.minRole.value <= role.value && it.maxRole.value >= role.value) || role.isAdmin }
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    fun fetchForm(user: CmschUser?, path: String): FormView {
        val form = formRepository.findAllByUrl(path).getOrNull(0)
            ?: return FormView(status = FormStatus.NOT_FOUND)

        val role = user?.role ?: RoleType.GUEST
        if ((form.minRole.value > role.value || form.maxRole.value < role.value) && !role.isAdmin)
            return FormView(status = FormStatus.NOT_FOUND)

        val now = clock.getTimeInSeconds()
        if (!form.open)
            return FormView(status = FormStatus.NOT_ENABLED)
        if (form.availableFrom > now)
            return FormView(status = FormStatus.TOO_EARLY)
        if (form.availableUntil < now)
            return FormView(status = FormStatus.TOO_LATE)

        if (user?.id != null && form.allowedGroups.isNotBlank() && userRepository.findById(user.id)
                .map { it.groupName !in form.allowedGroups.split(Regex(", *")) }
                .orElse(true)) {
            return FormView(status = FormStatus.GROUP_NOT_PERMITTED, message = form.groupRejectedMessage)
        }

        val groupId = user?.groupId
        if (form.ownerIsGroup && groupId == null)
            return FormView(status = FormStatus.GROUP_NOT_PERMITTED, message = form.groupRejectedMessage)

        val submission: Optional<ResponseEntity> = if (form.ownerIsGroup) {
            responseRepository.findByFormIdAndSubmitterGroupId(form.id, groupId ?: 0)
        } else {
            user?.id?.let { responseRepository.findByFormIdAndSubmitterUserId(form.id, it) } ?: Optional.empty()
        }

        if (submission.isPresent) {
            val entity = submission.orElseThrow()
            val submittedFields = objectMapper.readerFor(object : TypeReference<Map<String, String>>() {})
                .readValue<Map<String, String>>(entity.submission)

            return when {
                entity.rejected -> FormView(
                    form = FormEntityDto(form),
                    submission = submittedFields,
                    detailsValidated = entity.detailsValidated,
                    status = FormStatus.REJECTED,
                    message = entity.rejectionMessage
                )
                entity.accepted -> FormView(
                    form = FormEntityDto(form),
                    submission = submittedFields,
                    detailsValidated = entity.detailsValidated,
                    status = FormStatus.ACCEPTED,
                    message = form.acceptedMessage
                )
                else -> FormView(
                    form = FormEntityDto(form),
                    submission = submittedFields,
                    detailsValidated = entity.detailsValidated,
                    status = FormStatus.SUBMITTED,
                    message = form.submittedMessage
                            + (if (!entity.detailsValidated && entity.rejectionMessage.isNotBlank())
                                    ("\n\n " + formComponent.langMessageFromOrganizers + entity.rejectionMessage) else "")
                )
            }
        }

        if (isFull(form))
            return FormView(status = FormStatus.FULL)

        return FormView(
            form = FormEntityDto(form),
            submission = null,
            status = FormStatus.NO_SUBMISSION,
            message = null
        )
    }

    private fun isFull(form: FormEntity): Boolean {
        return form.submissionLimit >= 0 && (responseRepository.countAllByFormIdAndRejectedFalse(form.id) >= form.submissionLimit)
    }

    data class FormSubmissionResult(
        val status: FormSubmissionStatus,
        val exitId: Int,
        val data: FormSubmissionData? = null
    )

    data class FormSubmissionData(
        val form: FormEntity,
        val email: String?,
        val submission: MutableMap<String, String>
    )


    @Retryable(value = [SQLException::class], maxRetries = 5, delay = 500L, multiplier = 1.5)
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun submitForm(user: CmschUser?, path: String, data: Map<String, String>, update: Boolean): FormSubmissionResult {
        val form = formRepository.findAllByUrl(path).getOrNull(0)
            ?: return FormSubmissionResult(FormSubmissionStatus.FORM_NOT_AVAILABLE, 1)

        val role = user?.role ?: RoleType.GUEST
        if ((form.minRole.value > role.value || form.maxRole.value < role.value) && !role.isAdmin)
            return FormSubmissionResult(FormSubmissionStatus.FORM_NOT_AVAILABLE, 2)
        val now = clock.getTimeInSeconds()
        if (!form.open || !clock.inRange(form.availableFrom, form.availableUntil, now))
            return FormSubmissionResult(FormSubmissionStatus.FORM_NOT_AVAILABLE, 3)

        val groupId = user?.groupId
        if (form.ownerIsGroup && groupId == null)
            return FormSubmissionResult(FormSubmissionStatus.FORM_NOT_AVAILABLE, 4)

        val submissionEntity: ResponseEntity? = if (form.ownerIsGroup) {
            responseRepository.findByFormIdAndSubmitterGroupId(form.id, groupId ?: 0).getOrNull()
        } else {
            user?.id?.let { responseRepository.findByFormIdAndSubmitterUserId(form.id, it).getOrNull() }
        }

        if (!update && submissionEntity != null)
            return FormSubmissionResult(FormSubmissionStatus.ALREADY_SUBMITTED, 5)
        if (update && submissionEntity == null)
            return FormSubmissionResult(FormSubmissionStatus.EDIT_SUBMISSION_NOT_FOUND, 6)
        if (update && submissionEntity!!.detailsValidated)
            return FormSubmissionResult(FormSubmissionStatus.EDIT_CANNOT_BE_CHANGED, 7)
        if (form.allowedGroups.isNotBlank() && user?.groupName !in form.allowedGroups.split(Regex(", *"))) {
            return FormSubmissionResult(FormSubmissionStatus.FORM_NOT_AVAILABLE, 8)
        }

        if (!update && isFull(form))
            return FormSubmissionResult(FormSubmissionStatus.FORM_IS_FULL, 9)

        val formStruct = objectMapper.readerFor(object : TypeReference<List<FormElement>>() {})
            .readValue<List<FormElement>>(form.formJson)

        val submission = mutableMapOf<String, String>()
        if (update) {
            objectMapper.readerFor(object : TypeReference<Map<String, String>>() {})
                .readValue<Map<String, String>>(submissionEntity!!.submission)
                .entries
                .forEach {
                    submission[it.key] = it.value
                }
        }

        val userEntity = user?.id?.let { userRepository.findById(it).getOrNull() }
        for (field in formStruct) {
            if (!field.type.persist)
                continue

            if (update && field.permanent)
                continue

            if (field.type.serverSide) {
                submission[field.fieldName] = field.type.fetchValue(userEntity)
            } else {
                var value = data[field.fieldName]
                if (value == null) {
                    log.info("User {} missing value {}", user?.id, field.fieldName)
                    return FormSubmissionResult(FormSubmissionStatus.INVALID_VALUES, 10)
                }

                when (field.type) {
                    FormElementType.NUMBER -> {
                        if (!value.matches(Regex("[0-9]+")) && (value != "" || field.required)) {
                            log.info("User {} invalid NUMBER value {} = '{}'", user?.id, field.fieldName, value)
                            return FormSubmissionResult(FormSubmissionStatus.INVALID_VALUES, 11)
                        }
                    }
                    FormElementType.EMAIL -> {
                        if (!value.matches(Regex(".+@.+\\..+"))) {
                            log.info("User {} invalid EMAIL value {} = '{}'", user?.id, field.fieldName, value)
                            return FormSubmissionResult(FormSubmissionStatus.INVALID_VALUES, 12)
                        }
                    }
                    FormElementType.CHECKBOX -> {
                        if (!value.equals("true", ignoreCase = true) && !value.equals("false", ignoreCase = true) && value != "") {
                            log.info("User {} invalid CHECKBOX value {} = '{}'", user?.id, field.fieldName, value)
                            return FormSubmissionResult(FormSubmissionStatus.INVALID_VALUES, 13)
                        }
                    }
                    FormElementType.SELECT -> {
                        if (value !in field.values.split(Regex(", *")).map { it.trim() }) {
                            log.info("User {} invalid SELECT value {} = '{}'", user?.id, field.fieldName, value)
                            return FormSubmissionResult(FormSubmissionStatus.INVALID_VALUES, 14)
                        }
                    }
                    FormElementType.MUST_AGREE -> {
                        if (value != "true") {
                            log.info("User {} invalid MUST_AGREE value {} = '{}'", user?.id, field.fieldName, value)
                            return FormSubmissionResult(FormSubmissionStatus.INVALID_VALUES, 15)
                        }
                    }
                    FormElementType.CHOICE_GRID -> {
                        val valueTree = try {
                            choiceGridReader.readValue<Map<String, String>>(value)
                        } catch (e: Exception) {
                            log.info("User {} invalid CHOICE_GRID value {} = '{}'", user?.id, field.fieldName, value)
                            return FormSubmissionResult(FormSubmissionStatus.INVALID_VALUES, 16)
                        }
                        val componentStruct = gridReader.readValue<FormGridValue>(field.values)
                        val newValue = mutableMapOf<String, String>()
                        val options = componentStruct.options.map { it.key }
                        componentStruct.questions.forEach { question ->
                            newValue[question.key] =
                                if (valueTree.containsKey(question.key) && valueTree[question.key] in options) {
                                    valueTree[question.key] ?: options.firstOrNull() ?: "n/a"
                                } else {
                                    options.firstOrNull() ?: "n/a"
                                }
                        }
                        value = choiceGridWriter.writeValueAsString(newValue)!!
                    }
                    FormElementType.SELECTION_GRID -> {
                        val valueTree = try {
                            selectionGridReader.readValue<Map<String, Boolean>>(value)
                        } catch (e: Exception) {
                            log.info("User {} invalid SELECTION_GRID value {} = '{}'", user?.id, field.fieldName, value)
                            return FormSubmissionResult(FormSubmissionStatus.INVALID_VALUES, 17)
                        }
                        val componentStruct = gridReader.readValue<FormGridValue>(field.values)
                        val newValue = mutableMapOf<String, Boolean>()
                        componentStruct.questions.forEach { question ->
                            componentStruct.options.forEach { option ->
                                newValue["${question.key}_${option.key}"] =
                                    valueTree["${question.key}_${option.key}"] ?: false
                            }
                        }
                        value = selectionGridWriter.writeValueAsString(newValue)!!
                    }
                    else -> {
                        // more validators not necessary
                    }
                }

                if (!value.matches(Regex(field.formatRegex))) {
                    log.info("User {} invalid REGEX value {} = {}", user?.id, field.fieldName, value)
                    return FormSubmissionResult(FormSubmissionStatus.INVALID_VALUES, 18)
                }

                submission[field.fieldName] = value
                if (field.type == FormElementType.CHECKBOX && value == "") {
                    submission[field.fieldName] = "false"
                }
            }
        }

        val email = getEmailAddressForConfirmation(form, userEntity, submission) ?: ""
        val entryToken = getEntryToken(form, submission) ?: ""

        if (update) {
            log.info("User {} changed form {} successfully", user?.id, form.id)
            submissionEntity!!.submission = objectMapper.writeValueAsString(submission)
            submissionEntity.lastUpdatedDate = now
            submissionEntity.entryToken = entryToken
            submissionEntity.email = email
            responseRepository.save(submissionEntity)
            listeners.forEach { it.onFormUpdated(user, form, submissionEntity) }
        } else {
            log.info("User {} filled out form {} successfully", user?.id, form.id)
            val responseEntity = ResponseEntity(
                entryToken = entryToken,
                submitterUserId = if (form.ownerIsGroup) null else user?.id,
                submitterUserName = if (form.ownerIsGroup) "" else user?.userName ?: "GUEST",
                submitterGroupId = if (form.ownerIsGroup) groupId else null,
                submitterGroupName = if (form.ownerIsGroup) user?.groupName ?: "UNKNOWN_GROUP" else "",
                formId = form.id,
                creationDate = now,
                accepted = false,
                rejected = false,
                email = email,
                submission = objectMapper.writeValueAsString(submission),
                line = (responseRepository.findTop1ByFormIdOrderByLineDesc(form.id).firstOrNull()?.line ?: 0) + 1
            )
            responseRepository.save(responseEntity)
            listeners.forEach { it.onFormSubmitted(user, form, responseEntity) }
        }

        val submissionData = FormSubmissionData(form, email, submission)
        if (form.grantAttendeeRole) {
            if (userEntity != null && userEntity.role.value <= RoleType.ATTENDEE.value) {
                userEntity.role = RoleType.ATTENDEE
                userRepository.save(userEntity)
                log.info("Granting ATTENDEE for user {} by filling form {} successfully", user.id, form.id)
            } else {
                log.info("NOT granting ATTENDEE for user {} for filling form {} successfully because higher role", user?.id, form.id)
            }
            return FormSubmissionResult(FormSubmissionStatus.OK_RELOG_REQUIRED, 201, submissionData)
        }

        if (form.grantPrivilegedRole) {
            if (userEntity != null && userEntity.role.value <= RoleType.PRIVILEGED.value) {
                userEntity.role = RoleType.PRIVILEGED
                userRepository.save(userEntity)
                log.info("Granting PRIVILEGED for user {} by filling form {} successfully", user.id, form.id)
            } else {
                log.info("NOT granting PRIVILEGED for user {} for filling form {} successfully because higher role", user?.id, form.id)
            }
            return FormSubmissionResult(FormSubmissionStatus.OK_RELOG_REQUIRED, 201, submissionData)
        }

        return FormSubmissionResult(FormSubmissionStatus.OK, 200, submissionData)
    }

    private fun getEntryToken(form: FormEntity, values: Map<String, String>): String? {
        val field = form.tokenFieldName
        if (field.isBlank()) return null

        return values[field]
    }

    fun sendConfirmationEmail(
        form: FormEntity,
        email: String?,
        update: Boolean,
        submission: MutableMap<String, String>
    ): Boolean {
        if (!form.sendConfirmationEmail) {
            return false
        }

        if (form.sendConfirmationEmailOnlyOnce && update) {
            return false
        }

        val emailService = emailService.getOrNull()
        if (emailService == null) {
            log.info("Cannot send email confirmation for form {}, because emailService is null", form.id)
            return false
        }

        if (email.isNullOrBlank()) {
            log.info("Attempted to form response confirmation email, but found no email address. form: {}", form.id)
            return false
        }
        val emailTemplate = emailService.getTemplateBySelector(form.emailTemplateSelector)
        if (emailTemplate == null) {
            log.info("Attempted to form response confirmation email, but couldn't find email template {}. form: {}", form.emailTemplateSelector, form.id)
            return false
        }

        val responsible = null // The email is sent by the system rather than an admin user
        emailService.sendTemplatedEmail(responsible, emailTemplate, submission, listOf(email))
        log.info("Sent confirmation email with template: {} for form: {}", form.emailTemplateSelector, form.id)

        return true
    }


    private fun getEmailAddressForConfirmation(
        form: FormEntity,
        user: UserEntity?,
        submission: MutableMap<String, String>
    ): String? {
        if (form.emailFieldName.isNotBlank()) {
            val email = submission[form.emailFieldName]
            if (!email.isNullOrBlank()) return email
        }
        return user?.email
    }

    @Transactional(readOnly = true)
    fun getSelectedForms(): List<FormEntity> {
        return formRepository.findAllBySelectedTrue()
    }

    @Transactional(readOnly = true)
    fun getForm(formId: Int): FormEntity? {
        return formRepository.findById(formId).getOrNull()
    }

    @Transactional(readOnly = true)
    fun getSubmissions(form: FormEntity): List<ResponseEntity> {
        return responseRepository.findAllByFormId(form.id)
    }

    @Transactional(readOnly = true)
    fun getSubmissionCount(form: FormEntity): Long {
        return responseRepository.countByFormId(form.id)
    }

    @Transactional(readOnly = true)
    fun getResponsesById(id: Int) = responseRepository.findAllByFormId(id)

    @Transactional(readOnly = true)
    fun getAllResponses(): List<ResponseEntity> = responseRepository.findAll()

    @Transactional(readOnly = true)
    fun doesGroupFilled(groupId: Int, formId: Int): Boolean {
        return responseRepository.countTop1ByFormIdAndSubmitterGroupId(formId, groupId) > 0
    }

    @Transactional(readOnly = true)
    fun doesUserFilled(userId: Int, formId: Int): Boolean {
        return responseRepository.countTop1ByFormIdAndSubmitterUserId(formId, userId) > 0
    }

}
