package hu.bme.sch.cmsch.component.profile

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.util.getUserEntityFromDatabaseOrNull
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(ProfileComponent::class)
class ProfileApiController(
    private val profileService: ProfileService,
    private val profileComponent: ProfileComponent
) {

    @JsonView(FullDetails::class)
    @GetMapping("/profile")
    @Operation(summary = "List all the available information from the authenticated user that is allowed by the " +
            "component configuration.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "The user is authenticated and data is provided or " +
                "no valid token present (in this case the loggedIn is false)"),
        ApiResponse(responseCode = "403", description = "Valid token is provided but the endpoint is not available " +
                "for the role that the user have")
    ])
    fun profile(auth: Authentication?): ResponseEntity<ProfileView> {
        val user = auth?.getUserEntityFromDatabaseOrNull()
            ?: return ResponseEntity.ok(ProfileView(loggedIn = false))

        if (!profileComponent.minRole.isAvailableForRole(user.role))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        return ResponseEntity.ok(profileService.getProfileForUser(user))
    }

    data class ProfileChangeRequest(var alias: String = "")

    @PutMapping("/profile/change-alias")
    fun changeAlias(@RequestBody body: ProfileChangeRequest, auth: Authentication?): ResponseEntity<Boolean> {
        val user = auth?.getUserEntityFromDatabaseOrNull()
            ?: return ResponseEntity.ok(false)

        if (!profileComponent.minRole.isAvailableForRole(user.role))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        if (!profileComponent.aliasChangeEnabled.isValueTrue())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        return ResponseEntity.ok(profileService.changeAlias(user, body.alias))
    }

}
