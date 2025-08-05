package hu.bme.sch.cmsch.component.conference

import com.fasterxml.jackson.annotation.JsonIgnore
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.model.Duplicatable
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment


@Entity
@Table(name="conferenceOrganizers")
@ConditionalOnBean(ConferenceComponent::class)
data class ConferenceOrganizerEntity(
    @Id
    @field:JsonIgnore
    @GeneratedValue
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Név")
    @property:GenerateOverview(columnName = "Név", order = 1)
    @property:ImportFormat
    var name: String = "",

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 2, label = "Beosztás")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var rank: String = "",

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 3, label = "Email cím")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var emailAddress: String = "",

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 255, order = 4, label = "Profilkép url")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var pictureUrl: String = "",

    @Column(nullable = false)
    @property:GenerateInput(type = InputType.NUMBER, order = 5, label = "Prioritás", defaultValue = "0")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var priority: Int = 0,

    @field:JsonIgnore
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.SWITCH, order = 6, label = "Látható")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var visible: Boolean = false
) : ManagedEntity, Duplicatable {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "ConferenceOrganizer",
        view = "control/conference-organizer",
        showPermission = StaffPermissions.PERMISSION_SHOW_CONFERENCE
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ConferenceOrganizerEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, name = $name)"
    }

    override fun duplicate(): ConferenceOrganizerEntity {
        return this.copy()
    }

}
