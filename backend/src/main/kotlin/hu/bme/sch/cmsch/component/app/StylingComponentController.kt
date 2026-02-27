package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/style")
@ConditionalOnBean(StylingComponent::class)
class StylingComponentController(
    adminMenuService: AdminMenuService,
    component: StylingComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    StylingComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_APP,
    componentMenuName = "Stílus beállítások",
    componentMenuIcon = "style",
    insertComponentCategory = false,
    componentCategory = ApplicationComponent.STYLING_CATEGORY,
    componentMenuPriority = 5,
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Stílus** komponens segítségével testre szabhatod a weboldal megjelenését CSS-ismeretek nélkül.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a témákat:

- **Világos téma (Light Mode)** – színek (háttér, szöveg, brand), háttérképek és logók beállítása nappali módhoz.
- **Sötét téma (Dark Mode)** – színek és képek éjszakai módhoz. Szabályozható, hogy a rendszer automatikusan kövesse-e az eszköz beállításait, vagy kényszerítve legyen valamelyik mód.
- **Tipográfia** – az oldalon használt betűtípusok (fontok) és azok forrásának (CDN) megadása.

## Funkciók

- **Brand-szín** – egyetlen szín megadásával az egész oldal arculatát a rendezvényhez igazíthatod (gombok, linkek, kiemelések).
- **Reszponzív hátterek** – külön háttérképet állíthatsz be asztali és mobil nézethez.

## Használati tippek

- Érdemes olyan **Brand-színt** választani, amely mindkét témában (világos/sötét) jól olvasható fehér vagy fekete szöveggel.
- A **Tipográfia** beállításnál Google Fonts-linkeket használhatsz a legegyszerűbben.
"""
)
