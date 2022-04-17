package hu.bme.sch.cmsch.controller.admin

import hu.bme.sch.cmsch.component.app.UserHandlingComponent
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.GroupToUserMappingEntity
import hu.bme.sch.cmsch.model.GuildToUserMappingEntity
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.GroupToUserMappingRepository
import hu.bme.sch.cmsch.repository.GuildToUserMappingRepository
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_GROUPS
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_GROUP_MAPPINGS
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_GUILD_MAPPINGS
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_USERS
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/admin/control/groups")
class GroupController(
        repo: GroupRepository,
        importService: ImportService,
        adminMenuService: AdminMenuService,
        component: UserHandlingComponent
) : AbstractAdminPanelController<GroupEntity>(
        repo,
        "groups", "Tankör", "Tankörök",
        "Az összes tankör kezelése. A tankörbe való hozzárendelés a felhasználók menüből érhető el!",
        GroupEntity::class, ::GroupEntity, importService, adminMenuService, component,
        mapOf("UserEntity" to { it?.members?.map {
            member -> "${member.fullName} (${member.role.name})"
        }?.toList() ?: listOf("Üres") }),
        permissionControl = PERMISSION_EDIT_GROUPS,
        importable = true, adminMenuPriority = 1, adminMenuIcon = "groups"
)

@Controller
@RequestMapping("/admin/control/users")
class UserController(
    repo: UserRepository,
    private val profileService: UserProfileGeneratorService,
    private val groups: GroupRepository,
    importService: ImportService,
    @Value("\${cmsch.profile.qr-enabled:true}") private val profileQrEnabled: Boolean,
    adminMenuService: AdminMenuService,
    component: UserHandlingComponent
) : AbstractAdminPanelController<UserEntity>(
        repo,
        "users", "Felhasználó", "Felhasználók",
        "Az összes felhasználó (gólyák és seniorok egyaránt) kezelése.",
        UserEntity::class, ::UserEntity, importService, adminMenuService, component,
        mapOf("GroupEntity" to { groups.findAll().map { it.name }.toList() }),
        permissionControl = PERMISSION_EDIT_USERS,
        importable = true, adminMenuPriority = 2, adminMenuIcon = "person"
) {

    override fun onDetailsView(entity: UserEntity, model: Model) {
        val staffPermissions = StaffPermissions.allPermissions().filter { it.permissionString.isNotEmpty() }
        val adminPermissions = ControlPermissions.allPermissions().filter { it.permissionString.isNotEmpty() }
        model.addAttribute("staffPermissions", staffPermissions)
        model.addAttribute("adminPermissions", adminPermissions)
        model.addAttribute("staffPermissionList", staffPermissions.map { it.permissionString })
        model.addAttribute("adminPermissionList", adminPermissions.map { it.permissionString })
    }

    override fun onEntityPreSave(entity: UserEntity, request: HttpServletRequest) {
        if (profileQrEnabled) {
            profileService.generateFullProfileForUser(entity)
        } else {
            profileService.generateProfileIdForUser(entity)
        }

        if (entity.groupName.isNotBlank()) {
            groups.findByName(entity.groupName).ifPresentOrElse({
                entity.group = it
            }, {
                entity.fullName = ""
                entity.group = null
            })
        }
    }
}

@Controller
@RequestMapping("/admin/control/guild-to-user")
class GuildToUserMappingController(
        repo: GuildToUserMappingRepository,
        importService: ImportService,
        adminMenuService: AdminMenuService,
        component: UserHandlingComponent
) : AbstractAdminPanelController<GuildToUserMappingEntity>(
        repo,
        "guild-to-user", "Gárda Tagság", "Gárda tagságok",
        "Felhasználók neptun kód alapján gárdába rendelése. A hozzárendelés minden bejelentkezésnél megtörténik ha van egyezés és még nincs beállítva.",
        GuildToUserMappingEntity::class, ::GuildToUserMappingEntity, importService, adminMenuService, component,
        permissionControl = PERMISSION_EDIT_GUILD_MAPPINGS,
        importable = true, adminMenuPriority = 4, adminMenuIcon = "badge"
)

@Controller
@RequestMapping("/admin/control/group-to-user")
class GroupToUserMappingController(
        repo: GroupToUserMappingRepository,
        private val groups: GroupRepository,
        importService: ImportService,
        adminMenuService: AdminMenuService,
        component: UserHandlingComponent
) : AbstractAdminPanelController<GroupToUserMappingEntity>(
        repo,
        "group-to-user", "Tankör Tagság", "Tankör tagságok",
        "Felhasználók neptun kód alapján tankörbe és szakra rendelése. A hozzárendelés minden bejelentkezésnél megtörténik ha van egyezés és még nincs beállítva.",
        GroupToUserMappingEntity::class, ::GroupToUserMappingEntity, importService, adminMenuService, component,
        mapOf("GroupEntity" to { groups.findAll().map { it.name }.toList() }),
        permissionControl = PERMISSION_EDIT_GROUP_MAPPINGS,
        importable = true, adminMenuPriority = 3, adminMenuIcon = "people"
)
