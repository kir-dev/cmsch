package hu.bme.sch.cmsch.component.tournament

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

data class MatchCountDto(
    var stageId: Int = 0,
    var matchCount: Long = 0
)

@Repository
@ConditionalOnBean(TournamentComponent::class)
interface TournamentMatchRepository : CrudRepository<TournamentMatchEntity, Int>,
    EntityPageDataSource<TournamentMatchEntity, Int> {

    override fun findAll(): List<TournamentMatchEntity>
    override fun findById(id: Int): Optional<TournamentMatchEntity>
    @Query("select t from TournamentMatchEntity t where t.stageId = ?1")
    fun findAllByStageId(stageId: Int): List<TournamentMatchEntity>

    @Query("""
        SELECT NEW hu.bme.sch.cmsch.component.tournament.MatchCountDto(
            t.stageId,
            COUNT(t.id)
        )
        FROM TournamentMatchEntity t
        GROUP BY t.stageId
    """)
    fun findAllAggregated(): List<MatchCountDto>
}