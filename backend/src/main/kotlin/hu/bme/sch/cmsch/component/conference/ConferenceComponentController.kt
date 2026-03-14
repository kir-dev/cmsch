package hu.bme.sch.cmsch.component.conference

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
@RequestMapping("/admin/control/component/conference")
@ConditionalOnBean(ConferenceComponent::class)
class ConferenceComponentController(
    adminMenuService: AdminMenuService,
    component: ConferenceComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    ConferenceComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_CONFERENCE,
    "Konferencia",
    "Konferencia testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Konferencia** komponens egy komplex eseménykezelő modul, amely kifejezetten tudományos vagy szakmai konferenciák igényeire lett szabva. Kezeli az előadókat, előadásokat, támogatókat és a regisztrációt.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a konferencia felületét:

- **Korábbi konferenciák** – szekció címe az archívumhoz.
- **Regisztráció** – Cooltix-integráció és a jelentkezési gomb szövege.
- **Mobil App** – az esemény saját alkalmazásának promóciója és letöltési linkjei.
- **Nyereményjáték** – a konferenciához kapcsolódó játék leírása, képe és szabályai.
- **Promó videó** – beágyazott YouTube-videó és leírása.
- **Támogatók** – a szponzorációs szekció címe.
- **Kiemelt előadás** – egy kiválasztott előadás hangsúlyos megjelenítése a főoldalon.

## Konferencia kezelése

A komponens számos entitást foglal magában:

1. **Konferenciák** – az egyes események (évek/alkalmak) adatai.
2. **Cégek (Companies)** – a támogató partnerek listája.
3. **Szervezők (Organizers)** – a konferencia lebonyolításáért felelős személyek.
4. **Előadások (Presentations)** – a programpontok részletei (időpont, helyszín, téma).
5. **Előadók (Presenters)** – a szakmai előadók bemutatása.
"""
)
