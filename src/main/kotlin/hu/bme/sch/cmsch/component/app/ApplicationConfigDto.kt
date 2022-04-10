package hu.bme.sch.cmsch.component.app

data class ApplicationConfigDto(
    // HTML head
    var title: String,
    var faviconUrl: String,

    // Colors
    var backgroundColor: String,
    var textColor1: String,
    var textColor2: String,
    var textColorAccent: String,
    var brandingColor1: String,
    var brandingColor2: String,

    // Branding images
    var logoUrl: String,
    var backgroundUrl: String,
    var mobileBackgroundUrl: String,

    // Typography
    var mainFontName: String,
    var mainFontCdn: String,
    var displayFontName: String,
    var displayFontCdn: String,

    // Menu
    var mainView: String,
    var menu: List<MenuItem>,

    // Components -> properties -> values
    var components: Map<String, Map<String, String>>,
)

data class MenuItem(
    var displayName: String,
    var url: String,
    var external: Boolean = false,
    var children: List<MenuItem>
)
