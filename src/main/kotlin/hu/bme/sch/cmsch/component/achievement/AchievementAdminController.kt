package hu.bme.sch.cmsch.component.achievement

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_ACHIEVEMENTS
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_ACHIEVEMENTS
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_ACHIEVEMENT_CATEGORIES
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/achievement")
@ConditionalOnBean(AchievementComponent::class)
class AchievementAdminController(
    adminMenuService: AdminMenuService,
    component: AchievementComponent,
) : ComponentApiBase(
    adminMenuService,
    AchievementComponent::class.java,
    component,
    PERMISSION_CONTROL_ACHIEVEMENTS,
    "Bucketlist",
    "Bucketlist beállítások"
)

@Controller
@RequestMapping("/admin/control/achievements")
@ConditionalOnBean(AchievementComponent::class)
class AchievementController(
    repo: AchievementEntityRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: AchievementComponent
) : AbstractAdminPanelController<AchievementEntity>(
    repo,
    "achievements", "Bucketlist Feladat", "Bucketlist feladatokek",
    "Bucketlist feladatok kezelése. A feladatok javítására használd a \"Bucketlist értékelése\" menüt!",
    AchievementEntity::class, ::AchievementEntity, importService, adminMenuService, component,
    permissionControl = PERMISSION_EDIT_ACHIEVEMENTS,
    importable = true, adminMenuIcon = "emoji_events"
)

@Controller
@RequestMapping("/admin/control/categories")
@ConditionalOnBean(AchievementComponent::class)
class AchievementCategoryController(
    repo: AchievementCategoryRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: AchievementComponent
) : AbstractAdminPanelController<AchievementCategoryEntity>(
    repo,
    "categories", "Feladat Kategória", "Feladat kategóriák",
    "Bucketlist feladatok kategóriájának kezelése. A feladatok javítására használd a Javítások menüt!",
    AchievementCategoryEntity::class, ::AchievementCategoryEntity, importService, adminMenuService, component,
    permissionControl = PERMISSION_EDIT_ACHIEVEMENT_CATEGORIES,
    importable = true, adminMenuPriority = 2, adminMenuIcon = "category"
)
