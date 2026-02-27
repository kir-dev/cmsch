package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_RIDDLE
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/riddle")
@ConditionalOnBean(RiddleComponent::class)
class RiddleComponentController(
    adminMenuService: AdminMenuService,
    component: RiddleComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    RiddleComponent::class.java,
    component,
    PERMISSION_CONTROL_RIDDLE,
    "Riddleök",
    "Riddleök testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Riddle-ök** komponens logikai feladványok kezelését teszi lehetővé. A felhasználók képeket vagy szöveges nyomokat kapnak, amikre meg kell találniuk a helyes választ.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a játékmenetet:

- **Lap címe** – a böngésző címsorában megjelenő szöveg.
- **Menü neve** – a menüben látható név.
- **Jogosultságok** – mely szerepkörökkel érhető el a riddle-ök oldala.
- **Válaszok ellenőrzése** – beállítható a kis- és nagybetűk, ékezetek és szóközök figyelmen kívül hagyása a megoldásoknál.
- **Pontozás** – a hinttel megoldott riddle-ök pontértékének (%) szabályozása.
- **Átugrás funkció** – bizonyos számú megoldó után elérhetővé tehető a feladvány átugrása.
- **Moderálás** – egyedi tiltólisták (Ban / Shadow Ban) kezelése játékosokra vagy csoportokra.

## Riddle-ök kezelése

A feladványokat kategóriákba rendezve kezelheted:

1. **Riddle kategóriák** – hozz létre csoportokat (pl. "Kezdő", "Haladó", "Extra").
2. **Riddle-ök** – itt töltheted fel a képeket és adhatod meg a megoldásokat.

## Riddle létrehozása / szerkesztése

- **Cím** – a feladvány neve (az admin felületen).
- **Megoldás** – a helyes válasz.
- **Hint** – segítség, ha a felhasználó elakad (opcionális pontlevonással).
- **Kép** – a feladvány képe.
- **Pontszám** – a helyes megoldásért járó pontszám.
- **Sorszám** – a kategórián belüli sorrend meghatározásához.

## Használati tippek

- Az **ékezetek figyelmen kívül hagyása** és a **kis- és nagybetűk figyelmen kívül hagyása** segít, hogy a felhasználók ne akadjanak el elütések miatt.
- A **Shadow Ban** funkcióval anélkül zárhatsz ki gyanúsan gyors megoldókat, hogy tudnának róla (a válaszaikat a felület elfogadja, de nem kapnak érte pontot).
"""
)
