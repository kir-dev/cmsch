package hu.bme.sch.cmsch.component.leaderboard

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/leaderboard")
@ConditionalOnBean(LeaderBoardComponent::class)
class LeaderBoardComponentController(
    adminMenuService: AdminMenuService,
    component: LeaderBoardComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    LeaderBoardComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_LEADERBOARD,
    componentCategoryName = "Toplista",
    componentMenuName = "Toplista testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Toplista** komponens összesíti a felhasználók és csapatok pontszámait különböző forrásokból (feladatok, riddle-ök, tokenek, beadások), és rangsort állít fel belőlük.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a toplista működését:

- **Lap címe** – a böngésző címsorában megjelenő szöveg.
- **Menü neve** – a menüben látható név.
- **Jogosultságok** – mely szerepkörökkel érhető el a toplista.
- **Működés** – bekapcsolható a részletes (kategóriánkénti) toplista, illetve befagyasztható az állás a verseny végén.
- **Pontszámítás** – beállítható, hogy melyik forrás (feladatok, riddle-ök, tokenek, beadások) hány százalékos súllyal számítson bele a végeredménybe.
- **Kijelzés** – szabályozható a megjelenített sorok száma, a keresési lehetőség, és hogy a felhasználói vagy a csoportos toplista (vagy mindkettő) látható legyen-e.

## Funkciók

- **Befagyasztás** – a `Toplista befagyasztott` opcióval megállíthatod a pontok frissülését a felhasználók felé, így az utolsó pillanatig titokban tartható a végeredmény.
- **Szűrés** – a `Legalább ennyi ponttal` opcióval elrejtheted az inaktív résztvevőket a listáról.
- **Ritkaság szerinti tokenek** – külön kijelezhető, hogy a különböző ritkaságú tokenekből mennyit gyűjtöttek össze a résztvevők.

## Használati tippek

- A **Feladatok/Riddle-ök/stb. szorzó (%)** segítségével súlyozhatod a különböző tevékenységeket. Ha egy forrást 0-ra állítasz, az egyáltalán nem számít bele a toplistába.
- Használd a **Felső szöveg** mezőt a pontszámítás szabályainak ismertetésére vagy a befagyasztás tényének közlésére.
"""
)
