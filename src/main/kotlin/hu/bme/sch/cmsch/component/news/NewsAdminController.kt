package hu.bme.sch.cmsch.component.news

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.controller.admin.AbstractAdminPanelController
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.PERMISSION_CONTROL_NEWS
import hu.bme.sch.cmsch.service.PermissionValidator
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
    permissionControl = PermissionValidator() { it.isAdmin() ?: false || it.grantMedia ?: false },
    importable = true, adminMenuIcon = "newspaper"
)
