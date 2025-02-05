package hu.bme.sch.cmsch.component.tournament

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

data class MatchCountDto(
    var tournamentId: Int = 0,
    var tournamentName: String = "",
    var tournamentLocation : String = "",
    var matchCount: Long = 0
)

@Repository
@ConditionalOnBean(TournamentComponent::class)
interface TournamentMatchRepository : CrudRepository<TournamentMatchEntity, Int>,
    EntityPageDataSource<TournamentMatchEntity, Int> {

    override fun findAll(): List<TournamentMatchEntity>
    override fun findById(id: Int): Optional<TournamentMatchEntity>
    fun findAllByStageId(stageId: Int): List<TournamentMatchEntity>
    @Query("select t from TournamentMatchEntity t where t.stage.tournamentId = ?1")
    fun findAllByStageTournamentId(tournamentId: Int): List<TournamentMatchEntity>

    @Query("""
        SELECT NEW hu.bme.sch.cmsch.component.tournament.MatchCountDto(
            s.tournament.id,
            s.tournament.title,
            s.tournament.location,
            COUNT(t.id)
        )
        FROM TournamentMatchEntity t
        JOIN t.stage s
        GROUP BY s.tournament
    """)
    fun findAllAggregated(): List<MatchCountDto>
}