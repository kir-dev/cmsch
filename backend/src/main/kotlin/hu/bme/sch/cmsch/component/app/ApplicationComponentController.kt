package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.service.AdminMenuCategory
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_APP
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/app")
@ConditionalOnBean(ApplicationComponent::class)
class ApplicationComponentController(
    adminMenuService: AdminMenuService,
    component: ApplicationComponent,
    menuService: MenuService,
    private val env: Environment,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    ApplicationComponent::class.java,
    component,
        PERMISSION_CONTROL_APP,
        "Admin",
        "Oldal beállítások",
        componentMenuIcon = "functions",
        menuService = menuService,
        insertComponentCategory = false,
        componentCategory = ApplicationComponent.FUNCTIONALITIES_CATEGORY,
        auditLogService = auditLogService,
        storageService = storageService,
        documentationMarkdown = """
    Az **Applikáció** (Application) komponens a rendszer alapvető globális beállításait tartalmazza. Ez a "főkapcsoló" és a központi konfigurációs felület.
    
    ## Beállítások
    
    A **Komponens beállításai** menüpontban állíthatod be a legfontosabb paramétereket:
    
    - **Oldal beállítások** – az oldal neve (pl. "Gólyatábor 2026") és a kezdőoldal (melyik komponens töltődjön be először).
    - **Admin oldal beállításai** – az admin felület URL-je, színe, neve és a szolgálati közlemények (MOTD).
    - **Figyelmeztető üzenet** – globális üzenet, amely minden felhasználónál megjelenik az oldal tetején (pl. "Karbantartás miatt az oldal lassabb lehet").
    
    ## Funkciók
    
    - **Szolgálati közlemény (MOTD)** – az adminisztrátorok számára megjelenő üzenet a belépés után.
    - **Dokumentumok** – hasznos linkek (pl. Google Drive, Sheets) gyűjteménye a szervezők számára.
    
    ## Használati tippek
    
    - Az **Admin oldal URL-je** beállítása kritikus a helyes működéshez, mert erre hivatkoznak a kiküldött emailek és a generált QR-kódok.
    - A **Dokumentumok** mezőben JSON-formátumban adhatsz meg linkeket, amelyek ikonokkal jelennek meg az admin dashboardon, segítve a szervezők munkáját.
    """
    )
     {

    override fun onUpdate() {
        adminMenuService.invalidateSiteContext()
    }

    override fun onInit() {
        adminMenuService.registerCategory(
            ApplicationComponent.CONTENT_CATEGORY,
            AdminMenuCategory("Tartalom",
                env.getProperty("hu.bme.sch.cmsch.${component.component}.content.priority")?.toIntOrNull() ?: 0)
        )
        adminMenuService.registerCategory(
            ApplicationComponent.STYLING_CATEGORY,
            AdminMenuCategory("Stílus",
                env.getProperty("hu.bme.sch.cmsch.${component.component}.style.priority")?.toIntOrNull() ?: 0)
        )
        adminMenuService.registerCategory(
            ApplicationComponent.DEVELOPER_CATEGORY,
            AdminMenuCategory("Fejlesztői",
                env.getProperty("hu.bme.sch.cmsch.${component.component}.dev.priority")?.toIntOrNull() ?: 0)
        )
        adminMenuService.registerCategory(
            ApplicationComponent.FUNCTIONALITIES_CATEGORY,
            AdminMenuCategory("Működés",
                env.getProperty("hu.bme.sch.cmsch.${component.component}.function.priority")?.toIntOrNull() ?: 0)
        )
        adminMenuService.registerCategory(
            ApplicationComponent.DATA_SOURCE_CATEGORY,
            AdminMenuCategory("Adat forrás",
                env.getProperty("hu.bme.sch.cmsch.${component.component}.data.priority")?.toIntOrNull() ?: 0)
        )
    }

}
