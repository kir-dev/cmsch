package hu.bme.sch.cmsch.controller.admin

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.dto.virtual.TokenStatVirtualEntity
import hu.bme.sch.cmsch.repository.TokenPropertyRepository
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/control/stamps")
class TokensStatsController(
    private val tokenPropertyRepository: TokenPropertyRepository,
    @Value("\${cmsch.group-select.organizer-group:Kiállító}") private val organizerGroupName: String
) {

    private val titleSingular = "Pecsét statisztika"
    private val titlePlural = "Pecsét statisztika"
    private val description = "Melyik kör pecsétje hány nem kiállító által került leolvasásra. Csak azok a körök látszódnak akiknek már legalább egy beolvasása volt."

    private val overviewDescriptor = OverviewBuilder(TokenStatVirtualEntity::class)

    @GetMapping("")
    fun view(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.groupName == organizerGroupName }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", "")
        model.addAttribute("columns", overviewDescriptor.getColumns())
        model.addAttribute("fields", overviewDescriptor.getColumnDefinitions())
        model.addAttribute("rows", fetchOverview())
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_NONE)

        return "overview"
    }

    private fun fetchOverview(): List<TokenStatVirtualEntity> {
        return tokenPropertyRepository.findAll()
            .asSequence()
            .filter { it.token?.type == "default" }
            .groupBy { it.token?.id }
            .map { it.value }
            .filter { it.isNotEmpty() }
            .map { submissions ->
                TokenStatVirtualEntity(
                    id =    submissions[0].token?.id ?: 0,
                    token = submissions[0].token?.title ?: "n/a",
                    type =  submissions[0].token?.type ?: "n/a",
                    count = submissions.count { it.ownerUser?.groupName != organizerGroupName }
                )
            }
            .filter { it.count > 0 }
            .sortedBy { it.token }
            .toList()
    }

}
