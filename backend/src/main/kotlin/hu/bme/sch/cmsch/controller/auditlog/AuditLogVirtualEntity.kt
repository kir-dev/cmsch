package hu.bme.sch.cmsch.controller.auditlog

import hu.bme.sch.cmsch.admin.GenerateOverview

data class AuditLogVirtualEntity(

    @property:GenerateOverview(columnName = "Fájl", order = 1)
    val id: String,

    @property:GenerateOverview(columnName = "Méret", order = 2)
    val size: String

)