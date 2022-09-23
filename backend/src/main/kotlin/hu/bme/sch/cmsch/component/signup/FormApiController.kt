package hu.bme.sch.cmsch.component.signup

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.util.getUserFromDatabaseOrNull
import hu.bme.sch.cmsch.util.getUserOrNull
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(SignupComponent::class)
class FormApiController(
    private val signupService: SignupService
) {

    internal val log = LoggerFactory.getLogger(javaClass)

    @JsonView(Preview::class)
    @GetMapping("/forms")
    fun forms(auth: Authentication?): List<SignupFormEntity> {
        return signupService.getAllForms(auth?.getUserOrNull()?.role ?: RoleType.BASIC)
    }

    @JsonView(FullDetails::class)
    @GetMapping("/form/{path}")
    fun specificForm(@PathVariable path: String, auth: Authentication?): SignupFormView {
        val user = auth?.getUserFromDatabaseOrNull() ?: return SignupFormView(status = FormStatus.NOT_FOUND)
        return signupService.fetchForm(user, path)
    }

    @PostMapping("/form/{path}")
    fun fillOutForm(@PathVariable path: String, auth: Authentication?, @RequestBody data: Map<String, String>): FormSubmissionStatus {
        val user = auth?.getUserFromDatabaseOrNull() ?: return FormSubmissionStatus.FORM_NOT_AVAILABLE
        val status = signupService.submitForm(user, path, data, false)
        log.info("User '{}' filling out form '{}' status: {}", user.fullName, path, status)
        return status
    }

    @PutMapping("/form/{path}")
    fun updateForm(@PathVariable path: String, auth: Authentication?, @RequestBody data: Map<String, String>): FormSubmissionStatus {
        val user = auth?.getUserFromDatabaseOrNull() ?: return FormSubmissionStatus.FORM_NOT_AVAILABLE
        val status = signupService.submitForm(user, path, data, true)
        log.info("User '{}' updating form '{}' status: {}", user.fullName, path, status)
        return status
    }

}
