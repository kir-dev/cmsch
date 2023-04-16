package hu.bme.sch.cmsch.controller.userhandling

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.app.UserHandlingComponent
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.model.GuildToUserMappingEntity
import hu.bme.sch.cmsch.repository.GuildToUserMappingRepository
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.StaffPermissions
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/guild-to-user")
class GuildToUserMappingController(
    repo: GuildToUserMappingRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: UserHandlingComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper
) : OneDeepEntityPage<GuildToUserMappingEntity>(
    "guild-to-user",
    GuildToUserMappingEntity::class, ::GuildToUserMappingEntity,
    "Gárda Tagság", "Gárda tagságok",
    "Felhasználók neptun kód alapján gárdába rendelése. A hozzárendelés minden bejelentkezésnél " +
            "megtörténik ha van egyezés és még nincs beállítva.",

    repo,
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,

    showPermission =   StaffPermissions.PERMISSION_SHOW_GUILD_MAPPINGS,
    createPermission = StaffPermissions.PERMISSION_CREATE_GUILD_MAPPINGS,
    editPermission =   StaffPermissions.PERMISSION_EDIT_GUILD_MAPPINGS,
    deletePermission = StaffPermissions.PERMISSION_DELETE_GUILD_MAPPINGS,

    createEnabled = true,
    editEnabled   = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "badge",
    adminMenuPriority = 4,
    adminMenuCategory = ApplicationComponent.DATA_SOURCE_CATEGORY
)
