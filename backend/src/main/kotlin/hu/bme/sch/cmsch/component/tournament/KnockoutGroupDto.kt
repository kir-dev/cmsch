package hu.bme.sch.cmsch.component.tournament

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_ID
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class KnockoutGroupDto(

    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @property:GenerateOverview(columnName = "Név", order = 1)
    var name: String = "",

    @property:GenerateOverview(columnName = "Helyszín", order = 4)
    var location: String = "",

    @property:GenerateOverview(columnName = "Résztvevők száma", order = 5)
    var participantCount: Int = 0,

    @property:GenerateOverview(columnName = "Szakaszok száma", order = 6)
    var stageCount: Int = 0

): IdentifiableEntity
