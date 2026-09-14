package hu.bme.sch.cmsch.component.bmejegy

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
import java.util.*

@Controller
@RequestMapping("/admin/control/component/bmejegy")
@ConditionalOnBean(BmejegyComponent::class)
class BmejegyComponentController(
    adminMenuService: AdminMenuService,
    component: BmejegyComponent,
    menuService: MenuService,
    private val legacyBmejegyTimer: Optional<LegacyBmejegyTimer>,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    BmejegyComponent::class.java,
    component,
        ControlPermissions.PERMISSION_CONTROL_BMEJEGY,
        "BME JEGY",
        "Jegyek testreszabása",
        auditLogService = auditLogService,
        menuService = menuService,
        storageService = storageService,
        documentationMarkdown = """
    A **BME JEGY** komponens a bmejegy.hu-s jegyvásárlásokat szinkronizálja a CMSch-ba: a megvásárolt jegyek a **Jegyek** menüben jelennek meg, a vásárló pedig automatikusan szerepkört vagy csoportot kaphat.

    ## Beállítások

    A **Jegyek testreszabása** menüpont **Működés** csoportjában:

    - **Szinkronizáció** – a bmejegy.hu-s automatikus letöltés főkapcsolója. Ha a jegyeket a Cheers rendszer küldi be API-n keresztül, ez a kapcsoló nem befolyásolja a beérkezést.
    - **Frissítési idő** – hány percenként nézze meg a bmejegy.hu-t (alapból 10).
    - **Buffer méret** – [ADVANCED] a letöltött válasz maximális mérete; csak akkor növeld, ha a szinkronizálás mérethirdő hibát ír a naplóba.
    - A **NEM TÁMOGATOTT** jelölésű mezőket csak indokolt esetben módosítsd: a **Keresés NEPTUN alapján** nincs implementálva, a **Szig. szám mező neve** viszont az egyeztetéshez kell.

    ## Fizetés utáni műveletek

    A **Fizetés utáni művelet #1**, **#2** és **#3** csoportokban három termékszabály adható meg:

    - **Termék neve** – a megvásárolt termék nevének részlete; üresen hagyva a szabály nem él.
    - **Adjon-e ATTENDEE ROLE-t**, **Adjon-e PRIVILEGED ROLE-t** – a vevő szerepkörét állítja. Ha mindkettő be van kapcsolva, a PRIVILEGED marad.
    - **Csoportba helyezés** – a vevő átkerül az itt megadott nevű csoportba (pontos csoportnév kell, üresen nem állít).

    ## Jegy és felhasználó összekapcsolása

    Szerepkör és csoport csak akkor jár, ha a jegyhez tartozik beazonosított felhasználó – ez a **Jegyek** táblában a **Beazonosított user ID-ja** mező. Az egyeztetés csak az **Űrlapok** menüben a **BME jegy integráció** kapcsolóval megjelölt űrlapok beküldéseiből dolgozik, és csak azokra a jegyekre fut le, ahol a mező még 0:

    | Jegyek honnan | Kapcsoló | Az űrlapmező neve |
    | --- | --- | --- |
    | bmejegy.hu letöltés | **Keresés SZIGSZÁM alapján** | **Szig. szám mező neve** |
    | Cheers feltöltés | **Keresés EMAIL alapján** | **Email mező neve** |

    ## Jegyek menü

    Itt láthatók és szerkeszthetők a szinkronizált sorok (a listában **Termék**, **Vásárló**, **Email**, **Státusz**, **QR**), a sorok importálhatók és exportálhatók. A szinkronizáló **csak új jegyet vesz fel** – a **Rendelés termék azonosító** alapján dönti el, hogy egy jegyet ismer-e már –, a meglévő sorokat nem írja felül, így a kézi javításaid megmaradnak.

    ## Figyelmeztetések

    - A szerepkör-állítás csak **STAFF** alatti felhasználókra hat. Ha csak az **Adjon-e ATTENDEE ROLE-t** van bekapcsolva, a magasabb jogú (PRIVILEGED) vevő is **ATTENDEE**-re csökken.
    - A **Beazonosított user ID-ja** kézi átírása maradandó: az automatikus egyeztetés csak a 0 értékű sorokat nézi.
    - A jegy **QR** mezője a voucher-kód. Bmejegy.hu-s letöltésnél a **Jegyellenőrzés** menü ezzel lépteti be a jegyest (ha a beléptetésnél a **BME Jegyesek beengedése** be van kapcsolva); Cheers-es feltöltésnél a kód a **Profil beállítások** menü **BMEJEGY kód küldése** kapcsolójával kerül a felhasználó QR-kódjába.
    """
    )
     {

    @GetMapping("/action/clean")
    fun actionClean(auth: Authentication?): String {
        if (auth?.getUserOrNull()?.role?.isAdmin == true) {
            legacyBmejegyTimer.ifPresent { it.clean() }
        }
        return "redirect:/admin/control/component/bmejegy"
    }

}
