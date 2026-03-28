package hu.bme.sch.cmsch.component.location

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
@RequestMapping("/admin/control/component/location")
@ConditionalOnBean(LocationComponent::class)
class LocationComponentController(
    adminMenuService: AdminMenuService,
    component: LocationComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    LocationComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_LOCATION,
    componentCategoryName = "Helymegosztás",
    componentMenuName = "Helymegosztás",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Helymeghatározás** (Térkép) komponens lehetővé teszi a felhasználók és csoportok valós idejű követését egy térképen. Ehhez egy külső tracker alkalmazás (pl. OwnTracks) használata szükséges.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a térképet:

- **Térkép menü neve** – a menüben látható név.
- **Jogosultságok** – mely szerepkörökkel érhető el a térkép.
- **Megjelenés** – egyedi üzenetek a térkép oldalán.
- **Csoportszínek** – beállítható, hogy melyik csoport (pl. SENIOR, KIRDEV, LEAD) milyen színnel jelenjen meg a térképen.
- **Tracker alkalmazás** – a felhasználók számára megjelenő telepítési útmutató és az appok letöltési linkjei.
- **Megjelenítés** – szabályozható, hogy mi szerepeljen a térképen lévő jelölők (markerek) alatt (név, becenév, csoportnév), és mennyi ideig maradjanak láthatóak a markerek frissítés nélkül.

## Funkciók

- **Valós idejű követés** – a rendszer fogadja a tracker-alkalmazásokból érkező GPS-koordinátákat és megjeleníti azokat egy interaktív térképen.
- **Csoportosítás** – a különböző színek segítenek gyorsan megkülönböztetni a szervezői egységeket.
"""
)
