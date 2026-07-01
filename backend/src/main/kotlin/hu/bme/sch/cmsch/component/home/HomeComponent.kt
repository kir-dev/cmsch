package hu.bme.sch.cmsch.component.home

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnBooleanProperty(value = ["hu.bme.sch.cmsch.component.load.home"])
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
        fieldName = "Jogosultságok", description = "Mely szerepkörökkel nyitható meg az oldal")

    /// -------------------------------------------------------------------------------------------------------------------

    val displayGroup by SettingGroup(fieldName = "Megjelenés", description = "A kezdőlap megjelenése")

    var welcomeMessage by StringSettingRef("Üdvözlünk a {} portálon", fieldName = "Üdvözlő üzenet",
        description = "A {} helyére az oldal neve kerül. Ha üres, nem jelenik meg.")

    var youtubeVideoIds by StringSettingRef(fieldName = "Promó videó(k)",
        description = "Csak YouTube videó ID-kkal működik. Több videó esetén vesszővel választandóak el. Például: '8PhToFtwKvY'. Ha üres, nem jelenik meg.")

    var content by StringSettingRef(type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Megjelenő szöveg", description = "A kezdőlapon megjelenő szöveg. Ha üres, nem jelenik meg.")

    /// -------------------------------------------------------------------------------------------------------------------

    val eventsEmbeddedComponentGroup by SettingGroup(fieldName = "Események rész",
        description = "Csak akkor van hatása, ha az események (event) komponens be van kapcsolva")

    var showEvents by BooleanSettingRef(fieldName = "Események láthatóak",
        description = "Bekapcsolt állapotban az események láthatóak a kezdőlapon")

    /// -------------------------------------------------------------------------------------------------------------------

    val newsEmbeddedComponentGroup by SettingGroup(fieldName = "Hírek rész",
        description = "Csak akkor van hatása, ha a hírek (news) komponens be van kapcsolva")

    var maxVisibleCount by NumberSettingRef(3, serverSideOnly = true, strictConversion = false,
        fieldName = "Max megjelenő hír", description = "A főoldali hírkomponensben megjelenő hírek maximális száma")

    var showNews by BooleanSettingRef(fieldName = "Hírek láthatóak",
        description = "Bekapcsolt állapotban a hírek láthatóak a kezdőlapon")

    /// -------------------------------------------------------------------------------------------------------------------

    val galleryEmbeddedComponentGroup by SettingGroup(fieldName = "Galéria rész",
        description = "Csak akkor van hatása, ha a galéria (gallery) komponens be van kapcsolva")

    var showGalleryImages by BooleanSettingRef(fieldName = "Galéria képek láthatóak",
        description = "Megjeleníti egy carouselben azokat a galériaképeket, amelyeknél a kezdőlapon való megjelenítés engedélyezett")
}
