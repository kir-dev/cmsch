package hu.bme.sch.cmsch.component.race

import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_RACE_CATEGORY
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/race-categories")
@ConditionalOnBean(RaceComponent::class)
class RaceCategoryAdminController(
    repo: RaceCategoryRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: RaceComponent,
) : AbstractAdminPanelController<RaceCategoryEntity>(
    repo,
    "race-categories", "Kategória", "Mérés kategóriák",
    "Időmérés extra kategóriái",
    RaceCategoryEntity::class, ::RaceCategoryEntity, importService, adminMenuService, component,
    mapOf(),
    permissionControl = PERMISSION_EDIT_RACE_CATEGORY,
    importable = true,
    adminMenuPriority = 4,
    adminMenuIcon = "category"
)
