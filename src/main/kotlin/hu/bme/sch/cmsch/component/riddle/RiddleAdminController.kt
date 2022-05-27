package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_RIDDLE
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_RIDDLES
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_RIDDLE_CATEGORIES
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/riddle")
@ConditionalOnBean(RiddleComponent::class)
class RiddleAdminController(
    adminMenuService: AdminMenuService,
    component: RiddleComponent,
    menuService: MenuService
) : ComponentApiBase(
    adminMenuService,
    RiddleComponent::class.java,
    component,
    PERMISSION_CONTROL_RIDDLE,
    "Riddleök",
    "Riddleök testreszabása",
    menuService = menuService
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
    permissionControl = PERMISSION_EDIT_RIDDLES,
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
    permissionControl = PERMISSION_EDIT_RIDDLE_CATEGORIES,
    importable = true, adminMenuPriority = 2, adminMenuIcon = "category"
)

