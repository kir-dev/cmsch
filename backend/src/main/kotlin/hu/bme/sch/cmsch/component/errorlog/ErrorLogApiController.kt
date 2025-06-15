package hu.bme.sch.cmsch.component.errorlog

import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.util.getUserOrNull
import hu.bme.sch.cmsch.util.isAvailableForRole
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController()
@RequestMapping("/api")
class ErrorLogApiController(
    private val errorLogComponent: Optional<ErrorLogComponent>,
    private val errorLogService: Optional<ErrorLogService>
) {

    data class ErrorReportDto(val message: String?, val stack: String?, val userAgent: String?, val href: String?)

    @PostMapping("/error/submit")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    fun submitError(auth: Authentication?, @RequestBody error: ErrorReportDto): ResponseEntity<Any> {
        if (error.message == null && error.stack == null && error.userAgent == null && error.href == null)
            return ResponseEntity.badRequest().build()

        val role = auth?.getUserOrNull()?.role ?: RoleType.GUEST
        if (!errorLogComponent.map { it.minRole.isAvailableForRole(role) }.orElse(true))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        errorLogService.ifPresent {
            it.submit(
                message = error.message ?: "",
                stack = error.stack ?: "",
                userAgent = error.userAgent ?: "",
                href = error.href ?: "",
                role = role
            )
        }

        return ResponseEntity.ok().build()
    }

}
