package hu.bme.sch.cmsch.component.home

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
@RequestMapping("/admin/control/component/home")
@ConditionalOnBean(HomeComponent::class)
class HomeComponentController(
    adminMenuService: AdminMenuService,
    component: HomeComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    HomeComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_HOME,
    insertComponentCategory = false,
    componentCategory = ApplicationComponent.CONTENT_CATEGORY,
    componentMenuName = "Kezdőlap",
    componentMenuPriority = 5,
    componentMenuIcon = "home",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Kezdőlap** komponens a weboldal nyitóoldalát kezeli. Itt fogadhatod a látogatókat üdvözlő üzenettel, videókkal és a többi komponensből (hírek, események, galéria) kiemelt tartalmakkal.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a kezdőlap tartalmát:

- **Lap címe** – a böngésző címsorában megjelenő szöveg.
- **Menü neve** – a menüben látható név.
- **Jogosultságok** – mely szerepkörökkel érhető el az oldal.
- **Üdvözlő üzenet** – egy rövid köszöntés az oldal tetején.
- **Megjelenő szöveg** – tetszőleges Markdown-tartalom (leírás, linkek, tájékoztató).
- **Promó videó(k)** – beágyazott YouTube-videó(k) az oldal tetején.
- **Hírek rész** – a legfrissebb hírek automatikus megjelenítése.
- **Események rész** – az aktuális vagy közelgő programok kiemelése.
- **Galéria rész** – válogatott képek megjelenítése egy carouselben.

## Funkciók

- **Integráció** – a kezdőlap képes behúzni a tartalmat más aktív komponensekből, így egy központi információs felületként szolgál.
- **Markdown-támogatás** – a leírás mezőben gazdag formázást használhatsz.

## Használati tippek

- A **Promó videó** mezőbe csak a YouTube-videó azonosítóját (pl. `8PhToFtwKvY`) írd be.
- Használd a **Galéria képek láthatóak** opciót, hogy kiválaszd, mely fotók kerüljenek ki a nyitóoldalra.
- Törekedj a rövid, lényegre törő kezdőlapi szövegre, a részletes információkat pedig helyezd el statikus oldalakon vagy hírekben.
"""
)
