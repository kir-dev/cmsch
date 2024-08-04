package hu.bme.sch.cmsch.component.form

import com.fasterxml.jackson.annotation.JsonView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.util.getUserOrNull
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@ConditionalOnBean(FormComponent::class)
class FormApiController(
    private val formService: FormService
) {

    internal val log = LoggerFactory.getLogger(javaClass)
    private val objectMapper = jacksonObjectMapper()

    @JsonView(Preview::class)
    @GetMapping("/forms")
    fun forms(auth: Authentication?): List<FormEntity> {
        return formService.getAllForms(auth?.getUserOrNull()?.role ?: RoleType.BASIC)
    }

    @JsonView(FullDetails::class)
    @GetMapping("/form/{path}")
    fun specificForm(@PathVariable path: String, auth: Authentication?): FormView {
        val user = auth?.getUserOrNull()
            ?: return FormView(status = FormStatus.NOT_FOUND)

        return formService.fetchForm(user, path)
    }

    @PostMapping("/form/{path}")
    fun fillOutForm(@PathVariable path: String, auth: Authentication?, @RequestBody data: Map<String, String>): FormSubmissionStatus {
        val user = auth?.getUserOrNull()
            ?: return FormSubmissionStatus.FORM_NOT_AVAILABLE

        val (status, exitId) = formService.submitForm(user, path, data, false)
        log.info("User '{}' filling out form '{}' status: {} exitId: {} data: {}", user.userName, path, status, exitId, objectMapper.writeValueAsString(data))
        return status
    }

    @PutMapping("/form/{path}")
    fun updateForm(@PathVariable path: String, auth: Authentication?, @RequestBody data: Map<String, String>): FormSubmissionStatus {
        val user = auth?.getUserOrNull()
            ?: return FormSubmissionStatus.FORM_NOT_AVAILABLE

        val (status, exitId) = formService.submitForm(user, path, data, true)
        log.info("User '{}' updating form '{}' status: {} exitId: {} data: {}", user.userName, path, status, exitId, objectMapper.writeValueAsString(data))
        return status
    }

}
