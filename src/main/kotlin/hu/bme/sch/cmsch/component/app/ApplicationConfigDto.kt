package hu.bme.sch.cmsch.component.app

data class ApplicationConfigDto(
    // Menu
    var menu: List<MenuItemDto>,

    // Components -> properties -> values
    var components: Map<String, Map<String, String>>,
)

data class MenuItemDto(
    var displayName: String,
    var url: String,
    var external: Boolean = false,
    var children: List<MenuItemDto>
)
