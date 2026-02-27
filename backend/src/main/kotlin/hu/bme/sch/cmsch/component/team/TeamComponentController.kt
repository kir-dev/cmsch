package hu.bme.sch.cmsch.component.team

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
@RequestMapping("/admin/control/component/team")
@ConditionalOnBean(TeamComponent::class)
class TeamComponentController(
    adminMenuService: AdminMenuService,
    component: TeamComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    TeamComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_TEAM,
    "Csapatok",
    "Csapatok beállítások",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Csapatok** komponens a felhasználók közösségbe szerveződését és a csapatok közötti versengést kezeli. Lehetővé teszi csapatok létrehozását, csatlakozást, csapatmenedzsmentet és a csapatok adatlapjának megtekintését.

## Beállítások

A komponens rendkívül részletesen testre szabható:

- **Csapatom** – a felhasználó saját csapatának információs oldala.
- **Csapatlista** – az összes regisztrált csapat felsorolása, rendezési és keresési lehetőségekkel.
- **Csapat létrehozása** – ki és hogyan hozhat létre új csapatot, név-ellenőrzési szabályok (regex, tiltólista).
- **Csapat admin felület** – a csapatvezetők eszköztára: tagok kezelése (eltávolítás, jogosultság átadása), feladatok és űrlapok követése.
- **Csapat adatlap** – mi látszódjon egy csapatról (tagok, pontszám, statisztikák).
- **Csapat statisztika** – integráció más komponensekkel (Leaderboard, QR Fight, Riddle, Race), hogy a csapat eredményei egy helyen látszódjanak.

## Csapatok kezelése

Az admin felületen kezelheted az összes csapatot, módosíthatod az adataikat vagy a tagságokat.

## Funkciók felhasználóknak

- **Csapat készítése** – névválasztással, leírással és logóval indíthatnak új közösséget.
- **Csatlakozás** – a felhasználók jelentkezhetnek meglévő csapatokba (ha a csapat nyitott).
- **Vezetői felület** – a saját csapatukon belül követhetik a verseny állását és a teendőket a vezetők.

## Használati tippek

- A **PRIVILEGED jog a csapatkészítőnek** opcióval automatikusan vezetői jogot kap, aki a csapatot létrehozza.
- A **Csatlakozás engedélyezve** kapcsolóval lezárhatod a csapatváltást a verseny egy adott pontján.
- Érdemes beállítani a **Csapatnév szabályt (regex)**, hogy elkerüld a nemkívánatos karaktereket a nevekben.
"""
)
