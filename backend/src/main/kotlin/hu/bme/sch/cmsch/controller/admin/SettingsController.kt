package hu.bme.sch.cmsch.controller.admin

import hu.bme.sch.cmsch.component.staticpage.StaticPageService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.PermissionGroupService
import hu.bme.sch.cmsch.service.PermissionValidator
import hu.bme.sch.cmsch.service.PermissionsService
import hu.bme.sch.cmsch.service.UserService
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserEntityFromDatabase
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*


@Controller
@RequestMapping("/admin/control")
class SettingsController(
    private val adminMenuService: AdminMenuService,
    private val staticPageService: Optional<StaticPageService>,
    private val permissionsService: PermissionsService,
    private val permissionGroupService: PermissionGroupService,
    private val userService: UserService,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/settings")
    fun setting(model: Model, auth: Authentication): String {
        val user = auth.getUserEntityFromDatabase(userService)
        adminMenuService.addPartsForMenu(user, model)
        model.addAttribute("user", user)

        val userPermissions = user.permissionsAsList
        val userPermissionGroups = user.permissionGroupsAsList

        model.addAttribute("customPermissions", staticPageService.map { service ->
            service.getAll().groupBy { it.permissionToEdit }.map { group ->
                PermissionValidator(
                    group.key,
                    "Szükséges a(z) '${group.value.joinToString("', '") { it.title }}' " +
                            "nevű oldal(ak) szerkesztéséhez"
                )
            }
        }.orElse(listOf())
            .filter { userPermissions.contains(it.permissionString) })

        model.addAttribute("staffPermissions", permissionsService.allStaffPermissions
            .filter { it.permissionString.isNotEmpty() }
            .filter { userPermissions.contains(it.permissionString) })

        model.addAttribute("adminPermissions", permissionsService.allControlPermissions
            .filter { it.permissionString.isNotEmpty() }
            .filter { userPermissions.contains(it.permissionString) })

        model.addAttribute("permissionGroups", permissionGroupService.allPermissionGroups
            .filter { userPermissionGroups.contains(it.key) })

        return "settings"
    }

    @GetMapping("/settings/relog")
    fun refreshUserData(model: Model, auth: Authentication): String {
        val user = auth.getUserEntityFromDatabase(userService)

        auth.getUser().refresh(user)
        adminMenuService.invalidateUser(user.internalId)
        SecurityContextHolder.getContext().authentication = auth.principal?.let {
            UsernamePasswordAuthenticationToken(
                it, auth.credentials,
                mutableListOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
            )
        }
        log.info("User ${user.fullName} has just relogged")

        return "redirect:/admin/control/settings"
    }

}
