package hu.bme.sch.cmsch.component

import hu.bme.sch.cmsch.service.PermissionValidator

data class EntityConfig(
    val name: String,
    val view: String,
    val showPermission: PermissionValidator
)
