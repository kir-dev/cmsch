package hu.bme.sch.cmsch.component.form

import com.fasterxml.jackson.annotation.JsonView
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.UserService
import hu.bme.sch.cmsch.util.getUserEntityFromDatabaseOrNull
import hu.bme.sch.cmsch.util.getUserOrNull
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@ConditionalOnBean(FormComponent::class)
class FormApiController(
    private val formService: FormService,
    private val userService: UserService,
) {

    internal val log = LoggerFactory.getLogger(javaClass)
    private val objectMapper = jacksonObjectMapper()

    @JsonView(Preview::class)
    @GetMapping("/forms")
    fun forms(auth: Authentication?): List<FormEntity> {
        return formService.getAllForms(auth?.getUserOrNull()?.role ?: RoleType.GUEST)
    }

    @JsonView(FullDetails::class)
    @GetMapping("/form/{path}")
    fun getForm(@PathVariable path: String, auth: Authentication?): FormView {
        val user = auth?.getUserOrNull()
        return formService.fetchForm(user, path)
    }

    @PostMapping("/form/{path}")
    fun fillOutForm(
        @PathVariable path: String,
        auth: Authentication?,
        @RequestBody data: Map<String, String>
    ): FormSubmissionStatus {
        val user = auth?.getUserEntityFromDatabaseOrNull(userService)
        return submitForm(false, path, user, data)
    }

    @PutMapping("/form/{path}")
    fun updateForm(
        @PathVariable path: String,
        auth: Authentication?,
        @RequestBody data: Map<String, String>
    ): FormSubmissionStatus {
        val user = auth?.getUserEntityFromDatabaseOrNull(userService)
            ?: return FormSubmissionStatus.FORM_NOT_AVAILABLE
        return submitForm(true, path, user, data)
    }

    private fun submitForm(
        update: Boolean,
        path: String,
        user: UserEntity?,
        data: Map<String, String>
    ): FormSubmissionStatus {
        val (status, exitId, result) = formService.submitForm(user, path, data, update)
        log.info(
            "User '{}' {} form '{}' status: {} exitId: {} data: {}",
            user?.userName,
            if (update) "updating" else "filling out",
            path,
            status,
            exitId,
            objectMapper.writeValueAsString(data)
        )

        if (result != null) {
            runCatching {
                formService.sendConfirmationEmail(result.form, result.email, update, result.submission)
            }.exceptionOrNull()?.let { log.error("Failed to send email", it) }
        }
        return status
    }

}
