package hu.bme.sch.cmsch.component.communities

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
@RequestMapping("/admin/control/component/communities")
@ConditionalOnBean(CommunitiesComponent::class)
class CommunitiesComponentController(
    adminMenuService: AdminMenuService,
    component: CommunitiesComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    CommunitiesComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_COMMUNITIES,
    componentCategoryName = "Körök",
    componentMenuName = "Beállítások",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Körök** (közösségek) komponens a különböző öntevékeny körök, szakosztályok és reszortok bemutatását és kezelését teszi lehetővé.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a közösségi oldalakat:

- **Jogosultságok** – mely szerepkörökkel érhető el az oldal.
- **Körök** – az egyes körök (pl. Kir-Dev, HA5KTT) listázása, leírása és keresési lehetősége.
- **Reszortok** – a köröket összefogó nagyobb szervezetek (pl. Simonyi Károly Szakkollégium) bemutatása.
- **Tinder** – egy speciális, játékos funkció, amivel a felhasználók "matchelhetnek" a hozzájuk illő körökkel.

## Közösségek kezelése

Két fő entitást kezelhetsz:

1. **Körök** – az egyes közösségek adatai (név, leírás, logó, reszorttagság, elérhetőségek).
2. **Reszortok** – a felsőbb szintű szervezeti egységek.

## Kör létrehozása / szerkesztése

- **Név** – a kör teljes neve.
- **Rövid név** – a kör beceneve (pl. Kir-Dev).
- **URL** – a kör adatlapjának címe (pl. `kir-dev`).
- **Reszort** – melyik reszorthoz tartozik.
- **Leírás** – részletes bemutatkozó szöveg Markdown-formátumban.
- **Logó / Kép** – a kör vizuális megjelenése.
- **Kulcsszavak** – a Tinder funkcióhoz és kereséshez használt címkék.
"""
)
