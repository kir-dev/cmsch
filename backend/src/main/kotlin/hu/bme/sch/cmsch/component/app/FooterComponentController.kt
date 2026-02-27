package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/footer")
@ConditionalOnBean(ApplicationComponent::class)
class FooterComponentController(
    adminMenuService: AdminMenuService,
    component: FooterComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    FooterComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_FOOTER,
    componentMenuName = "Lábléc",
    componentMenuIcon = "footprint",
    menuService = menuService,
    insertComponentCategory = false,
    componentCategory = ApplicationComponent.STYLING_CATEGORY,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Lábléc** (Footer) komponens az oldal alján megjelenő információkat kezeli.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a láblécet:

- **Lábléc** – alapvető adatok: szervező logója, linkje, közösségi média elérhetőségek és a copyright szöveg.
- **Támogatók** – a rendezvény szponzorainak logói és weboldalai.
- **Partnerek** – együttműködő partnerek (pl. BME, VIK, SCH) logóinak megjelenítése.

## Funkciók

- **Minimalisztikus lábléc** – ha be van kapcsolva, a lábléc kevesebb helyet foglal, és csak a legszükségesebb információkat mutatja.
- **Dinamikus partnerek** – a szponzorok és partnerek listája vesszővel elválasztott URL-ek megadásával egyszerűen bővíthető.

## Használati tippek

- A **Támogatók** és **Partnerek** szekciókban figyelj arra, hogy a logók (URL), az alternatív szövegek (Alt) és a weboldal linkek sorrendje és darabszáma megegyezzen.
- A **Hiba jelentése** link automatikusan a Kir-Dev kapcsolatfelvételi oldalára mutat, ezt érdemes meghagyni, hogy a felhasználók jelezhessék a technikai problémákat.
"""
)
