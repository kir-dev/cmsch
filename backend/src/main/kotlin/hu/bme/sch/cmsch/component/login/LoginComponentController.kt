package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/login")
@ConditionalOnBean(LoginComponent::class)
class LoginComponentController(
    adminMenuService: AdminMenuService,
    component: LoginComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    LoginComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_PROFILE,
    componentMenuName = "Auth beállítások",
    componentMenuIcon = "login",
    insertComponentCategory = false,
    componentCategory = ApplicationComponent.FUNCTIONALITIES_CATEGORY,
    componentMenuPriority = 6,
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
)

@Controller
@RequestMapping("/admin/control/component/unit-scope")
@ConditionalOnBean(LoginComponent::class)
class UnitScopeComponentController(
    adminMenuService: AdminMenuService,
    component: UnitScopeComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    UnitScopeComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_PROFILE,
    componentMenuName = "Jogviszony beállítások",
    componentMenuIcon = "verified",
    insertComponentCategory = false,
    componentCategory = ApplicationComponent.DATA_SOURCE_CATEGORY,
    componentMenuPriority = 7,
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Jogviszony beállítások** oldalon az AuthSCH-tól kapott BME jogviszony adatok alapján a belépés pillanatában automatikusan ROLE-t adhatsz és csoportba sorolhatod a felhasználókat. A szabályok kizárólag AuthSCH-s belépésnél futnak le, Google / Keycloak / emailes belépésnél nem.

## Előkészítés

1. **Auth beállítások** oldal, **AuthSCH** csoport, **Oauth scopeok**: szerepeljen benne a `BME_UNIT_SCOPE`. Ha nincs benne, az AuthSCH nem küld jogviszony adatot, és itt semmi nem fog történni.
2. **Jogviszony beállítások** oldal, **Jogviszonyok** csoport: kapcsold be a **Jogok adása** opciót. Amíg ez ki van kapcsolva, az összes többi beállítás hatástalan.
3. A **Csoportba áthelyezés** mezőkben megadott csoportok létezzenek a **Csoportok** menüpontban, pontos névvel – ha nincs ilyen nevű csoport, a belépéskor csendben nem történik semmi.

## Kategóriák és érvényesülési sorrendjük

Egy felhasználó több kategóriába is beletartozhat, ezért a szabályok mindig ebben a sorrendben futnak le, és a későbbi felülírja a korábbi döntését:

| # | Csoport a beállításokban | Kire vonatkozik |
|---|---|---|
| 1 | **BME-s felhasználók** | bármilyen érvényes BME jogviszony |
| 2 | **Aktív hallgató felhasználók** | aktív hallgatói státusz |
| 3 | **Első éves felhasználók** | elsőéves hallgató |
| 4 | **VIK-es felhasználók** | VIK-es jogviszony |
| 5 | **VIK-es elsőéves felhasználók** | VIK-es elsőéves |
| 6 | **VBK-s felhasználók** | VBK-s jogviszony |
| 7 | **VBK-s elsőéves felhasználók** | VBK-s elsőéves |

Mind a hét kategóriában ugyanez a három beállítás érhető el:

- **ATTENDEE role adása** – ATTENDEE ("Résztvevő") ROLE-t ad.
- **PRIVILEGED role adása** – PRIVILEGED ("Kiemelt") ROLE-t ad.
- **Csoportba áthelyezés** – a felhasználó ebbe a csoportba kerül; üresen hagyva nem nyúl a csoporthoz.

## Jó tudni

- A szabály a belépéskor **felülírja** a ROLE-t (a meglévőt is), nem csak emeli: ha egy későbbi kategóriában csak az **ATTENDEE role adása** van bekapcsolva, az egy korábban adott PRIVILEGED-et is ATTENDEE-re vihet vissza. Csak azt a kategóriát állítsd be, amelynek a ROLE-ját mindenképp adni akarod.
- Rendező (STAFF) és annál magasabb ROLE-t a rendszer nem módosít.
- Csoportba áthelyezés csak akkor történik meg, ha a felhasználónak még nincs csoportja, vagy a jelenlegi csoportja **Elhagyható** (Csoportok menüpont).
- A módosítások a következő belépésnél érvényesülnek: a már belépett felhasználók megtartják a jelenlegi ROLE-jukat és csoportjukat.
- Ha a jogviszony szabályai nem sorolták be a felhasználót, utána az **Auth beállítások** oldal **Automatikus GROUP** csoportjában beállított **Fallback csoport neve** érvényesül.
"""
)
