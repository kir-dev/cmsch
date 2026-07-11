package hu.bme.sch.cmsch.component.support

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_SUPPORT
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/support")
@ConditionalOnBean(SupportComponent::class)
class SupportComponentController(
    adminMenuService: AdminMenuService,
    component: SupportComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    SupportComponent::class.java,
    component,
    PERMISSION_CONTROL_SUPPORT,
    "Ügyfélszolgálat",
    "Ügyfélszolgálat testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
Az **Ügyfélszolgálat** komponens egy beépített megkeresés-kezelő rendszer, amelyen keresztül a résztvevők kérdéseket, problémákat jelezhetnek a rendezőknek. A megkeresések a webes felületen indíthatók, emailen is érkezhetnek.

## Amit a résztvevők látnak

A résztvevők az oldalon listázhatják a saját megkereséseiket, új megkeresést indíthatnak, és megtekinthetik a felelős válaszait. Ha emailen érkezett a visszajelzés a megkeresésre, a levélben lévő linken keresztül bejelentkezés nélkül is visszatérhetnek a megkeresésükhöz.

## Megkeresések kezelése (felelős szemszögéből)

A **Megkeresés** menüpontban az összes beérkező megkeresés listázódik, a legutóbbi aktivitás szerint rendezve. Az állapot ikonja mutatja, hogy épp kire vár a lépés:

- [NEW] **Felelős válaszára vár** – az ügyfél üzenetet küldött
- 🔵 **Ügyfél válaszára vár** – a felelős válaszolt
- ✅ **Lezárva** – a megkeresés le van zárva

A **Megtekint / Válaszol** gombra kattintva megnyílik a részletes nézet, ahol:

- Láthatók az összes üzenetek és a megkeresést indító szerepköre
- **Igénylés** – magadhoz rendeled a megkeresést felelősként
- **Válasz küldése** – emailt küld az ügyfélnek, és az állapot „Ügyfél válaszára vár"-ra vált
- **Belső megjegyzés** – csak a rendezőknek látható feljegyzés; nem küld emailt, nem vált státuszt
- **Lezárás / Újranyitás** – lezárt megkeresés visszanyitható, nyitott lezárható

A megkeresésnél látható a várakozási idő (ó:pp), azaz mióta vár az ügyfél válaszra.

Az admin **Beállítások** oldalon (`/admin/control/settings`) megadhatod az alapértelmezett megjelenített nevedet, amellyel a rendszer automatikusan kitölti a felelős mezőt igényléskor és válaszoláskor.

## Megjelenés testreszabása

- **Oldal neve** – a frontend oldalon megjelenő főcím
- **Felső üzenet (lista oldal)** – Markdown szöveg a megkeresések listája felett
- **Gomb felirata** – az „Új megkeresés" gomb szövege
- **Admin megnevezése** – hogyan jelenik meg a felelős az ügyfélnek (pl. „rendező", „support")
- **Felső / alsó üzenet (új megkeresés)** – Markdown szöveg az új megkeresés form körül

## Korlátok és tiltólista

- **Max nyitott megkeresések** – egy felhasználó egyszerre legfeljebb ennyi nyitott megkeresést tarthat
- **Max válasz hossz** – üzenetek maximális karakterszáma
- **Max egymás utáni ügyfél válasz** – ennyi egymás utáni üzenet után admin válasz szükséges
- **Tiltott emailek / azonosítók** – vesszővel elválasztott lista; ezek nem küldhetnek üzenetet

## Email értesítők

Ha az **Email küldése felelős válaszkor** be van kapcsolva, a felelős válaszakor a rendszer emailt küld az ügyfélnek az **Válasz email sablon** mezőben megadott sablonnal, „RE: [tárgy]" emailtárggyal. A sablonban elérhető változók: `{{title}}`, `{{message}}`, `{{messageHtml}}` (sortörések `<br>`-ként, `*kövér*` → `<b>` elem), `{{solver}}`, `{{threadUrl}}`, `{{creationDate}}`, `{{lastAnswerDate}}`.

Ha a **Megkeresés visszaigazoló sablon** meg van adva és létezik, az új megkeresés megnyitásakor a rendszer automatikusan visszaigazoló emailt küld az ügyfélnek. Ugyanazok a változók érhetők el, mint a válasz sablonnál (a `{{solver}}` üres lesz).

> A `{{threadUrl}}` az Alkalmazás komponens `siteUrl` beállításából épül fel – ellenőrizd, hogy ott a helyes frontend URL van megadva.
> A `{{creationDate}}` és `{{lastAnswerDate}}` magyar formátumban jelenik meg (pl. „2024. január 15. 14:30").

## Beosztás alapú automatikus hozzárendelés

Ha az **Automatikus hozzárendelés beosztás alapján** be van kapcsolva, új megkeresés vagy ügyfél üzenet esetén a rendszer automatikusan hozzárendel egy ügyfélszolgálatost:

1. Megnézi, hogy az aktuális időpontban ki elérhető a beosztás szerint (**Ügyfélszolgálat beosztás** oldal).
2. Ha több elérhető ügyfélszolgálatos is van, egyet véletlenszerűen választ.
3. Ha a beosztásban senki sem elérhető, az **Alapértelmezett ügyfélszolgálatosok** listájából választ egyet véletlenszerűen.
4. Beállítja a megkeresésnél a felelős nevét, majd emailt küld a kiválasztott ügyfélszolgálatosnak a **Hozzárendelés email sablon** alapján.

A hozzárendelési email sablon változói: `{{title}}`, `{{message}}`, `{{messageHtml}}`, `{{userName}}` (ügyfél neve), `{{creationDate}}`, `{{lastAnswerDate}}`, `{{adminUrl}}` (admin oldal linkje a megkereséshez).

> Az `{{adminUrl}}` az Alkalmazás komponens `adminSiteUrl` beállításából épül fel.

## Bejövő email webhook

Ha a **Bejövő email webhook** be van kapcsolva, az emailen érkező megkeresések automatikusan megjelennek a rendszerben. A rendszer a tárgy és a valódi küldő email alapján eldönti, hogy meglévő megkereséshez fűzi-e a levelet, vagy újat nyit.

A webhook URL tartalmazza a titkos azonosítót: `POST /api/support/incoming-email/{titkos-azonosito}`. A titkos azonosítót a **Biztonság** szekcióban találod; alapértelmezetten véletlenszerű UUID kerül generálásra.

Szűrési lehetőségek:
- **Engedélyezett 'To' cím** – csak erre a célcímre érkező emailek kerülnek feldolgozásra
- **Engedélyezett 'Resent-From' cím** – csak erről a továbbítási címről érkező emailek engedélyezettek

Az SRS (Sender Rewriting Scheme) formátumú feladó címek automatikusan dekódolásra kerülnek az eredeti email cím visszanyeréséhez.

## Statisztikák

Az **Ügyfélszolgálat statisztikák** oldalon megtekinthető, hogy az egyes felelősök hány megkeresést zártak le, illetve ezek közül mennyi lett lezárva 8 órán belül.
"""
)
