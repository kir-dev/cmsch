package hu.bme.sch.cmsch.component.key

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
@RequestMapping("/admin/control/component/accessKeys")
@ConditionalOnBean(AccessKeyComponent::class)
class AccessKeyComponentController(
    adminMenuService: AdminMenuService,
    component: AccessKeyComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    AccessKeyComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_EVENTS,
    "Hozzáférési kulcsok",
    "Hozzáférések testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,

    storageService = storageService,
    documentationMarkdown = """
A **Hozzáférési kulcsok** komponens segítségével egyedi kódokat generálhatsz, amelyeket a felhasználók beválthatnak bizonyos előnyökért (pl. csoportba kerülés, jogosultság szerzés).

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a kódbeváltás folyamatát:

- **Lap címe** – a böngésző címsorában megjelenő szöveg.
- **Menü neve** – a menüben látható név.
- **Hibaüzenetek** – testre szabható üzenetek különböző esetekre (hibás kód, már felhasznált kód, nincs bejelentkezve stb.).
- **Működés** – engedélyezhető vagy tiltható a beváltás, illetve beállítható, hogy egy felhasználó több kódot is felhasználhat-e.
- **Megjelenés** – egyedi leírás és mezőnév a beváltó oldalon.

## Kulcsok kezelése

A **Hozzáférési kulcsok** menüpont alatt:

- **Új kulcs létrehozása** – egyedi kód generálása.
- **Szerkesztés / Törlés** – kulcsok módosítása.

## Kulcs létrehozása / szerkesztése

- **Név** – a kulcs belső neve.
- **Kulcs** – maga a beváltandó kód (pl. `SECRET123`).
- **Csoport** – melyik belső csoportba kerüljön a felhasználó beváltás után.
- **Gárda** – melyik gárdába kerüljön a felhasználó.
- **Szerepkör** – milyen jogosultságot kapjon (pl. ATTENDEE).

## Használati tippek

- Ezt a komponenst tipikusan "belépő kódok" vagy "titkos kódok" kezelésére használják, amikkel a felhasználók aktiválhatják a profiljukat a rendszerben.
- A **Csoportba áthelyezés** funkcióval automatizálható a résztvevők szétosztása a különböző csapatokba/tankörökbe a kódjuk alapján.
"""
)
