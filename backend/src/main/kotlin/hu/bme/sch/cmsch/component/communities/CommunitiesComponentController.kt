package hu.bme.sch.cmsch.component.communities

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
@RequestMapping("/admin/control/component/communities")
@ConditionalOnBean(CommunitiesComponent::class)
class CommunitiesComponentController(
    adminMenuService: AdminMenuService,
    component: CommunitiesComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    CommunitiesComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_COMMUNITIES,
    componentCategoryName = "Körök",
    componentMenuName = "Beállítások",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Körök** komponens a kollégiumi öntevékeny köröket és az őket összefogó reszortokat mutatja be a nyilvános oldalon, és egy Tinder nevű párosító játékot is tartalmaz.

- **Körök** (`/community`) – kereshető körlista, a kör adatlapja a `/community/{id}` címen.
- **Reszortok** (`/organization`) – ugyanez reszortokra, adatlap a `/organization/{id}` címen.
- **Tinder** (`/tinder`) – bejelentkezett felhasználók a kérdésekre adott válaszaik alapján húzogatják a köröket, a kedveltek a `/tinder/liked` oldalon gyűlnek.

## Beállítások

A **Beállítások** oldalon három csoport van: **Körök**, **Reszortok** és **Tinder**. A **Körök** és a **Reszortok** csoport ugyanazokat a mezőket tartalmazza a saját listájára (a reszortok leírás mezőjének neve is **Körök leírása**, és a reszortlista fölé kerül).

- **Körök lap címe** / **Reszortok lap címe** – böngésző címsor és a lista fejléce.
- **Körök menü neve** / **Reszortok menü neve** – a menüpont neve.
- **Jogosultságok** – mely szerepkörök érhetik el az oldalt. Ha egy szerepkör kimarad, az oldal helyett a „komponens nem elérhető” üzenet jön, ezért csak megfontoltan szűkítsd.
- **Körök leírása** – Markdown bevezető a lista tetején.
- **Keresés engedélyezése** – egyelőre nincs hatása, a keresőmező mindig megjelenik, és a kör neve, **Kulcsszavak** és **Érdeklődési körök** alapján szűr.

**Tinder** csoport:

- **Tinder engedélyezése** – enélkül a `/tinder` oldalak használhatatlanok, és a Tinder menüpont sem jelenik meg.
- **Jogosultságok** – csak azt dönti el, kinek látszik a Tinder menüpont, és alapból üres, azaz csak adminnak. A játék eléréséhez a felső **Jogosultságok** mezőben is szerepelnie kell a szerepkörnek.

## Admin oldalak

A **Körök** kategória menüpontjai:

| Menüpont | Mire való |
| --- | --- |
| **Körök** | Körök felvétele, szerkesztése, törlése, duplikálása, CSV import/export. Soronként innen nyílik a **Válaszok megtekintése** és a **Válaszok szerkesztése** (a kör Tinder válaszai) |
| **Reszortok** | Reszortok kezelése, duplikálás, import/export |
| **Tinder kérdések** | A párosítás kérdései, a **Válaszlehetőségek** vesszővel elválasztva |
| **Tinder válaszok** | A felhasználók válaszai. A körök saját válaszait ne itt keresd, az a **Körök** listáról érhető el |
| **Interakciók** | A húzások körönként csoportosítva, **Jobbra húzva** / **Balra húzva** darabszámmal |

## Tinder beállítási sorrend

1. **Tinder kérdések**: vedd fel a kérdéseket, mindegyikhez a **Válaszlehetőségek** vesszővel elválasztva.
2. A **Körök** listáról, a **Válaszok szerkesztése** művelettel add meg minden kör válaszait – csak a kérdéseknél felsorolt lehetőségeket fogadja el. Új kör mentésekor a rendszer üres válaszrekordot hoz létre, ezért a kör felvétele után térj vissza ide.
3. Kapcsold be a **Tinder engedélyezése** beállítást, és add meg a **Jogosultságok** mezőkben az érintett szerepköröket.
4. A felhasználók a **Tinder kérdések** oldalon válaszolnak, majd a Tinder oldalon húzogatnak; a döntés végleges, de a **Tinder kérdések** oldalon az **Interakciók törlése** gombbal a saját lista nullázható.

## A kör adatlapja

A **Kör neve**, a **Rövid leírás** (a listában és a Tinder kártyán látszik) és a **Teljes leírás** (Markdown, az adatlap törzsében) adja a szöveges tartalmat, a **Logó url** / **Sötét logó url** párból a témához illő jelenik meg. Az **Alapítva**, **E-mail cím**, **Tagok száma** és **Érdeklődési körök** az adatlap adatsávjába kerül, a **Website url**, **Facebook URL**, **Instagram URL** és **Jelentkezés URL-je** gombokat ad (az üres mezők egyszerűen kimaradnak). A **Képek URL-jei** és a **Videók URL-jei** vesszővel elválasztott lista, utóbbiba YouTube videó ID kerül, nem teljes URL.

## Mire figyelj

- **Reszort előbb**: a kör **Reszort ID-je** mezőjébe a reszort numerikus azonosítója kell. Ha nem létező ID szerepel benne (a 0 is ilyen), a mentés hibaüzenet nélkül elmarad, ezért előbb vedd fel a reszortot.
- **Látható**: csak a bekapcsolt körök és reszortok jelennek meg a nyilvános oldalakon. A Tinder ezt nem nézi, minden kört listáz.
- **Név rejtett**: csak az adatlapról tünteti el a nevet, a listában és a Tinder kártyán továbbra is látszik.
- Néhány mező egyelőre nem hat semmire: **Szín**, **SVG térképek id-je**, **Keresés engedélyezése**, valamint a Tinder kérdés **Látható** kapcsolója.
"""
)
