package hu.bme.sch.cmsch.component.messaging

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
@RequestMapping("/admin/control/component/messaging")
@ConditionalOnBean(MessagingComponent::class)
class MessagingComponentController(
    adminMenuService: AdminMenuService,
    component: MessagingComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    MessagingComponent::class.java,
    component,
    ControlPermissions.PERMISSION_SEND_MESSAGE,
    "Értesítések",
    "Értesítések beállítása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
Az **Üzenetküldés** (Messaging) komponens egy külső üzenetküldő proxy (pl. Telegram bot, Matrix híd) kezelését teszi lehetővé.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a proxy kapcsolatot:

- **Proxy elérhetősége** – a külső üzenetküldő szolgáltatás alap URL-je.
- **Token a proxyhoz** – a hitelesítéshez szükséges egyedi kulcs.

## Funkciók

- **Integráció** – a rendszer ezen a proxyn keresztül képes üzeneteket továbbítani külső platformokra.
- **Értesítések** – a belső eseményekről vagy rendszerüzenetekről küldhető tájékoztatás a konfigurált csatornákon.

## Használati tippek

- Ezt a komponenst általában csak akkor kell beállítani, ha a rendezvény szervezői egy külső csevegőalkalmazáson (pl. Telegram) keresztül szeretnének automatikus értesítéseket kapni a rendszer eseményeiről.
"""
)
