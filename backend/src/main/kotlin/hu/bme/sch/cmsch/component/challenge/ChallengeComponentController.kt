package hu.bme.sch.cmsch.component.challenge

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
@RequestMapping("/admin/control/component/challenge")
@ConditionalOnBean(ChallengeComponent::class)
class ChallengeComponentController(
    adminMenuService: AdminMenuService,
    component: ChallengeComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    ChallengeComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_CHALLENGE,
    "Beadások",
    "Beadások testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Beadások** komponens kézzel felvitt pontbejegyzések adminisztrációja. Nincs résztvevői oldala: a pontokat nem a résztvevők küldik be, hanem a szervezők rögzítik (helyszínen értékelt feladatok, külső rendszerből átvett pontok, korrekció, levonás). A bejegyzések a **Toplista** pontszámításába folynak be.

## Hol találod

Az admin menü **Beadások** kategóriájában: **Beadások** (a bejegyzések listája), **Beadások testreszabása** (a komponens beállításai) és **Beadások Dokumentáció** (ez az oldal).

A listához **Beadások megtekintése**, új bejegyzéshez **Beadások létrehozása**, módosításhoz **Beadások szerkesztése**, törléshez **Beadások törlése** jogosultság kell. A beállítások és a dokumentáció a **Beadások komponens testreszabása** jogosultság mögött van.

## Egy bejegyzés mezői

| Mező | Jelentése |
| --- | --- |
| **Kategória** | szabad szöveg; a **Toplista kategória szerint aktív** nézetben ez lesz a pont sora, ezért érdemes mindig ugyanazt a szöveget írni |
| **Felhasználó** | a pontot kapó felhasználó (csak USER üzemmódban számít) |
| **Csoport** | a pontot kapó csoport (csak GROUP üzemmódban számít) |
| **Adott pont** | negatív érték is adható, így levonásra és korrekcióra is használható |
| **Cimke** | szabad szöveges címke, a listában nem látszik |

A soroknál **Megtekintés**, **Szerkesztés**, **Másolat készítése** és **Törlés**, felül **Új Beadás**, **Import / Export** és **Összes törlése** érhető el; a kereső a Kategória, Felhasználó, Csoport és Pont oszlopokon működik.

## Felhasználó vagy csoport kapja a pontot?

Ezt nem a Beadások beállításaiban, hanem a backend induló konfigurációjában lehet megadni (OWNER_CHALLENGE, azaz hu.bme.sch.cmsch.startup.challenge-ownership-mode), módosítása újraindítást igényel:

- **USER**: a **Felhasználó** mezőt kell kitölteni, a csoportot a rendszer a felhasználó csoportjából írja be.
- **GROUP**: a **Csoport** mezőt kell kitölteni, ilyenkor a bejegyzésben megadott felhasználó törlődik.

Ha egyik mező sincs kitöltve (marad a "-"), a bejegyzés elmentődik, de senkihez nem tartozik, így a toplistán sem jelenik meg.

## Hogyan lesz ebből toplista pont?

- A pontok a **Toplista** komponens **Beadások szorzó (%)** beállításával skálázódnak (100 = 1x).
- USER üzemmódban a felhasználói toplistán jelennek meg; a csoportos pontszámba csak a **Felhasználói pontok felhasználása pontszámításnál** bekapcsolásával számítanak bele.
- GROUP üzemmódban csak azok a csoportok kapnak belőle pontot, amelyeknél a csoport adatlapján be van jelölve a **Játszik a csoport a versenyben?**.

## Figyelmeztetések

- **CSV importnál nem fut le a hivatkozás feloldása**: a **userId** és **groupId** oszlopot is ki kell tölteni, különben a pont nem a megfelelő résztvevőhöz kerül. A programból exportált CSV ezeket tartalmazza, kézzel írt fájlban pótolni kell.
- A **Felhasználó**/**Csoport** mezőt mindig a legördülő listából válaszd, mert mentéskor a rendszer a kiválasztott rekord belső azonosítóját tárolja.
- A csoportos pontszámítás a csoportot név szerint keresi, ezért egy csoport átnevezése után a korábbi bejegyzései kiesnek a csoportos toplistából.
- A **Jogosultságok** beállítás jelenleg nem nyit meg semmit, mert a komponensnek nincs résztvevői oldala; az admin oldalakat a fenti jogosultságok szabályozzák.
"""
)
