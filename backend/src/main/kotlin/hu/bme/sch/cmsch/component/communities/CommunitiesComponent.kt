package hu.bme.sch.cmsch.component.communities

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.app.MenuSettingItem
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import hu.bme.sch.cmsch.util.isAvailableForRole
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnBooleanProperty(value = ["hu.bme.sch.cmsch.component.load.communities"])
class CommunitiesComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "communities",
    "/community",
    "Körök",
    ControlPermissions.PERMISSION_CONTROL_COMMUNITIES,
    listOf(CommunityEntity::class, OrganizationEntity::class),
    env
) {

    val communitiesGroup by SettingGroup(fieldName = "Körök")

    final var title by StringSettingRef(defaultValue = "Körök",
        fieldName = "Körök lap címe", description = "Ez jelenik meg a böngésző címsorában")

    final override var menuDisplayName by StringSettingRef(defaultValue = "Körök", serverSideOnly = true,
        fieldName = "Körök menü neve", description = "Ez lesz a neve a menünek")

    final override var minRole by MinRoleSettingRef(defaultValue = MinRoleSettingRef.ALL_ROLES,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal")

    final var description by StringSettingRef(defaultValue = "A körök fogják össze az azonos érdeklődési körű hallgatókat. A körök a Schönherz Kollégiumban működnek.",
        fieldName = "Körök leírása", description = "Ez jelenik meg a körök lapon",
        type = SettingType.LONG_TEXT_MARKDOWN)

    final var seachEnabled by BooleanSettingRef(defaultValue = false, fieldName = "Keresés engedélyezése",
        description = "Engedélyezi a körök közötti keresést")

    /// -------------------------------------------------------------------------------------------------------------------

    val resortGroup by SettingGroup(fieldName = "Reszortok")

    final var titleResort by StringSettingRef(defaultValue = "Reszortok",
        fieldName = "Reszortok lap címe", description = "Ez jelenik meg a böngésző címsorában")

    final var menuDisplayNameResort by StringSettingRef(defaultValue = "Reszortok", serverSideOnly = true,
        fieldName = "Reszortok menü neve", description = "Ez lesz a neve a menünek")

    final var minRoleResort by MinRoleSettingRef(defaultValue = MinRoleSettingRef.ALL_ROLES,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal")

    final var descriptionResort by StringSettingRef(defaultValue = "Az egyes reszortok a hasonló jellegű köröket összefogó szervezetek.",
        fieldName = "Körök leírása", description = "Ez jelenik meg a körök lapon",
        type = SettingType.LONG_TEXT_MARKDOWN)

    final var seachEnabledResort by BooleanSettingRef(defaultValue = false, fieldName = "Keresés engedélyezése",
        description = "Engedélyezi a reszortok közötti keresést")

    ///-------------------------------------------------------------------------------------------------------------------

    val tinderGroup by SettingGroup(fieldName = "Tinder")

    final var tinderEnabled by BooleanSettingRef(defaultValue = false, fieldName = "Tinder engedélyezése",
        description = "Engedélyezi a körök és userek közötti tinder szerű párosítást")

    final var minRoleTinder by MinRoleSettingRef(defaultValue = setOf(), fieldName = "Jogosultságok",
        description = "Melyik roleokkal nyitható meg az oldal")


    override fun getAdditionalMenus(role: RoleType): List<MenuSettingItem> {
        val result = mutableListOf<MenuSettingItem>()
        if (minRoleResort.isAvailableForRole(role) || role.isAdmin) {
            result.add(MenuSettingItem(
                this.javaClass.simpleName + "@org",
                menuDisplayNameResort, "/organization", 0,
                visible = false, subMenu = false, external = false
            ))
        }
        if (tinderEnabled && (minRoleTinder.isAvailableForRole(role) || role.isAdmin)) {
            result.add(MenuSettingItem(
                this.javaClass.simpleName + "@tinder",
                "Tinder", "/tinder", 0,
                visible = false, subMenu = false, external = false
            ))
        }
        return result
    }

}
