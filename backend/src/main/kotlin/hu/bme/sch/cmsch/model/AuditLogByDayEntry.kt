package hu.bme.sch.cmsch.model

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_DATE

data class AuditLogByDayEntry(
    @property:GenerateOverview(columnName = "Nap", order = 1, renderer = OVERVIEW_TYPE_DATE)
    val id: Long,

    @property:GenerateOverview(columnName = "Bejegyzések", order = 2)
    val size: Long
)
