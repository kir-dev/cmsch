package hu.bme.sch.g7.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.admin.*
import hu.bme.sch.g7.dto.Edit
import hu.bme.sch.g7.dto.FullDetails
import javax.persistence.*

@Entity
@Table(name="locations",
        uniqueConstraints = [ UniqueConstraint(columnNames = ["userId"]) ],
        indexes = [ Index(columnList = "groupName") ]
)
data class LocationEntity(
        @Id
        @GeneratedValue
        @Column(nullable = false)
        @JsonView(value = [ Edit::class, FullDetails::class ])
        @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
        @property:GenerateOverview(visible = false)
        override var id: Int = 0,

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, FullDetails::class ])
        @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
        @property:GenerateOverview(visible = false)
        var userId: Int = 0,

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, FullDetails::class ])
        @property:GenerateInput(maxLength = 64, order = 1, label = "Felhasználó")
        @property:GenerateOverview(columnName = "Felhasználó", order = 1)
        @property:ImportFormat(ignore = false, columnId = 0)
        var userName: String = "",

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, FullDetails::class ])
        @property:GenerateInput(maxLength = 64, order = 1, label = "Tankör neve")
        @property:GenerateOverview(columnName = "Tankör", order = 2, centered = true)
        @property:ImportFormat(ignore = false, columnId = 0)
        var groupName: String = "",

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, FullDetails::class ])
        @property:GenerateInput(maxLength = 32, order = 8, label = "Longitude")
        @property:GenerateOverview(columnName = "Longitude", order = 4)
        var longitude: Double = 0.0,

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, FullDetails::class ])
        @property:GenerateInput(maxLength = 32, order = 9, label = "Latitude")
        @property:GenerateOverview(columnName = "Latitude", order = 3)
        var latitude: Double = 0.0,

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, FullDetails::class ])
        @property:GenerateInput(maxLength = 32, order = 9, label = "Pontosság")
        @property:GenerateOverview(visible = false)
        var accuracy: Float = 0.0f,

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, FullDetails::class ])
        @property:GenerateInput(maxLength = 32, order = 9, label = "Magasság")
        @property:GenerateOverview(visible = false)
        var altitude: Double = 0.0,

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, FullDetails::class ])
        @property:GenerateInput(type = INPUT_TYPE_DATE, order = 10, label = "Helyzet frissült ekkor")
        @property:GenerateOverview(columnName = "Frissült", order = 5, renderer = OVERVIEW_TYPE_DATE, centered = true)
        var timestamp: Long = 0

): ManagedEntity {
    override fun toString(): String {
        return "[$id]: $userName $groupName"
    }
}