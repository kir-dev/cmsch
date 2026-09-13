package hu.bme.sch.cmsch.component.conference

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
@RequestMapping("/admin/control/component/conference")
@ConditionalOnBean(ConferenceComponent::class)
class ConferenceComponentController(
    adminMenuService: AdminMenuService,
    component: ConferenceComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    ConferenceComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_CONFERENCE,
    "Konferencia",
    "Konferencia testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Konferencia** komponens szolgálja ki a konferencia nyilvános oldalát: a korábbi konferenciák, regisztráció, mobilapp, nyereményjáték, promó videó, támogatók, rendezők, kiemelt előadások és előadáslista szekciók tartalma az admin listákból, a feliratok és linkek a **Konferencia testreszabása** oldal beállításaiból jönnek. Ütemezett feladat nincs: minden szöveg és lista kézzel szerkesztett, a **Látható** kapcsolók döntenek a megjelenésről.

## Admin oldalak

A **Konferencia** adminmenüben öt lista és a beállítások oldala (**Konferencia testreszabása**) található. A listákban felvétel, szerkesztés, törlés, duplikálás, keresés, valamint CSV import és export van.

| Menü | Mezők |
| --- | --- |
| **Korábbi konferenciák** | **Név**, **Prioritás**, **Képek URL-jei** (vesszővel elválasztva) |
| **Cégek** | **Név**, **Logó URL**, **URL**, **Kategória** (MAIN_SPONSOR / FEATURED_SPONSOR / SPONSOR / NO_ASSOCIATION), **Selector**, **Látható** |
| **Előadók** | **Név**, **Beosztás**, **Fotó URL**, **Cégének a seletora**, **Selector**, **Látható** |
| **Előadások** | **Cím**, **Slug**, **Kezdet ideje**, **Eddig tart**, **Terem** (IB028 / IB025 / OTHERS), **Nyelv** (HU / EN), **Leírás**, **Kérdések URL**, **Előadó a seletora**, **Selector**, **Látható**, **Szünet-e?** |
| **Rendezők** | **Név**, **Beosztás**, **Email cím**, **Profilkép url**, **Prioritás**, **Látható** |

## A listák a Selector mezőkkel kapcsolódnak össze

Az összetartozó sorokat nem legördülő listával, hanem szöveges **Selector** mezőkkel kell összekötni:

- **Előadók** → **Cégének a seletora** a **Cégek** egy sorának **Selector**ja.
- **Előadások** → **Előadó a seletora** az **Előadók** egy **Selector**ja.
- A **Kiemelt előadások selectorjai, vesszővel elválasztva** beállítás az **Előadások** **Selector**jait sorolja fel.

A **Selector** legyen egyedi, és pontosan egyezzen a rá hivatkozó mezővel: üres vagy elgépelt selectornál az adott előadó/cég helyén egyszerűen nem jelenik meg semmi, hibaüzenet nincs.

## Beállítások

| Beállítás | Szerepe |
| --- | --- |
| **previousConferences.sectionTitle mező**, **giveaway.sectionTitle mező**, **promoVideo.sectionTitle mező**, **sponsors.sectionTitle mező**, **featuredPresentation.sectionTitle mező** | Az egyes szekciók címei. |
| **registration.buttonText mező** | A regisztrációs gomb felirata. |
| **registration.cooltixEventId mező** | A gomb linkje, azaz a Cooltix esemény URL-je. Alapértéke `https://url.com/`, mindenképp írd át. |
| **mobileApp.description mező** | A mobilapp szekció szövege. |
| **mobileApp.androidUrl mező**, **mobileApp.iosUrl mező** | Az app letöltési linkjei. |
| **giveaway.description mező** | A nyereményjáték leírása. |
| **giveaway.pictureUrl mező** | A nyereményjáték képe (URL vagy feltöltött kép). |
| **giveaway.rules mező** | A játék szabályai, Markdown formázással. |
| **promoVideo.youtubeUrl mező** | Beágyazható YouTube URL (a megosztás iframe kódjából), nem sima videólink. |
| **promoVideo.description mező** | A videó alatti leírás. |
| **featuredPresentation.description mező** | A kiemelt előadás szekció leírása. |
| **Kiemelt előadások selectorjai, vesszővel elválasztva** | A kiemeltként megjelenő előadások **Selector**ja, vesszővel elválasztva. |

## Amire figyelni kell

- A **Korábbi konferenciáknál** nincs **Látható** kapcsoló, minden sor megjelenik – egy régi konferencia csak törléssel tüntethető el.
- A szekciók csak a **Látható** sorokat listázzák, kivéve a selectorral behivatkozottakat: a kiemelt előadás és az előadóhoz rendelt cég **Látható** nélkül is megjelenik.
- **Kezdet ideje** és **Eddig tart** szabad szöveges mező (nincs dátumválasztó és formátumellenőrzés), ezért írd egységes formátumban, pl. `2026-05-10 14:00:00`.
- A **featuredPresentation.sectionTitle mező** és a **featuredPresentation.description mező** alapértéke a promó videó szövege; ha nem írod át, az jelenik meg a kiemelt szekcióban.
- **Szünet-e?** bekapcsolva a sor szünetként kerül a programba, ilyenkor is a **Cím** jelenik meg.
"""
)
