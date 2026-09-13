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
A **Lábléc** a felhasználói oldalak alján minden oldalon megjelenő sáv. A beállításai a **Stílus** kategória **Lábléc** menüpontjában találhatók, a színei (**Footer színe**, **Footer alsó sáv színe**, **A footerre alkalmazott filter**) viszont a **Stílus beállítások** menüpontban állíthatók, világos és sötét témához külön.

## Felépítés

- Bal oldalon a **Footer szöveg**, mellette a szervező logója a linkjeivel és a Kir-Dev logó.
- Felette a támogatói és partneri logósáv, legalul pedig egy fix, nem állítható sáv (Made with ♥ by Kir-Dev / Minden jog fenntartva, az aktuális évszámmal).

## Lábléc

- **Minimalisztikus lábléc** – elrejti a támogatói és partneri logósávot, valamint a szervező linkjeit (Weboldal, Facebook, Instagram); csak a logók, a **Footer szöveg** és az alsó sáv maradnak.
- **Esemény szervezőjének a logója** – feltölthető kép vagy URL; ha üres, nem jelenik meg kép. **Esemény szervezőjének alt szövege** – ha a kép nem tölt be, ez látszik.
- **Esemény szervezőjének oldala** – a logó melletti Weboldal link. **Facebook url**, **Instagram url** – üresen hagyva az adott ikon nem jelenik meg.
- **Footer szöveg** – a lábléc szöveges tartalma, Markdown formázással, több sorban is írható.
- **A kir-dev oldala**, **A kir-dev kapcsolat linkje** – a Kir-Dev logó melletti linkek, csak **Fejlesztő** (SUPERUSER) szerepkörrel szerkeszthetők.

## Támogatók

- **Sponsorok láthatóak** – enélkül a támogatói blokk egyáltalán nem jelenik meg.
- **Szponzorok fejléc** – a blokk fölé kerülő cím (alapértéke: Támogatóink).
- **Sponsor logók**, **Sponsor alt üzenetek**, **Sponsor weblapok** – vesszővel elválasztott listák, amelyek pozíció szerint párosulnak: az első logóhoz az első alt szöveg és az első weblap tartozik. Ha egy weblap üres, az adott logó nem lesz kattintható.

## Partnerek

- **BME VIK logó**, **BME logó**, **Schönherz logó**, **schdesign logó** – beépített logók, világos és sötét témához külön változatban.
- **Szponzorok fejléc** – a partneri blokk címe (a felirat tévesen ugyanaz, mint a támogatóknál, alapértéke: Partnereink), továbbá **Partner logók**, **Partner alt üzenetek**, **Partner weblapok** – a támogatókéhoz hasonló vesszős listák.
- A partneri blokk akkor jelenik meg, ha bármelyik beépített logó be van kapcsolva, vagy a **Partner logók** mező nem üres.

## Figyelmeztetések

- A **Sponsor logók** és **Partner logók** alapértéke `url1,url2`: írd felül valódi URL-ekkel vagy töröld, különben törött képek jelennek meg.
- A három lista mindig ugyanannyi és ugyanolyan sorrendű elemet tartalmazzon, különben elcsúsznak az alt szövegek és a linkek.
- A támogatói és partneri sáv a **Minimalisztikus lábléc** bekapcsolása esetén nem jelenik meg, hiába állítod be a többi beállítást.
- A Kir-Dev logó és linkjei az admin felületről nem kapcsolhatók ki, csak a frontend `VITE_HIDE_KIR_DEV_IN_FOOTER` beállításával.
"""
)
