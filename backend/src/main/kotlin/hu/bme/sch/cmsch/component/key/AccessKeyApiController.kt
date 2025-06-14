package hu.bme.sch.cmsch.component.key

import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.util.getUserOrNull
import hu.bme.sch.cmsch.util.isAvailableForRole
import hu.bme.sch.cmsch.util.mapIfTrue
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
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
            title = accessKeyComponent.title,
            topMessage = accessKeyComponent.enabled.mapIfTrue { accessKeyComponent.topMessage } ?: "",
            fieldName = accessKeyComponent.fieldName,
            enabled = accessKeyComponent.enabled,
        ))
    }

    @PostMapping("/access-key")
    fun submitKey(auth: Authentication?, @RequestBody payload: AccessKeyRequest): AccessKeyResponse {
        if (!accessKeyComponent.enabled) {
            return AccessKeyResponse(
                success = false,
                reason = accessKeyComponent.disabledErrorMessage,
                refreshSession = false
            )
        }

        val user = auth?.getUserOrNull()
        if (!accessKeyComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return AccessKeyResponse(success = false, reason = "", refreshSession = false)

        if (user == null) {
            return AccessKeyResponse(
                success = false,
                reason = accessKeyComponent.mustLogInErrorMessage,
                refreshSession = false
            )
        }
        return accessKeyService.validateKey(user, payload.key)
    }

}
