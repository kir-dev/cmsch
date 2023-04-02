package hu.bme.sch.cmsch.component.news

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
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
    menuService: MenuService
) : ComponentApiBase(
    adminMenuService,
    NewsComponent::class.java,
    component,
    PERMISSION_CONTROL_NEWS,
    "Hírek",
    "Hírek testreszabása",
    menuService = menuService
)

@Controller
@RequestMapping("/admin/control/news")
@ConditionalOnBean(NewsComponent::class)
class NewsController(
    repo: NewsRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: NewsComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper
) : OneDeepEntityPage<NewsEntity>(
    "news",
    NewsEntity::class, ::NewsEntity,
    "Hír", "Hírek",
    "A oldalon megjelenő hírek kezelése.",

    repo,
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,

    showPermission = PERMISSION_EDIT_NEWS,
    createPermission = PERMISSION_EDIT_NEWS,
    editPermission = PERMISSION_EDIT_NEWS,
    deletePermission = PERMISSION_EDIT_NEWS,

    createEnabled = true,
    editEnabled = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "newspaper",
    adminMenuPriority = 1,
)

