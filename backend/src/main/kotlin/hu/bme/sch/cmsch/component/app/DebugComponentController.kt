package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/debug")
@ConditionalOnBean(ApplicationComponent::class)
class DebugComponentController(
    adminMenuService: AdminMenuService,
    component: DebugComponent,
    menuService: MenuService,
    auditLogService: AuditLogService
) : ComponentApiBase(
    adminMenuService,
    FooterComponent::class.java,
    component,
    ControlPermissions.PERMISSION_DEV_DEBUG,
    componentMenuName = "Fejlesztő beállítások",
    componentMenuIcon = "code_blocks",
    menuService = menuService,
    insertComponentCategory = false,
    componentCategory = ApplicationComponent.DEVELOPER_CATEGORY,
    auditLogService = auditLogService
)