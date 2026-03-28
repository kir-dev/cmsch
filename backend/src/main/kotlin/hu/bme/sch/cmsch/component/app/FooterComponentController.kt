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
# Stílus

A **Stílus** komponens segítségével testre szabhatod a weboldal megjelenését CSS-ismeretek nélkül.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a témákat:

- **Világos téma (Light Mode)** – színek (háttér, szöveg, brand), háttérképek és logók beállítása nappali módhoz.
- **Sötét téma (Dark Mode)** – színek és képek éjszakai módhoz. Szabályozható, hogy a rendszer automatikusan kövesse-e az eszköz beállításait, vagy kényszerítve legyen valamelyik mód.
- **Tipográfia** – az oldalon használt betűtípusok (fontok) és azok forrásának (CDN) megadása.

## Funkciók

- **Brand-szín** – egyetlen szín megadásával az egész oldal arculatát a rendezvényhez igazíthatod (gombok, linkek, kiemelések).
- **Reszponzív hátterek** – külön háttérképet állíthatsz be asztali és mobil nézethez.

# Manifest

A **Manifest** komponens a webalkalmazás (PWA - Progressive Web App) telepítési tulajdonságait szabályozza. Ez határozza meg, hogyan jelenik meg az oldal, ha a felhasználó hozzáadja a kezdőképernyőjéhez.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a manifest fájlt:

- **A manifest.json tartalma** – az alkalmazás neve, rövid neve, leírása, színei és megjelenítési módja (pl. `standalone`, `browser`).
- **Ikonok** – a különböző eszközökhöz és felbontásokhoz szükséges ikonok feltöltése.

## Funkciók

- **PWA támogatás** – a helyesen beállított manifest lehetővé teszi, hogy az oldal alkalmazásként viselkedjen (ikon a főképernyőn, nincs böngésző keret).

# Lábléc

A **Lábléc** (Footer) komponens az oldal alján megjelenő információkat kezeli.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a láblécet:

- **Lábléc** – alapvető adatok: szervező logója, linkje, közösségi média elérhetőségek és a copyright szöveg.
- **Támogatók** – a rendezvény szponzorainak logói és weboldalai.
- **Partnerek** – együttműködő partnerek (pl. BME, VIK, SCH) logóinak megjelenítése.

## Funkciók

- **Minimalisztikus lábléc** – ha be van kapcsolva, a lábléc kevesebb helyet foglal, és csak a legszükségesebb információkat mutatja.
- **Dinamikus partnerek** – a szponzorok és partnerek listája vesszővel elválasztott URL-ek megadásával egyszerűen bővíthető.
"""
)
