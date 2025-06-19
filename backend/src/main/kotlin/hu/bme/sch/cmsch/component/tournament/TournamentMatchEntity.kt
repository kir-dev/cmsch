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
    HT,
    FT,
    AET,
    AP,
    IN_PROGRESS,
    CANCELLED
}

@Entity
@Table(name = "tournament_match")
@ConditionalOnBean(TournamentComponent::class)
data class TournamentMatchEntity(

    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID")
    override var id: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.NUMBER, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "Game ID")
    @property:ImportFormat
    var gameId: Int = 0,

    @Column(nullable = false)
    @property:GenerateInput(type = InputType.NUMBER, min = 1, order = 1, label = "Stage ID")
    @property:GenerateOverview(columnName = "Stage ID", order = 1)
    @property:ImportFormat
    var stageId: Int = 0,

    @Column(nullable = false)
    @property:GenerateInput(type = InputType.NUMBER, min = 1, order = 1, label = "Level")
    @property:GenerateOverview(columnName = "Level", order = 1)
    @property:ImportFormat
    var level: Int = 0,

    @Column(nullable = false)
    @property:GenerateInput(type = InputType.NUMBER, min = Int.MIN_VALUE, order = 2, label = "Home seed")
    var homeSeed: Int = 0,

    @Column(nullable = true)
    var homeTeamId: Int? = null,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.TEXT, order = 2, label = "Home team name")
    @property:GenerateOverview(columnName = "Home team name", order = 2)
    @property:ImportFormat
    var homeTeamName: String = "",

    @Column(nullable = false)
    @property:GenerateInput(type = InputType.NUMBER, min = Int.MIN_VALUE, order = 3, label = "Away seed")
    var awaySeed: Int = 0,

    @Column(nullable = true)
    var awayTeamId: Int? = null,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.TEXT, min = 1, order = 3, label = "Away team name")
    @property:GenerateOverview(columnName = "Away team name", order = 3)
    @property:ImportFormat
    var awayTeamName: String = "",

    @Column(nullable = true)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.DATE, order = 4, label = "Kickoff time")
    @property:GenerateOverview(columnName = "Kickoff time", order = 4)
    @property:ImportFormat
    var kickoffTime: Long = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.TEXT, order = 5, label = "Location")
    @property:GenerateOverview(columnName = "Location", order = 5)
    @property:ImportFormat
    var location: String = "",

    @Column(nullable = true)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Home team score", order = 6)
    @property:ImportFormat
    var homeTeamScore: Int? = null,

    @Column(nullable = true)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Away team score", order = 7)
    @property:ImportFormat
    var awayTeamScore: Int? = null,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 8, label = "Match status",
        source = [ "NOT_STARTED", "FIRST_HALF", "HT", "SECOND_HALF", "FT", "EXTRA_TIME", "AET", "PENALTY_KICKS", "AP", "IN_PROGRESS", "CANCELLED" ],
        visible = false, ignore = true
    )
    @property:GenerateOverview(columnName = "Match status", order = 8)
    @property:ImportFormat
    val status: MatchStatus = MatchStatus.NOT_STARTED,

): ManagedEntity{

    fun stage(): KnockoutStageEntity? = KnockoutStageService.getBean().findById(stageId)

    @PrePersist
    @PreUpdate
    fun setTeams() {
        val teams = KnockoutStageService.getBean().getParticipants(stageId)
        homeTeamId = teams.find { it.teamName == homeTeamName }?.teamId
        awayTeamId = teams.find { it.teamName == awayTeamName }?.teamId
    }

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

    override fun hashCode(): Int = javaClass.hashCode()

    fun winnerId(): Int? {
        return when {
            status in listOf(MatchStatus.NOT_STARTED, MatchStatus.IN_PROGRESS, MatchStatus.CANCELLED) -> null
            homeTeamScore == null || awayTeamScore == null -> null
            homeTeamScore!! > awayTeamScore!! -> homeTeamId
            homeTeamScore!! < awayTeamScore!! -> awayTeamId
            else -> null
        }
    }

    fun isDraw(): Boolean {
        return when {
            status in listOf(MatchStatus.NOT_STARTED, MatchStatus.IN_PROGRESS, MatchStatus.CANCELLED) -> false
            homeTeamScore == null || awayTeamScore == null -> false
            homeTeamScore!! == awayTeamScore!! -> true
            else -> false
        }
    }

}
