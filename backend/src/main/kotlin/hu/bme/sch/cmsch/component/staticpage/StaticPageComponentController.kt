package hu.bme.sch.cmsch.component.staticpage

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_STATIC_PAGES
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/staticPage")
@ConditionalOnBean(StaticPageComponent::class)
class StaticPageComponentController(
    adminMenuService: AdminMenuService,
    component: StaticPageComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    StaticPageComponent::class.java,
    component,
    PERMISSION_CONTROL_STATIC_PAGES,
    "Statikus Oldalak",
    "Oldalak testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Statikus oldalak** komponens segítségével tetszőleges tartalmú oldalakat hozhatsz létre, amelyek Markdown formátumban szerkeszthetők.

## Beállítások

A komponensnek nincsenek globális beállításai, az oldalakat egyenként kell konfigurálni.

## Oldalak kezelése

A **Statikus oldalak** menüpont alatt:

- **Új oldal létrehozása** – új tartalom rögzítése.
- **Szerkesztés / Törlés** – meglévő oldalak módosítása.

## Oldal létrehozása / szerkesztése

- **URL** – az oldal címe a böngészőben (pl. `info`). A frontenden a `/page/{url}` címen lesz elérhető.
- **Cím** – az oldal neve.
- **Tartalom** – a megjelenített szöveg Markdown formátumban.
- **Látható** – ha be van kapcsolva, az oldal elérhető a felhasználók számára.
- **Minimum szerepkör a megtekintéshez** – korlátozhatod az oldal láthatóságát.

## Használati tippek

- Használd a statikus oldalakat szabályzatok, általános tájékoztatók vagy GYIK megjelenítésére.
- A Markdown formázással képeket, táblázatokat és linkeket is elhelyezhetsz a szövegben.
"""
)
