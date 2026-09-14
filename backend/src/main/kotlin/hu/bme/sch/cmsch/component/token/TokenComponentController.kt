package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_TOKEN
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/token")
@ConditionalOnBean(TokenComponent::class)
class TokenComponentController(
    adminMenuService: AdminMenuService,
    component: TokenComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    TokenComponent::class.java,
    component,
    PERMISSION_CONTROL_TOKEN,
    "Tokenek",
    "Tokenek testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Tokenek** (QR kódok) komponenssel QR kódokat generálhatsz, amiket a résztvevők beolvasnak: pecsétet gyűjtenek vele, pontot szereznek, és egy megadott darabszám elérését is kijelezheted. A szervezők a kódokat létrehozzák és kinyomtatják, a felhasználók a **QR kódok** oldalon (és a profiljukon) látják a haladásukat.

## Beüzemelés

1. **Jogosultságok**: pipáld be azokat a szerepköröket, akik megnyithatják a QR kódok oldalát – üresen csak adminok látják. Itt állítható a **Lap címe** és a **Menü neve** is.
2. **QR frontend url**: az oldal saját címe a `token=` paraméterrel a végén (pl. `.../token/scan?token=`). Ez kerül a QR kódokba, ezért mindenképp a saját instance címére írd át.
3. **Pecsét gyűjtés aktív** – ha a beolvasásokból teljesítést is akarsz számolni: **Szükséges pecsét**, valamint **Pecsét token típusa** (a token **Kategória** mezőjére szűr, `*` = bármelyik). A **'Nincs elég' üzenet**ben a `{}` helyére a hiányzó darabszám kerül, a **'Már van elég' üzenet** a teljesítéskor jelenik meg; a sáv a QR kódok oldal és a profil tetején látszik.
4. Tokenek felvétele: a **Tokenek** oldalon egyenként, vagy a **Token generálás** menüben sok egyszerre (**Nevek** soronként, **Kódok hossza**, **Típus**, **Ikon**, **Pont érték**, **Hány napig érhető el** – a kódokat a rendszer generálja).
5. Nyomtatás a **QR export** menüben: az összes tokenből zip készül. Az **Oldal URL-jének csatolása** teszi a QR kódot sima kamerával is olvashatóvá, mellette a hibajavító szint, a fájlformátum (png/jpg), a **Képek mérete** és az **Extra szöveg** (a QR alján) állítható.

## Egy token mezői

| Mező | Mit jelent |
| --- | --- |
| **Token neve** | a token megjelenő neve |
| **Token** | a QR kódba kerülő egyedi kód. A szerkesztő két QR-t mutat: a sűrűbb (benne az oldal URL-je) sima kamerával is olvasható, a másik csak az oldalon belüli olvasóval |
| **Beolvasható-e a token** | kikapcsolva a beolvasás "Rossz QR" hibát ad |
| **Kategória** | szöveges típus; a pecsétgyűjtés és a jelenléti ív erre szűr |
| **Pont** | a beolvasásért járó pont, a ranglista összesítésébe is beszámít |
| **Scannelhető innentől / eddig** | ezen az időablakon kívül "Ez a QR jelenleg nem aktív" |
| **Kijelzett szöveg**, **Kijelzett kép URL-je** | sikeres beolvasás után megjelenő markdown szöveg és kép |
| **Rarity** | besorolás; a ranglista e szerint tudja bontani a begyűjtött tokeneket |

A **Kiváltott esemény** (`capture:`, `history:`, `enslave:`, `treasure:`) és az **Aktív cél** csak a QR Fight komponensnél számít; ilyenkor a **Kategória** a QR Fight szintjét is kijelöli.

## Beolvasás

- A felhasználó a QR kódok oldalon a kamerás olvasóval vagy a QR-ben lévő URL megnyitásával olvas be; kijelentkezve előbb be kell jelentkeznie, a kód beküldése utána magától megtörténik.
- Egy token felhasználónként (az instance beállításától függően csoportonként) csak egyszer szerezhető meg; a második próbálkozás "Már beolvasott QR", pont nélkül. A kód csak akkor talál, ha a **Token** mezővel pontosan egyezik, a **Beolvasható-e a token** be van kapcsolva, és az időablakban vagyunk.

## Admin oldalak

- **Tokenek** – a tokenek listája, innen nyílik a **Generálás** és a **QR export** is; a lista CSV-ben importálható és exportálható.
- **Nyers beolvasás** – minden beolvasás (**Felhasználó név**, **Csoport név**, **Pont**, **Token**, **Beolvasva**). A sor törlésével a pecsét és a pont eltűnik (a felhasználó újra beolvashatja).
- **Token statisztika** – tokenenként a beolvasások száma, lenyitva a **Tulajdonos** és a **Beolvasva** időpont.
- **Felhasználói tokenek**, illetve a két **Csoportos tokenek** oldal („Tokenek csoportonként csoportosítva”, illetve „Felhasználói tokenek csoportonként” – utóbbi a csoportlétszámmal korrigált pontokat mutatja).
- **Pecsét statisztika** – csak a `default` kategóriájú tokeneket és csak a szervezői csoporton kívüli beolvasásokat számolja. A szervezői csoport nevét a Login komponens **Szervező csoport neve** beállítása adja; az oldalt a szervezői csoport tagjai is látják, nem csak adminok.
- **Jelenléti export** – tankörönként egy sor, a **Mentés** gombbal PDF jelenléti ív tölt le. A PDF a **Jelenléti ív címe**, **Jelenléti ív leírás**, **Jelenléti ív logója**, **Jelenléti footer szöveg** és a **Riport összefoglaló táblázat oszlopai** (stamp, attendance, riddle, achievement, time-between-scans) beállításokat használja, a jelenlétet pedig a **Szükséges pecsét** és a **Pecsét token típusa** alapján számolja.

## Figyelmeztetések

- A **Pecsét gyűjtés** beállításait csak akkor írd át, ha tudod, mit csinálsz: egy elrontott küszöb vagy típus miatt a résztvevők nem kapják meg a jelenlétet.
- A **Megszerző neve látszik**, az **Alapértelmezett ikon** és az **Alapértelmezett teszt ikon** beállításoknak jelenleg nincs hatásuk a felületre.
"""
)
