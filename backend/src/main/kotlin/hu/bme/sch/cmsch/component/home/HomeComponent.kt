package hu.bme.sch.cmsch.component.home

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["home"],
    havingValue = "true",
    matchIfMissing = false
)
class HomeComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "home",
    "/home",
    "Kezdőlap",
    ControlPermissions.PERMISSION_CONTROL_HOME,
    listOf(),
    env
) {

    final override val allSettings by lazy {
        listOf(
            homeGroup,
            title, menuDisplayName, minRole,

            displayGroup,
            welcomeMessage,
            youtubeVideoIds,
            content,
            showEvents,

            newsEmbeddedComponentGroup,
            maxVisibleCount,
            showNews,
            showGalleryImages,
        )
    }

    val homeGroup = SettingGroup(component, "homeGroup", fieldName = "Kezdőlap")

    final val title = StringSettingRef(componentSettingService, component,
        "title", "Kezdőlap", fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = StringSettingRef(componentSettingService, component,
        "menuDisplayName", "Kezdőlap", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", MinRoleSettingRef.ALL_ROLES,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val displayGroup = SettingGroup(component, "displayGroup", fieldName = "Megjelenés",
        description = "A kezdőlap megjelenése")

    val welcomeMessage = StringSettingRef(componentSettingService, component,
        "welcomeMessage", "Üdvözlünk a {} portálon", fieldName = "Üdvözlő üzenet",
        description = "Ha üres akkor nincs, a {} pedig ki van cserélve az oldal nevére"
    )

    val youtubeVideoIds = StringSettingRef(componentSettingService, component,
        "youtubeVideoIds", "", fieldName = "Promó videó(k)",
        description = "Ha üres akkor nincs, csak youtube videó id-vel működik, ha többet szeretnél, vesszővel felsorolva tudod ezt megtenni" +
                " pl: '8PhToFtwKvY' (A '?controls=0' az opcionális)"
    )

    val content = StringSettingRef(componentSettingService, component,
        "content", "", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Megjelenő szöveg", description = "A kezdőlapon megjelenő szöveg. Ha üres akkor nincs ilyen."
    )

    val showEvents = BooleanSettingRef(componentSettingService, component,
        "showEvents", false, fieldName = "Események láthatóak",
        description = "Ha be van kapcsolva akkor az események láthatóak a kezdőlapon"
    )

    val showGalleryImages = BooleanSettingRef(componentSettingService, component,
        "showGalleryImages", false, fieldName = "Galéria képek láthatóak",
        description = "Megjelennek egy carousel-ben azok a képek a galériából, melyeknél be van kapcsolva, hogy a kezdőlapra kerülhetnek"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val newsEmbeddedComponentGroup = SettingGroup(component, "embeddedGroup", fieldName = "Hírek rész",
        description = "Csak akkor van hatása ha a news komponens be van kapcsolva"
    )

    val maxVisibleCount = NumberSettingRef(componentSettingService, component,
        "embeddedMaxVisibleCount", 3, serverSideOnly = true, strictConversion = false,
        fieldName = "Max megjelenő hír", description = "Ennyi hír jelenik meg a főoldali hirdetés komponensben"
    )

    val showNews = BooleanSettingRef(componentSettingService, component,
        "showNews", false, fieldName = "Hírek láthatóak",
        description = "Ha be van kapcsolva akkor a hírek láthatóak a kezdőlapon"
    )

}
