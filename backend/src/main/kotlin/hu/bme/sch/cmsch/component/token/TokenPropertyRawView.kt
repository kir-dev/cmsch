package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class TokenPropertyRawView(

    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @property:GenerateOverview(columnName = "Felhasználó id", order = 1)
    @property:ImportFormat
    var ownerUserId: Int = 0,

    @property:GenerateOverview(columnName = "Felhasználó név", order = 2)
    @property:ImportFormat
    var ownerUserName: String = "",

    @property:GenerateOverview(columnName = "Csoport id", order = 3)
    @property:ImportFormat
    val ownerGroupId: Int = 0,

    @property:GenerateOverview(columnName = "Csoport név", order = 4)
    @property:ImportFormat
    var ownerGroupName: String = "",

    @property:GenerateOverview(columnName = "Pont", order = 5)
    @property:ImportFormat
    var score: Int = 0,

    @property:GenerateOverview(columnName = "Token", order = 6)
    @property:ImportFormat
    var token: String = "",

    @property:GenerateOverview(columnName = "Beolvasva", order = 7, centered = true, renderer = OVERVIEW_TYPE_DATE)
    @property:ImportFormat
    var timestamp: Long = 0

) : IdentifiableEntity