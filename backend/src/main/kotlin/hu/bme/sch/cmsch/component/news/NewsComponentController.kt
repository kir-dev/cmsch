package hu.bme.sch.cmsch.component.news

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_NEWS
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/news")
@ConditionalOnBean(NewsComponent::class)
class NewsComponentController(
    adminMenuService: AdminMenuService,
    component: NewsComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService,
    appComponent: ApplicationComponent
) : ComponentApiBase(
    adminMenuService,
    NewsComponent::class.java,
    component,
    PERMISSION_CONTROL_NEWS,
    "Hírek",
    "Hírek testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Hírek** komponens segítségével híreket és közleményeket tudsz létrehozni és megjeleníteni a felhasználók számára.  
Az adminfelületen keresztül minden fontos beállítást elérsz.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a hírek megjelenését:

- **Lap címe** – ez jelenik meg a böngésző címsorában.
- **Menü neve** – a menüben látható név.
- **Jogosultságok** – meghatározhatod, hogy minimum milyen szerepkörrel érhető el a hírek oldal  
  (pl. mindenki, bejelentkezett felhasználók, rendezők, adminok).
- **Részletes nézet** – ha be van kapcsolva, akkor a hírek külön oldalon is megnyithatók, nem csak listában.

## Hírek kezelése

A hírek listájában láthatod az összes eddig létrehozott hírt. Innen tudsz:

- **Új hír létrehozása** gombbal új hírt rögzíteni.
- Meglévő hírt **szerkeszteni** vagy **törölni**.
- Állapotukról információt szerezni (látható / kiemelt / publikálás ideje).

## Hír létrehozása / szerkesztése

Új hír felvételekor vagy szerkesztéskor a következő mezőket tudod beállítani:

- **Url** – rövid azonosító, ami a hír webcímében szerepel. Csak kisbetűk és kötőjelek használhatók. A frontenden ${appComponent.siteUrl}news/{url} linken érhető el.
- **Cím** – a hír fő címe.
- **Rövid tartalom** – rövid leírás, ami a hírek listájában jelenik meg.
- **Tartalom** – a hír teljes szövege, markdown formázással.
- **Kép** – illusztráció a hírhez (feltöltés szükséges).
- **Látható** – ha be van kapcsolva, a hír megjelenik a felhasználók számára.
- **Kiemelt** – ha be van jelölve, a hír külön kiemeltként jelenhet meg a felületen.
- **Publikálás időpontja** – időzítésre használható. Az itt beállított időpont előtt nem látszik a hír.
- **Minimum rang a megtekintéshez** – korlátozhatod, hogy csak bizonyos jogosultsági szinttel rendelkező felhasználók lássák.
- **OG:Title, OG:Image, OG:Description** – a közösségi megosztásokhoz tartozó metaadatok  
  (Facebook, Messenger, stb. megosztáskor jelennek meg). ${appComponent.adminSiteUrl}share/news/{url} linkkel tudod megosztani a híreket, hogy megjelenjenek megfelelően.

## Használati tippek

- Ha **előre be szeretnéd időzíteni** a hírt, állítsd be a publikálás időpontját, és jelöld be a **Látható** kapcsolót.  
  A hír csak az időpont után fog megjelenni.
- A **Kiemelt hírek** előtérbe kerülnek a felhasználói oldalon, ezért fontos közleményeknél használd.
- A **Jogosultságok** mezővel egyszerűen korlátozhatod, hogy egy hír csak a szervezőknek,  
  vagy csak a bejelentkezett résztvevőknek látszódjon.
"""
)
