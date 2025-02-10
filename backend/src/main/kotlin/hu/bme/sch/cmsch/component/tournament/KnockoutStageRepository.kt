package hu.bme.sch.cmsch.component.tournament

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

data class StageCountDto(
    var tournamentId: Int = 0,
    var tournamentName: String = "",
    var tournamentLocation : String = "",
    var participantCount: Int = 0,
    var stageCount: Long = 0
)

@Repository
@ConditionalOnBean(TournamentComponent::class)
interface KnockoutStageRepository : CrudRepository<KnockoutStageEntity, Int>,
    EntityPageDataSource<KnockoutStageEntity, Int> {

    override fun findAll(): List<KnockoutStageEntity>
    override fun findById(id: Int): Optional<KnockoutStageEntity>
    @Query("select k from KnockoutStageEntity k where k.tournament.id = ?1")
    fun findAllByTournamentId(tournamentId: Int): List<KnockoutStageEntity>

    @Query("""
        SELECT NEW hu.bme.sch.cmsch.component.tournament.StageCountDto(
            s.tournament.id,
            s.tournament.title,
            s.tournament.location,
            s.tournament.participantCount,
            COUNT(s.id)
        )
        FROM KnockoutStageEntity s
        GROUP BY s.tournament
    """)
    fun findAllAggregated(): List<StageCountDto>

}