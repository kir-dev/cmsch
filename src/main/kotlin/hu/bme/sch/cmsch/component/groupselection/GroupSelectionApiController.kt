package hu.bme.sch.cmsch.component.groupselection

import hu.bme.sch.cmsch.dto.GroupSelectionResponse
import hu.bme.sch.cmsch.dto.GroupSelectionResponseType
import hu.bme.sch.cmsch.component.groupselection.GroupSelectionService
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
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
