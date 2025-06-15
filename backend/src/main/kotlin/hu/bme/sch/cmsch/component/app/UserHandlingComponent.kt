package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.AdminMenuCategory
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.setting.MinRoleSettingRef
import jakarta.annotation.PostConstruct
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class UserHandlingComponent(
    private val adminMenuService: AdminMenuService,
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "userHandling",
    "/",
    "Felhasználó kezelés",
    ControlPermissions.PERMISSION_CONTROL_APP,
    listOf(),
    env
) {

    final override val menuDisplayName = null

    final override var minRole by MinRoleSettingRef(setOf(), minRoleToEdit = RoleType.SUPERUSER,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal")

    @PostConstruct
    fun menuSetup() {
        adminMenuService.registerCategory(javaClass.simpleName,
            AdminMenuCategory("Felhasználó kezelés", this.menuPriority))
    }

}
