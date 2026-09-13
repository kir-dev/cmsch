package hu.bme.sch.cmsch.component.proto

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
@RequestMapping("/admin/control/component/proto")
@ConditionalOnBean(ProtoComponent::class)
class ProtoComponentController(
    adminMenuService: AdminMenuService,
    component: ProtoComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    ProtoComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_PROTO,
    "Prototípusok",
    "Prototípusok",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Prototípusok** (Proto) komponens egy „dummy” végpontgyűjtemény: a benne felvett sorokra az oldal egy előre megadott HTTP-választ ad vissza, bejelentkezés nélkül. A rendezvény lebonyolításában nincs szerepe, jellemzően külső rendszerek (olvasó, mobilapp, integráció) kipróbálásához és hibakereséséhez használjuk. Minden élesített oldalon alapértelmezés szerint be van kapcsolva, és nincs olyan beállítása, amivel a viselkedését érdemben szabályozni lehetne.

## Végpont létrehozása

1. **Prototípusok** menü → **Válaszok** → **Új Válasz**.
2. Töltsd ki a mezőket (lásd a táblázatot), kapcsold be az **Aktív** kapcsolót, és mentsd el.
3. A végpont máris működik, újraindítás nem kell: `GET /api/proto/<Útvonal>`, például **Útvonal** = `/health` esetén `GET /api/proto/health`.

| Mező | Jelentése |
| --- | --- |
| **Útvonal** | A végpont útvonala, kötelezően `/` jellel kezdődik. Pontos egyezéssel keresődik, regex és minta nem használható. |
| **Válasz** | A visszaadott válasz törzse, szó szerint, változóhelyettesítés nélkül. |
| **Mime type** | A válasz `Content-Type` fejléce, például `application/json`. |
| **HTTP code** | A válasz státuszkódja, például `200`. |
| **Aktív** | Kikapcsolt sor nem érhető el: a kérés 404-gyel tér vissza. |

## Fontos tudnivalók

- A végpont **bejelentkezés nélkül, bárki számára elérhető**, ezért soha ne kerüljön ide titok, jelszó vagy belső adat.
- Csak **GET** kérést szolgál ki; nem létező vagy kikapcsolt útvonalra 404-et ad.
- Ha több sor ugyanazzal az **Útvonal**-lal szerepel, nem meghatározott, melyik válaszol – ne hozz létre duplikátumot.
- A **Válaszok** tábla CSV importot/exportot és **Másolat készítése** műveletet is kínál.
- A komponens egyetlen beállítása a **Jogosultságok**, de mivel a komponensnek nincs nyilvános oldala és menüpontja, ez a végpontok elérését nem befolyásolja.
"""
)
