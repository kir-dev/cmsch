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
interface KnockoutStageRepository : CrudRepository<KnockoutStageEntity, Int>,
    EntityPageDataSource<KnockoutStageEntity, Int> {

    override fun findAll(): List<KnockoutStageEntity>
    override fun findById(id: Int): Optional<KnockoutStageEntity>
    fun findAllByTournamentId(tournamentId: Int): List<KnockoutStageEntity>

    @Query("""
        SELECT NEW hu.bme.sch.cmsch.component.tournament.StageCountDto(
            s.tournamentId,
            COUNT(s.id)
        )
        FROM KnockoutStageEntity s
        GROUP BY s.tournamentId
    """)
    fun findAllAggregated(): List<StageCountDto>


    @Query("select k from KnockoutStageEntity k where k.tournamentId = ?1 and k.level = ?2")
    fun findAllByTournamentIdAndLevel(tournamentId: Int, level: Int): List<KnockoutStageEntity>

    fun deleteAllByTournamentId(tournamentId: Int): Int

}