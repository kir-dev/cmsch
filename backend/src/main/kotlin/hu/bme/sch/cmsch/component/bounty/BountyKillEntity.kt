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
@Table(name = "bountyKills")
@ConditionalOnBean(BountyComponent::class)
data class BountyKillEntity(

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
    @property:GenerateInput(type = InputType.NUMBER, order = 3, label = "Gyilkos felhasználó ID-je",
        note = "Ezt a rendszer tartja karban, ne módosítsd!")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var killerUserId: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(maxLength = 128, order = 4, label = "Gyilkos neve",
        note = "Ez csak megjegyzés, nem ez alapján számolódik.")
    @property:GenerateOverview(columnName = "Gyilkos", order = 4)
    @property:ImportFormat
    var killerUserName: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(type = InputType.NUMBER, order = 5, label = "Gyilkos csapat ID-je",
        note = "Ezt a rendszer tartja karban, ne módosítsd!")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var killerGroupId: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(maxLength = 128, order = 6, label = "Gyilkos csapat neve",
        note = "Ez csak megjegyzés, nem ez alapján számolódik.")
    @property:GenerateOverview(columnName = "Gyilkos csapat", order = 6)
    @property:ImportFormat
    var killerGroupName: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(type = InputType.NUMBER, order = 7, label = "Áldozat felhasználó ID-je",
        note = "Ezt a rendszer tartja karban, ne módosítsd!")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var victimUserId: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(maxLength = 128, order = 8, label = "Áldozat neve",
        note = "Ez csak megjegyzés, nem ez alapján számolódik.")
    @property:GenerateOverview(columnName = "Áldozat", order = 8)
    @property:ImportFormat
    var victimUserName: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(type = InputType.NUMBER, order = 9, label = "Pontszám",
        note = "Ezt a rendszer tartja karban, ne módosítsd!")
    @property:GenerateOverview(columnName = "Pont", order = 9)
    @property:ImportFormat
    var points: Long = 0,

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, FullDetails::class])
    @property:GenerateInput(type = InputType.DATE, order = 10, label = "Időpont",
        note = "Ezt a rendszer tartja karban, ne módosítsd!")
    @property:GenerateOverview(columnName = "Időpont", order = 10, renderer = OverviewType.DATE, useForSearch = false)
    @property:ImportFormat
    var createdAt: Long = 0,

    ) : ManagedEntity, Duplicatable {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "BountyKill",
        view = "control/bounty-kills",
        showPermission = StaffPermissions.PERMISSION_SHOW_BOUNTY_KILLS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as BountyKillEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, killer = '$killerUserName', victim = '$victimUserName')"
    }

    override fun duplicate(): BountyKillEntity {
        return this.copy()
    }

}
