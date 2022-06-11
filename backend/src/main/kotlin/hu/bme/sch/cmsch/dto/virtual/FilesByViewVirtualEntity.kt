package hu.bme.sch.cmsch.dto.virtual

import hu.bme.sch.cmsch.admin.GenerateOverview

data class FilesByViewVirtualEntity(

        @property:GenerateOverview(columnName = "Típus", order = 1)
        val id: String,

        @property:GenerateOverview(columnName = "Mennyiség", order = 2)
        val count: Long,

        @property:GenerateOverview(columnName = "Méret", order = 3)
        val size: String

)
