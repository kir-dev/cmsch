package hu.bme.sch.cmsch.component.achievement

import hu.bme.sch.cmsch.admin.GenerateOverview

data class GradedAchievementGroupDto(

    @property:GenerateOverview(visible = false)
    val id: Int,

    @property:GenerateOverview(columnName = "Cím", order = 1)
    val achievementName: String,

    @property:GenerateOverview(columnName = "OK", order = 2, centered = true)
    val approved: Int,

    @property:GenerateOverview(columnName = "Nem OK", order = 3, centered = true)
    val rejected: Int,

    @property:GenerateOverview(columnName = "Értékelésre vár", order = 4, centered = true)
    val notGraded: Int

)
