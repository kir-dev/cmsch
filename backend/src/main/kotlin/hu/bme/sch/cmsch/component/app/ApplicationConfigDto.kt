package hu.bme.sch.cmsch.component.app

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import hu.bme.sch.cmsch.model.RoleType

data class ApplicationConfigDto(
    var role: RoleType,
    var menu: List<MenuItem>,

    // Components -> properties -> values: Map<String, Map<String, Any>>
    @field:JsonSerialize(using = ApplicationConfigFastSerializer::class)
    var components: String
)

class ApplicationConfigFastSerializer : JsonSerializer<String>() {
    override fun serialize(value: String?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        gen?.writeRawValue(value ?: "")
    }
}