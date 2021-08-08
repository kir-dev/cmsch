package hu.bme.sch.g7.dto.virtual

import hu.bme.sch.g7.admin.GenerateOverview

data class DebtsByGroup(

        @property:GenerateOverview(visible = false)
        val id: Int,

        @property:GenerateOverview(columnName = "Tankör", order = 1)
        val groupName: String,

        @property:GenerateOverview(columnName = "Forgalom [JMF]", order = 2, centered = true)
        val total: Int,

        @property:GenerateOverview(columnName = "Fizetetlen [JMF]", order = 3, centered = true)
        val notPayed: Int,

        @property:GenerateOverview(columnName = "Lezáratlan [JMF]", order = 4, centered = true)
        val notFinished: Int

)