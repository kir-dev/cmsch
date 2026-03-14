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
A **Tokenek** (vagy QR kódok) komponens segítségével QR kódokat generálhatsz, amiket a felhasználók beolvashatnak. Ez használható pecsétgyűjtésre, jelenléti ívek készítésére vagy egyszerűen pontgyűjtésre.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a tokenek működését:

- **Lap címe** – a böngésző címsorában megjelenő szöveg.
- **Menü neve** – a menüben látható név.
- **Jogosultságok** – mely szerepkörökkel érhető el a kódok oldala.
- **QR frontend URL** – a generált QR kódok alapja.
- **Pecsét gyűjtés** – beállítható, hogy egy bizonyos mennyiségű token összegyűjtése után egyedi üzenet jelenjen meg (pl. tanköri jelenlét igazolása).
- **Stílus** – a megjelenítés testreszabása (ikonok, nevek láthatósága).
- **Jelenléti ív** – a generálható PDF-alapú jelenléti ív (riport) testreszabása.

## Tokenek kezelése

A **Tokenek** menüpont alatt kezelheted a kódokat:

- **Új token létrehozása** – új beolvasható kód rögzítése.
- **Szerkesztés / Törlés** – meglévő kódok módosítása.
- **Exportálás** – a tokenek listájának kimentése.

## Token létrehozása / szerkesztése

- **Cím** – a token neve (pl. "Kir-Dev stand").
- **Token** – az egyedi azonosító, ami a QR kódban szerepel.
- **Típus** – a token kategóriája (szűréshez és pecsétgyűjtéshez).
- **Pont** – mennyit ér a beolvasás.
- **Ikon** – egyedi ikon a tokenhez.
- **Látható** – megjelenjen-e a felhasználónak, ha már megszerezte.
"""
)
