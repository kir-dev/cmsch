package hu.bme.sch.cmsch.component.leaderboard

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.IMPORT_INT
import hu.bme.sch.cmsch.admin.ImportFormat
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview

data class TopListAsUserEntryDto(

    @JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(visible = false)
    var id: Int,

    @JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Felhasználó", order = 1)
    @property:ImportFormat(ignore = false, columnId = 0)
    override var name: String,

    @JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Csoport", order = 2, centered = true)
    @property:ImportFormat(ignore = false, columnId = 1, type = IMPORT_INT)
    var groupName: String,

    @JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Feladatok", order = 3, centered = true)
    @property:ImportFormat(ignore = false, columnId = 2, type = IMPORT_INT)
    override var taskScore: Int = 0,

    @JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Riddleök", order = 4, centered = true)
    @property:ImportFormat(ignore = false, columnId = 3, type = IMPORT_INT)
    override var riddleScore: Int = 0,

    @JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Beadások", order = 5, centered = true)
    @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_INT)
    override var challengeScore: Int = 0,

    @JsonView(value = [Preview::class, FullDetails::class])
    @property:GenerateOverview(columnName = "QR Kódok", order = 6, centered = true)
    @property:ImportFormat(ignore = false, columnId = 5, type = IMPORT_INT)
    override var tokenScore: Int = 0,

    @JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Totál", order = 100, centered = true)
    @property:ImportFormat(ignore = false, columnId = 6, type = IMPORT_INT)
    override var totalScore: Int = 0,

) : TopListAbstractEntry
