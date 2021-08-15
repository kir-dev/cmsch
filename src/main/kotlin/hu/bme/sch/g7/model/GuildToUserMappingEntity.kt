package hu.bme.sch.g7.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.admin.*
import hu.bme.sch.g7.dto.Edit
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.dto.Preview
import javax.persistence.*

@Entity
@Table(name="guildToUser")
data class GuildToUserMappingEntity(
        @Id
        @GeneratedValue
        @Column(nullable = false)
        @JsonView(value = [ Edit::class ])
        @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
        @property:GenerateOverview(visible = false)
        override var id: Int = 0,

        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @Column(nullable = false)
        @property:GenerateInput(maxLength = 6, order = 1, label = "Neptun kód")
        @property:GenerateOverview(columnName = "Neptun kód", order = 1)
        @property:ImportFormat(ignore = false, columnId = 0)
        var neptun: String = "",

        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @Enumerated(EnumType.STRING)
        @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 2, label = "Gárda", source = [ "UNKNOWN", "BLACK", "BLUE", "RED", "WHITE", "YELLOW" ])
        @property:GenerateOverview(columnName = "Gárda", centered = true, order = 2)
        @property:ImportFormat(ignore = false, columnId = 1, type = IMPORT_ENUM, enumSource = GuildType::class, defaultValue = "UNKNOWN")
        var guild: GuildType = GuildType.UNKNOWN

): ManagedEntity {
    override fun toString(): String {
        return "[$id]: $neptun <-> $guild"
    }
}