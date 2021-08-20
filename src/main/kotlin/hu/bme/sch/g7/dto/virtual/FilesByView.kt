package hu.bme.sch.g7.dto.virtual

import hu.bme.sch.g7.admin.GenerateOverview

data class FilesByView(

        @property:GenerateOverview(columnName = "Típus", order = 1)
        val id: String,

        @property:GenerateOverview(columnName = "Mennyiség", order = 2)
        val count: Long,

        @property:GenerateOverview(columnName = "Méret", order = 3)
        val size: String

)