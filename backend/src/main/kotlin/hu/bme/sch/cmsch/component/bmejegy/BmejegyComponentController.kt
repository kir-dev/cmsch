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
    A **BME Jegy** komponens a bmejegy.hu rendszerével való integrációt valósítja meg. Lehetővé teszi a kifizetett jegyek automatikus szinkronizálását és a sikeres vásárlás utáni jogosultságkiosztást.
    
    ## Beállítások
    
    A **Komponens beállításai** menüpontban konfigurálhatod a szinkronizációt:
    
    - **Működés** – engedélyezhető az automatikus szinkronizáció és beállítható annak gyakorisága.
    - **Fizetés utáni műveletek** – meghatározható, hogy egy adott termék (pl. "Gólyatábor jegy") megvásárlása után a felhasználó milyen jogosultságot (ATTENDEE, PRIVILEGED) kapjon, vagy melyik belső csoportba kerüljön át.
    
    ## Funkciók
    
    - **Automatikus szinkronizáció** – a rendszer rendszeres időközönként lekéri a bmejegy.hu-ról a friss vásárlásokat.
    - **Voucher-kezelés** – a szinkronizált jegyek adatai (voucher-kód, típus) tárolódnak a rendszerben, és felhasználhatók beléptetésnél.
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
