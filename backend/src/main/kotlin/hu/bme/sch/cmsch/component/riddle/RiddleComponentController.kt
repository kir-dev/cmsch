package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_RIDDLE
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/riddle")
@ConditionalOnBean(RiddleComponent::class)
class RiddleComponentController(
    adminMenuService: AdminMenuService,
    component: RiddleComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    RiddleComponent::class.java,
    component,
    PERMISSION_CONTROL_RIDDLE,
    "Riddleök",
    "Riddleök testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Riddleök** komponens képrejtvény-játék: a játékosok kategóriánként egyszerre néhány feladványt látnak (kép, leírás), beírják a megoldást, a helyes válaszért **Pont** jár. A pontok a leaderboardon (a Leaderboard komponens **Riddle szorzó (%)**-ával súlyozva) jelennek meg, a riddle oldal maga nem mutat pontszámot. A játékos a **Riddleök** menüpont alatt játszik, a **Megoldott riddleök** gombon pedig a megoldott feladványokat nézheti vissza, megoldással és hinttel együtt.

## Felépítés sorrendje

1. **Riddle Kategóriák** – vedd fel a kategóriákat: **Cím**, **Kategória id-je**, **Látható-e a riddle kategória**, **Minimum rang**.
2. **Riddleök** – a feladványok felvétele. A **Kategória id-je** mezőbe a kategória **Kategória id-je** értéke kerül, nem a sor ID-ja.
3. **Riddleök testreszabása** – a játékmenet beállításai.

## Egy feladvány mezői

- **Cím**, **A képrejtvény** (kép URL), **Pont**, **Sorrend** (a kategórián belüli sorrend), **Kategória id-je**.
- **Megoldás** – több elfogadott válasz pontosvesszővel elválasztva; maga a válasz nem tartalmazhat pontosvesszőt.
- **Hint** – a segítség szövege, csak hint kérés után látszik a játékosnak.
- **Leírás** (Markdown) és **Riddle készítője** – a játékos is látja; az **Első megoldó** mezőt a rendszer tölti ki az első nem átugrott megoldásnál.

## Beállítások, amik a játékmenetet alakítják

| Beállítás | Hatás |
| --- | --- |
| **Egyidőben mutatott riddle-ök száma** | kategóriánként ennyi megoldatlan riddle látszik **Sorrend** szerint; egy megoldása után kerül elő a következő (alapértéke 1). |
| **Hint engedélyezve** | megjelenik a **Hintet kérek** gomb a játékos oldalán. |
| **Hint pont érték (%)** | a hintet kért riddle ennyi százalékot ér a leaderboardon (100 = nincs levonás). A hint kérésével a feladvány véglegesen hintezettnek számít, akkor is, ha a játékos a szöveg nélkül is megoldotta volna. |
| **Átugrás engedélyezve** / **Átugrás ennyi megoldó után** | az átugrás gomb akkor engedett, ha a feladványt már ennyien (átugrás nélkül) megoldották; az átugrott riddle 0 pontot ér, de megoldottnak számít. |
| **Kis- és nagybetű / Szóközök és elválasztók / Ékezetek figyelmen kívül hagyása** | a beküldött válasz és a tárolt megoldás összehasonlítása; a szóközök elhagyásakor a szóköz, a kötőjel, a `&`, a `+` és a vessző tűnik el. |
| **Hibás válaszok számának mentése** | gyűjti a hibás próbálkozásokat (erőforrásigényes); a próbálkozás-számok legfeljebb 5 percenként íródnak ki az adatbázisba. |
| **Userek szerinti csoportosítás** | csapatos játéknál a megoldásokat felhasználónként is rögzíti. |

Az egyéni vagy csapatos játékmód telepítési beállítás (`OWNER_RIDDLE`, azaz `hu.bme.sch.cmsch.startup.riddle-ownership-mode`), az admin felületen nem kapcsolható.

## Moderálás (Ban / Shadow Ban)

A **Riddle beadások moderálása – Ban** és a **– Shadow Ban** csoportba írt játékosok és csoportok beadása nem kerül rögzítésre, de a feladványokat továbbra is látják. Bannál a játékos a „Ki vagy tiltva a riddleökből!” üzenetet kapja; shadow bannál minden ugyanígy történik, csak ő „Helytelen válasz!”-t lát, így nem szerez tudomást a tiltásról. Az azonosítók soronként vagy vesszővel elválasztva adhatók meg: játékosnál a **PéK internal id** (a Felhasználók oldalon a felhasználó szerkesztésénél), csoportnál a csoport numerikus ID-ja (a **Csoportok** oldal ID oszlopa) – a beállítás leírása itt tévesen csoportnevet ír. A listák mentéskor lépnek életbe, és a korábbi eredményeket nem törlik.

## Admin oldalak

- **Riddleök** és **Riddle Kategóriák** – a két lista, CSV import/exporttal.
- **Riddle felhasználónként** / **Riddle csoportonként** – beadások megoldónként: **Beadó**, **Elfogadott**, **Hintek felhasználva**, megnyitva pedig **Riddle**, **Hint**, **Megoldva**, **Átugorva**, **Próbálkozás**, **Beadva**. A csoportos oldalon **Riddle statisztika export** (CSV) gomb is van; a sorok törlésével beadás törölhető.
- **Riddle MS dashboard** – csak microservice-es telepítésnél látszik a menüben: ping, cache újratöltés, minden adat mentése és a zárolások felengedése a riddle node felé. A **Beállítások szinkronizálása** beállítás nincs implementálva, a node cache-e nem frissül magától.

## Figyelmeztetések

- A feladvány és a kategória a **Kategória id-je** értékkel kapcsolódik: ha nincs ilyen azonosítójú **Látható** kategória, vagy a játékos rangja a kategória **Minimum rang**ja alatt van, a feladvány soha nem jelenik meg. Ha több kategóriának ugyanaz a **Kategória id-je**, a besorolás nem egyértelmű.
- Ha az **Egyidőben mutatott riddle-ök száma** kevés, és a soron következő feladvány megoldhatatlan, átugrás nélkül elakad a játék.
- A feladványok és a beadások memóriában élnek: a feladvány vagy a kategória mentése azonnal érvényesül, a törlésük viszont csak újraindításkor; a törölt beadás is csak az adatbázisból tűnik el, a játékos a szerver újraindításáig (microservice-es telepítésnél a **Teljes cache ürítése** gombig) megoldottként látja.
"""
)
