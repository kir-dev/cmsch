package hu.bme.sch.cmsch.component.email

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_EMAILS
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/email")
@ConditionalOnBean(EmailComponent::class)
class EmailComponentController(
    adminMenuService: AdminMenuService,
    component: EmailComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    EmailComponent::class.java,
    component,
    PERMISSION_CONTROL_EMAILS,
    "Emailek",
    "Emailek testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
Az **Email** komponens a rendszerből kiküldött automatikus levelek (pl. regisztráció megerősítése, jelszóemlékeztető) kezeléséért felel. Támogatja a Mailgun és a belső Kir Mail szolgáltatókat.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a levélküldést:

- **Jogosultságok** – mely szerepkörökkel érhető el az oldal.
- **Email-szolgáltató** – választható a Mailgun vagy a Kir Mail.
- **Mailgun beállításai** – API-alapú küldés konfigurációja (domain, feladó neve).
- **Kir Mail beállításai** – belső (Kir-Dev-es) levélküldő rendszer használata tokenalapú hitelesítéssel.
- **Válasz email-cím** – megadható, hogy a felhasználók válaszai hova érkezzenek (reply-to).

## Funkciók

- **Sablonok kezelése** – az egyes eseményekhez (regisztráció, űrlap elfogadása) tartozó levélszövegek szerkesztése.
- **Automatikus küldés** – a rendszer eseményei (pl. sikeres regisztráció) automatikusan triggerelik a levélküldést.

## Használati tippek

- A **Kir Mail** a javasolt szolgáltató belső használatra, mivel ez kifejezetten a körös projektekhez lett optimalizálva.
- Ügyelj rá, hogy az **Email domainje** helyesen legyen beállítva, különben a levelek a spam mappába kerülhetnek.
- A **Válasz email-cím** legyen egy élő, figyelt postafiók, hogy a résztvevők segítséget kaphassanak, ha válaszolnak a rendszerüzenetre.
"""
)
