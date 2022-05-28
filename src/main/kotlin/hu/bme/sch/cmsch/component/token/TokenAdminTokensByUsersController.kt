package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.controller.CONTROL_MODE_DELETE
import hu.bme.sch.cmsch.controller.CONTROL_MODE_VIEW
import hu.bme.sch.cmsch.controller.INVALID_ID_ERROR
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_TOKENS
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.annotation.PostConstruct

@Controller
@RequestMapping("/admin/control/token-properties-user")
@ConditionalOnBean(TokenComponent::class)
class TokenAdminTokensByUsersController(
    private val tokenPropertyRepository: TokenPropertyRepository,
    private val groupRepository: GroupRepository,
    private val adminMenuService: AdminMenuService
) {

    private val view = "token-properties-user"
    private val titleSingular = "Fehasználói tokenek"
    private val titlePlural = "Fehasználói tokenek"
    private val description = "Beolvasott tokenek felhasználónként csoportosítva"
    private val permissionControl = PERMISSION_EDIT_TOKENS

    private val overviewDescriptor = OverviewBuilder(TokenListByUserVirtualEntity::class)
    private val propertyDescriptor = OverviewBuilder(TokenVirtualEntity::class)

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            TokenComponent::class.simpleName!!, AdminMenuEntry(
                titlePlural,
                "local_activity",
                "/admin/control/${view}",
                2,
                permissionControl
            )
        )
    }

    @GetMapping("")
    fun view(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("columns", overviewDescriptor.getColumns())
        model.addAttribute("fields", overviewDescriptor.getColumnDefinitions())
        model.addAttribute("rows", fetchOverview())
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_VIEW)

        return "overview"
    }

    private fun fetchOverview(): List<TokenListByUserVirtualEntity> {
        return tokenPropertyRepository.findAll().groupBy { it.ownerUser?.id ?: 0 }
                .map { it.value }
                .filter { it.isNotEmpty() }
                .map { it ->
                    val groupName = groupRepository.findById(it[0].ownerUser?.id ?: 0).map { it.name }.orElse("n/a")
                    TokenListByUserVirtualEntity(
                            it[0].ownerUser?.id ?: 0,
                            it[0].ownerUser?.fullName ?: "n/a",
                            groupName,
                            it.count()
                    )
                }
    }

    @GetMapping("/view/{id}")
    fun viewAll(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("columns", propertyDescriptor.getColumns())
        model.addAttribute("fields", propertyDescriptor.getColumnDefinitions())
        model.addAttribute("rows", fetchProperties(id))
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_DELETE)

        return "overview"
    }

    private fun fetchProperties(user: Int): List<TokenVirtualEntity> {
        return tokenPropertyRepository.findAllByOwnerUser_Id(user)
            .map {
                TokenVirtualEntity(
                    it.id,
                    it.token?.title ?: "n/a",
                    it.token?.type ?: "n/a",
                    it.recieved
                )
            }
    }

    @GetMapping("/delete/{id}")
    fun deleteConfirm(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titleSingular)
        model.addAttribute("view", view)
        model.addAttribute("id", id)
        model.addAttribute("user", user)

        val entity = tokenPropertyRepository.findById(id)
        if (entity.isEmpty) {
            model.addAttribute("error", INVALID_ID_ERROR)
        } else {
            model.addAttribute("item", entity.orElseThrow().toString())
        }
        return "delete"
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        val entity = tokenPropertyRepository.findById(id).orElseThrow()
        tokenPropertyRepository.delete(entity)
        return "redirect:/admin/control/$view/"
    }

}
