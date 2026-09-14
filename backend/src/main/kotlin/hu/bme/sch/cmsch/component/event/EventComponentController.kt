package hu.bme.sch.cmsch.component.event

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_EVENTS
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/event")
@ConditionalOnBean(EventComponent::class)
class EventComponentController(
    adminMenuService: AdminMenuService,
    component: EventComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService,
    appComponent: ApplicationComponent
) : ComponentApiBase(
    adminMenuService,
    EventComponent::class.java,
    component,
    PERMISSION_CONTROL_EVENTS,
    "Események",
    "Események testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
    Az **Események** komponens a rendezvény programlistája: a programokat az adminfelületen veszed fel,
    a látogatók pedig a nyilvános oldalon (alapértelmezésben **Programok**) böngészik napokra bontva vagy naptárnézetben.

    ## Első beállítás

    1. Az **Események testreszabása** oldalon a **Jogosultságok** mezőben jelöld ki, kik nyithatják meg az oldalt.
       Üresen hagyva csak ADMIN és SUPERUSER látja, a kijelentkezett látogatók nem, ezért ha mindenkinek látszania kell,
       a GUEST szerepkört is vedd fel a listára.
    2. Az **Események** menüpontban vidd fel a programokat.
    3. Ha az eseményre kattintva külön oldal nyíljon meg, kapcsold be az **Elérhető a részletes nézet (külön lapon)** opciót.
       Enélkül a listaelemek nem kattinthatók, és a megosztott linkek sem nyílnak meg.

    ## Beállítások

    | Beállítás | Hatás |
    | --- | --- |
    | **Lap címe** | az oldal címe, ami a kezdőlap esemény-szekciójának fejléce is |
    | **Menü neve** | a menüben látható név |
    | **Oldal tetején megjelenő szöveg** | Markdown szöveg a lista és a naptár tetején (üresen nem jelenik meg) |
    | **Keresés elérhető** | keresőmező a lista tetején; csak a **Cím** mezőben keres, és ilyenkor a már lejárt események kikerülnek a listából |
    | **Elérhető a részletes nézet (külön lapon)** | kattintható listaelemek és külön eseményoldal a **Hosszú leírással**, **Teljes képpel** és az extra gombbal |
    | **Ha be van kapcsolva, kategória / helyszín / nap alapján is lehet szűrni** | három külön kapcsoló; mindegyik szűrőfület tesz a lista tetejére, a fül értékei pedig a **Kategória**, illetve a **Helyszín** mezők tartalmából épülnek fel |
    | **Tekerjen oda a jelenlegi programhoz**, **Külön csoportosítva naponként** | jelenleg nincs hatásuk: a lista mindig napokra bontva jelenik meg |

    ## Esemény mezői

    - **Cím**, **Kategória**, **Helyszín** – rövid adatok; a kategóriát és a helyszínt írd következetesen, mert ezekből épülnek a szűrők.
    - **Url** – az esemény azonosítója; a megosztható link a `share/event/` útvonalból és ebből az értékből áll.
    - **Mikor lesz a program?** / **Meddig tart a program?** – a befejezés után az esemény lejártnak számít: eltűnik a kezdőlapról és a keresőből.
    - **Rövid leírás** – sima szöveg a listakártyán; **Hosszú leírás** – Markdown, csak a részletes nézetben látszik.
    - **Előnézeti kép** – a listakártya háttere; **Teljes kép** – a részletes nézet nagy képe.
    - **Extra gomb szöveg** / **Extra gomb URL** – a gomb az URL kitöltésekor jelenik meg a részletes nézetben, felirata a szöveg mező.
    - **OG:Title / OG:Image / OG:Description** – a megosztott link előnézetéhez; üresen hagyva a megosztás címe, képe és leírása is üres lesz.
    - **Látható** – kikapcsolva az esemény nem kerül ki a nyilvános oldalra.
    - **Minimum rang a megtekintéshez** – a látható esemény is csak az ennél legalább ilyen rangú felhasználóknak jelenik meg (GUEST = kijelentkezett, BASIC = belépett, STAFF = rendező).

    ## Admin oldalak

    - **Események** – a programok listája **Cím**, **Időpont**, **Helyszín** és **Látható** oszloppal; gombok: **Új Esemény**, **Import / Export**,
      soronként pedig **Megtekintés**, **Szerkesztés**, **Másolat készítése** és **Törlés**.
    - Az import CSV-fájlból dolgozik, ami nem tartalmaz azonosítót, ezért egy korábban exportált lista visszatöltése minden sort új eseményként vesz fel.

    ## Amit érdemes tudni

    - A lejárt események nem tűnnek el maguktól, a listán maradnak; csak a naptárnézet és a nap szerinti szűrő **Korábbi** csoportja választja le őket.
    - Minden esemény kézzel kerül be és módosul, a komponens magától semmit nem tesz.
    - A kezdőlapon az események csak akkor jelennek meg, ha a Kezdőlap beállításai között az **Események láthatóak** is be van kapcsolva.
    """)
