package hu.bme.sch.cmsch.component.communities

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.component.app.MenuSettingItem
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
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
) : ComponentBase(
    "communities",
    "/community",
    "Körök",
    ControlPermissions.PERMISSION_CONTROL_COMMUNITIES,
    listOf(CommunityEntity::class, OrganizationEntity::class),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            communitiesGroup,
            title,
            menuDisplayName,
            minRole,

            resortGroup,
            titleResort,
            menuDisplayNameResort,
            minRoleResort
        )
    }

    val communitiesGroup = SettingProxy(componentSettingService, component,
        "communitiesGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Körök",
        description = ""
    )

    final val title = SettingProxy(componentSettingService, component,
        "title", "Körök",
        fieldName = "Körök lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Körök", serverSideOnly = true,
        fieldName = "Körök menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val resortGroup = SettingProxy(componentSettingService, component,
        "resortGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Reszortok",
        description = ""
    )

    final val titleResort = SettingProxy(componentSettingService, component,
        "titleResort", "Reszortok",
        fieldName = "Reszortok lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final val menuDisplayNameResort = SettingProxy(componentSettingService, component,
        "menuDisplayNameResort", "Reszortok", serverSideOnly = true,
        fieldName = "Reszortok menü neve", description = "Ez lesz a neve a menünek"
    )

    final val minRoleResort = MinRoleSettingProxy(componentSettingService, component,
        "minRoleResort", MinRoleSettingProxy.ALL_ROLES,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    override fun getAdditionalMenus(role: RoleType): List<MenuSettingItem> {
        val result = mutableListOf<MenuSettingItem>()
        if (minRoleResort.isAvailableForRole(role) || role.isAdmin) {
            result.add(MenuSettingItem(
                this.javaClass.simpleName + "@org",
                    menuDisplayNameResort.getValue(), "/organization", 0,
                visible = false, subMenu = false, external = false
            ))
        }
        return result
    }

}
