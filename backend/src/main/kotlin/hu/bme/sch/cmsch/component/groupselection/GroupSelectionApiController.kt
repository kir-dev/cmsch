package hu.bme.sch.cmsch.component.groupselection

import hu.bme.sch.cmsch.component.profile.ProfileComponent
import hu.bme.sch.cmsch.service.UserService
import hu.bme.sch.cmsch.util.getUserEntityFromDatabaseOrNull
import hu.bme.sch.cmsch.util.isAvailableForRole
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api")
@ConditionalOnBean(GroupSelectionComponent::class)
class GroupSelectionApiController(
    private val groupSelectionService: GroupSelectionService,
    private val profileComponent: Optional<ProfileComponent>,
    private val userService: UserService,
) {

    @PostMapping("/group/select/{groupId}")
    fun selectGroup(@PathVariable groupId: Int, auth: Authentication?): GroupSelectionResponse {
        val user = auth?.getUserEntityFromDatabaseOrNull(userService)
            ?: return GroupSelectionResponse(GroupSelectionResponseType.UNAUTHORIZED)

        if (!profileComponent.map { it.selectionEnabled }.orElse(false)
                || !profileComponent.map { it.minRole.isAvailableForRole(user.role) }.orElse(false)) {
            return GroupSelectionResponse(GroupSelectionResponseType.PERMISSION_DENIED)
        }

        return groupSelectionService.selectGroup(user, groupId)
    }

}
