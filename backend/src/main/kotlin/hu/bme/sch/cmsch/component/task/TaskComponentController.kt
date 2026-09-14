package hu.bme.sch.cmsch.component.task

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_TASKS
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/task")
@ConditionalOnBean(TaskComponent::class)
class TaskComponentController(
    adminMenuService: AdminMenuService,
    component: TaskComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    TaskComponent::class.java,
    component,
    PERMISSION_CONTROL_TASKS,
    "Feladatok",
    "Feladat beállítások",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Feladatok** komponens feladványok kiírására és online beadatására szolgál: a résztvevők (az indulási `task-ownership-mode` beállítástól függően felhasználónként vagy csapatonként) szöveget, képet, PDF-et vagy ZIP-et küldhetnek be, amit a rendezők értékelnek és pontoznak.

## Első lépések

1. **Feladat kategóriák** menü: itt hozd létre a kategóriát (**Kategória neve**, **Kategória id-je**, **Beadhatóak ekkortól**, **Beadhatóak eddig**, **Típus**).
2. **Feladatok** menü: a feladat **Kategória id-je** mezőjébe pontosan a kategória **Kategória id-je** számát írd – ez a két szám köti össze őket, nem a sorok azonosítója. A **Kategória id-je** értékének egyedinek kell lennie, különben összeakad a rendszer.
3. **Látható** bekapcsolása nélkül a feladat senkinek sem jelenik meg.
4. **Értékelések** menü: a beérkező beadások elbírálása.

## Kategória beállításai

- **Típus** – REGULAR: a feladatok listáján szerepel. PROFILE_REQUIRED: külön blokkban, a lista tetején, és a profil csak akkor számít kitöltöttnek, ha ezekre a feladatokra van elfogadott beadás.
- **Hírdetett** – a csapat komponens is kiemelten listázza.
- **Minimum/Maximum rang a megtekintéshez** – a kategória csak az adott rangtartományba tartozóknak látszik.
- A **Beadhatóak ekkortól / eddig** ablakon kívül a kategória a listában sem jelenik meg.

## Feladat beállításai

- **Típus** – mit fogadjon el a szerver: TEXT (szöveg), IMAGE (kép: png/jpg/jpeg/gif/webp), BOTH (szöveg és kép), ONLY_PDF (csak .pdf), ONLY_ZIP (csak .zip). A kiterjesztést a szerver ellenőrzi.
- **Formátum** – hogyan lehet beadni: NONE (nincs online beadás, személyesen kell leadni), TEXT (szövegmező vagy fájltallózó), CODE (kódszerkesztő), FORM (saját űrlap).
- **Formátum leírása** – FORM formátumnál ide kerül a mezők leírása: [{"title":"","type":"number|text|textarea","suffix":""}].
- **Max pont**, **Beadható ekkortól**, **Beadható eddig** – ezen az ablakon kívül beadás nem lehetséges.
- **Beadandó formátum** – rövid útmutató a beadó mező mellett; **Leírás** – a feladat szövege Markdownban.
- **Mintamegoldás** – Markdown szöveg, ami csak a határidő lejárta után jelenik meg a résztvevőknek.
- **Kiemelt** – „hamarosan lejár” jelzés; **Sorrend** – kategórián belüli sorrend; **Minimum/Maximum rang a megtekintéshez** – a feladat láthatósága rang szerint.

## Értékelés

- **Értékelések** – feladatonként összesítve (elfogadva / elutasítva / nincs értékelve). Az **Értékel** gomb a még el nem bírált beadásokat listázza, a **Kijavít** gomb nyitja az értékelő űrlapot: **Elfogadva**, **Elutasítva**, **Adott pont**, **Értékelés** (ez a szöveg a beadónak is megjelenik). Ha mindkettőt bejelölöd, az **Elfogadva** nyer; a döntéseket a **Beadás történet** naplózza.
- **Nyers beadások** – minden beadás egy listában, kézzel is javítható, CSV exporttal.
- **Személyes beadás értékelése** – felhasználó/csoport és feladat kiválasztásával egy lépésben rögzíthetsz értékelést; ha még nincs beadás, létrehozza, így személyes leadás pótlására is jó.
- **Pontok ellenőrzése** – az elfogadott beadások közül azok, amelyek pontszáma nem 0 és nem a **Max pont** (elgépelt pontszámok kiszűrésére).

## Működés

- **Újraküldés lehetséges** – kikapcsolva az elfogadott vagy el nem bírált beadás nem módosítható (az elutasított viszont újra beadható); bekapcsolva a határidőig bármelyik beadás újraküldhető, ilyenkor az addigi döntés törlődik, és a beadás újra értékelésre vár.
- **Pontok látszódnak közben** – kikapcsolva a pontszám csak elfogadott beadásnál, a határidő lejárta után látszik; **Pontok látszódnak egyáltalán** – kikapcsolva egyénileg soha, csak az összesítésekben.
- **Feladatok megnyitásának logolása** – naplózza, ki nyitott meg egy feladatot.
- **Kötelező feladatok fejléc szövege / alatti szöveg** és **Feladatok fejléc szövege / alatti szöveg** – a feladatok oldal két blokkjának címe, illetve a cím alatti Markdown szöveg.
- **Endpoint elérhető** – bekapcsolva a `/export-tasks` oldal a bejelentkezett résztvevő saját csapata beadásairól ad nyomtatható összesítőt (Ctrl+P → PDF), a **Főrendezők üzenetével** és a **Logó URL-je** képpel. Menüből nem érhető el, a linket neked kell megosztanod.
- A pontszámok a ranglista összesítésébe is bekerülnek, a Leaderboard komponens **tasksPercent** arányában.
- **Jogosultságok** alapból üres, ilyenkor a Feladatok oldalt csak adminok látják, ezért élesítés előtt mindenképp vedd fel a résztvevői szerepköröket.
"""
)
