package hu.bme.sch.g7.dto.virtual

import hu.bme.sch.g7.admin.GenerateOverview

data class DebtsByUser(

        @property:GenerateOverview(visible = false)
        val id: Int,

        @property:GenerateOverview(columnName = "Felhasználó", order = 1)
        val username: String,

        @property:GenerateOverview(columnName = "Tanköre", order = 2, centered = true)
        val groupName: String,

        @property:GenerateOverview(columnName = "Totál [JMF]", order = 3, centered = true)
        val total: Int,

        @property:GenerateOverview(columnName = "Fizetetlen [JMF]", order = 4, centered = true)
        val notPayed: Int,

        @property:GenerateOverview(columnName = "Lezáratlan [JMF]", order = 5, centered = true)
        val notFinished: Int

)