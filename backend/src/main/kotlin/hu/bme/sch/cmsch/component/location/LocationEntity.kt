package hu.bme.sch.cmsch.component.location

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import jakarta.persistence.*

@Entity
@Table(name="locations",
        uniqueConstraints = [ UniqueConstraint(columnNames = ["userId"]) ],
        indexes = [ Index(columnList = "groupName") ]
)
@ConditionalOnBean(LocationComponent::class)
data class LocationEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    var userId: Int = 0,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 1, label = "Felhasználó")
    @property:GenerateOverview(columnName = "Felhasználó", order = 1)
    var userName: String = "",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 11, label = "Becenév")
    var alias: String = "",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 1, label = "Csoport neve")
    @property:GenerateOverview(columnName = "Csoport", order = 2, centered = true)
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
    @property:GenerateOverview(columnName = "Magasság", order = 5)
    var altitude: Double = 0.0,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 10, label = "Helyzet frissült ekkor")
    @property:GenerateOverview(columnName = "Frissült", order = 6, renderer = OVERVIEW_TYPE_DATE, centered = true)
    var timestamp: Long = 0

): ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "Location",
        view = "control/locations",
        showPermission = StaffPermissions.PERMISSION_SHOW_LOCATIONS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as LocationEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
