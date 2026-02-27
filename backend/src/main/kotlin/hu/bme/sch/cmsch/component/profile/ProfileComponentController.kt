package hu.bme.sch.cmsch.component.profile

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
@RequestMapping("/admin/control/component/profile")
@ConditionalOnBean(ProfileComponent::class)
class ProfileComponentController(
    adminMenuService: AdminMenuService,
    component: ProfileComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    ProfileComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_PROFILE,
    componentMenuName = "Profil beállítások",
    componentMenuIcon = "account_circle",
    insertComponentCategory = false,
    componentCategory = ApplicationComponent.FUNCTIONALITIES_CATEGORY,
    componentMenuPriority = 6,
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Profil** komponens a felhasználók személyes adatlapját kezeli. Itt láthatják a saját adataikat, eredményeiket, és itt érhetik el a személyre szabott funkciókat (pl. belépő QR-kód).

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a profil tartalmát:

- **Lap címe** – a böngésző címsorában megjelenő szöveg.
- **Menü neve** – a menüben látható név.
- **Jogosultságok** – mely szerepkörökkel érhető el az oldal.
- **Számlálók** – kör alakú folyamatjelzők (progressbarok) megjelenítése a teljesített feladatokról, riddle-ökről és begyűjtött tokenekről.
- **Profiladatok** – szabályozható, hogy mely adatok (név, szak, Neptun-kód, email-cím, csoport) látszódjanak a felhasználóról. Itt engedélyezhető a becenév módosítása is.
- **Személyes QR-kód** – egyedi azonosító kód (CMSCH ID) megjelenítése, amely beléptetéshez vagy fizetéshez használható.
- **Csoportvezetők adatai** – a felhasználó saját csoportának vezetői (pl. tankörseniorok) és azok elérhetőségeinek megjelenítése.
- **Profil kitöltöttsége** – figyelmeztető üzenetek, ha a felhasználó még nem teljesített minden kötelező feladatot (pl. nyilatkozatok kitöltése).
- **Token cél megjelenítése** – egyedi üzenet a szükséges pecsétek számáról (pl. tanköri jelenléthez).

## Funkciók

- **Becenév szerkesztése** – a felhasználók megadhatnak egy becenevet, amely a toplistákon és más nyilvános felületeken megjelenhet. A formátum reguláris kifejezéssel (regex) korlátozható.
- **Üzenődoboz** – globális vagy csoportszintű üzenetek megjelenítése a profil tetején.

## Használati tippek

- A **Számlálók** használatával a résztvevők azonnal látják a haladásukat a versenyben.
- A **Profil kitöltöttsége** funkció segít a szervezőknek, hogy minden résztvevő leadja a szükséges dokumentumokat vagy adatokat.
- Ha a rendezvényen QR-kódos beléptetés van, győződj meg róla, hogy az **Egyedi QR kód látható** opció be van kapcsolva.
"""
)
