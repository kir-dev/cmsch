package hu.bme.sch.cmsch.component.groupselection

import hu.bme.sch.cmsch.component.profile.ProfileComponent
import hu.bme.sch.cmsch.util.getUserEntityFromDatabaseOrNull
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
    private val profileComponent: Optional<ProfileComponent>
) {

    @PostMapping("/group/select/{groupId}")
    fun selectGroup(@PathVariable groupId: Int, auth: Authentication?): GroupSelectionResponse {
        val user = auth?.getUserEntityFromDatabaseOrNull()
            ?: return GroupSelectionResponse(GroupSelectionResponseType.UNAUTHORIZED)

        if (!profileComponent.map { it.selectionEnabled.getValue() }.orElse(false)
                || !profileComponent.map { it.minRole.isAvailableForRole(user.role) }.orElse(false)) {
            return GroupSelectionResponse(GroupSelectionResponseType.PERMISSION_DENIED)
        }

        return groupSelectionService.selectGroup(user, groupId)
    }

}
