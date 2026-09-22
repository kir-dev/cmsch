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
@Table(name = "bountyTeams")
@ConditionalOnBean(BountyComponent::class)
data class BountyTeamEntity(

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
    @property:GenerateInput(type = InputType.NUMBER, order = 3, label = "Csapat ID-je",
        note = "Ezt a rendszer tartja karban, ne módosítsd!")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var groupId: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(maxLength = 128, order = 4, label = "Csapat neve",
        note = "Ez csak megjegyzés, nem ez alapján számolódik.")
    @property:GenerateOverview(columnName = "Csapat", order = 4)
    @property:ImportFormat
    var groupName: String = "",

    @Column
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(type = InputType.NUMBER, order = 5, label = "Célpont csapat ID-je",
        note = "Ezt a rendszer tartja karban, ne módosítsd!")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var targetGroupId: Int? = null,

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(maxLength = 128, order = 6, label = "Ellenséges csapat",
        note = "Ezt a rendszer tartja karban, ne módosítsd!")
    @property:GenerateOverview(columnName = "Ellenség", order = 5)
    @property:ImportFormat
    var targetGroupName: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(maxLength = 128, order = 7, label = "Fegyver",
        note = "Ezt a rendszer tartja karban, felül lehet írni, de amikor a csapat kiejti az ellenséget, akkor változni fog a fegyver az ellenséges csapatéra.")
    @property:GenerateOverview(columnName = "Fegyver", order = 6)
    @property:ImportFormat
    var weapon: String = "",

    @Column
    @field:JsonView(value = [Edit::class, FullDetails::class])
    @property:GenerateInput(type = InputType.DATE, order = 8, label = "Kiesés időpontja",
        note = "Ezt a rendszer tartja karban, ne módosítsd!")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var eliminatedAt: Long? = null,

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, FullDetails::class])
    @property:GenerateInput(type = InputType.SWITCH, order = 9, label = "Győztes",
        note = "Ezt a rendszer tartja karban, ne módosítsd!")
    @property:GenerateOverview(columnName = "Győztes", order = 7, centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var winner: Boolean = false,

    @Column
    @field:JsonView(value = [Edit::class, FullDetails::class])
    @property:GenerateInput(type = InputType.NUMBER, order = 10, label = "Helyezés",
        note = "Ezt a rendszer tartja karban, ne módosítsd!")
    @property:GenerateOverview(columnName = "Helyezés", order = 8)
    @property:ImportFormat
    var rank: Int? = null,

    @Column
    @field:JsonView(value = [Edit::class, FullDetails::class])
    @property:GenerateInput(type = InputType.NUMBER, order = 11, label = "Túlélési pontok",
        note = "Ezt a rendszer tartja karban, ne módosítsd!")
    @property:GenerateOverview(columnName = "Túlélési pont", order = 9)
    @property:ImportFormat
    var survivalPoints: Long? = null,

    ) : ManagedEntity, Duplicatable {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "BountyTeam",
        view = "control/bounty-teams",
        showPermission = StaffPermissions.PERMISSION_SHOW_BOUNTY_TEAMS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as BountyTeamEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, group = '$groupName', round = '$roundName')"
    }

    override fun duplicate(): BountyTeamEntity {
        return this.copy()
    }

}
