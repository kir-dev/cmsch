package hu.bme.sch.cmsch.component.extrapage

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.token.TokenComponent
import hu.bme.sch.cmsch.controller.admin.AbstractAdminPanelController
import hu.bme.sch.cmsch.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/extraPage")
@ConditionalOnBean(ExtraPageComponent::class)
class ExtraPageAdminController(
    adminMenuService: AdminMenuService,
    component: ExtraPageComponent,
) : ComponentApiBase(
    adminMenuService,
    ExtraPageComponent::class.java,
    component,
    PERMISSION_CONTROL_EXTRAPAGES,
    "Oldalak",
    "Oldalak testreszabása"
)

@Controller
@RequestMapping("/admin/control/extra-pages")
@ConditionalOnBean(ExtraPageComponent::class)
class ExtraPageController(
    repo: ExtraPageRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: ExtraPageComponent
) : AbstractAdminPanelController<ExtraPageEntity>(
    repo,
    "extra-pages", "Extra Oldal", "Extra Oldalak",
    "Egyedi oldalak kezelése.",
    ExtraPageEntity::class, ::ExtraPageEntity, importService, adminMenuService, component,
    permissionControl = PermissionValidator() { it.isAdmin() ?: false || it.grantMedia ?: false },
    adminMenuIcon = "article"
)
