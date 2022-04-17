package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.ComponentSettingService
import hu.bme.sch.cmsch.component.MinRoleSettingProxy
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.AdminMenuCategory
import hu.bme.sch.cmsch.service.AdminMenuService
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class UserHandlingComponent(
    private val adminMenuService: AdminMenuService,
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase("userHandling", "/", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            minRole,
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "", minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    @PostConstruct
    fun menuSetup() {
        adminMenuService.registerCategory(javaClass.simpleName, AdminMenuCategory("Felhasználó kezelés", this.menuPriority))
    }

}
