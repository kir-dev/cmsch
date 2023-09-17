package hu.bme.sch.cmsch.component.key

import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(AccessKeyComponent::class)
class AccessKeyApiController(
    private val accessKeyComponent: AccessKeyComponent,
    private val accessKeyService: AccessKeyService
) {

    @GetMapping("/access-key")
    fun accessKey(auth: Authentication?): ResponseEntity<AccessKeyView> {
        val user = auth?.getUserOrNull()
        if (!accessKeyComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        return ResponseEntity.ok(AccessKeyView(
            title = accessKeyComponent.title.getValue(),
            topMessage = accessKeyComponent.enabled
                .mapIfTrue { accessKeyComponent.topMessage.getValue() } ?: "",
            fieldName = accessKeyComponent.fieldName.getValue(),
            enabled = accessKeyComponent.enabled.isValueTrue(),
        ))
    }

    @PostMapping("/access-key")
    fun submitKey(auth: Authentication?, @RequestBody payload: AccessKeyRequest): AccessKeyResponse {
        if (!accessKeyComponent.enabled.isValueTrue()) {
            return AccessKeyResponse(
                success = false,
                reason = accessKeyComponent.disabledErrorMessage.getValue(),
                refreshSession = false
            )
        }

        val user = auth?.getUserOrNull()
        if (!accessKeyComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return AccessKeyResponse(success = false, reason = "", refreshSession = false)

        if (user == null) {
            return AccessKeyResponse(
                success = false,
                reason = accessKeyComponent.mustLogInErrorMessage.getValue(),
                refreshSession = false
            )
        }
        return accessKeyService.validateKey(user, payload.key)
    }

}