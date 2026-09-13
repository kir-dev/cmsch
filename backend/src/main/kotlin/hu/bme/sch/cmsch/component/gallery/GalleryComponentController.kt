package hu.bme.sch.cmsch.component.gallery

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_GALLERY
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/gallery")
@ConditionalOnBean(GalleryComponent::class)
class GalleryComponentController(
    adminMenuService: AdminMenuService,
    component: GalleryComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    GalleryComponent::class.java,
    component,
    PERMISSION_CONTROL_GALLERY,
    "Galéria",
    "Galéria testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Galéria** komponens a rendezvényen készült fotók gyűjteménye. A képeket az adminfelületen töltitek fel, a látogatók a galéria oldalán böngészik (kattintásra nagyban is megnyílnak), a kezdőlapra jelölt képek pedig a kezdőlapi carouselben jelennek meg.

## Beállítások

A **Galéria** menü **Galéria testreszabása** pontjában:

- **Jogosultságok** – mely szerepkörök nyithatják meg a galéria oldalát. Alapból üres, ilyenkor csak adminok látják, ezért élesítés előtt mindenképp állítsd be.
- **Oldal tetején megjelenő szöveg** / **Oldal alján megjelenő szöveg** – Markdown szöveg a galériaoldal tetején, illetve alján; üresen nem jelenik meg.

## Képek feltöltése

A **Képfeltöltés** menüpontban egyszerre több fájlt is kijelölhetsz, és mindegyikhez külön **Cím**, **Leírás**, valamint **Kiemelt** és **Kezdőlapra** kapcsoló tartozik. Feltölteni **GALLERY_CREATE** („Galéria képek létrehozása”) jogosultsággal lehet; a feltöltés végén a felület visszajelzi a hozzáadott képek nevét.

Feltöltéskor minden képhez készül egy legfeljebb 800×800 pixeles JPEG előnézet (**Thumbnail Url**), a galéria rácsában ez látszik, nagy nézetben viszont az eredeti fájl. Az eredeti feltöltött kép nem kerül átméretezésre vagy tömörítésre, ezért érdemes előre optimalizált képet feltölteni – a kezdőlapi carousel is az eredetit tölti. A szerver a teljes feltöltési kérést 30 MB-ra korlátozza, ennél nagyobb kép (vagy egyszerre túl sok kép) esetén a feltöltés hibára fut.

## Képek listája

A **Galéria** menüpont listázza az összes képet: innen tudsz egyesével új képet felvenni (ekkor az **Url** és **Thumbnail Url** mezőt neked kell kitöltened), szerkeszteni, törölni, illetve CSV-ben importálni és exportálni. A lista keresője a **Cím** és **Leírás** mezőben keres.

| Mező | Jelentés |
| --- | --- |
| **Cím** | A kép neve. |
| **Leírás** | Rövid leírás, a galéria rácsában a kép alatt jelenik meg. |
| **Url** | A kép linkje (a feltöltő automatikusan kitölti). |
| **Thumbnail Url** | Az előnézet linkje (a feltöltő automatikusan kitölti; ha üres, a rács az eredeti képet használja). |
| **Ezek a képek szerepelnek először** | A feltöltőn **Kiemelt**; jelenleg a galéria sorrendjét nem befolyásolja. |
| **Megjelenhet a kezdőlapon** | A feltöltőn **Kezdőlapra**; a képet beveszi a kezdőlapi carouselbe, de csak ha a **Kezdőlap** komponensnél a **Galéria képek láthatóak** be van kapcsolva. |
"""
)
