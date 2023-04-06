package hu.bme.sch.cmsch.component.token

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.component.login.LoginComponent
import hu.bme.sch.cmsch.controller.admin.SimpleEntityPage
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUserFromDatabase
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/stamps")
@ConditionalOnBean(value = [TokenComponent::class, LoginComponent::class])
class TokenPublicTokensStatsController(
    private val loginComponent: LoginComponent,
    private val tokenPropertyRepository: TokenPropertyRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: TokenComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper
) : SimpleEntityPage<TokenStatVirtualEntity>(
    "stamps",
    TokenStatVirtualEntity::class, ::TokenStatVirtualEntity,
    "Pecsét statisztika", "Pecsét statisztika",
    "Melyik kör pecsétje hány nem kiállító által került leolvasásra. Csak azok a körök " +
            "látszódnak akiknek már legalább egy beolvasása volt.",

    { tokenPropertyRepository.findAll()
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
        .toList() },

    permission = ImplicitPermissions.PERMISSION_IMPLICIT_ANYONE,

    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,

    adminMenuIcon = "approval",
    adminMenuPriority = 10,
) {
    @GetMapping("")
    override fun view(model: Model, auth: Authentication): String {
        val user = auth.getUserFromDatabase()
        if (user.isAdmin() || user.groupName == loginComponent.organizerGroupName.getValue()) {
            model.addAttribute("user", user)
            return "admin403"
        }

        return super.view(model, auth)
    }
}
