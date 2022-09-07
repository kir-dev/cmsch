package hu.bme.sch.cmsch.component.race

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_TIME
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.ManagedEntity

data class RaceEntryDto(

    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @JsonView(FullDetails::class)
    @property:GenerateOverview(columnName = "Név", order = 1)
    var name: String = "",

    @JsonView(FullDetails::class)
    @property:GenerateOverview(columnName = "Csoport", order = 2)
    var groupName: String? = null,

    @JsonView(FullDetails::class)
    @property:GenerateOverview(columnName = "Idő", order = 3, renderer = OVERVIEW_TYPE_TIME)
    var time: Float = 0.0f,

) : ManagedEntity
