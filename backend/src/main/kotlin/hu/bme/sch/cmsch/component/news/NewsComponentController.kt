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
A **Hírek** komponens hírek és közlemények közzétételére való: a híreket az adminfelületen veszed fel, a látogatók pedig a nyilvános **Hírek** oldalon olvassák. Egy hír csak akkor kerül ki, ha a **Látható a hír** be van kapcsolva, a **Publikálás időpontja** már elmúlt, és a néző rangja eléri a **Minimum rang a megtekintéshez** értékét.

## Hírek felvétele

A **Hírek** menüpontban (**Új Hír** gomb) veszed fel a híreket. A lista oszlopai **ID**, **Cím**, **Látható** és **Kiemelt**, a kereső pedig csak a **Cím** mezőben keres. Soronként **Megtekintés**, **Szerkesztés**, **Másolat készítése** és **Törlés**, a lap tetején **Import / Export** és **Összes törlése** gomb van.

| Mező | Jelentés |
| --- | --- |
| **Cím** | a hír címe, ez látszik a listában és a részletes oldalon is. |
| **Url** | a hír azonosítója, ez szerepel a részletes oldal és a megosztott link címében. Csak nem ékezetes kisbetűt és kötőjelet használj, és minden hírnél legyen egyedi. |
| **Rövid tartalom** | Markdown szöveg, a listában a cím alatt jelenik meg. |
| **Tartalom** | Markdown szöveg, csak a részletes nézetben látszik. |
| **Kép a hír mellé** | kép linkje vagy feltöltött fájl; a listában kis négyzetben, a részletes nézetben nagyban jelenik meg. |
| **Látható a hír** | kikapcsolva a hír sehol nem jelenik meg, akkor sem, ha az időpontja már elmúlt. |
| **Kiemelt hír** | a lista tetejére kerül, nagyobb címmel és kiemelt színű kerettel. |
| **Publikálás időpontja** | eddig az időpontig a hír nem jelenik meg; egyben a lista sorrendje is ez, csökkenően. |
| **Minimum rang a megtekintéshez** | a látható hírt is csak az ennél legalább ilyen rangú felhasználók látják (GUEST = kijelentkezett, BASIC = belépett, STAFF = rendező). |
| **OG:Title**, **OG:Image**, **OG:Description** | a megosztott link előnézetéhez; üresen hagyva az előnézet címe, képe és leírása is üres lesz. |

## Megjelenés a látogatóknál

- A **Hírek** oldalon a **Kiemelt hír** bejegyzések jönnek elöl, utána a többi hír **Publikálás időpontja** szerint csökkenő sorrendben. A lista tetején a kereső a **Cím** mezőben szűr.
- A listaelem címe csak akkor kattintható, ha a **Hírek testreszabása** oldalon a **Részletes nézet** be van kapcsolva: ekkor nyílik meg a külön hír oldal a **Tartalom** mezővel. Kikapcsolva a részletes oldal nem érhető el.
- A kezdőlapon is megjelennek a hírek, de csak ha a Kezdőlap beállításai között a **Hírek láthatóak** be van kapcsolva, és legfeljebb a **Max megjelenő hír** értéknek megfelelő darabszámban.
- A `share/news/{Url}` cím közösségi megosztásra való előnézetet ad, és a hír oldalára visz tovább.

## Amit érdemes tudni

- Ha nem adsz meg **Publikálás időpontját**, a hír azonnal látható, de a lista végére kerül, mert a sorrend időpont szerint csökkenő.
- A **Másolat készítése** minden mezőt átmásol, az **Url**-t is: mentés előtt írd át, különben két hír kerül ki ugyanazzal az azonosítóval, és a részletes nézet hibára fut.
- A CSV import és export nem tartalmazza a **Kép a hír mellé** és az OG mezőket, ezeket import után kézzel kell kitölteni.
- Az **Összes törlése** az összes hírt véglegesen törli.
"""
)
