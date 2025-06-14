package hu.bme.sch.cmsch.component.race

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.app.MenuSettingItem
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import hu.bme.sch.cmsch.util.isAvailableForRole
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
    componentSettingService,
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

    val raceGroup by SettingGroup(fieldName = "Verseny")

    final var title by StringSettingRef("Sörmérés",
        fieldName = "Lap címe",
        description = "Ez jelenik meg a böngésző címsorában"
    )

    final override var menuDisplayName by StringSettingRef("Sörmérés", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override var minRole by MinRoleSettingRef(setOf(),
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val displayGroup by SettingGroup(fieldName = "Kijelzés")

    var visible by BooleanSettingRef(false, fieldName = "Látható", description = "Leküldésre kerüljön-e a toplista"
    )

    var extraCategoriesVisible by BooleanSettingRef(false, fieldName = "Extra kategóriák láthatóak",
        description = "Leküldésre kerüljön-e az extra kategóriás toplista"
    )

    var ascendingOrder by BooleanSettingRef(true, serverSideOnly = true, fieldName = "Növekvő sorrend",
        description = "A toplista elemei növekvő sorrendben vannak"
    )

    var defaultCategoryDescription by StringSettingRef(
        "", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Alapértelmezett kategória leírása",
    )

    var searchEnabled by BooleanSettingRef(false,
        fieldName = "Keresés elérhető",
        description = "Legyen-e kereső az oldal tetején"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val freestyleGroup by SettingGroup(fieldName = "Szabad kategória")

    var freestyleCategoryName by StringSettingRef("Funky mérés",
        fieldName = "Szabad kategória neve", description = "Ez lesz a szabad kategória neve, és a menü neve is"
    )

    var freestyleCategoryDescription by StringSettingRef("", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Szabad kategória leírása", description = "Ez lesz a szabad kategória leírása"
    )

    override fun getAdditionalMenus(role: RoleType): List<MenuSettingItem> {
        if (minRole.isAvailableForRole(role) || role.isAdmin) {
            return listOf(
                MenuSettingItem(
                    this.javaClass.simpleName + "@funky",
                    freestyleCategoryName, "/race/freestyle", 0,
                    visible = false, subMenu = false, external = false
                )
            )
        }
        return listOf()
    }

}
