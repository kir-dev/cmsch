package hu.bme.sch.cmsch.component.tournament

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

data class StageCountDto(
    var tournamentId: Int = 0,
    var stageCount: Long = 0
)

@Repository
@ConditionalOnBean(TournamentComponent::class)
interface TournamentStageRepository : CrudRepository<TournamentStageEntity, Int>,
    EntityPageDataSource<TournamentStageEntity, Int> {

    override fun findAll(): List<TournamentStageEntity>
    override fun findById(id: Int): Optional<TournamentStageEntity>
    fun findAllByTournamentId(tournamentId: Int): List<TournamentStageEntity>

    @Query(
        """
        SELECT NEW hu.bme.sch.cmsch.component.tournament.StageCountDto(
            s.tournamentId,
            COUNT(s.id)
        )
        FROM TournamentStageEntity s
        GROUP BY s.tournamentId
    """
    )
    fun findAllAggregated(): List<StageCountDto>


    fun findByTournamentIdAndLevel(tournamentId: Int, level: Int): Optional<TournamentStageEntity>

    fun deleteAllByTournamentId(tournamentId: Int): Int

}