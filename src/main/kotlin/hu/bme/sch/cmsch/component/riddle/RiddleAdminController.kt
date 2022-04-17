package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.PERMISSION_CONTROL_RIDDLE
import hu.bme.sch.cmsch.service.PermissionValidator
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/riddle")
@ConditionalOnBean(RiddleComponent::class)
class RiddleAdminController(
    adminMenuService: AdminMenuService,
    component: RiddleComponent,
) : ComponentApiBase(
    adminMenuService,
    RiddleComponent::class.java,
    component,
    PERMISSION_CONTROL_RIDDLE,
    "Riddleök",
    "Riddleök testreszabása"
)

@Controller
@RequestMapping("/admin/control/riddles")
@ConditionalOnBean(RiddleComponent::class)
class RiddleController(
    repo: RiddleEntityRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: RiddleComponent
) : AbstractAdminPanelController<RiddleEntity>(
    repo,
    "riddles", "Riddle", "Riddleök",
    "Képrejtvények kezelése.",
    RiddleEntity::class, ::RiddleEntity, importService, adminMenuService, component,
    permissionControl = PermissionValidator() { it.isAdmin() ?: false || it.grantRiddle ?: false },
    importable = true, adminMenuIcon = "task"
)

@Controller
@RequestMapping("/admin/control/riddle-categories")
@ConditionalOnBean(RiddleComponent::class)
class RiddleCategoryController(
    repo: RiddleCategoryRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: RiddleComponent
) : AbstractAdminPanelController<RiddleCategoryEntity>(
    repo,
    "riddle-categories", "Riddle Kategória", "Riddle Kategóriák",
    "Képrejtvény kategóriák kezelése.",
    RiddleCategoryEntity::class, ::RiddleCategoryEntity, importService, adminMenuService, component,
    permissionControl = PermissionValidator() { it.isAdmin() ?: false || it.grantRiddle ?: false },
    importable = true, adminMenuPriority = 2, adminMenuIcon = "category"
)

