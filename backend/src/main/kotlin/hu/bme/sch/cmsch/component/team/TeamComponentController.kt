package hu.bme.sch.cmsch.component.team

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
@RequestMapping("/admin/control/component/team")
@ConditionalOnBean(TeamComponent::class)
class TeamComponentController(
    adminMenuService: AdminMenuService,
    component: TeamComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    TeamComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_TEAM,
    "Csapatok",
    "Csapatok beállítások",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Csapatok** komponens a résztvevőket csapatokba szervezi. A csapatok valójában a **Csoportok** adminon kezelt csoportok, ezért a **Toplista** csoportos listája és több más komponens is ezekre épül. A résztvevők csapatot hozhatnak létre, jelentkezhetnek mások csapatába, a kapitány pedig a saját csapatát kezeli.

Fontos: a kapcsolók nagy része alapból ki van kapcsolva, ezért friss telepítésnél még a csapatlista sem látszik, és sem létrehozni, sem jelentkezni nem lehet.

## Beüzemelés

1. A **Csapatok beállítások** oldalon kapcsold be: **Csapatlista megjelenítése**, **Részletek megjelenítése**, **Csapatkészítés engedélyezve**, **Csatlakozás engedélyezve**, **Kilépés engedélyezve**. **Részletek megjelenítése** nélkül a csapat adatlapja hibát ad, **Csapatlista megjelenítése** nélkül üres a lista.
2. Névszabályok: **Csapatnév szabály (Regex)** és **Tiltott nevek** (vesszővel elválasztva). Egy csapatnév ezen kívül nem egyezhet meg kis-nagybetűre sem egy már létező csapat nevével.
3. Jogosultságok: **Csapatok menü jogosultságok** (a listát, az adatlapot és a jelentkezést is ez engedi), **Csapatom menü jogosultságai**, **Csapatkészítés menü jogosultságai**, **Admin oldal jogosultságai** (a kapitányi felület, alapból PRIVILEGED-től). Ezek pontos szerepkör-listák: a **Csapatkészítés** alapból csak a BASIC szerepkört tartalmazza, így adminisztrátor sem tud csapatot létrehozni.
4. Szerepkörök: **PRIVILEGED jog a csapatkészítőnek** – aki csapatot hoz létre, azonnal kapitány lesz; **ATTENDEE jog a csapattagoknak** – az elfogadott jelentkező ATTENDEE szerepkört kap.
5. **Alapból versenyzik** és **Alapból lehet jelentkezni** az új csapatok kezdőértéke a **Csoportok** admin **Játszik a csoport a versenyben?** és **Kiválasztható** kapcsolójához. Nem versenyző csapat csak a **Nem versenyző csapatok megjelenítése** bekapcsolásával látszik, kikapcsolt **Kiválasztható** csapatnál "Nem lehet csatlakozni", az adminon felvett csapatok pedig az **Admin által nevezett csapatok megjelenítése** kapcsolóval kerülnek a listába.
6. A csapatlista keresője a **Keresés engedélyezése**; **Rendezés név alapján** bekapcsolva névsorban, kikapcsolva meghatározatlan sorrendben jelenik meg a lista.

## A résztvevők útja

1. **Csapat létrehozása** – csak egy nevet kell megadni (a **Csapatkészítés felső szöveg** markdown szöveg jelenik meg felette). A létrehozó lesz a **Csapatkapitány**, és automatikusan kap egy "Csapatkapitány: ..." bemutatkozást.
2. **Bemutatkozás és logó** – a kapitány a **Csapatszerkesztés** oldalon írja le a csapatot (**Csapatszerkesztés engedélyezése**), és tölthet fel logót (**Csapatlogó feltöltés engedélyezése**; csak png/jpg/jpeg/gif). Az új bemutatkozás csak a **Bemutatkozások** adminon adott **Elfogadva** után látszik, addig a régi marad; **Elutasítva** és **Elutasítás oka** esetén a saját csapata látja az indoklást.
3. **Jelentkezés** – a csapat adatlapján a "Jelentkezés a csapatba" gomb kérést készít, amit a kapitány a **Csapatom** oldal "Jelentkezők" szakaszában fogad el vagy utasít el. A kérelmek a **Kérelmek** adminon is megjelennek.
4. **Tagság** – egy résztvevő egyszerre csak egy csapat tagja lehet. A kapitány a **Vezetőség átadása**, **Tagok eltávolítása** és **Jogosultságok kezelése** kapcsolókkal rúghat ki tagot vagy adhat neki kapitányi jogot, a saját oldalát pedig a **Feladatok megjelenítése**, **Űrlapok megjelenítése** és **Üzenet a vezetőknek** beállításokkal alakítja.
5. **Kilépés** – **Kilépés engedélyezve** esetén a tag kiléphet; a kapitány csak akkor, ha előbb átadta a vezetőséget.

## Admin oldalak (Csapatok menü)

| Oldal | Fontos mezők |
| --- | --- |
| **Kérelmek** | Felhasználó, Felhasználó ID-je, Csapat, Csapat ID-je – a beérkezett jelentkezések; elfogadni a kapitányi felületen lehet. |
| **Bemutatkozások** | Csapat, Bemutatkozás, Logó url, Elfogadva, Elutasítva, Elutasítás oka – itt hagyod jóvá a bemutatkozásokat (az **Elfogadva** felülírja az elutasítást). |
| **Címkék** | Csapatonkénti címkék (**Csoport**, **A címke ami megjelenik**, **Szín**, **Leírás**, **Listába megjelenik**) a csapatlista jelöléseihez. |

A csapat adatai – név, **Csoport borítóképe**, versenyző státusz, **Csoport létszáma** – a **Csoportok** adminon szerkeszthetők, a tagok hozzárendelése a felhasználók menüben történik.

A csapat adatlapján és a saját csapat oldalán megjelenő csempéket a **Csapat statisztika** csoport kapcsolói adják: **Tagok számának megjelenítése**, **Helyezés megjelenítése**, **Pontszám megjelenítése**, **QR Fight megjelenítése**, **Versenyeredmény megjelenítése**, **Riddle eredmény megjelenítése** (ezek a Toplista / QR Fight / Verseny / Riddle komponenst igénylik, a fejlécek külön állíthatók). A **Mérés gomb megjelenítése** a csapat versenyeredményeihez tesz gombot az adatlapra. A csapattagok listája a **Tagok publikussá tétele** nélkül csak a saját csapatnál látszik.

## Buktatók

- A csapat egyben csoport: a **Csoportok** adminon átnevezve a tagoknál tárolt csapatnév nem frissül, ezért a csapattagok listája eltűnik.
- A **Csapat adatlap** csoport **Pontszám megjelenítése** és **Részletes pontszám gomb** beállításait a felület nem használja; a pontszám csempét a **Csapat statisztika** **Pontszám megjelenítése** kapcsolója vezérli.
- A statisztikák a **Toplista** gyorsítótárából dolgoznak, ezért elavult állást mutathatnak.
- A **Csapatom**, **Csapatkészítés** és **Csapatom kezelése** oldalak rejtett menüpontok: a menüben nem jelennek meg, a csapatlistából és a csapat adatlapjáról nyithatók.
"""
)
