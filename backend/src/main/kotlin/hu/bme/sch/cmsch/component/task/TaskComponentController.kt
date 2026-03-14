package hu.bme.sch.cmsch.component.task

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_TASKS
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/task")
@ConditionalOnBean(TaskComponent::class)
class TaskComponentController(
    adminMenuService: AdminMenuService,
    component: TaskComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    TaskComponent::class.java,
    component,
    PERMISSION_CONTROL_TASKS,
    "Feladatok",
    "Feladat beállítások",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Feladatok** komponens segítségével különböző típusú feladványokat (szöveges, kép- vagy fájlfeltöltős) írhatsz ki a felhasználók vagy csapatok számára, amiket az adminisztrátorok értékelhetnek.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a feladatok működését:

- **Lap címe** – a böngésző címsorában megjelenő szöveg.
- **Menü neve** – a menüben látható név.
- **Jogosultságok** – mely szerepkörökkel érhető el a feladatok oldala.
- **Nyelvi beállítások** – a kötelező (pl. profil kitöltéséhez szükséges) és a normál feladatok csoportosítása és leírása.
- **Működés** – beállítható az újraküldés lehetősége, a pontszámok láthatósága és a megnyitások naplózása.
- **Beadások exportálása** – konfigurálható egy PDF-export, amely a beadott feladatokat összesíti.

## Feladatok kezelése

A feladatok kezelése több szinten történik:

1. **Feladat kategóriák** – csoportosítsd a feladatokat (pl. "Kreatív", "Sport", "Beugró").
2. **Feladatok** – itt hozhatod létre magukat a feladványokat.
3. **Beadások** – a beküldött megoldások listája, ahol az adminisztrátorok pontozhatnak és visszajelzést adhatnak.

## Feladat létrehozása / szerkesztése

- **Típus** – Szöveges (TEXT), Kép (IMAGE), Fájl (FILE) vagy csak leírás (ONLY_DESCRIPTION).
- **Kategória** – melyik csoportba tartozik.
- **Pontszám** – a feladatért járó maximális pontszám.
- **Határidők** – mikortól és meddig adható be a megoldás.
- **Megjelenés** – leírás Markdown formátumban, kép vagy fájl melléklet.
"""
)
