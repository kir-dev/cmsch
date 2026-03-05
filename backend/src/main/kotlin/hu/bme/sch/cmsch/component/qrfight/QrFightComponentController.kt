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
    A **QR Fight** komponens egy interaktív, területfoglalós játékot valósít meg. A csapatok QR-kódok ("tornyok") beolvasásával szerezhetnek területeket és pontokat.
    
    ## Beállítások
    
    A **Komponens beállításai** menüpontban konfigurálhatod a játékot:
    
    - **Lap címe** – a böngésző címsorában megjelenő szöveg.
    - **Menü neve** – a menüben látható név.
    - **Jogosultságok** – mely szerepkörökkel érhető el a játék oldala.
    - **QR Fight engedélyezve** – a játék aktív állapotának kapcsolója.
    - **Napi limit** – szabályozható, hogy egy játékos hányszor olvashat be egy tornyot egy nap (visszaélések elkerülésére).
    - **InduláSch integráció** – speciális összeköttetés az InduláSch rendszerrel, ahol a toronyfoglalások állása külső kijelzőkön is megjeleníthető.
    
    ## QR Fight kezelése
    
    Két fő entitással dolgozhatsz:
    
    1. **Szintek (Levels)** – a játék különböző fázisai vagy területei.
    2. **Tornyok (Towers)** – a konkrét beolvasandó pontok. Megadható a nevük, a selectoruk és az értékük.
    
    ## Torony létrehozása / szerkesztése
    
    - **Név** – a torony megnevezése.
    - **Selector** – egyedi azonosító a toronyhoz.
    - **Pontszám** – mennyit ér a torony elfoglalása vagy megtartása.
    - **Látható** – megjelenjen-e a térképen/listában.
    
    ## Használati tippek
    
    - A **Napi torony beolvasás limit** (-1-re állítva kikapcsolható) segít abban, hogy a csapatok ne tudják folyamatos "spammeléssel" uralni a tornyokat.
    - Az **InduláSch integráció** segítségével valós időben mutathatod a rendezvény helyszínén, hogy éppen melyik csapat vezeti a harcot.
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
