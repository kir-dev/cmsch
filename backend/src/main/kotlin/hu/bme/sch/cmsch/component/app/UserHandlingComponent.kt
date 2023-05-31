package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.ComponentSettingService
import hu.bme.sch.cmsch.component.MinRoleSettingProxy
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.AdminMenuCategory
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import jakarta.annotation.PostConstruct

@Service
class UserHandlingComponent(
    private val adminMenuService: AdminMenuService,
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "userHandling",
    "/",
    "Felhasználó kezelés",
    ControlPermissions.PERMISSION_CONTROL_APP,
    listOf(),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            minRole,
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "", minRoleToEdit = RoleType.SUPERUSER,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    @PostConstruct
    fun menuSetup() {
        adminMenuService.registerCategory(javaClass.simpleName, AdminMenuCategory("Felhasználó kezelés", this.menuPriority))
    }

}
