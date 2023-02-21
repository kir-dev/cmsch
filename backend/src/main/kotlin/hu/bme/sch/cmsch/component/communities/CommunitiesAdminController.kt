package hu.bme.sch.cmsch.component.communities

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.StaffPermissions
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/communities")
@ConditionalOnBean(CommunitiesComponent::class)
class CommunitiesAdminController(
    adminMenuService: AdminMenuService,
    component: CommunitiesComponent,
    menuService: MenuService
) : ComponentApiBase(
    adminMenuService,
    CommunitiesComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_COMMUNITIES,
    componentCategoryName = "Körök",
    componentMenuName = "Beállítások",
    menuService = menuService
)

@Controller
@RequestMapping("/admin/control/community")
@ConditionalOnBean(CommunitiesComponent::class)
class CommunitiesController(
    repo: CommunityRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: CommunitiesComponent
) : AbstractAdminPanelController<CommunityEntity>(
    repo,
    "community", "Kör", "Körök",
    "Körök kezelése",
    CommunityEntity::class, ::CommunityEntity, importService, adminMenuService, component,
    permissionControl = StaffPermissions.PERMISSION_EDIT_COMMUNITIES,
    importable = true,
    adminMenuIcon = "emoji_events"
)


@Controller
@RequestMapping("/admin/control/organization")
@ConditionalOnBean(CommunitiesComponent::class)
class OrganizationController(
    repo: OrganizationRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: CommunitiesComponent
) : AbstractAdminPanelController<OrganizationEntity>(
    repo,
    "organization", "Reszort", "Reszortok",
    "Reszortok kezelése",
    OrganizationEntity::class, ::OrganizationEntity, importService, adminMenuService, component,
    permissionControl = StaffPermissions.PERMISSION_EDIT_COMMUNITIES,
    importable = true,
    adminMenuPriority = 2,
    adminMenuIcon = "category"
)