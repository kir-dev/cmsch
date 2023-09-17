package hu.bme.sch.cmsch.component.location

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name="waypoints")
@ConditionalOnBean(LocationComponent::class)
data class WaypointEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false, columnDefinition = "VARCHAR(64) default ''")
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 1, label = "Kijelzett szöveg")
    @property:GenerateOverview(columnName = "Longitude", order = 1)
    var displayName: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 32, order = 3, label = "Longitude", type = INPUT_TYPE_FLOAT)
    @property:GenerateOverview(columnName = "Longitude", order = 4)
    var longitude: Double = 0.0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 32, order = 2, label = "Latitude", type = INPUT_TYPE_FLOAT)
    @property:GenerateOverview(columnName = "Latitude", order = 3)
    var latitude: Double = 0.0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 32, order = 4, label = "Magasság", defaultValue = "0", type = INPUT_TYPE_FLOAT)
    @property:GenerateOverview(visible = false)
    var altitude: Double = 0.0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 32, order = 5, label = "Pontosság", defaultValue = "0", type = INPUT_TYPE_FLOAT)
    @property:GenerateOverview(columnName = "Pontosság", order = 5)
    var accuracy: Float = 0.0f,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 32, order = 6, label = "Magasság pontossága", defaultValue = "0", type = INPUT_TYPE_FLOAT)
    @property:GenerateOverview(visible = false)
    var altitudeAccuracy: Float = 0.0f,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 32, order = 7, label = "Haladási irány", defaultValue = "0", type = INPUT_TYPE_FLOAT)
    @property:GenerateOverview(visible = false)
    var heading: Double = 0.0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 32, order = 8, label = "Sebesség", defaultValue = "0", type = INPUT_TYPE_FLOAT)
    @property:GenerateOverview(visible = false)
    var speed: Double = 0.0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 9, label = "Helyzet frissült ekkor")
    @property:GenerateOverview(columnName = "Frissült", order = 6, renderer = OVERVIEW_TYPE_DATE, centered = true)
    var timestamp: Long = 0,

    @Enumerated(EnumType.STRING)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 10, label = "Forma",
        source = [ "CIRCLE", "SQUARE", "INFO", "CAR", "CROSSHAIRS", "CAMP", "TOWER", "MARKER", "HOME" ])
    @property:GenerateOverview(visible = false)
    var markerShape: MapMarkerShape = MapMarkerShape.CIRCLE,

    @Column(nullable = false, columnDefinition = "VARCHAR(16) default ''")
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 16, order = 11, label = "Szín")
    @property:GenerateOverview(visible = false)
    var markerColor: String = "#000000",

    @Lob
    @Column(nullable = false, columnDefinition = "VARCHAR(64) default ''")
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(order = 12, label = "Leírás", type = INPUT_TYPE_BLOCK_TEXT)
    @property:GenerateOverview(visible = false)
    var description: String = "",

) : ManagedEntity {

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

    fun toMapMarker() = MapMarker(
        displayName = displayName,
        longitude = longitude,
        latitude = latitude,
        altitude = altitude,
        accuracy = accuracy,
        altitudeAccuracy = altitudeAccuracy,
        heading = heading,
        speed = speed,
        timestamp = timestamp,
        markerShape = markerShape,
        markerColor = markerColor,
        description = description,
    )
}
