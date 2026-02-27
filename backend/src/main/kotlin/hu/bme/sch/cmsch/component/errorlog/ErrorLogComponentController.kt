package hu.bme.sch.cmsch.component.errorlog

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_ERROR_LOG
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/errorlog")
@ConditionalOnBean(ErrorLogComponent::class)
class ErrorLogComponentController(
    adminMenuService: AdminMenuService,
    component: ErrorLogComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService,
) : ComponentApiBase(
    adminMenuService,
    ErrorLogComponent::class.java,
    component,
    PERMISSION_CONTROL_ERROR_LOG,
    "Kliens hibaüzenetek",
    "Kliens hibaüzenetek testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Kliens-hibaüzenetek** (Error Log) komponens a felhasználók böngészőjében vagy alkalmazásában fellépő technikai hibák automatikus naplózására szolgál.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a hibanaplózást:

- **Jogosultságok** – mely szerepkörök esetén küldjön a kliens hibaüzeneteket a szervernek.
- **Kliens hibajelentések fogadása** – a hibanaplózás főkapcsolója.

## Hibák kezelése

A **Kliens-hibaüzenetek** menüpont alatt:

- **Hibalista megtekintése** – láthatod az összes beérkezett hibajelentést, a hiba típusával, idejével és a felhasználó adataival.
- **Részletek** – megtekinthető a hiba pontos helye és a stack trace (ha elérhető).

## Használati tippek

- Ez a komponens elsősorban a fejlesztők és az üzemeltetők számára hasznos a hibák azonosításához.
- Ha túl sok hiba érkezik, átmenetileg kikapcsolható a fogadás, amíg a javítás el nem készül.
"""
)
