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
    componentSettingService,
    "home",
    "/home",
    "Kezdőlap",
    ControlPermissions.PERMISSION_CONTROL_HOME,
    listOf(),
    env
) {

    val homeGroup by SettingGroup(fieldName = "Kezdőlap")

    final var title by StringSettingRef("Kezdőlap",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában")

    final override var menuDisplayName by StringSettingRef("Kezdőlap", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek")

    final override var minRole by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal")

    /// -------------------------------------------------------------------------------------------------------------------

    val displayGroup by SettingGroup(fieldName = "Megjelenés", description = "A kezdőlap megjelenése")

    var welcomeMessage by StringSettingRef("Üdvözlünk a {} portálon", fieldName = "Üdvözlő üzenet",
        description = "Ha üres akkor nincs, a {} pedig ki van cserélve az oldal nevére")

    var youtubeVideoIds by StringSettingRef(fieldName = "Promó videó(k)",
        description = "Ha üres akkor nincs, csak youtube videó id-vel működik, ha többet szeretnél, vesszővel felsorolva tudod ezt megtenni pl: '8PhToFtwKvY' (A '?controls=0' az opcionális)")

    var content by StringSettingRef(type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Megjelenő szöveg", description = "A kezdőlapon megjelenő szöveg. Ha üres akkor nincs ilyen.")

    /// -------------------------------------------------------------------------------------------------------------------

    val eventsEmbeddedComponentGroup by SettingGroup(fieldName = "Események rész",
        description = "Csak akkor van hatása ha a event komponens be van kapcsolva")

    var showEvents by BooleanSettingRef(fieldName = "Események láthatóak",
        description = "Ha be van kapcsolva akkor az események láthatóak a kezdőlapon")

    /// -------------------------------------------------------------------------------------------------------------------

    val newsEmbeddedComponentGroup by SettingGroup(fieldName = "Hírek rész",
        description = "Csak akkor van hatása ha a news komponens be van kapcsolva")

    var maxVisibleCount by NumberSettingRef(3, serverSideOnly = true, strictConversion = false,
        fieldName = "Max megjelenő hír", description = "Ennyi hír jelenik meg a főoldali hirdetés komponensben")

    var showNews by BooleanSettingRef(fieldName = "Hírek láthatóak",
        description = "Ha be van kapcsolva akkor a hírek láthatóak a kezdőlapon")

    /// -------------------------------------------------------------------------------------------------------------------

    val galleryEmbeddedComponentGroup by SettingGroup(fieldName = "Galéria rész",
        description = "Csak akkor van hatása ha a gallery komponens be van kapcsolva")

    var showGalleryImages by BooleanSettingRef(fieldName = "Galéria képek láthatóak",
        description = "Megjelennek egy carousel-ben azok a képek a galériából, melyeknél be van kapcsolva, hogy a kezdőlapra kerülhetnek")
}
