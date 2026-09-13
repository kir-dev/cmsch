package hu.bme.sch.cmsch.component.key

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
@RequestMapping("/admin/control/component/accessKeys")
@ConditionalOnBean(AccessKeyComponent::class)
class AccessKeyComponentController(
    adminMenuService: AdminMenuService,
    component: AccessKeyComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    AccessKeyComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_EVENTS,
    "Hozzáférési kulcsok",
    "Hozzáférések testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,

    storageService = storageService,
    documentationMarkdown = """
A **Hozzáférési kulcsok** komponenssel egyszer beváltható kódokat adhatsz ki: a kódot beváltó felhasználó csoportot és/vagy szerepkört kap. Minden kód egy sor a kulcsok listájában, és a beváltás ténye rákerül a sorra.

## Beüzemelés

1. A **Hozzáférések testreszabása** oldalon kapcsold be a **Lehet beváltani** opciót, és a **Jogosultságok** listában pipáld be azokat a szerepköröket, akik beválthatnak (pl. `ATTENDEE`). Ha egyet sem pipálsz be, a menüpontot és az oldalt az adminokon kívül senki nem látja.
2. A **Hozzáférési kulcsok** oldalon az **Új Hozzáférési kulcs** gombbal vedd fel a kódokat. Kódgenerátor nincs: a **Kulcs** mezőbe kézzel írt szöveget kell beírni, a **Cimke** pedig csak emlékeztető (kit vagy mit jelöl).
3. A **Menü neve** és a **Lap címe** adja a menüpont és az oldal nevét. A **Lapon megjelenő szöveg** (markdown) az oldal tetején jelenik meg, de csak akkor, ha a beváltás be van kapcsolva.

## Mit ad a kulcs?

A beváltás önmagában csak felhasználttá teszi a kulcsot; csoportot és szerepkört a soron lévő kapcsolók adnak:

| Mező a kulcs sorában | Hatás beváltáskor |
| --- | --- |
| **Csoport átállítása** + **Csoport neve** | a felhasználó csoportja erre a névre áll be. A névnek pontosan egyeznie kell egy létező csoporttal (a **Csoportok** menüben láthatók), különben a csoport nem változik, viszont a kód elhasználódik. |
| **Szerep átállítása** + **Szerepkör** | a felhasználó szerepe erre áll be (`BASIC`-tól `SUPERUSER`-ig). |

Beváltáskor a **Felhasználó ID-je**, a **Felhasználó neve** és a **Mikor használta fel** mezők kitöltődnek; a név és az idő csak napló, a **Felhasználó ID-je** dönti el, hogy a kód fel van-e használva. Ha ezt visszaírod 0-ra, a kód újra beváltható.

## Amire figyelni kell

- Egy **Kulcs** csak egyszer érvényes, és pontos egyezéssel keresődik: a beírt szöveg elejéről és végéről a szóköz levágódik, de a kis- és nagybetű számít.
- Ha az **Egy felhasználó többet is beválthat** ki van kapcsolva, aki már beváltott egy kódot, az semmilyen további kódot nem tud beváltani (ilyenkor a **Te már használtál fel hibaüzenet** jön).
- A **Szerepkör** valódi jogosultságot ad, akár `SUPERUSER`-ig, ezért a kulcs gyakorlatilag jelszó: csak annak add oda, akit fel akarsz jogosítani.
- A hibaüzenetek külön beállításokként átírhatók: **Hibás kód hibaüzenet**, **Kód be lett váltva hibaüzenet**, **Nem lett bejelentkezve hibaüzenet** (bejelentkezés nélkül nem lehet beváltani), **Te már használtál fel hibaüzenet**, **Kikapcsolt hibaüzenet**.
- A **Lehet beváltani** kikapcsolásakor a beváltó oldal figyelmeztetést mutat, beküldésre pedig a **Kikapcsolt hibaüzenet** szövege jön vissza.
- A kulcsok listája CSV-ben importálható és exportálható, egy sorról pedig a **Másolat készítése** gombbal készíthető új kulcs.
"""
)
