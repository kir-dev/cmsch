package hu.bme.sch.cmsch.component.communities

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.component.app.MenuSettingItem
import hu.bme.sch.cmsch.model.RoleType
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["communities"],
    havingValue = "true",
    matchIfMissing = false
)
class CommunitiesComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase("communities", "/community", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            title, menuDisplayName,
            titleResort, menuDisplayNameResort,
            minRole
        )
    }

    final val title = SettingProxy(componentSettingService, component,
        "title", "Körök",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Körök", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final val titleResort = SettingProxy(componentSettingService, component,
        "title", "Reszortok",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final val menuDisplayNameResort = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Reszortok", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    override fun getAdditionalMenus(role: RoleType): List<MenuSettingItem> {
        val result = mutableListOf<MenuSettingItem>()
        if (minRole.isAvailableForRole(role) || role.isAdmin) {
            result.add(MenuSettingItem(
                this.javaClass.simpleName + "@org",
                    menuDisplayNameResort.getValue(), "/organization", 0,
                visible = false, subMenu = false, external = false
            ))
        }
        return result
    }

}
