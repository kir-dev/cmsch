package hu.bme.sch.cmsch.component.leaderboard

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.ImportFormat
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class LeaderBoardAsUserEntryDto(

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Felhasználó", order = 1)
    @property:ImportFormat
    override var name: String = "",

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Csoport", order = 2, centered = true)
    @property:ImportFormat
    var groupName: String = "",

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Feladatok", order = 3, centered = true)
    @property:ImportFormat
    override var taskScore: Int = 0,

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Riddleök", order = 4, centered = true)
    @property:ImportFormat
    override var riddleScore: Int = 0,

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Beadások", order = 5, centered = true)
    @property:ImportFormat
    override var challengeScore: Int = 0,

    @field:JsonView(value = [Preview::class, FullDetails::class])
    @property:GenerateOverview(columnName = "QR Kódok", order = 6, centered = true)
    @property:ImportFormat
    override var tokenScore: Int = 0,

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Totál", order = 100, centered = true)
    @property:ImportFormat
    override var totalScore: Int = 0,

) : LeaderBoardEntry, IdentifiableEntity
