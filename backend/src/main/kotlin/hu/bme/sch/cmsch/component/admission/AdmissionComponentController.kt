package hu.bme.sch.cmsch.component.admission

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/admission")
@ConditionalOnBean(AdmissionComponent::class)
class AdmissionComponentController(
    adminMenuService: AdminMenuService,
    component: AdmissionComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    AdmissionComponent::class.java,
    component,
        ControlPermissions.PERMISSION_CONTROL_ADMISSION,
        componentCategoryName = "Beléptetés",
        componentMenuName = "Jogosultságok",
        menuService = menuService,
        auditLogService = auditLogService,
        storageService = storageService,
        documentationMarkdown = """
    A **Beléptetés** (Admission) komponens a rendezvényre való bejutást és a helyszíni jegyellenőrzést kezeli. Lehetővé teszi a hozzáférési szintek finomhangolását csoportok, felhasználók és szerepkörök alapján.
    
    ## Beállítások
    
    A **Komponens beállításai** menüpontban konfigurálhatod a beléptetési szabályokat:
    
    - **Beléptetés működése** – szabályozható a belépési naplózás és az űrlapalapú beléptetés (csak elfogadott jelentkezéssel engedjen be).
    - **Csoportok hozzáférése** – csoportonként (pl. VIP, Szervezők, Fellépők) definiálható a hozzáférési szint.
    - **Felhasználók hozzáférése** – egyedi CMSCH ID alapján adható kiemelt hozzáférés.
    - **Szerepkörök hozzáférése** – globális szabályok a felhasználói szerepkörök (pl. STAFF, ADMIN) szerinti beléptetéshez.
    - **Tiltólista** – csoportok vagy egyének kizárása a beléptetésből.
    - **Jegyek** – BME Jegy integráció és a korábbi belépések számának megjelenítése a beolvasónál.
    
    ## Funkciók
    
    - **Hozzáférési szintek** – támogatott szintek: USER, VIP, PERFORMER, ORGANIZER, LEAD_ORGANIZER. A rendszer mindig a felhasználó számára elérhető legmagasabb szintet veszi alapul.
    - **Naplózás** – minden sikeres és sikertelen belépési kísérlet rögzíthető az utólagos ellenőrzéshez.
    """
    )
     {
    init {
        adminMenuService.registerEntry(AdmissionComponent::class.java.simpleName, AdminMenuEntry(
            "Beléptetés",
            "mobile_friendly",
            "/admin/admission/",
            1,
            StaffPermissions.PERMISSION_VALIDATE_ADMISSION
        ))
        adminMenuService.registerEntry(AdmissionComponent::class.java.simpleName, AdminMenuEntry(
            "Jegyellenőrzés",
            "mobile_friendly",
            "/admin/admission/ticket",
            6,
            StaffPermissions.PERMISSION_VALIDATE_ADMISSION
        ))
    }
}
