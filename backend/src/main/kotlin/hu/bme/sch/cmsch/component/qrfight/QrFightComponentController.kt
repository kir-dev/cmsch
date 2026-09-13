package hu.bme.sch.cmsch.component.qrfight

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.StorageService
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/qrFight")
@ConditionalOnBean(QrFightComponent::class)
class QrFightComponentController(
    adminMenuService: AdminMenuService,
    component: QrFightComponent,
    menuService: MenuService,
    private val qrFightService: QrFightService,
    private val auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    QrFightComponent::class.java,
    component,
        ControlPermissions.PERMISSION_CONTROL_QR_FIGHT,
        "QR Fight",
        "QR Fight beállítások",
        auditLogService = auditLogService,
        menuService = menuService,
        storageService = storageService,
        documentationMarkdown = """
    A **QR Fight** egy területfoglalós játék: a résztvevők QR kódokat olvasnak be, amivel szinteket teljesítenek, és tornyokat foglalnak el egymástól. A játék oldala a `/qr-fight` címen érhető el, a menüben a **Menü neve** szerinti néven.

    A játékhoz háromféle adat tartozik:

    | Hol | Mi |
    | --- | --- |
    | **Szintek** menü | a játék fázisai (kategóriák); ide tartoznak a tornyok és a hozzájuk beolvasandó tokenek |
    | **Tornyok** menü | a konkrét, QR-kóddal foglalható pontok |
    | **Tokenek** komponens | a kinyomtatható QR kódok; ezek beolvasásakor történik a foglalás |

    ## Beüzemelés menete

    1. A **QR Fight beállítások** oldalon kapcsold be a **QR Fight engedélyezve** kapcsolót, és állítsd át a **Napi torony beolvasás limit**-et (0-val nem lehet tornyot foglalni).
    2. **Szintek:** vegyél fel egy szintet. A **Kategória** legyen egyedi, erre hivatkoznak a tornyok és a tokenek is. A szint csak akkor jelenik meg a játékosoknak, ha a **Szint látható** és a **Szint elérhető** is be van kapcsolva.
    3. **Tornyok:** vedd fel a tornyokat. A **Kategória** egyezzen a szint kategóriájával, a **Selector név** pedig az legyen, amit a token akciója hivatkozik.
    4. **Tokenek:** készíts tokent a toronyhoz. A **Kategória** a szint kategóriája legyen, a **Kiváltott esemény** pedig `capture:<selector>` (foglalás, a torony elvehető), `history:<selector>` (csak naplózza a beolvasást) vagy `enslave:<selector>` (csak akkor sikerül, ha a torony még senkié). Az **Aktív cél** kell ahhoz, hogy a token beleszámítson a szint teljesítésébe.
    5. Nyomtasd ki és helyezd ki a tokenek QR kódjait; a játékosok a játék oldal **QR kód beolvasása** gombjáról érik el őket.

    ## Szintek

    - **Elérhető ekkortól / eddig** – időablak, azon kívül a szint nem elérhető.
    - **Min. token a teljesítéshez** – ennyi aktív célú token kell a teljesítéshez; 0 esetén a szint azonnal teljesítettnek számít.
    - **Előfeltétel** – egy másik szint kategóriája; amíg az nincs teljesítve, ez a szint „Zárt”, és a tokenjei sem olvashatók be.
    - **Leírás amég nem elérhető / ha elérhető / miután teljesítve lett** – a játékosoknak mutatott szöveg az adott állapotban (markdown).
    - **Extra szint** – külön fülön jelenik meg a játékosoknak. A **Treasure hunt szint** kincskereső szint, ahol a már megszerzett tokenek adják a következőkhöz a hintet; ez csak egyéni (nem csapatos) birtoklási módban működik, és a felületen jelenleg nincs rá fül.

    ## Tornyok

    - **Selector név** – ez az azonosító szerepel a token `capture:` / `history:` / `enslave:` akciójában; ha nem létezik ilyen torony, a beolvasás hibát ad.
    - **Lezárva**, **Foglalható ekkortól / eddig** – zárt vagy lejárt tornyot sem foglalni, sem naplózni nem lehet.
    - **Publikus leírás** mindenkinek látszik, a **Leírás a tulajdonosoknak** csak az éppen birtokló csapatnak.
    - **Idő logolása** – ha be van kapcsolva, a torony számolja a birtoklási időt; enélkül nem lesz helytartója.
    - **Totem** – csapatos módban az első foglalás után véglegesen az adott csapaté: sem a `capture:`, sem az `enslave:` nem veszi el többé, és az időzítő sem számol rá birtoklási időt.
    - **Tulajdonos ...**, **Helytartó**, **Birtoklás állása**, **Beolvasás log** – a rendszer tartja karban, kézzel ne írd.

    ## Automatikus működés

    - 10 percenként lefut a torony időzítő: minden **Idő logolása** kapcsolós, nem **Lezárva** és időben nyitott toronynál növeli az aktuális birtokos számlálóját. A **Helytartó** a legtöbb időt birtokló csapat lesz, a **Helytartás ennyi időegysége (perc)** pedig a birtoklás hossza percben – ezt a játékosok a torony „Helytartó” felirata alatt látják.
    - Ugyanekkor frissül az InduláSch kijelző is. Kézi futtatás: `/admin/control/component/qrFight/execute-towers` (csak sysadmin érheti el, a felületen nincs hozzá gomb).
    - A **Napi torony beolvasás limit** egy játékos egy tornyot 24 órán belül ennyiszer foglalhat el, **-1 = korlátlan**. Alapértéke 0, ami minden foglalást letilt. Csak a `capture:` akcióra érvényes, és csak csapatos birtoklási módban.

    ## InduláSch integráció

    Az **Indulásch torony** bekapcsolásával a szerver a **Torony selector**-ral kiválasztott torony állását kiírja a **Kioszk azonosító**-val megadott InduláSch kioszk szöveges widgetjére (fő szöveg: a helytartó neve, alszöveg: „Birtokos: ...”). Az **API Kulcs** is kell hozzá; ha bármelyik üres, a frissítés elmarad. Külső kijelzős lekérdezéshez nem ez, hanem az **API tokenek** beállítás való (`selector:token` párok vesszővel elválasztva): a `GET /api/qrfight/tower/<selector>?token=<token>` végpont adja vissza a torony nevét, birtokosát, helytartóját és a birtoklás hosszát.

    ## Amire figyelni kell

    - A tornyok beolvasása a szint állapotán is múlik: ha a token kategóriájához tartozó szint le van tiltva, lejárt vagy nincs megnyitva, a foglalás elutasításra kerül.
    - A játékosok oldalán a szintek „Fő szintek” és „Extra szintek” fülön, állapotjelvényzővel (Elérhető, Teljesítve, Zárt, Nem elérhető), a kategória legjobb csapataival, valamint a tornyokkal és totemekkel jelennek meg. Az **Oldal tetején megjelenő szöveg** markdown szöveget tesz az oldal tetejére.
    - Csapatos vagy egyéni birtoklás: ezt a szerver indulási beállítása dönti el (`hu.bme.sch.cmsch.startup.token-ownership-mode`, env: `OWNER_TOKEN`), a beállítási felületen nem állítható. Egyéni módban nincs napi limit, és a játékosok nem csapatként versenyeznek.
    """
    )
     {

    // FIXME: Add button
    @GetMapping("/execute-towers")
    fun forceExecuteTowers(auth: Authentication): String {
        if (auth.getUserOrNull()?.isSuperuser() != true) {
            return "redirect:/admin/control/component/qrFight/settings?error=filed-to-execute"
        }
        qrFightService.executeTowerTimer()
        return "redirect:/admin/control/component/qrFight/settings?status=executed"
    }

}
