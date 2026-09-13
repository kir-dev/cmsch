package hu.bme.sch.cmsch.component.location

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/location")
@ConditionalOnBean(LocationComponent::class)
class LocationComponentController(
    adminMenuService: AdminMenuService,
    component: LocationComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    LocationComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_LOCATION,
    componentCategoryName = "Helymegosztás",
    componentMenuName = "Helymegosztás",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A helymegosztás a **CMSch Bacon** tracker alkalmazással működik: az app a telefon pozícióját küldi a CMSch-nek, a szerver pedig a résztvevők **Térkép** oldalán és az admin követő oldalakon jeleníti meg a jelölőket.

## Beállítás menete

1. **Telepítési útmutató** – ez a szöveg jelenik meg a **Helymeghatározás** admin oldalon, ide írd a telepítés lépéseit. Az **Android App URL-je** és az **iOS App URL-je** a letöltési gombok célja, a **Megnyitás a CMSch Bacon-ben** gomb pedig egy kattintással beállítja az appot.
2. **Csoport színek** – minden színhez külön beállítás tartozik (**Kék csoport neve**, **Narancs csoport neve**, **Fekete csoport színe**, …). A mezőkbe a csoport **nevét** írd (pl. SENIOR), nem a színt; a felsoroltakon kívüli csoportok az **Alapértelmezett csoport színe** színt kapják.
3. **Megjelenítés** – **Felhasználó nevének kiírása**, **Becenév nevének kiírása**, **Csoport nevének kiírása**: mi látszódjon a jelölő alatt. A **Láthatóság ideje** (másodperc) letelte után a jelölő eltűnik, ha nem érkezik új pozíció; alapból 600.
4. **Megjelenés** – **Oldal tetején megjelenő szöveg** és **Oldal alján megjelenő szöveg** (markdown) a Térkép oldalon.

## Admin oldalak

| Menüpont | Leírás |
| --- | --- |
| **Követés (térkép)** | Élő térkép az összes csoport jelölőjével, 5 másodpercenként frissül. |
| **Csoport követése** | Azok a csoportok, amelyektől van pozíció; a **Térkép** gombbal csak az adott csoport jelölői látszanak. |
| **Pozíciók** | A beérkező nyers pozíciók listája. Új pozíció itt nem vehető fel, de szerkeszthető, törölhető és exportálható; a **Frissítés** gomb a felhasználói adatokat (név, becenév, csoport) tölti újra. |
| **Jelzők** | Fix pontok a térképen (pl. büfé, elsősegély): **Kijelzett szöveg**, **Latitude**, **Longitude**, **Forma**, **Szín**, **Leírás**. A jelzők a résztvevők Térkép oldalán mindenkinek látszanak (a követő admin térképen nem), és nem tűnnek el. |
| **Helymeghatározás** | A helymegosztók oldala: token, útmutató, app-letöltés. |

## Fontos

- Pozíciót **kizárólag Rendező (STAFF) vagy magasabb szerepkörű** felhasználó küldhet, a többiek appja hibát kap. A token a **Helymeghatározás** oldalon látható, és nem szabad kiadni.
- A résztvevők a **Térkép** menüben a jelzők mellett csak a **saját csoportjuk** jelölőit látják, és csak akkor, ha a Profil komponensben be van kapcsolva a **Vezetők helyzetének mutatása**.
- Aki megkapta a `Közvetítés funkció (mindenki láthassa)` jogot, az az appban bekapcsolhatja, hogy a pozíciója mindenkinél megjelenjen. A **Pozíciók** listában ezt a **Publikus helyzet** kapcsoló mutatja, de a következő beérkező pozíció felülírja.
- A pozíciók **csak memóriában** élnek: újraindítás után üres a **Pozíciók** lista (az appok néhány percen belül újraküldik).
- A **Térkép** oldal 15, az admin **Követés (térkép)** 5 másodpercenként frissül.
"""
)
