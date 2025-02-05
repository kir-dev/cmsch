package hu.bme.sch.cmsch.component.tournament

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.*
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

enum class MatchStatus {
    NOT_STARTED,
    FIRST_HALF,
    HALF_TIME,
    SECOND_HALF,
    FULL_TIME,
    EXTRA_TIME,
    PENALTY_KICKS,
    CANCELLED,
    FINISHED
}

@Entity
@Table(name = "tournament_match")
@ConditionalOnBean(TournamentComponent::class)
data class TournamentMatchEntity(

    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID")
    override var id: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, min = 1, order = 1, label = "Stage ID")
    @property:GenerateOverview(columnName = "Stage ID", order = 1)
    @property:ImportFormat
    var stageId: Int = 0,

    @ManyToOne(targetEntity = KnockoutStageEntity::class)
    @JoinColumn(name = "stageId", insertable = false, updatable = false)
    var stage: KnockoutStageEntity? = null,

    @Column(nullable = false)
    var homeTeamId: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, min = 1, order = 2, label = "Home team name")
    @property:GenerateOverview(columnName = "Home team name", order = 2)
    @property:ImportFormat
    var homeTeamName: String = "",

    @Column(nullable = false)
    var awayTeamId: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, min = 1, order = 3, label = "Away team name")
    @property:GenerateOverview(columnName = "Away team name", order = 3)
    @property:ImportFormat
    var awayTeamName: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 4, label = "Kickoff time")
    @property:GenerateOverview(columnName = "Kickoff time", order = 4)
    @property:ImportFormat
    var kickoffTime: Long = 0,

    @Column(nullable = true)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, min = 0, order = 5, label = "Home team score")
    @property:GenerateOverview(columnName = "Home team score", order = 5)
    @property:ImportFormat
    var homeTeamScore: Int? = null,

    @Column(nullable = true)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, min = 0, order = 6, label = "Away team score")
    @property:GenerateOverview(columnName = "Away team score", order = 6)
    @property:ImportFormat
    var awayTeamScore: Int? = null,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 7, label = "Match status",
        source = [ "NOT_STARTED", "FIRST_HALF", "HT", "SECOND_HALF", "FT", "EXTRA_TIME", "AET", "PENALTY_KICKS", "AP","CANCELLED" ],
        visible = false, ignore = true
    )
    @property:GenerateOverview(columnName = "Match status", order = 7)
    @property:ImportFormat
    val status: MatchStatus = MatchStatus.NOT_STARTED,

): ManagedEntity{

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "TournamentMatch",
        view = "control/tournament/match",
        showPermission = StaffPermissions.PERMISSION_SHOW_TOURNAMENTS,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TournamentMatchEntity

        return id != 0 && id == other.id
    }

    override fun toString(): String {
        return javaClass.simpleName + "(id = $id)"
    }

}
