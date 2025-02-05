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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import java.security.Provider
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.pow


@Entity
@Table(name = "knockout_stage")
@ConditionalOnBean(TournamentComponent::class)
data class KnockoutStageEntity(

    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID")
    override var id: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 1, label = "Szakasz neve")
    @property:GenerateOverview(columnName = "Név", order = 1)
    @property:ImportFormat
    var name: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, min = 1, order = 2, label = "Verseny ID")
    @property:GenerateOverview(columnName = "Verseny ID", order = 2)
    @property:ImportFormat
    var tournamentId: Int = 0,

    @ManyToOne(targetEntity = TournamentEntity::class)
    @JoinColumn(name = "tournamentId", insertable = false, updatable = false)
    var tournament: TournamentEntity? = null,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, min = 1, order = 3, label = "Szint")
    @property:GenerateOverview(columnName = "Szint", order = 3, centered = true)
    @property:ImportFormat
    var level: Int = 1, //ie. Csoportkör-1, Csoportkör-2, Kieséses szakasz-3

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, min = 1, order = 3, label = "Résztvevők száma")
    @property:GenerateOverview(columnName = "RésztvevőSzám", order = 3, centered = true)
    @property:ImportFormat
    var participantCount: Int = 1,

    @Column(nullable = false)
    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Következő kör", order = 4, centered = true)
    @property:ImportFormat
    var nextRound: Int = 0,

): ManagedEntity {

    @Autowired
    @Transient
    private lateinit var knockoutStageService: KnockoutStageService

    fun rounds() = ceil(log2(participantCount.toDouble())).toInt() + 1
    fun matches() = 2.0.pow(ceil(log2(participantCount.toDouble()))).toInt() - 1

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "KnockoutStage",
        view = "control/tournament/knockout-stage",
        showPermission = StaffPermissions.PERMISSION_SHOW_TOURNAMENTS,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KnockoutStageEntity) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return this::class.simpleName + "(id = $id, name = $name, tournamentId = $tournament.id, participantCount = $participantCount)"
    }


    @PrePersist
    fun onPrePersist(){
       knockoutStageService.createMatchesForStage(this)
    }

}