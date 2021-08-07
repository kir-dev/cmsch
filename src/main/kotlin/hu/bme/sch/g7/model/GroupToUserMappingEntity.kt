package hu.bme.sch.g7.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.admin.*
import hu.bme.sch.g7.dao.UserRepository
import hu.bme.sch.g7.dto.Edit
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.dto.Preview
import javax.persistence.*

@Entity
@Table(name="groupToUser")
data class GroupToUserMappingEntity(
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
        var neptun: String = "",

        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_ENTITY_SELECT, order = 2, label = "Tankör", entitySource = "GroupEntity")
        @property:GenerateOverview(columnName = "Tankör", order = 2, centered = true)
        var groupName: String = "",

        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @Enumerated(EnumType.STRING)
        @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 3, label = "Szak", source = [ "UNKNOWN", "IT", "EE", "BPROF" ])
        @property:GenerateOverview(columnName = "Szak", order = 3, centered = true)
        var major: MajorType = MajorType.UNKNOWN

): ManagedEntity {
    override fun toString(): String {
        return "[$id]: $neptun <-> ($groupName, $major)"
    }
}