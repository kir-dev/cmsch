package hu.bme.sch.cmsch.component.race

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(RaceComponent::class)
interface RaceRecordRepository : CrudRepository<RaceRecordEntity, Int> {

    override fun findAll(): List<RaceRecordEntity>

}
