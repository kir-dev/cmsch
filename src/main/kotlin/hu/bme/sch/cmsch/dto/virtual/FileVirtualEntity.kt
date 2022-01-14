package hu.bme.sch.cmsch.dto.virtual

import hu.bme.sch.cmsch.admin.GenerateOverview

data class FileVirtualEntity(

        @property:GenerateOverview(columnName = "Név", order = 1)
        val id: String,

        @property:GenerateOverview(columnName = "Méret", order = 2)
        val size: String

)
