package hu.bme.sch.cmsch.controller.userhandling

import tools.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.app.UserHandlingComponent
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.controller.admin.calculateSearchSettings
import hu.bme.sch.cmsch.model.RoleToUserMappingEntity
import hu.bme.sch.cmsch.repository.RoleToUserMappingRepository
import hu.bme.sch.cmsch.service.*
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/role-to-user")
class RoleToUserMappingController(
    repo: RoleToUserMappingRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: UserHandlingComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    env: Environment,
    storageService: StorageService
) : OneDeepEntityPage<RoleToUserMappingEntity>(
    "role-to-user",
    RoleToUserMappingEntity::class, ::RoleToUserMappingEntity,
    "Jogkör Hozzárendelés", "Jogkör hozzárendelések",
    "Felhasználók neptun kód vagy email cím alapján jogkörhöz rendelése. A hozzárendelés minden bejelentkezésnél " +
            "megtörténik ha van egyezés és a felhasználónak még nincs BASIC-nél magasabb jogköre.",

    transactionManager,
    repo,
    importService,
    adminMenuService,
    storageService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission =   StaffPermissions.PERMISSION_SHOW_ROLE_MAPPINGS,
    createPermission = StaffPermissions.PERMISSION_CREATE_ROLE_MAPPINGS,
    editPermission =   StaffPermissions.PERMISSION_EDIT_ROLE_MAPPINGS,
    deletePermission = StaffPermissions.PERMISSION_DELETE_ROLE_MAPPINGS,

    createEnabled = true,
    editEnabled   = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "admin_panel_settings",
    adminMenuPriority = 5,
    adminMenuCategory = ApplicationComponent.DATA_SOURCE_CATEGORY,

    searchSettings = calculateSearchSettings<RoleToUserMappingEntity>(false)
)
