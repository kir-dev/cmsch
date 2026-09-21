package hu.bme.sch.cmsch.component.bounty

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.Duplicatable
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name = "bountyRegistrations")
@ConditionalOnBean(BountyComponent::class)
data class BountyRegistrationEntity(

    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(type = InputType.NUMBER, order = 1, label = "Kör ID-je",
        note = "Ezt a rendszer tartja karban, ne módosítsd!")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var roundId: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(maxLength = 64, order = 2, label = "Kör neve",
        note = "Ez csak megjegyzés, nem ez alapján számolódik.")
    @property:GenerateOverview(columnName = "Kör", order = 2)
    @property:ImportFormat
    var roundName: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(type = InputType.NUMBER, order = 3, label = "Felhasználó ID-je",
        note = "Ezt a rendszer tartja karban, ne módosítsd!")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var userId: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(maxLength = 128, order = 4, label = "Felhasználó neve",
        note = "Ez csak megjegyzés, nem ez alapján számolódik.")
    @property:GenerateOverview(columnName = "Név", order = 4)
    @property:ImportFormat
    var userName: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(type = InputType.NUMBER, order = 5, label = "Csapat ID-je",
        note = "Ezt a rendszer tartja karban, ne módosítsd!")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var groupId: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(maxLength = 128, order = 6, label = "Csapat neve",
        note = "Ez csak megjegyzés, nem ez alapján számolódik.")
    @property:GenerateOverview(columnName = "Csapat", order = 6)
    @property:ImportFormat
    var groupName: String = "",

    @Column(nullable = false, unique = true)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(maxLength = 32, order = 7, label = "Titkos kód",
        note = "Ezt a rendszer tartja karban, ne módosítsd! Ezzel a kóddal lehet megölni a játékost.")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var code: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(type = InputType.SWITCH, order = 8, label = "Életben van",
        note = "Ezt a rendszer tartja karban, ne módosítsd!")
    @property:GenerateOverview(columnName = "Él", order = 8, centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var alive: Boolean = true,

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class])
    @property:GenerateInput(type = InputType.DATE, defaultValue = "0", order = 9, label = "Inaktivitási határidő",
        note = "Ezt a rendszer tartja karban, ne módosítsd! Gyilkolással újraindítható a visszaszámláló.")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var deadline: Long = 0,

    @Column
    @field:JsonView(value = [Edit::class, FullDetails::class])
    @property:GenerateInput(type = InputType.DATE, order = 10, label = "Kiesés időpontja",
        note = "Ezt a rendszer tartja karban, ne módosítsd!")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var eliminatedAt: Long? = null,

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(type = InputType.SWITCH, order = 11, label = "Inaktivitás miatt esett ki",
        note = "Ezt a rendszer tartja karban, ne módosítsd!")
    @property:GenerateOverview(columnName = "Inaktivitás", order = 9, centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var eliminatedByInactivity: Boolean = false,

    ) : ManagedEntity, Duplicatable {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "BountyRegistration",
        view = "control/bounty-registrations",
        showPermission = StaffPermissions.PERMISSION_SHOW_BOUNTY_REGISTRATIONS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as BountyRegistrationEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, user = '$userName', round = '$roundName')"
    }

    override fun duplicate(): BountyRegistrationEntity {
        return this.copy()
    }

}
