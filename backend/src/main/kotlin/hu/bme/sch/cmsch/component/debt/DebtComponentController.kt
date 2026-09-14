package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_DEBTS
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/debt")
@ConditionalOnBean(DebtComponent::class)
class DebtComponentController(
    adminMenuService: AdminMenuService,
    component: DebtComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    DebtComponent::class.java,
    component,
    PERMISSION_CONTROL_DEBTS,
    "Tartozások",
    "Tartozások testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Tartozások** komponens a rendezvény alatti vásárlások nyilvántartása: ki mit vett, mennyiért (JMF), fizetett-e már, és melyik csoport felel érte. A vásárlók a **Fogyasztás** menüpontban látják a saját tételeiket.

## Beállítások

- **Oldal tetején megjelenő szöveg** – markdown szöveg a vásárlói oldal tetején (fizetés módja, határidő). Ha üres, nem jelenik meg.
- **Lap címe** és **Menü neve** alapértéke „Fogyasztás", miközben az admin menüben minden **Tartozások** néven szerepel.

## Beüzemelés sorrendje

1. A **Termékek** oldalon vedd fel a terméket, és állítsd **Elérhető**-re, amit árulni fogtok.
2. Árusításkor a sor végén az **Árusít** gomb nyitja a QR-fizetés oldalt: a vevő profil-QR kódját olvassa be (kézzel a **Neptun-kód** is megadható), és megerősítés után azonnal létrejön a tranzakció.
3. A vevő a **Fogyasztás** oldalon és a **Saját tartozásaim** oldalon követi, mi tartozik hozzá. A tartozás mindig a vevő csoportjához kerül.
4. A csoport bármely tagja a **Csoportom tartozásai** oldalon a **Fizetve** gombbal jelölheti, hogy átvette a pénzt – onnantól a **Felelős neve** ő lesz, és neki kell elszámolnia a gazdaságissal.
5. A **Tranzakciók** oldalon (és a csoportosított listák szerkesztésénél) az **Átadva**, **Fizetve**, **Lezárva** jelölők kézzel is átállíthatók; a **Napló** mező naplózza a változtatásokat.

## Termék mezői

| Mező | Mit jelent |
| --- | --- |
| **Név**, **Ár** | A termék neve és egységára JMF-ben. |
| **Típus** | MERCH / FOOD / OTHER. Csak ennek alapján kerül fel a termék az **Étel árusítás** vagy **Merch árusítás** listára; az OTHER típus csak a **Termék árusítás** listán jelenik meg. |
| **Elérhető** | Csak az elérhető terméket lehet eladni, ezt ellenőrzi az árusítás. |
| **Látható** | Jelenleg semmilyen felületet nem szabályoz. |
| **Termék leírása**, **Kép a termékről**, **Material Ikon** | Megjelenítéshez használt adatok. |

## Admin oldalak

| Oldal | Mit tudsz itt |
| --- | --- |
| **Termékek** | A vásárolható termékek kezelése, importtal és exporttal. |
| **Termék árusítás**, **Étel árusítás**, **Merch árusítás** | Árusító listák (mind / FOOD / MERCH típus), innen nyílik a QR-fizetés. |
| **Tranzakciók** | Az összes eladás. Új tranzakció nem hozható létre, a vevő/eladó/termék adatai nem szerkeszthetők, csak a jelölők. |
| **Eladott termékek** | Eladott darabszám termékenként. |
| **Saját tartozásaim** | Mindenki a saját tételeit látja. |
| **Csoportom tartozásai** | A saját csoportod tételei; csak csoporttagoknak jelenik meg. |
| **Csoportok tartozásai** | Csoportonkénti összesítés: **Forgalom [JMF]**, **Fizetetlen [JMF]**, **Lezáratlan [JMF]**. |
| **Felhasználó tartozásai** | Felhasználónkénti összesítés ugyanígy. |

## Jogosultságok és buktatók

- Árusításhoz **Bármilyen típusú termék eladása**, **Étel típusú termék eladása** vagy **Merch típusú termék eladása** jogosultság kell; az összesítésekhez **Összes tartozás megtekintése**, **Tartozások szerkesztése**, **Eladott termékek statisztikájának megtekintése**. Jogosultságot a **Felhasználó kezelés** alatt, a **Jogkörök** vagy a **Felhasználók** oldalon adhatsz.
- A vevőnek csoportban kell lennie, különben nem jön létre a vásárlás.
- A tranzakció a vásárlás pillanatában rögzíti a termék nevét és árát, az utólagos árváltozás a már eladott tételeket nem érinti.
- A **Fizetve** gomb a **Csoportom tartozásai** oldalon nem vonható vissza (már fizetett tételre nem csinál semmit), a **Tranzakciók** oldalon viszont a jelölő átállítható.
"""
)
