package hu.bme.sch.cmsch.component.race

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
    name = ["race"],
    havingValue = "true",
    matchIfMissing = false
)
class RaceComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "race",
    "/race",
    "Verseny",
    ControlPermissions.PERMISSION_CONTROL_RACE,
    listOf(
        RaceRecordEntity::class,
        RaceCategoryEntity::class,
        FreestyleRaceRecordEntity::class
    ),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            raceGroup,
            title, menuDisplayName, minRole,

            displayGroup,
            visible,
            extraCategoriesVisible,
            ascendingOrder,
            defaultCategoryDescription,
            searchEnabled,

            freestyleGroup,
            freestyleCategoryName,

        )
    }

    val raceGroup = SettingProxy(componentSettingService, component,
        "raceGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Verseny",
        description = ""
    )

    final val title = SettingProxy(componentSettingService, component,
        "title", "Sörmérés",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Sörmérés", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val displayGroup = SettingProxy(componentSettingService, component,
        "displayGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Kijelzés",
        description = ""
    )

    val visible = SettingProxy(componentSettingService, component,
        "visible", "false", type = SettingType.BOOLEAN,
        fieldName = "Látható",
        description = "Leküldésre kerüljön-e a toplista"
    )

    val extraCategoriesVisible = SettingProxy(componentSettingService, component,
        "extraCategoriesVisible", "false", type = SettingType.BOOLEAN,
        fieldName = "Extra kategóriák láthatóak",
        description = "Leküldésre kerüljön-e az extra kategóriás toplista"
    )

    val ascendingOrder = SettingProxy(componentSettingService, component,
        "ascendingOrder", "true", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Növekvő sorrend",
        description = "A toplista elemei növekvő sorrendben vannak"
    )

    val defaultCategoryDescription = SettingProxy(componentSettingService, component,
        "defaultCategoryDescription", "", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Alapértelmezett kategória leírása",
        description = ""
    )

    val searchEnabled = SettingProxy(componentSettingService, component,
        "searchEnabled", "false", type = SettingType.BOOLEAN,
        fieldName = "Keresés elérhető",
        description = "Legyen-e kereső az oldal tetején"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val freestyleGroup = SettingProxy(componentSettingService, component,
        "freestyleGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Szabad kategória",
        description = ""
    )

    val freestyleCategoryName = SettingProxy(componentSettingService, component,
        "freestyleCategoryName", "Funky mérés",
        fieldName = "Szabad kategória neve", description = "Ez lesz a szabad kategória neve, és a menü neve is"
    )

    val freestyleCategoryDescription = SettingProxy(componentSettingService, component,
        "freestyleCategoryDescription", "", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Szabad kategória leírása", description = "Ez lesz a szabad kategória leírása"
    )

    override fun getAdditionalMenus(role: RoleType): List<MenuSettingItem> {
        if (minRole.isAvailableForRole(role) || role.isAdmin) {
            return listOf(
                MenuSettingItem(
                    this.javaClass.simpleName + "@funky",
                    freestyleCategoryName.getValue(), "/race/freestyle", 0,
                    visible = false, subMenu = false, external = false
                )
            )
        }
        return listOf()
    }

}
