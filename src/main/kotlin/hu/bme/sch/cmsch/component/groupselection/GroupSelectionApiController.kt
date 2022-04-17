package hu.bme.sch.cmsch.component.groupselection

import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(GroupSelectionComponent::class)
class GroupSelectionApiController(
    private val groupSelectionService: GroupSelectionService
) {

    @PostMapping("/group/select/{groupId}")
    fun selectGroup(@PathVariable groupId: Int, request: HttpServletRequest): GroupSelectionResponse {
        val userFromSession = request.getUserOrNull()
            ?: return GroupSelectionResponse(GroupSelectionResponseType.UNAUTHORIZED)
        return groupSelectionService.selectGroup(userFromSession, groupId, request)
    }

}
