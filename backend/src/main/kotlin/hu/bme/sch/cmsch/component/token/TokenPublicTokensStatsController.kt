package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.component.login.LoginComponent
import hu.bme.sch.cmsch.controller.admin.CONTROL_MODE_NONE
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserFromDatabase
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/control/stamps")
@ConditionalOnBean(value = [TokenComponent::class, LoginComponent::class])
class TokenPublicTokensStatsController(
    private val loginComponent: LoginComponent,
    private val tokenPropertyRepository: TokenPropertyRepository,
    private val adminMenuService: AdminMenuService
) {

    private val titleSingular = "Pecsét statisztika"
    private val titlePlural = "Pecsét statisztika"
    private val description = "Melyik kör pecsétje hány nem kiállító által került leolvasásra. Csak azok a körök látszódnak akiknek már legalább egy beolvasása volt."

    private val overviewDescriptor = OverviewBuilder(TokenStatVirtualEntity::class)

    @GetMapping("")
    fun view(model: Model, auth: Authentication): String {
        val user = auth.getUserFromDatabase()
        if (user.isAdmin() || user.groupName == loginComponent.organizerGroupName.getValue()) {
            model.addAttribute("user", user)
            return "admin403"
        }

        adminMenuService.addPartsForMenu(user, model)
        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", "")
        model.addAttribute("columns", overviewDescriptor.getColumns())
        model.addAttribute("fields", overviewDescriptor.getColumnDefinitions())
        model.addAttribute("rows", fetchOverview())
        model.addAttribute("user", user)
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
                    count = submissions.count { it.ownerUser?.groupName != loginComponent.organizerGroupName.getValue() }
                )
            }
            .filter { it.count > 0 }
            .sortedBy { it.token }
            .toList()
    }

}
