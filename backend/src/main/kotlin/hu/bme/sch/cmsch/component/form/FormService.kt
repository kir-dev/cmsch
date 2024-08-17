package hu.bme.sch.cmsch.component.form

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hu.bme.sch.cmsch.component.app.DebugComponent
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.extending.FormSubmissionListener
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.TimeService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.sql.SQLException
import kotlin.jvm.optionals.getOrNull

@Service
@ConditionalOnBean(FormComponent::class)
open class FormService(
    private val formRepository: FormRepository,
    private val responseRepository: ResponseRepository,
    private val userRepository: UserRepository,
    private val clock: TimeService,
    private val debugComponent: DebugComponent,
    private val listeners: MutableList<out FormSubmissionListener>,
    private val formComponent: FormComponent,
) {

    internal val log = LoggerFactory.getLogger(javaClass)

    private val objectMapper = jacksonObjectMapper()
    private val gridReader = objectMapper.readerFor(FormGridValue::class.java)
    private val choiceGridReader = objectMapper.readerFor(object : TypeReference<MutableMap<String, String>>() {})
    private val selectionGridReader = objectMapper.readerFor(object : TypeReference<MutableMap<String, Boolean>>() {})
    private val choiceGridWriter = objectMapper.writerFor(object : TypeReference<MutableMap<String, String>>() {})
    private val selectionGridWriter = objectMapper.writerFor(object : TypeReference<MutableMap<String, Boolean>>() {})

    @Transactional(readOnly = true)
    open fun getAllForms(role: RoleType): List<FormEntity> {
        val now = clock.getTimeInSeconds()
        return formRepository.findAllByOpenTrueAndAvailableFromLessThanAndAvailableUntilGreaterThan(now, now)
            .filter { (it.minRole.value <= role.value && it.maxRole.value >= role.value) || role.isAdmin }
    }

    @Transactional(readOnly = true)
    open fun getAllAdvertised(role: RoleType): List<FormEntity> {
        val now = clock.getTimeInSeconds()
        return formRepository.findAllByAdvertizedTrueAndOpenTrueAndAvailableFromLessThanAndAvailableUntilGreaterThan(now, now)
            .filter { (it.minRole.value <= role.value && it.maxRole.value >= role.value) || role.isAdmin }
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    open fun fetchForm(user: CmschUser, path: String): FormView {
        val form = formRepository.findAllByUrl(path).getOrNull(0)
            ?: return FormView(status = FormStatus.NOT_FOUND)

        if ((form.minRole.value > user.role.value || form.maxRole.value < user.role.value) && !user.role.isAdmin)
            return FormView(status = FormStatus.NOT_FOUND)

        val now = clock.getTimeInSeconds() + (debugComponent.submitDiff.getValue().toLongOrNull() ?: 0)
        if (!form.open)
            return FormView(status = FormStatus.NOT_ENABLED)
        if (form.availableFrom > now)
            return FormView(status = FormStatus.TOO_EARLY)
        if (form.availableUntil < now)
            return FormView(status = FormStatus.TOO_LATE)

        if (form.allowedGroups.isNotBlank() && userRepository.findById(user.id)
                .map { it.groupName !in form.allowedGroups.split(Regex(", *")) }
                .orElse(true)) {
            return FormView(status = FormStatus.GROUP_NOT_PERMITTED, message = form.groupRejectedMessage)
        }

        val groupId = user.groupId
        if (form.ownerIsGroup && groupId == null)
            return FormView(status = FormStatus.GROUP_NOT_PERMITTED, message = form.groupRejectedMessage)

        val submission = if (form.ownerIsGroup) {
            responseRepository.findByFormIdAndSubmitterGroupId(form.id, groupId ?: 0)
        } else {
            responseRepository.findByFormIdAndSubmitterUserId(form.id, user.id)
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
                                    ("\n\n " + formComponent.langMessageFromOrganizers.getValue() + entity.rejectionMessage) else "")
                )
            }
        }

        if (isFull(form))
            return FormView(status = FormStatus.FULL)

        return FormView(form = FormEntityDto(form), submission = null, status = FormStatus.NO_SUBMISSION, message = null)
    }

    private fun isFull(form: FormEntity): Boolean {
        return form.submissionLimit >= 0 && (responseRepository.countAllByFormIdAndRejectedFalse(form.id) >= form.submissionLimit)
    }

    @Retryable(value = [ SQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun submitForm(user: CmschUser, path: String, data: Map<String, String>, update: Boolean): Pair<FormSubmissionStatus, Int> {
        val form = formRepository.findAllByUrl(path).getOrNull(0)
            ?: return Pair(FormSubmissionStatus.FORM_NOT_AVAILABLE, 1)

        if ((form.minRole.value > user.role.value || form.maxRole.value < user.role.value) && !user.role.isAdmin)
            return Pair(FormSubmissionStatus.FORM_NOT_AVAILABLE, 2)
        val now = clock.getTimeInSeconds()
        if (!form.open || !clock.inRange(form.availableFrom, form.availableUntil, now))
            return Pair(FormSubmissionStatus.FORM_NOT_AVAILABLE, 3)

        val groupId = user.groupId
        if (form.ownerIsGroup && groupId == null)
            return Pair(FormSubmissionStatus.FORM_NOT_AVAILABLE, 4)

        val submissionEntity = if (form.ownerIsGroup) {
            responseRepository.findByFormIdAndSubmitterGroupId(form.id, groupId ?: 0).orElse(null)
        } else {
            responseRepository.findByFormIdAndSubmitterUserId(form.id, user.id).orElse(null)
        }

        if (!update && submissionEntity != null)
            return Pair(FormSubmissionStatus.ALREADY_SUBMITTED, 5)
        if (update && submissionEntity == null)
            return Pair(FormSubmissionStatus.EDIT_SUBMISSION_NOT_FOUND, 6)
        if (update && submissionEntity.detailsValidated)
            return Pair(FormSubmissionStatus.EDIT_CANNOT_BE_CHANGED, 7)
        if (form.allowedGroups.isNotBlank() && user.groupName !in form.allowedGroups.split(Regex(", *"))) {
            return Pair(FormSubmissionStatus.FORM_NOT_AVAILABLE, 8)
        }

        if (!update && isFull(form))
            return Pair(FormSubmissionStatus.FORM_IS_FULL, 9)

        val formStruct = objectMapper.readerFor(object : TypeReference<List<FormElement>>() {})
            .readValue<List<FormElement>>(form.formJson)

        val submission = mutableMapOf<String, String>()
        if (update) {
            objectMapper.readerFor(object : TypeReference<Map<String, String>>() {})
                .readValue<Map<String, String>>(submissionEntity.submission)
                .entries
                .forEach {
                    submission[it.key] = it.value
                }
        }

        val userEntity by lazy { userRepository.findById(user.id).orElseThrow() }
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
                    log.info("User {} missing value {}", user.id, field.fieldName)
                    return Pair(FormSubmissionStatus.INVALID_VALUES, 10)
                }

                when (field.type) {
                    FormElementType.NUMBER -> {
                        if (!value.matches(Regex("[0-9]+"))) {
                            log.info("User {} invalid NUMBER value {} = '{}'", user.id, field.fieldName, value)
                            return Pair(FormSubmissionStatus.INVALID_VALUES, 11)
                        }
                    }
                    FormElementType.EMAIL -> {
                        if (!value.matches(Regex(".+@.+\\..+"))) {
                            log.info("User {} invalid EMAIL value {} = '{}'", user.id, field.fieldName, value)
                            return Pair(FormSubmissionStatus.INVALID_VALUES, 12)
                        }
                    }
                    FormElementType.CHECKBOX -> {
                        if (!value.equals("true", ignoreCase = true) && !value.equals("false", ignoreCase = true) && value != "") {
                            log.info("User {} invalid CHECKBOX value {} = '{}'", user.id, field.fieldName, value)
                            return Pair(FormSubmissionStatus.INVALID_VALUES, 13)
                        }
                    }
                    FormElementType.SELECT -> {
                        if (value !in field.values.split(Regex(", *")).map { it.trim() }) {
                            log.info("User {} invalid SELECT value {} = '{}'", user.id, field.fieldName, value)
                            return Pair(FormSubmissionStatus.INVALID_VALUES, 14)
                        }
                    }
                    FormElementType.MUST_AGREE -> {
                        if (value != "true") {
                            log.info("User {} invalid MUST_AGREE value {} = '{}'", user.id, field.fieldName, value)
                            return Pair(FormSubmissionStatus.INVALID_VALUES, 15)
                        }
                    }
                    FormElementType.CHOICE_GRID -> {
                        val valueTree = try {
                            choiceGridReader.readValue<Map<String, String>>(value)
                        } catch (e: Exception) {
                            log.info("User {} invalid CHOICE_GRID value {} = '{}'", user.id, field.fieldName, value)
                            return Pair(FormSubmissionStatus.INVALID_VALUES, 16)
                        }
                        val componentStruct = gridReader.readValue<FormGridValue>(field.values)
                        val newValue = mutableMapOf<String, String>()
                        val options = componentStruct.options.map { it.key }
                        componentStruct.questions.forEach { question ->
                            newValue[question.key] = if (valueTree.containsKey(question.key) && valueTree[question.key] in options) {
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
                            log.info("User {} invalid SELECTION_GRID value {} = '{}'", user.id, field.fieldName, value)
                            return Pair(FormSubmissionStatus.INVALID_VALUES, 17)
                        }
                        val componentStruct = gridReader.readValue<FormGridValue>(field.values)
                        val newValue = mutableMapOf<String, Boolean>()
                        componentStruct.questions.forEach { question ->
                            componentStruct.options.forEach { option ->
                                newValue["${question.key}_${option.key}"] = valueTree["${question.key}_${option.key}"] ?: false
                            }
                        }
                        value = selectionGridWriter.writeValueAsString(newValue)!!
                    }
                    else -> {
                        // more validators not necessary
                    }
                }

                if (!value.matches(Regex(field.formatRegex))) {
                    log.info("User {} invalid REGEX value {} = {}", user.id, field.fieldName, value)
                    return Pair(FormSubmissionStatus.INVALID_VALUES, 18)
                }

                submission[field.fieldName] = value
                if (field.type == FormElementType.CHECKBOX && value == "") {
                    submission[field.fieldName] = "false"
                }
            }
        }

        if (update) {
            log.info("User {} changed form {} successfully", user.id, form.id)
            submissionEntity.submission = objectMapper.writeValueAsString(submission)
            submissionEntity.lastUpdatedDate = now
            responseRepository.save(submissionEntity)
            listeners.forEach { it.onFormUpdated(user, form, submissionEntity) }
        } else {
            log.info("User {} filled out form {} successfully", user.id, form.id)
            val responseEntity = ResponseEntity(
                submitterUserId = if (form.ownerIsGroup) null else user.id,
                submitterUserName = if (form.ownerIsGroup) "" else user.userName,
                submitterGroupId = if (form.ownerIsGroup) groupId else null,
                submitterGroupName = if (form.ownerIsGroup) user.groupName else "",
                formId = form.id,
                creationDate = now,
                accepted = false,
                rejected = false,
                email = userEntity.email,
                submission = objectMapper.writeValueAsString(submission),
                line = (responseRepository.findTop1ByFormIdOrderByLineDesc(form.id).firstOrNull()?.line ?: 0) + 1
            )
            responseRepository.save(responseEntity)
            listeners.forEach { it.onFormSubmitted(user, form, responseEntity) }
        }

        if (form.grantAttendeeRole) {
            if (userEntity.role.value <= RoleType.ATTENDEE.value) {
                userEntity.role = RoleType.ATTENDEE
                userRepository.save(userEntity)
                log.info("Granting ATTENDEE for user {} by filling form {} successfully", user.id, form.id)
            } else {
                log.info("NOT granting ATTENDEE for user {} for filling form {} successfully because higher role", user.id, form.id)
            }
            return Pair(FormSubmissionStatus.OK_RELOG_REQUIRED, 201)
        }

        return Pair(FormSubmissionStatus.OK, 200)
    }

    @Transactional(readOnly = true)
    open fun getSelectedForms(): List<FormEntity> {
        return formRepository.findAllBySelectedTrue()
    }

    @Transactional(readOnly = true)
    open fun getForm(formId: Int): FormEntity? {
        return formRepository.findById(formId).getOrNull()
    }

    @Transactional(readOnly = true)
    open fun getSubmissions(form: FormEntity): List<ResponseEntity> {
        return responseRepository.findAllByFormId(form.id)
    }

    @Transactional(readOnly = true)
    open fun getSubmissionCount(form: FormEntity): Long {
        return responseRepository.countByFormId(form.id)
    }

    @Transactional(readOnly = true)
    open fun getResponsesById(id: Int) = responseRepository.findAllByFormId(id)

    @Transactional(readOnly = true)
    open fun getAllResponses(): List<ResponseEntity> = responseRepository.findAll()

    @Transactional(readOnly = true)
    open fun doesGroupFilled(groupId: Int, formId: Int): Boolean {
        return responseRepository.countTop1ByFormIdAndSubmitterGroupId(formId, groupId) > 0
    }

    @Transactional(readOnly = true)
    open fun doesUserFilled(userId: Int, formId: Int): Boolean {
        return responseRepository.countTop1ByFormIdAndSubmitterUserId(formId, userId) > 0
    }

}
