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
A **Statikus oldalak** komponenssel a látogatóknak szánt, egyszerű tartalmi oldalakat hozhattok létre (GYIK, szabályzat, kapcsolat, program). A tartalom Markdown, az oldal a `/page/{url}` címen nyílik meg (pl. `/page/gyik`). A komponensnek nincs saját látogatói menüpontja: az oldalakat egyenként tehetitek be a menübe.

## Beállítások

Az **Oldalak testreszabása** oldalon egyetlen beállítás van:

- **Jogosultságok** – mely szerepkörök nyithatnak meg bármelyik statikus oldalt. Alapból mindenki benne van; ha szűkíted, a kimaradó szerepkörök minden statikus oldalra hibát kapnak.

## Oldal létrehozása

1. **Statikus oldalak** menüpont → **Új Statikus Oldal**.
2. Add meg az **Url**-t (ékezet nélküli kisbetű, pl. `gyik`), a **Cím**-et és **Az oldal tartalma** mezőt Markdown szöveggel.
3. Kapcsold be a **Látható**-t, különben az oldal senkinek sem nyílik meg.
4. Menübe tételhez a **Látható a menüben** be, majd a **Tartalom → Menü beállítások** oldalon szerepkörönként pipáld be és sorold be; a menüben a **Menü cím** szövege jelenik meg.

## Mezők

| Mező | Mit jelent |
| --- | --- |
| **Url** | Az oldal azonosítója: ebből lesz a `/page/{url}` cím, a megosztható link pedig a `/share/page/{url}`. |
| **Cím** | Az oldal neve, ez látszik a böngésző címsorában és az oldal tetején. |
| **Az oldal tartalma** | Markdown: címsorok, felsorolások, kiemelés, linkek, képek, táblázatok, kódblokkok, idézetek. A nyers HTML-t nem jeleníti meg. |
| **Látható** | Csak bekapcsolva nyitható meg az oldal; kikapcsolva a látogató hibát kap. |
| **Elérhető** | Megjelenik a listában, de a működésre jelenleg nincs hatása. |
| **Jog a szerkesztéshez** | Üresen hagyva mindenki szerkesztheti, aki a listát látja. Kitöltve (pl. `STATICPAGE_EDIT_GYIK`) csak azok, akiknek ezt a jogot a **Felhasználók** vagy a **Jogkörök** oldalon megadtad – ilyenkor az oldal a többiek listájából el is tűnik. A mezőt csak adminok látják. |
| **Látható a menüben** | Bekapcsolva az oldal kiválasztható a **Menü beállítások**ban. |
| **Menü cím** | A menüben megjelenő szöveg a **Cím** helyett. |
| **Minimum jogkör** | Az ennél alacsonyabb szerepkörű látogató nem nyithatja meg az oldalt, és a **Menü beállítások**ban sem választható ki. |
| **OG:Title / OG:Image / OG:Description** | A `/share/page/{url}` link megosztásakor megjelenő előnézet. |

## Jó tudni

- A **Statikus oldalak** lista a **Cím**, **Látható** és **Elérhető** oszlopokat mutatja, a kereső a **Cím**-ben keres. A soroknál szerkesztés, **Másolat készítése** és törlés érhető el, valamint CSV import és export; másolásnál az **Url**-t mindenképp írd át.
- Az admin oldalakhoz **Statikus oldalak megtekintése** (`STATIC_PAGE_SHOW`), új oldal felvételéhez **Statikus oldalak létrehozása** (`STATIC_PAGE_CREATE`) jog kell; a szerkesztés és törlés jogát a **Felhasználók**/**Jogkörök** oldalon adhatod meg.
- A látogatói menü a menü- vagy komponensbeállítások mentésekor frissül: ha egy már menüben lévő oldal **Menü cím**-ét módosítod vagy kiveszed a menüből, a **Menü beállítások** újramentése kell hozzá.
- Két oldalnak ne legyen ugyanaz az **Url**-je, és a menübe tett oldalon a **Látható** is legyen bekapcsolva, különben a menüpont hibára visz.
"""
)
