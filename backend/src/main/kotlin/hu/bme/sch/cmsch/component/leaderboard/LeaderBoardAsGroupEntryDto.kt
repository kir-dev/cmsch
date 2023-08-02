package hu.bme.sch.cmsch.component.leaderboard

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.IMPORT_INT
import hu.bme.sch.cmsch.admin.ImportFormat
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class LeaderBoardAsGroupEntryDto(

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Csoport", order = 1)
    @property:ImportFormat(ignore = false, columnId = 0)
    override var name: String = "",

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Feladatok", order = 2, centered = true)
    @property:ImportFormat(ignore = false, columnId = 1, type = IMPORT_INT)
    override var taskScore: Int = 0,

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Riddleök", order = 3, centered = true)
    @property:ImportFormat(ignore = false, columnId = 2, type = IMPORT_INT)
    override var riddleScore: Int = 0,

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Beadások", order = 4, centered = true)
    @property:ImportFormat(ignore = false, columnId = 3, type = IMPORT_INT)
    override var challengeScore: Int = 0,

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "QR Kódok", order = 5, centered = true)
    @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_INT)
    override var tokenScore: Int = 0,

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Totál", order = 100, centered = true)
    @property:ImportFormat(ignore = false, columnId = 5, type = IMPORT_INT)
    override var totalScore: Int = 0,

) : LeaderBoardEntry, IdentifiableEntity
