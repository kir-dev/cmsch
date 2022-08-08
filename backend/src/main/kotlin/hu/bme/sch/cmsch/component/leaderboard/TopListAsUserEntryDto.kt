package hu.bme.sch.cmsch.component.leaderboard

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview

data class TopListAsUserEntryDto(

    @JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(visible = false)
    var id: Int,

    @JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Felhasználó", order = 1)
    override var name: String,

    @JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Csoport", order = 2, centered = true)
    var groupName: String,

    @JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Feladatok", order = 3, centered = true)
    override var taskScore: Int = 0,

    @JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Riddleök", order = 4, centered = true)
    override var riddleScore: Int = 0,

    @JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Beadások", order = 5, centered = true)
    override var challengeScore: Int = 0,

    @JsonView(value = [Preview::class, FullDetails::class])
    @property:GenerateOverview(columnName = "QR Kódok", order = 6, centered = true)
    override var tokenScore: Int = 0,

    @JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Totál", order = 100, centered = true)
    override var totalScore: Int = 0,

) : TopListAbstractEntry
