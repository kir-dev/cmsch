package hu.bme.sch.cmsch.controller.userhandling

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.app.UserHandlingComponent
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.component.staticpage.StaticPageService
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.*
import org.springframework.core.env.Environment
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*

@Controller
@RequestMapping("/admin/control/users")
class UserController(
    repo: UserRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: UserHandlingComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    private val profileService: UserProfileGeneratorService,
    private val groups: GroupRepository,
    private val staticPageService: Optional<StaticPageService>,
    private val startupPropertyConfig: StartupPropertyConfig,
    components: List<ComponentBase>,
    env: Environment,
    private val permissionsService: PermissionsService
) : OneDeepEntityPage<UserEntity>(
    "users",
    UserEntity::class, ::UserEntity,
    "Felhasználó", "Felhasználók",
    "Az összes felhasználó (résztvevők és rendezők egyaránt) kezelése.",

    repo,
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,
    env,

    entitySourceMapping = mapOf("GroupEntity" to { groups.findAll().map { it.name }.toList() }),

    showPermission =   StaffPermissions.PERMISSION_SHOW_USERS,
    createPermission = ImplicitPermissions.PERMISSION_NOBODY,
    editPermission =   StaffPermissions.PERMISSION_EDIT_USERS,
    deletePermission = StaffPermissions.PERMISSION_DELETE_USERS,

    createEnabled = false,
    editEnabled   = true,
    deleteEnabled = true,
    importEnabled = false,
    exportEnabled = true,

    adminMenuIcon = "person",
    adminMenuPriority = 2,
) {

    private val componentClasses = components.map { it::class }.toSet()

    override fun onDetailsView(entity: CmschUser, model: Model) {
        val customPermissions = staticPageService.map { service ->
            service.getAll().groupBy { it.permissionToEdit }.map { group ->
                PermissionValidator(
                    group.key,
                    "Szükséges a(z) '${group.value.joinToString("', '") { it.title }}' " +
                            "nevű oldal(ak) szerkesztéséhez"
                )
            }
        }.orElse(listOf())

        val staffPermissions = permissionsService.allStaffPermissions
            .filter { it.component != null }
            .filter { it.component in componentClasses }
            .filter { it.permissionString.isNotEmpty() }
        val adminPermissions = permissionsService.allControlPermissions
            .filter { it.component != null }
            .filter { it.component in componentClasses }
            .filter { it.permissionString.isNotEmpty() }
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

        adminMenuService.invalidateUser(entity.internalId)
        return true
    }

}
