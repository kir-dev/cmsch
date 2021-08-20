package hu.bme.sch.g7.dto.virtual

import hu.bme.sch.g7.admin.GenerateOverview

data class GroupMemberVirtualEntity (

        @property:GenerateOverview(visible = false)
        val id: Int,

        @property:GenerateOverview(columnName = "Név", order = 1)
        val name: String,

        @property:GenerateOverview(columnName = "", order = 2, centered = true)
        val roleName: String,

)