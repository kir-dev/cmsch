package hu.bme.sch.cmsch.component.race

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_RACE
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/race")
@ConditionalOnBean(RaceComponent::class)
class RaceComponentController(
    adminMenuService: AdminMenuService,
    component: RaceComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    RaceComponent::class.java,
    component,
    PERMISSION_CONTROL_RACE,
    "Verseny",
    "Verseny testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Verseny** komponens (jellemzően „Sörmérés”) időeredmények rögzítésére és toplistázására szolgál. Minden eredményt **Mért idő** formában, másodpercben adsz meg, és egy kategóriához tartozik: az üres kategóriájú sorok az alap toplistára kerülnek (`/race`), a többi kategória a saját oldalát kapja (`/race/SLUG`). A toplista csapatonként vagy felhasználónként összesít – ezt telepítési beállítás dönti el, az admin felületen nem váltható.

## Beállítások

A **Verseny testreszabása** oldalon:

| Beállítás | Hatás |
| --- | --- |
| **Látható** | főkapcsoló; kikapcsolva a felhasználók a toplista helyett hibaoldalt kapnak |
| **Extra kategóriák láthatóak** | enélkül a nem alap kategóriák oldalai nem érhetők el |
| **Növekvő sorrend** | bekapcsolva a kisebb, kikapcsolva a nagyobb idő a jobb; a lista sorrendjét és a résztvevőnként beszámított értéket (minimum/maximum) is ez dönti el |
| **Alapértelmezett kategória leírása** | markdown szöveg az alap toplista tetején; üresen nem jelenik meg |
| **Keresés elérhető** | keresőmező a toplista fölött |
| **Szabad kategória neve** | a szabad kategóriás oldal és menüpontjának neve (alapértéke: Funky mérés) |
| **Szabad kategória leírása** | markdown szöveg a szabad kategóriás toplista tetején |

## Beállítás menete

1. Kapcsold be a **Látható**, extra kategóriákhoz az **Extra kategóriák láthatóak** beállítást.
2. A **Mérés kategóriák** menüpontban vedd fel a méréseket (**Név**, **Slug (url)**, **Leírás**, **Látható-e a kategória**); az alap toplistához nem kell kategóriát létrehozni.
3. A **Mérések** menüpontban rögzítsd az eredményeket kézzel vagy importtal.
4. A **Menü beállítások** oldalon engedélyezd szerepkörönként a menüpontokat: a **Menü neve** szerinti fő menüt, valamint a kategóriák és a szabad kategória külön menüpontját (utóbbiak csak **Látható-e a kategória** bekapcsolása után választhatók).

## Eredmény rögzítése

- **Kategória** – a legördülő a kategóriák **Slug (url)** értékét kínálja; az üres az alap kategória.
- **Felhasználó** / **Csoport** – az üzemmód dönti el, melyiket kell kitölteni; a **Felhasználó** formátuma `Teljes Név | id | [a/g] email`, és ilyenkor a **Csoport** a felhasználó csapatából töltődik.
- **Mért idő** – másodpercben, ponttal elválasztva, legfeljebb 3 tizedesjegyig (pl. 12.345).
- **Címke** és **Szín** – a név melletti színes jelvény a toplistán.
- **Időbélyeg** – mentéskor automatikusan kitöltődik, kézzel nem szerkeszthető.

## Amire figyelni kell

- Ha nem választasz csoportot vagy felhasználót, a sor névtelenül, egyetlen összevont toplistaelemként jelenik meg; a mentés akkor is elutasításra kerül, ha a megadott név nem létezik.
- A toplista résztvevőnként egy sort mutat, de a **Címke**/**Szín** nem feltétlenül a legjobb időhöz tartozó sorból származik, ezért egy résztvevőnél érdemes mindig ugyanazt használni.
- A szabad kategóriás lista minden beadást külön sorol fel (**Beadás módja** szöveggel), nincs résztvevőnkénti összevonás.
- A profiloldali mérési statisztika a **Profil beállítások** oldal **Mérés eredmény látható** kapcsolójától függ, minden kategóriát összevon, és mindig a kisebb időt tekinti jobbnak.

## Admin oldalak

- **Mérések** – a nyers eredmények felvétele, szerkesztése, importja és exportja.
- **Mérés kategóriák** – a mérések kategóriái.
- **Extra mérések** – kategóriánkénti, résztvevőnként összevont toplista e-mail címmel; a soronkénti **Json Export** gomb valójában CSV-t tölt le.
- **Mérés toplista** – az alap kategória toplistája (amit a felhasználók is látnak), exportálható.
- **Funky mérések** – a szabad kategória beadásai; a menü neve fix, a **Szabad kategória neve** beállítást nem követi.
"""
)
