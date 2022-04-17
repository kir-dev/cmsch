package hu.bme.sch.cmsch.component.news

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_NEWS
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_NEWS
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/news")
@ConditionalOnBean(NewsComponent::class)
class NewsAdminController(
    adminMenuService: AdminMenuService,
    component: NewsComponent,
) : ComponentApiBase(
    adminMenuService,
    NewsComponent::class.java,
    component,
    PERMISSION_CONTROL_NEWS,
    "Hírek",
    "Hírek testreszabása"
)

@Controller
@RequestMapping("/admin/control/news")
@ConditionalOnBean(NewsComponent::class)
class NewsController(
    repo: NewsRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: NewsComponent
) : AbstractAdminPanelController<NewsEntity>(
    repo,
    "news", "Hír", "Hírek",
    "A oldalon megjelenő hírek kezelése.",
    NewsEntity::class, ::NewsEntity, importService, adminMenuService, component,
    permissionControl = PERMISSION_EDIT_NEWS,
    importable = true, adminMenuIcon = "newspaper"
)
