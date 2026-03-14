package hu.bme.sch.cmsch.component.race

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_RACE
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/race")
@ConditionalOnBean(RaceComponent::class)
class RaceComponentController(
    adminMenuService: AdminMenuService,
    component: RaceComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    RaceComponent::class.java,
    component,
    PERMISSION_CONTROL_RACE,
    "Verseny",
    "Verseny testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Verseny** komponens időalapú vagy pontalapú mérések eredményeinek rögzítésére és rangsorolására szolgál.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a méréseket:

- **Lap címe** – a böngésző címsorában megjelenő szöveg.
- **Menü neve** – a menüben látható név.
- **Jogosultságok** – mely szerepkörökkel érhető el az oldal.
- **Kijelzés** – szabályozható a toplista láthatósága, a rendezési elv (növekvő/csökkenő) és a keresési lehetőség.
- **Szabad kategória** – egyedi, kötetlenebb mérési kategória (pl. "Funky mérés") beállítása.

## Verseny kezelése

Három szinten kezelheted az adatokat:

1. **Versenykategóriák** – hozz létre csoportokat a különböző méréseknek (pl. "Alap mérés", "Profi mérés").
2. **Mérési eredmények** – itt rögzítheted a konkrét eredményeket (idő, név, csapat).
3. **Szabad kategóriás eredmények** – a kötetlenebb mérések eredményeinek helyszíne.

## Eredmény rögzítése / szerkesztése

- **Név** – a résztvevő neve.
- **Csapat** – a résztvevő csapata.
- **Idő / Pont** – a mért eredmény.
- **Kategória** – melyik méréshez tartozik.
"""
)
