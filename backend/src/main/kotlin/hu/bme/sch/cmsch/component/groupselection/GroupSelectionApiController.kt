package hu.bme.sch.cmsch.component.groupselection

import hu.bme.sch.cmsch.util.getUserFromDatabaseOrNull
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(GroupSelectionComponent::class)
class GroupSelectionApiController(
    private val groupSelectionService: GroupSelectionService
) {

    @PostMapping("/group/select/{groupId}")
    fun selectGroup(@PathVariable groupId: Int, auth: Authentication): GroupSelectionResponse {
        val user = auth.getUserFromDatabaseOrNull()
            ?: return GroupSelectionResponse(GroupSelectionResponseType.UNAUTHORIZED)
        return groupSelectionService.selectGroup(user, groupId)
    }

}
