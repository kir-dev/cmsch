package hu.bme.sch.cmsch.component.race

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.ManagedEntity
import org.springframework.core.env.Environment

data class FreestyleRaceEntryDto(

    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(FullDetails::class)
    @property:GenerateOverview(columnName = "Név", order = 1)
    @property:ImportFormat(ignore = false)
    var name: String = "",

    @field:JsonView(FullDetails::class)
    @property:GenerateOverview(columnName = "Csoport", order = 2)
    @property:ImportFormat(ignore = false)
    var groupName: String? = null,

    @field:JsonView(FullDetails::class)
    @get:JsonProperty("score")
    @property:GenerateOverview(columnName = "Idő", order = 3, renderer = OVERVIEW_TYPE_TIME)
    @property:ImportFormat(ignore = false)
    var time: Float = 0.0f,

    @field:JsonView(FullDetails::class)
    @property:GenerateOverview(columnName = "Beadás módja", order = 4)
    @property:ImportFormat(ignore = false)
    var description: String = "",

    @field:JsonView(Edit::class)
    @property:GenerateOverview(columnName = "Email", order = 5)
    @property:ImportFormat(ignore = false)
    var email: String = "",

) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = null

}
