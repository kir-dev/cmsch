package hu.bme.sch.cmsch.component.race

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
    env
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
            freestyleCategoryDescription,
        )
    }

    val raceGroup = ControlGroup(component, "raceGroup", fieldName = "Verseny")

    final val title = StringSettingRef(componentSettingService, component,
        "title", "Sörmérés", fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = StringSettingRef(componentSettingService, component,
        "menuDisplayName", "Sörmérés", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val displayGroup = ControlGroup(component, "displayGroup", fieldName = "Kijelzés")

    val visible = BooleanSettingRef(componentSettingService, component,
        "visible", false, fieldName = "Látható", description = "Leküldésre kerüljön-e a toplista"
    )

    val extraCategoriesVisible = BooleanSettingRef(componentSettingService, component,
        "extraCategoriesVisible", false, fieldName = "Extra kategóriák láthatóak",
        description = "Leküldésre kerüljön-e az extra kategóriás toplista"
    )

    val ascendingOrder = BooleanSettingRef(componentSettingService, component,
        "ascendingOrder", true, serverSideOnly = true, fieldName = "Növekvő sorrend",
        description = "A toplista elemei növekvő sorrendben vannak"
    )

    val defaultCategoryDescription = StringSettingRef(componentSettingService, component,
        "defaultCategoryDescription", "",
        type = SettingType.LONG_TEXT_MARKDOWN, fieldName = "Alapértelmezett kategória leírása",
    )

    val searchEnabled = BooleanSettingRef(componentSettingService, component,
        "searchEnabled", false, fieldName = "Keresés elérhető", description = "Legyen-e kereső az oldal tetején"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val freestyleGroup = ControlGroup(component, "freestyleGroup", fieldName = "Szabad kategória")

    val freestyleCategoryName = StringSettingRef(componentSettingService, component,
        "freestyleCategoryName", "Funky mérés",
        fieldName = "Szabad kategória neve", description = "Ez lesz a szabad kategória neve, és a menü neve is"
    )

    val freestyleCategoryDescription = StringSettingRef(componentSettingService, component,
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
