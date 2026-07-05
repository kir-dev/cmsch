package hu.bme.sch.cmsch.component.kirpay

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/kirpay")
@ConditionalOnBean(KirPayComponent::class)
class KirPayComponentController(
  adminMenuService: AdminMenuService,
  component: KirPayComponent,
  menuService: MenuService,
  auditLogService: AuditLogService,
  storageService: StorageService
) : ComponentApiBase(
  adminMenuService,
  KirPayComponent::class.java,
  component,
  ControlPermissions.PERMISSION_CONTROL_KIRPAY,
  "Kir-Pay",
  "Kir-Pay testreszabása",
  menuService = menuService,
  auditLogService = auditLogService,

  storageService = storageService
)
