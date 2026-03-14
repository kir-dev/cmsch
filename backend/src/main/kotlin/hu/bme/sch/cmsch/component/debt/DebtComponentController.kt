package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_DEBTS
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/debt")
@ConditionalOnBean(DebtComponent::class)
class DebtComponentController(
    adminMenuService: AdminMenuService,
    component: DebtComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    DebtComponent::class.java,
    component,
    PERMISSION_CONTROL_DEBTS,
    "Tartozások",
    "Tartozások testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Tartozások** (vagy Fogyasztás) komponens a rendezvény alatt vásárolt termékek és az értük fizetendő összegek nyilvántartására szolgál.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a modult:

- **Lap címe** – a böngésző címsorában megjelenő szöveg.
- **Menü neve** – a menüben látható név.
- **Jogosultságok** – mely szerepkörökkel érhető el a saját fogyasztás oldala.
- **Oldal tetején megjelenő szöveg** – egyedi tájékoztató a fizetés módjáról vagy a határidőkről. Ha üres, nem jelenik meg.

## Tartozások kezelése

Két fő részből áll a rendszer:

1. **Termékek** – itt veheted fel a megvásárolható tételeket (pl. "Póló", "Ételjegy", "Sör"). Megadható a név és az ár.
2. **Eladott termékek** – a konkrét vásárlások listája. Itt látszik, hogy ki mit vett, és hogy kifizette-e már (fizetve státusz).

## Termék létrehozása / szerkesztése

- **Név** – a termék megnevezése.
- **Ár** – a termék egységára.
- **Típus** – kategória (opcionális).
- **Látható** – elérhető-e a termék az eladáshoz.
"""
)
