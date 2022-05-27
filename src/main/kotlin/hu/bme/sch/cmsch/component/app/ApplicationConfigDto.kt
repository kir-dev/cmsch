package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.model.RoleType

data class ApplicationConfigDto(
    var role: RoleType,
    var menu: List<MenuItem>,

    // Components -> properties -> values
    var components: Map<String, Map<String, String>>,
)
