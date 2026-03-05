package hu.bme.sch.cmsch.component.impressum

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_IMPRESSUM
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/impressum")
@ConditionalOnBean(ImpressumComponent::class)
class ImpressumComponentController(
    adminMenuService: AdminMenuService,
    component: ImpressumComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    ImpressumComponent::class.java,
    component,
    PERMISSION_CONTROL_IMPRESSUM,
    componentMenuName = "Impresszum",
    menuService = menuService,
    componentMenuIcon = "alternate_email",
    insertComponentCategory = false,
    componentCategory = ApplicationComponent.CONTENT_CATEGORY,
    componentMenuPriority = 21,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
Az **Impresszum** komponens a weboldal készítőinek, a fejlesztőknek és a rendezvény szervezőinek bemutatására szolgál.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod az impresszum tartalmát:

- **Lap címe** – a böngésző címsorában megjelenő szöveg.
- **Jogosultságok** – mely szerepkörökkel érhető el az impresszum.
- **Fejlesztők** – a Kir-Dev fejlesztőcsapat tagjainak bemutatása, profilképek beállítása.
- **Rendezők** – a rendezvény főrendezőinek és további szervezőinek felsorolása (név, beosztás, elérhetőség).
- **Szöveges testreszabás** – egyedi üzenetek, köszönetnyilvánítások és technológiai leírások elhelyezése az oldalon.

## Funkciók

- **Szervezők kezelése** – egy speciális felületen (MULTIPLE_PEOPLE típus) adhatod meg a rendezők adatait, amelyek szépen formázva jelennek meg a frontenden.
- **Technológiai leírás** – bemutathatod a használt eszközöket és a projekt nyílt forráskódú jellegét.

## Használati tippek

- Használd az **Oldal tetején megjelenő szöveg** mezőt a rendezvény és a weboldal rövid összefoglalására.
- A **Főrendezők** részbe kerüljenek a hivatalos kapcsolattartók, akikhez a felhasználók a kérdéseikkel fordulhatnak.
"""
)
