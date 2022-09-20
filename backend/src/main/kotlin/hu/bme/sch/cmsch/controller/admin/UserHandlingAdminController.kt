package hu.bme.sch.cmsch.controller.admin

import hu.bme.sch.cmsch.admin.IMPORT_INT
import hu.bme.sch.cmsch.admin.ImportFormat
import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.component.app.UserHandlingComponent
import hu.bme.sch.cmsch.component.extrapage.ExtraPageService
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.config.StartupPropertyConfig
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
import hu.bme.sch.cmsch.util.getUser
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/admin/control/groups")
class GroupController(
        repo: GroupRepository,
        importService: ImportService,
        adminMenuService: AdminMenuService,
        component: UserHandlingComponent
) : AbstractAdminPanelController<GroupEntity>(
        repo,
        "groups", "Csoport", "Csoportok",
        "Az összes csoport kezelése. A csoportba való hozzárendelés a felhasználók menüből érhető el!",
        GroupEntity::class, ::GroupEntity, importService, adminMenuService, component,
        mapOf("UserEntity" to { it?.members?.map {
            member -> "${member.fullName} (${member.role.name})"
        }?.toList() ?: listOf("Üres") }),
        permissionControl = PERMISSION_EDIT_GROUPS,
        importable = true, adminMenuPriority = 1, adminMenuIcon = "groups"
) {

    data class IdExportGroupView(
        @property:ImportFormat(ignore = false, columnId = 0, type = IMPORT_INT)
        var groupId: Int = 0,

        @property:ImportFormat(ignore = false, columnId = 1)
        var groupName: String = ""
    )

    private val idDescriptor = OverviewBuilder(IdExportGroupView::class)

    @ResponseBody
    @GetMapping("/id-export/csv", produces = [ MediaType.APPLICATION_OCTET_STREAM_VALUE ])
    fun idExport(auth: Authentication, response: HttpServletResponse): ByteArray {
        if (ControlPermissions.PERMISSION_IMPORT_EXPORT.validate(auth.getUser()).not()) {
            throw IllegalStateException("Insufficient permissions")
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"$view-filtered-export.csv\"")
        return idDescriptor.exportToCsv(fetchOverview().map { IdExportGroupView(it.id, it.name) }).toByteArray()
    }

}

@Controller
@RequestMapping("/admin/control/users")
class UserController(
    repo: UserRepository,
    private val profileService: UserProfileGeneratorService,
    private val groups: GroupRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: UserHandlingComponent,
    private val extraPageService: Optional<ExtraPageService>,
    private val startupPropertyConfig: StartupPropertyConfig
) : AbstractAdminPanelController<UserEntity>(
        repo,
        "users", "Felhasználó", "Felhasználók",
        "Az összes felhasználó (résztvevők és rendezők egyaránt) kezelése.",
        UserEntity::class, ::UserEntity, importService, adminMenuService, component,
        mapOf("GroupEntity" to { groups.findAll().map { it.name }.toList() }),
        permissionControl = PERMISSION_EDIT_USERS,
        importable = true, adminMenuPriority = 2, adminMenuIcon = "person"
) {

    override fun onDetailsView(entity: CmschUser, model: Model) {
        val customPermissions = extraPageService.map { service ->
            service.getAll().groupBy { it.permissionToEdit }.map { group ->
                PermissionValidator(
                    group.key,
                    "Szükséges a(z) '${group.value.joinToString("', '") { it.title }}' " +
                            "nevű oldal(ak) szerkesztéséhez"
                )
            }
        }.orElse(listOf())

        val staffPermissions = StaffPermissions.allPermissions().filter { it.permissionString.isNotEmpty() }
        val adminPermissions = ControlPermissions.allPermissions().filter { it.permissionString.isNotEmpty() }
        model.addAttribute("customPermissions", customPermissions)
        model.addAttribute("staffPermissions", staffPermissions)
        model.addAttribute("adminPermissions", adminPermissions)
        model.addAttribute("customPermissionList", customPermissions.map { it.permissionString })
        model.addAttribute("staffPermissionList", staffPermissions.map { it.permissionString })
        model.addAttribute("adminPermissionList", adminPermissions.map { it.permissionString })
    }

    override fun onEntityPreSave(entity: UserEntity, auth: Authentication): Boolean {
        if (startupPropertyConfig.profileQrEnabled) {
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
        return true
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
        "group-to-user", "Csoport Tagság", "Csoport tagságok",
        "Felhasználók neptun kód alapján csoportba és szakra rendelése. A hozzárendelés minden bejelentkezésnél megtörténik ha van egyezés és még nincs beállítva.",
        GroupToUserMappingEntity::class, ::GroupToUserMappingEntity, importService, adminMenuService, component,
        mapOf("GroupEntity" to { groups.findAll().map { it.name }.toList() }),
        permissionControl = PERMISSION_EDIT_GROUP_MAPPINGS,
        importable = true, adminMenuPriority = 3, adminMenuIcon = "people"
)
