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
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name = "bountyRounds")
@ConditionalOnBean(BountyComponent::class)
data class BountyRoundEntity(

    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(maxLength = 64, order = 1, label = "Kör neve")
    @property:GenerateOverview(columnName = "Név", order = 1)
    @property:ImportFormat
    var name: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class])
    @property:GenerateInput(type = InputType.DATE, defaultValue = "0", order = 2, label = "Regisztráció kezdete")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var registrationStart: Long = 0,

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class])
    @property:GenerateInput(type = InputType.DATE, defaultValue = "0", order = 3, label = "Regisztráció vége")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var registrationEnd: Long = 0,

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class])
    @property:GenerateInput(type = InputType.DATE, defaultValue = "0", order = 4, label = "Játék kezdete")
    @property:GenerateOverview(columnName = "Játék kezdete", order = 6, renderer = OverviewType.DATE, useForSearch = false)
    @property:ImportFormat
    var gameStart: Long = 0,

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class])
    @property:GenerateInput(type = InputType.DATE, defaultValue = "0", order = 5, label = "Játék vége")
    @property:GenerateOverview(columnName = "Játék vége", order = 7, renderer = OverviewType.DATE, useForSearch = false)
    @property:ImportFormat
    var gameEnd: Long = 0,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 6, label = "Nehézség",
        note = "A fegyverek az ehhez tartozó poolból sorsolódnak, a gyilkosságért kapott pont is ettől függ.",
        source = ["EASY", "MEDIUM", "HARD"])
    @property:GenerateOverview(columnName = "Nehézség", order = 2)
    @property:ImportFormat
    var difficulty: BountyDifficulty = BountyDifficulty.EASY,

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class])
    @property:GenerateInput(type = InputType.NUMBER, defaultValue = "48", order = 7, label = "Inaktivitási idő (óra)",
        note = "Ennyi óra gyilkolás nélkül kiesik a játékos a körből.")
    @property:GenerateOverview(columnName = "Inaktivitási idő (óra)", order = 8, renderer = OverviewType.NUMBER)
    @property:ImportFormat
    var inactivityHours: Long = 48,

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class])
    @property:GenerateInput(type = InputType.SWITCH, order = 8, label = "Inicializálva",
        note = "Ezt a rendszer tartja karban, ne módosítsd! A regisztráció lezárultával igazra állítódik.")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var initialized: Boolean = false,

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, FullDetails::class])
    @property:GenerateInput(type = InputType.SWITCH, order = 9, label = "Lezárva",
        note = "Ezt a rendszer tartja karban, ne módosítsd! A kör végeztével igazra állítódik.")
    @property:GenerateOverview(columnName = "Lezárva", order = 3, centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var finalized: Boolean = false,

    ) : ManagedEntity, Duplicatable {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "BountyRound",
        view = "control/bounty-rounds",
        showPermission = StaffPermissions.PERMISSION_SHOW_BOUNTY_ROUNDS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as BountyRoundEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, name = '$name')"
    }

    override fun duplicate(): BountyRoundEntity {
        return this.copy()
    }

}
