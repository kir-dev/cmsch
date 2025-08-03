package hu.bme.sch.cmsch.component.script

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_SCRIPT
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/script")
@ConditionalOnBean(ScriptComponent::class)
class ScriptComponentController(
    adminMenuService: AdminMenuService,
    component: ScriptComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    ScriptComponent::class.java,
    component,
    PERMISSION_CONTROL_SCRIPT,
    "Scriptek",
    "Scriptek testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService
)
