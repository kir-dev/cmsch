package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.admin.GenerateOverview

data class DebtsByGroupVirtualEntity(

        @property:GenerateOverview(visible = false)
        val id: Int,

        @property:GenerateOverview(columnName = "Csoport", order = 1)
        val groupName: String,

        @property:GenerateOverview(columnName = "Forgalom [JMF]", order = 2, centered = true)
        val total: Int,

        @property:GenerateOverview(columnName = "Fizetetlen [JMF]", order = 3, centered = true)
        val notPayed: Int,

        @property:GenerateOverview(columnName = "Lezáratlan [JMF]", order = 4, centered = true)
        val notFinished: Int

)
