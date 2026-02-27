package hu.bme.sch.cmsch.component.pushnotification

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
@RequestMapping("/admin/control/component/pushnotification")
@ConditionalOnBean(PushNotificationComponent::class)
class PushNotificationComponentController(
    adminMenuService: AdminMenuService,
    component: PushNotificationComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    PushNotificationComponent::class.java,
    component,
    ControlPermissions.PERMISSION_SEND_NOTIFICATIONS,
    "Push Értesítések",
    "Push Értesítések beállítása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Push-értesítések** komponens lehetővé teszi rövid üzenetek küldését közvetlenül a felhasználók készülékeire (böngészőn vagy mobilalkalmazáson keresztül).

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod az értesítések alapvető működését:

- **Értesítések engedélyezése** – a rendszerszintű főkapcsoló a push-üzenetekhez.
- **Jogosultságkérés beállításai** – testre szabható a felugró ablak szövege és a gombok feliratai, amivel a felhasználóktól engedélyt kérsz az értesítések küldésére.

## Funkciók

- **Célzott üzenetküldés** – küldhetsz üzenetet mindenkinek, vagy csak bizonyos csoportoknak/szerepköröknek.
- **Firebase-integráció** – a háttérben a Google Firebase (FCM) szolgáltatása gondoskodik az üzenetek célba juttatásáról.

## Használati tippek

- Az **Engedélykérés szövege** legyen rövid és meggyőző, hogy a felhasználók szívesen iratkozzanak fel.
- Ne küldj túl sok értesítést, mert a felhasználók hamar letilthatják azokat.
- A push-értesítések kiválóan alkalmasak fontos programváltozások, azonnali hírek vagy eredményhirdetések közlésére.
"""
)
