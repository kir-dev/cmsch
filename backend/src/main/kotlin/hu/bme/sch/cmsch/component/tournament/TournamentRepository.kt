package hu.bme.sch.cmsch.component.tournament

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(TournamentComponent::class)
interface TournamentRepository : CrudRepository<TournamentEntity, Int>,
    EntityPageDataSource<TournamentEntity, Int> {

    override fun findAll(): List<TournamentEntity>

}
