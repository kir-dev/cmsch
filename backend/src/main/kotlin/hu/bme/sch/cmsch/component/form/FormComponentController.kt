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
Az **Űrlapok** (jelentkezések) komponens segítségével egyedi adatbekérő íveket, regisztrációs űrlapokat vagy jelentkezési felületeket hozhatsz létre.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod az általános üzeneteket:

- **Jogosultságok** – mely szerepkörökkel érhető el az űrlapok modul.
- **Nyelvi beállítások** – testreszabhatók az űrlapok különböző állapotaihoz (túl korán, lejárt, betelt, elfogadva, elutasítva stb.) tartozó visszajelzések.

## Űrlapok kezelése

A **Űrlapok** menüpont alatt:

1. **Űrlapok** – itt hozhatod létre magukat a kérdőíveket. Megadhatod a kitöltési időszakot, a férőhelyek számát és az űrlap felépítését (JSON-formátumban).
2. **Beadott űrlapok** – a felhasználók által beküldött válaszok listája. Itt tudod elfogadni, elutasítani vagy módosítani a jelentkezéseket.

## Űrlap létrehozása / szerkesztése

- **URL** – az űrlap egyedi címe (pl. `regisztracio`). A frontenden a `/form/{url}` címen érhető el.
- **Név** – az űrlap belső megnevezése.
- **Időintervallum** – mikortól meddig tölthető ki.
- **Férőhely** – hányan jelentkezhetnek összesen.
- **Csoportos jelentkezés** – ha be van kapcsolva, a csapatkapitány az egész csapat nevében töltheti ki.
- **Struktúra** – a kérdések és beviteli mezők definíciója.
"""
)
