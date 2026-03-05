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
@RequestMapping("/admin/control/component/manifest")
@ConditionalOnBean(ManifestComponent::class)
class ManifestComponentController(
    adminMenuService: AdminMenuService,
    component: ManifestComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    ManifestComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_APP,
    componentMenuName = "Manifest beállítások",
    componentMenuIcon = "image",
    insertComponentCategory = false,
    componentCategory = ApplicationComponent.STYLING_CATEGORY,
    componentMenuPriority = 28,
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Manifest** komponens a webalkalmazás (PWA - Progressive Web App) telepítési tulajdonságait szabályozza. Ez határozza meg, hogyan jelenik meg az oldal, ha a felhasználó hozzáadja a kezdőképernyőjéhez.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a manifest fájlt:

- **A manifest.json tartalma** – az alkalmazás neve, rövid neve, leírása, színei és megjelenítési módja (pl. `standalone`, `browser`).
- **Ikonok** – a különböző eszközökhöz és felbontásokhoz szükséges ikonok feltöltése.

## Funkciók

- **PWA támogatás** – a helyesen beállított manifest lehetővé teszi, hogy az oldal alkalmazásként viselkedjen (ikon a főképernyőn, nincs böngésző keret).

## Használati tippek

- Az **Ikonok** feltöltéséhez használj online generátort, hogy minden szükséges méret (192x192, 512x512 stb.) rendelkezésre álljon.
- A **Kijelzés módja** (display) beállítással szabályozhatod, hogy látszódjon-e a böngésző címsora (browser) vagy teljes képernyős legyen az élmény (standalone).
"""
)
