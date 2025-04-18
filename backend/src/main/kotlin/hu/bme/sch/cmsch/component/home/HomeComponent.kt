package hu.bme.sch.cmsch.component.home

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.MinRoleSettingProxy
import hu.bme.sch.cmsch.setting.SettingProxy
import hu.bme.sch.cmsch.setting.SettingType
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

    val homeGroup = SettingProxy(componentSettingService, component,
        "homeGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Kezdőlap",
        description = ""
    )

    final val title = SettingProxy(componentSettingService, component,
        "title", "Kezdőlap",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Kezdőlap", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val displayGroup = SettingProxy(componentSettingService, component,
        "displayGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Megjelenés",
        description = "A kezdőlap megjelenése"
    )

    val welcomeMessage = SettingProxy(componentSettingService, component,
        "welcomeMessage", "Üdvözlünk a {} portálon", type = SettingType.TEXT,
        fieldName = "Üdvözlő üzenet", description = "Ha üres akkor nincs, a {} pedig ki van cserélve az oldal nevére"
    )

    val youtubeVideoIds = SettingProxy(componentSettingService, component,
        "youtubeVideoIds", "", type = SettingType.TEXT,
        fieldName = "Promó videó(k)", description = "Ha üres akkor nincs, csak youtube videó id-vel működik, ha többet szeretnél, vesszővel felsorolva tudod ezt megtenni" +
                " pl: '8PhToFtwKvY' (A '?controls=0' az opcionális)"
    )

    val content = SettingProxy(componentSettingService, component,
        "content", "",
        type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Megjelenő szöveg", description = "A kezdőlapon megjelenő szöveg. Ha üres akkor nincs ilyen."
    )

    val showEvents = SettingProxy(componentSettingService, component,
        "showEvents", "false", type = SettingType.BOOLEAN,
        fieldName = "Események láthatóak",
        description = "Ha be van kapcsolva akkor az események láthatóak a kezdőlapon"
    )

    val showGalleryImages = SettingProxy(componentSettingService, component,
        "showGalleryImages", "false", type = SettingType.BOOLEAN,
        fieldName = "Galéria képek láthatóak",
        description = "Megjelennek egy carousel-ben azok a képek a galériából, melyeknél be van kapcsolva, hogy a kezdőlapra kerülhetnek"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val newsEmbeddedComponentGroup = SettingProxy(componentSettingService, component,
        "embeddedGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Hírek rész",
        description = "Csak akkor van hatása ha a news komponens be van kapcsolva"
    )

    val maxVisibleCount = SettingProxy(componentSettingService, component,
        "embeddedMaxVisibleCount", "3", serverSideOnly = true, type = SettingType.NUMBER,
        fieldName = "Max megjelenő hír", description = "Ennyi hír jelenik meg a főoldali hirdetés komponensben"
    )

    val showNews = SettingProxy(componentSettingService, component,
        "showNews", "false", type = SettingType.BOOLEAN,
        fieldName = "Hírek láthatóak",
        description = "Ha be van kapcsolva akkor a hírek láthatóak a kezdőlapon"
    )

}
