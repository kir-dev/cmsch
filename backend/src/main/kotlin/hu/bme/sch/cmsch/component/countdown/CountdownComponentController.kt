package hu.bme.sch.cmsch.component.countdown

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_COUNTDOWN
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/countdown")
@ConditionalOnBean(CountdownComponent::class)
class CountdownComponentController(
    adminMenuService: AdminMenuService,
    component: CountdownComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    CountdownComponent::class.java,
    component,
    PERMISSION_CONTROL_COUNTDOWN,
    componentMenuName = "Visszaszámlálás",
    menuService = menuService,
    componentMenuIcon = "alarm",
    insertComponentCategory = false,
    componentCategory = ApplicationComponent.FUNCTIONALITIES_CATEGORY,
    componentMenuPriority = 20,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Visszaszámlálás** komponens egy látványos visszaszámláló oldalt jelenít meg a rendezvény kezdetéig. Képes korlátozni a weboldal többi részének elérését, amíg az idő le nem jár.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a visszaszámlálót:

- **Lap címe** – a böngésző címsorában megjelenő szöveg.
- **Bekapcsolva** – a visszaszámláló főkapcsolója.
- **Visszaszámlálás eddig** – az időpont, ameddig a rendszer számol.
- **Kinek legyen erőltetett** – meghatározható, hogy mely szerepkörök (pl. mindenki a STAFF alatt) csak ezt az oldalt lássák.
- **Megjelenés** – egyedi üzenet, háttérkép beállítása és az időzítő láthatósága.
- **Ne engedjen be az oldalra lejárat után** – ha be van kapcsolva, az oldal elérése a visszaszámlálás vége után is korlátozott marad.

## Funkciók

- **Zárolás** – a komponens képes "megfogni" a felhasználókat a kezdőlapon, megakadályozva, hogy a menü többi pontját elérjék a hivatalos megnyitó előtt.
- **Brand színek** – a `[[ szöveg ]]` formátum használatával a kiemelt részek az oldal brand színével jelennek meg.

## Használati tippek

- Ezt a komponenst tipikusan a rendezvény előtt pár nappal érdemes aktiválni, hogy felcsigázd a résztvevőkeket.
- Állíts be egy látványos **Háttérképet**, és használd az **Elmosott háttér** opciót a jobb olvashatóság érdekében.
- A szervezők (STAFF) számára ne állítsd be az erőltetett megjelenítést, hogy ők előre tudják tesztelni a többi menüpontot.
"""
)
