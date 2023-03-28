package hu.bme.sch.cmsch.component.race

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.ManagedEntity
import org.springframework.core.env.Environment

data class RaceEntryDto(

    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @JsonView(FullDetails::class)
    @property:GenerateOverview(columnName = "Név", order = 1)
    @property:ImportFormat(ignore = false, columnId = 0)
    var name: String = "",

    @JsonView(FullDetails::class)
    @property:GenerateOverview(columnName = "Csoport", order = 2)
    @property:ImportFormat(ignore = false, columnId = 1)
    var groupName: String? = null,

    @JsonView(FullDetails::class)
    @get:JsonProperty("score")
    @property:GenerateOverview(columnName = "Idő", order = 3, renderer = OVERVIEW_TYPE_TIME)
    @property:ImportFormat(ignore = false, columnId = 2, type = IMPORT_FLOAT)
    var time: Float = 0.0f,

    @JsonView(Edit::class)
    @property:GenerateOverview(columnName = "Email", order = 4)
    @property:ImportFormat(ignore = false, columnId = 4)
    var email: String = "",

) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = null

}
