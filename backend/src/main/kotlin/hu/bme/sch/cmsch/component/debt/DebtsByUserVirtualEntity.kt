package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.admin.GenerateOverview

data class DebtsByUserVirtualEntity(

        @property:GenerateOverview(visible = false)
        val id: Int,

        @property:GenerateOverview(columnName = "Felhasználó", order = 1)
        val username: String,

        @property:GenerateOverview(columnName = "Csoport", order = 2, centered = true)
        val groupName: String,

        @property:GenerateOverview(columnName = "Totál [JMF]", order = 3, centered = true)
        val total: Int,

        @property:GenerateOverview(columnName = "Fizetetlen [JMF]", order = 4, centered = true)
        val notPayed: Int,

        @property:GenerateOverview(columnName = "Lezáratlan [JMF]", order = 5, centered = true)
        val notFinished: Int

)
