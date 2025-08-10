package hu.bme.sch.cmsch.component.tournament

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OverviewType
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class StageGroupDto(

    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @property:GenerateOverview(columnName = "Név", order = 1)
    var name: String = "",

    @property:GenerateOverview(columnName = "Helyszín", order = 2)
    var location: String = "",

    @property:GenerateOverview(columnName = "Résztvevők száma", order = 3)
    var participantCount: Int = 0,

    @property:GenerateOverview(columnName = "Szakaszok száma", order = 4)
    var stageCount: Int = 0

): IdentifiableEntity
