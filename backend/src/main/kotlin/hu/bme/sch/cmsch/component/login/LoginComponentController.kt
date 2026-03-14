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
"""
)
