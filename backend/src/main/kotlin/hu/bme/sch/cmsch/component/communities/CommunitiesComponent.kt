package hu.bme.sch.cmsch.component.communities

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.app.MenuSettingItem
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
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
    env
) {

    final override val allSettings by lazy {
        listOf(
            communitiesGroup,
            title,
            menuDisplayName,
            minRole,
            description,

            resortGroup,
            titleResort,
            menuDisplayNameResort,
            minRoleResort,
            descriptionResort
        )
    }

    val communitiesGroup = SettingGroup(component, "communitiesGroup", fieldName = "Körök")

    final val title = StringSettingRef(componentSettingService, component,
        "title", "Körök",
        fieldName = "Körök lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = StringSettingRef(componentSettingService, component,
        "menuDisplayName", "Körök", serverSideOnly = true,
        fieldName = "Körök menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", MinRoleSettingRef.ALL_ROLES,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    final val description = StringSettingRef(componentSettingService, component, "description",
        "A körök fogják össze az azonos érdeklődési körű hallgatókat. A körök a Schönherz Kollégiumban működnek.",
        fieldName = "Körök leírása", description = "Ez jelenik meg a körök lapon", type = SettingType.LONG_TEXT_MARKDOWN
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val resortGroup = SettingGroup(component, "resortGroup", fieldName = "Reszortok")

    final val titleResort = StringSettingRef(componentSettingService, component,
        "titleResort", "Reszortok",
        fieldName = "Reszortok lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final val menuDisplayNameResort = StringSettingRef(componentSettingService, component,
        "menuDisplayNameResort", "Reszortok", serverSideOnly = true,
        fieldName = "Reszortok menü neve", description = "Ez lesz a neve a menünek"
    )

    final val minRoleResort = MinRoleSettingRef(componentSettingService, component,
        "minRoleResort", MinRoleSettingRef.ALL_ROLES,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    final val descriptionResort = StringSettingRef(componentSettingService, component,
        "descriptionResort", "Az egyes reszortok a hasonló jellegű köröket összefogó szervezetek.",
        fieldName = "Körök leírása", description = "Ez jelenik meg a körök lapon", type = SettingType.LONG_TEXT_MARKDOWN
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
