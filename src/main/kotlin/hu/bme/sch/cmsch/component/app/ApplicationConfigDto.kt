package hu.bme.sch.cmsch.component.app

data class ApplicationConfigDto(
    // Menu
    var menu: List<MenuItem>,

    // Components -> properties -> values
    var components: Map<String, Map<String, String>>,
)
