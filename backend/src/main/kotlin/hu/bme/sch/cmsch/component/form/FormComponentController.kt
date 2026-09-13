package hu.bme.sch.cmsch.component.form

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_FORM
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/form")
@ConditionalOnBean(FormComponent::class)
class FormComponentController(
    adminMenuService: AdminMenuService,
    component: FormComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    FormComponent::class.java,
    component,
    PERMISSION_CONTROL_FORM,
    "Űrlap",
    "Űrlapok testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
Az **Űrlapok** komponenssel adatbekérő íveket és jelentkezési felületeket készíthetsz. Egy űrlap a `/form/<Url>` címen érhető el, a válaszokat pedig a rendezői felületen dolgozod fel. Egy felhasználó – illetve **Csoport a birtokos** esetén egy csoport – egyszer küldhet be kitöltést, utána ugyanaz a sor szerkeszthető, nem jön létre új rekord.

## Rendezői menük

| Menü | Mire való |
| --- | --- |
| **Űrlapok** | űrlapok felvétele és szerkesztése; soronként **Kitöltés** (kézi kitöltés bárki nevében), és ha a Sheets komponens be van kapcsolva, **Sheets integráció** / **Sheets frissítése** |
| **Kitöltések** | a beérkezett kitöltések űrlaponként, itt történik az elfogadás és az elutasítás; soronként **Json Export** és **CSV Export** |
| **Szavazások** | a VOTE és SELECT mezők szavazatainak összesítése űrlaponként |
| **Űrlapok testreszabása** | a komponens beállításai |

## Beállítások

- **Jogosultságok** – ezekkel a rangokkal kerül be a komponens a frontend konfigurációjába (innen jönnek a státusz szövegek), ezért vedd fel mindenkit, aki űrlapot tölthet.
- **Nyelvi beállítások** – a felhasználónak megjelenő szövegek: **'Túl korán' szöveg**, **'Túl késő' szöveg**, **'Nem elérhető' szöveg**, **'Betelt' szöveg**, **'Nem található' szöveg**, **'Nincs leadott jelentkezés' szöveg**, **'Beadva' szöveg**, **'Elfogadva' szöveg**, **'Elutasítva' szöveg**, **'Csoport nem jó' szöveg**, valamint a visszadobott kitöltésnél megjelenő **Visszadobás üzenet fejléce**.

## Új űrlap létrehozása

1. **Űrlapok** menü, új elem. **Url**: nem ékezetes kisbetűk és kötőjel, ez lesz a cím. **Űrlap címe**: ez jelenik meg a lap tetején. **Menüben megjelenő neve**: csak akkor kell, ha menüből is nyitható lesz, és a **Menü beállítások** oldalon is engedélyezni kell a menüpontot – enélkül csak közvetlen linkkel érhető el.
2. **Kitöltendő űrlap**: a mezők szerkesztője (**Új mező**, illetve **Export**/**Import** JSON-hoz). Mezőnként: **Mező neve** (ez lesz az adat kulcsa és az export oszlopfeje), **Típus**, **Cimke**, **Kötelező kitölteni**, **Nem módosítható beadás után**, **Alapértelmezett érték**, **Értékek** (SELECT és választós rácsok), **Leírás**, **RegEx minta**, **Hibás tartalom üzenete**. Az `INJECT_` kezdetű típusok automatikusan a profilból töltődnek (név, Neptun, e-mail, csoport, profilkép stb.).
3. **Kitölthető-e** – fő kapcsoló, kikapcsolva mindenki a **'Nem elérhető' szöveg**-et kapja.
4. **Kitölthető innentől** / **Kitölthető eddig** – a kitöltési időszak; ha az **eddig** 0 marad, az űrlap azonnal lejárt.
5. **Maximum kitöltés**: ennyi nem elutasított kitöltés után **Betelt**. `-1` a korlátlan, a `0` viszont azonnal beteltet jelent.

## Ki töltheti ki

- **Minimum rang a megtekintéshez** / **Maximum rang a megtekintéshez**: ki nyithatja meg az űrlapot (adminok mindig).
- **Csapatra korlátozás**: pontos csoportnevek vesszővel elválasztva; üresen mindenki tölthet. Aki nem tartozik ezekbe a csoportokba, a **Csoport tagság miatt eltiltva üzenet**-et kapja.
- **Csoport a birtokos**: a kitöltés a csoporthoz tartozik, csoportonként egy kitöltéssel.
- **ATTENDEE jog automatikusan** / **PRIVILEGED jog automatikusan**: sikeres kitöltésért rangot ad, de csak felfelé (magasabb rangút nem ront le), és a felhasználónak újra be kell jelentkeznie.
- **Hírdetett**: a csapat oldalán megjelenik kitöltendő formként, jelezve, hogy kitöltötték-e.

## A kitöltések feldolgozása

A **Kitöltések** menüben űrlaponként látod a darabszámokat (**Limit**, **Beküldött**, **Fizetve**, **Visszautasított**, **Elfogadva**), az egyes sorokban pedig:

- **Fizetve** – elfogadott kitöltés, a felhasználó az **Elfogadás utáni üzenet**-et látja.
- **Elutasítva** + **Elutasítás indoka** – elutasítás. Ha az **Elutasítva** nincs bekapcsolva, csak visszadobás történik: a felhasználó látja az indokot a **Visszadobás üzenet fejléce** szöveggel, és javíthatja a kitöltést.
- **Adatok elfogadva** – lezárja a kitöltést, utána a felhasználó nem módosíthatja (a **Nem módosítható beadás után** mezők egyébként sem javíthatók).
- **Kitöltés** – a beküldött értékek JSON-ban; **Sorszám** – exportokhoz és a Google Sheets sorokhoz; **Beadás történet** – a beadások naplója; **Email** és **Beléptető token** – a kapcsolódó adatok.

## Automatikus működés

- **E-mail küldése a kitöltés után** + **E-mail sablon hivatkozása** (az E-mail komponensben felvett sablon **Hivatkozási név**-e): a levél az **E-mail mező neve** mező értékére megy, ha az üres, a felhasználó e-mail címére. Az **E-mail küldése csak egyszer** miatt szerkesztéskor nem megy ki új levél.
- **Beléptető token mező neve**: a megadott `INJECT_RANDOM_TOKEN` típusú mező értéke kerül a kitöltés **Beléptető token** mezőjébe – belépés nélküli, tokenes kitöltéshez. Ez a mező legyen **Nem módosítható beadás után**.
- **BME jegy integráció**: ezzel jelölöd ki, melyik űrlapot dolgozza fel a BME Jegy / Nova integráció (e-mail cím alapján, fizetés és adategyeztetés).
- Ha a Sheets komponens be van kapcsolva, minden új vagy módosított kitöltés bekerül a csatolt táblázatba, törléskor pedig kikerül onnan; a **Sheets frissítése** a teljes újraszinkronizáláshoz való.

## Figyelmeztetések

- A **Kitöltés** (kézi kitöltés) oldalon nem futnak le az ellenőrzések: sem a **Maximum kitöltés**, sem a meglévő kitöltés ellenőrzése, minden mentés új sort hoz létre.
- A **Szavazások** lista csak a **Kitölthető-e** szerint bekapcsolt, aktuális időablakban lévő űrlapokat mutatja, lezárt űrlap eredményét ott nem találod.
- Ha kézzel írsz JSON-t a **Kitöltendő űrlap** mezőbe, minden mezőhöz adj **RegEx minta**-t (a szerkesztő `.*`-ot ír), különben a beküldés "Érvénytelen kitöltés" hibát ad.
"""
)
