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
    documentationMarkdown = """
A **Bejelentkezés** komponens kezeli a felhasználók azonosítását, a különböző SSO (Single Sign-On) szolgáltatókkal való integrációt és az automatikus jogosultságkiosztást.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a belépési módokat:

- **AuthSCH** – a BME-s AuthSCH rendszeren keresztüli belépés konfigurációja (scope-ok, láthatóság).
- **Google SSO** – bejelentkezés Google-fiókkal. Itt adhatók meg azok az email-címek is, amelyek automatikusan ADMIN jogot kapnak.
- **Keycloak** – integráció egyedi Keycloak-szerverrel.
- **Emailes belépés** – hagyományos felhasználónév/jelszó alapú regisztráció és belépés, captcha-védelemmel és email-megerősítéssel.
- **Automatikus szerepkör (ROLE)** – meghatározható, hogy mely AuthSCH-s csoporttagságok (Pék-csoportok) alapján kapjanak a felhasználók STAFF vagy ADMIN jogosultságot.
- **Automatikus csoport (GROUP)** – meghatározható, hogy a felhasználók mely belső csoportba kerüljenek a külső csoporttagságaik alapján.

## Funkciók

- **Jogosultságkezelés** – a rendszer a belépéskor automatikusan frissíti a felhasználó jogosultságait a külső források (pl. AuthSCH, Keycloak) alapján.
- **Rate Limit** – védelem a brute-force támadások ellen (bejelentkezési kísérletek korlátozása).
- **Szöveges testreszabás** – egyedi üzenetek jeleníthetők meg a bejelentkezési oldalon.

## Használati tippek

- Az **AuthSCH** a javasolt belépési mód a BME-s közösség számára, mivel ez biztosítja a legpontosabb jogosultságkezelést.
- Ha csak egy bizonyos kört (pl. rendezők) akarsz beengedni, használd a **Pék-csoportok** szerinti szűrést az automatikus szerepkör-kiosztásnál.
- A **Shadow Ban** nem itt, hanem az egyes feladatkomponenseknél (pl. Riddle) állítható be.
"""
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
A **Jogviszonyok** komponens lehetővé teszi a felhasználók automatikus besorolását és jogosultságkiosztását az egyetemi jogviszonyuk alapján (AuthSCH BME_UNIT_SCOPE adatok segítségével).

## Beállítások

A **Komponens beállításai** menüpontban különböző szabályokat definiálhatsz:

- **BME-s felhasználók** – mindenki, aki rendelkezik érvényes BME-s jogviszonnyal.
- **Aktív hallgatók** – azok, akiknek jelenleg aktív hallgatói státuszuk van.
- **Elsőévesek** – a frissen felvett hallgatók.
- **Kar szerinti szűrés** – külön szabályok a VIK-es vagy VBK-s hallgatókra, illetve ezek elsőéveseire.

## Funkciók

Minden kategóriánál (BME, Aktív, Elsőéves, Karok) háromféle művelet állítható be:

1. **ATTENDEE szerepkör adása** – a felhasználó alapszintű résztvevői jogot kap.
2. **PRIVILEGED szerepkör adása** – a felhasználó emelt szintű résztvevői jogot kap.
3. **Csoportba áthelyezés** – a felhasználó automatikusan bekerül egy megadott belső csoportba (pl. "Gólyák").

## Működés

A szabályok érvényesülési sorrendben futnak le (1-7). Az automatikus jogkiosztás csak akkor történik meg, ha a felhasználó szerepköre alacsonyabb, mint STAFF. Ez biztosítja, hogy a rendezők és adminisztrátorok manuálisan beállított jogai ne vesszenek el.

## Használati tippek

- Akkor használd ezt a komponenst, ha a rendezvényed csak bizonyos egyetemi csoportoknak szól (pl. csak gólyáknak vagy csak VIK-eseknek).
- Fontos, hogy az **Auth beállításoknál** a `BME_UNIT_SCOPE` szerepeljen az igényelt scope-ok között, különben ez a komponens nem kap adatokat.
"""
)
