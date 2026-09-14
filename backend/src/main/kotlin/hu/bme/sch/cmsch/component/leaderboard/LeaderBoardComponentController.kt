package hu.bme.sch.cmsch.component.leaderboard

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
@RequestMapping("/admin/control/component/leaderboard")
@ConditionalOnBean(LeaderBoardComponent::class)
class LeaderBoardComponentController(
    adminMenuService: AdminMenuService,
    component: LeaderBoardComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    LeaderBoardComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_LEADERBOARD,
    componentCategoryName = "Toplista",
    componentMenuName = "Toplista testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Toplista** a **Feladatok**, **Riddle-ök**, **Beadások** és **QR Kódok** komponensekben szerzett pontokat összesíti, és rangsorba állítja a résztvevőket és a csoportokat. A pontok nem élőben számolódnak, hanem egy gyorsítótárból jönnek, amit újra kell számoltatni.

## Beállítások (Toplista testreszabása)

- **Toplista aktív** – kikapcsolva a résztvevők üres toplistát kapnak.
- **Toplista részletek aktív** – a listában a sorok kinyithatók, és forrásonként bontva is látszanak a pontok (a forrás neve a Feladatok / Riddle-ök / Tokenek komponens **Menü neve**, a beadásoknál a beadás kategóriája).
- **Toplista kategória szerint aktív** + **Kategória megnevezése** – külön fül kategóriánkénti bontással; a fül felirata a **Kategória megnevezése** (alapból: Kategóriánként).
- **Toplista befagyasztott** – alapból be van kapcsolva. Ilyenkor az automatikus újraszámolás nem fut le, a résztvevők a legutóbb kiszámolt állást látják. Nem rejti el a toplistát, csak lefagyasztja az értékeket.
- **Pontok mutatása** – kikapcsolva a listákban csak a sorrend látszik; a **Saját pont** és a saját csoport pontját mutató csempe ettől függetlenül mutatja a pontot.

## Pont számítás

A szorzók a forrásokban szerzett pontokat szorozzák (100 = 1x, 0 = nem számít bele): **Feladatok szorzó (%)**, **Riddle szorzó (%)**, **Beadások szorzó (%)**, **QR Kódok szorzó (%)**. A **Felhasználói pontok felhasználása pontszámításnál** opció a felhasználói elszámolású források pontjait is beszámítja a csoportos toplistába.

## Kijelzés

- **Felhasználói toplista mutatása** és **Csoport toplista mutatása** – melyik lista (vagy mindkettő) látszódjon; **Keresés elérhető** – kereső az oldal tetején; **Felhasználó csoportjának kijelzése** – a felhasználói listán látszik-e a csoport.
- **Toplista sorainak száma** – kétszer szerepel, külön a felhasználói és a csoportos listához; -1 = az összes sor.
- **Legalább ennyi ponttal** – az ennél kevesebb pontot szerzők lekerülnek a listáról; alapértéke 1, ezért a 0 pontos résztvevők eleve nem látszanak.
- **Csoport toplista neve** – a csoportos lista fül felirata; **Saját csoport neve** – a saját csoport pontját mutató csempe felirata; **Felső szöveg** – az oldal tetején megjelenő markdown szöveg.
- **Begyűjtött tokenek száma ritkaság szerint** – a részletes listában a QR kódok sora alatt ritkaságonkénti darabszám, módosítás után újraszámolás szükséges. Az **Összes token szám ritkaság szerint** beállítás létezik, de a felület jelenleg nem használja.

## Admin oldalak (Toplista menü)

| Oldal | Oszlopok |
| --- | --- |
| **Felhasználói toplista** | Felhasználó, Csoport, Feladatok, Riddleök, Beadások, QR Kódok, Totál |
| **Csoport toplista** | Csoport, Feladatok, Riddleök, Beadások, QR Kódok, Totál |

Mindkét oldal alján **Újraszámol** (azonnali újraszámolás, a befagyasztást is felülírja) és **Mentés** (CSV export) gomb van.

## Automatizmus és buktatók

- A gyorsítótár induláskor, majd 10 óránként számolódik újra, de csak ha a **Toplista befagyasztott** ki van kapcsolva. A beállítások mentése önmagában nem indít újraszámolást.
- A csoportos toplistán csak azok a csoportok szerepelnek, amelyeknél a **Csoportok** adminon a **Játszik a csoport a versenyben?** be van kapcsolva.
- Egy forrás csak abban a nézetben ad pontot, amilyen elszámolásra telepítéskor be van állítva (felhasználói vagy csoportos), a másik listán üresen marad.
- A ritkaság szerinti bontás csak akkor jelenik meg, ha a Tokenek komponens **Menü neve** pontosan "QR kódok".
- A csapat oldal **Helyezés** / **Pontszám** csempéi is ebből a gyorsítótárból dolgoznak, ezért az elavult állás ott is látszik.
"""
)
