package hu.bme.sch.cmsch.component.tournament

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import kotlin.math.ceil
import kotlin.math.log2


enum class StageStatus {
    CREATED,
    SET,
    ONGOING,
    FINISHED,
    CANCELLED
}


@Entity
@Table(name = "tournament_stage")
@ConditionalOnBean(TournamentComponent::class)
data class TournamentStageEntity(

    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID")
    override var id: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 1, label = "Szakasz neve")
    @property:GenerateOverview(columnName = "Név", order = 1)
    @property:ImportFormat
    var name: String = "",

    @Column(nullable = false)
    @property:GenerateInput(type = InputType.HIDDEN, min = 1, order = 2, label = "Verseny ID")
    @property:GenerateOverview(columnName = "Verseny ID", order = 2, centered = true)
    @property:ImportFormat
    var tournamentId: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(columnName = "Típus", order = 3, centered = true)
    @property:ImportFormat
    var type: StageType = StageType.KNOCKOUT,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.NUMBER, min = 1, order = 3, label = "Szint")
    @property:GenerateOverview(columnName = "Szint", order = 4, centered = true)
    @property:ImportFormat
    var level: Int = 1, //ie. Csoportkör-1, Csoportkör-2, Kieséses szakasz-3

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.NUMBER, min = 1, order = 3, label = "Résztvevők száma", note = "Legfeljebb annyi csapat, mint a versenyen résztvevők száma")
    @property:GenerateOverview(columnName = "RésztvevőSzám", order = 5, centered = true)
    @property:ImportFormat
    var participantCount: Int = 1,

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ FullDetails::class ])
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var participants: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ FullDetails::class ])
    @property:GenerateInput(type = InputType.HIDDEN, visible = false, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var seeds: String = "",


    @Column(nullable = false)
    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Következő kör", order = 6, centered = true)
    @property:ImportFormat
    var nextRound: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Status", order = 7, centered = true)
    @property:ImportFormat
    var status: StageStatus = StageStatus.CREATED,

): ManagedEntity {

    fun rounds() = ceil(log2(participantCount.toDouble())).toInt()

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "KnockoutStage",
        view = "control/tournament/knockout-stage",
        showPermission = StaffPermissions.PERMISSION_SHOW_TOURNAMENTS,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TournamentStageEntity) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return this::class.simpleName + "(id = $id, name = $name, tournamentId = $tournamentId, participantCount = $participantCount)"
    }

}

enum class StageType {
    KNOCKOUT,
    GROUP_STAGE,
}
