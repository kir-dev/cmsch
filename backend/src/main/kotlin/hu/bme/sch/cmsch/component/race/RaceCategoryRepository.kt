package hu.bme.sch.cmsch.component.race

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@ConditionalOnBean(RaceComponent::class)
interface RaceCategoryRepository : CrudRepository<RaceCategoryEntity, Int>,
    EntityPageDataSource<RaceCategoryEntity, Int> {

    override fun findAll(): List<RaceCategoryEntity>
    fun findByVisibleTrueAndSlug(category: String): Optional<RaceCategoryEntity>
    override fun findById(id: Int): Optional<RaceCategoryEntity>
    fun findAllByVisibleTrue(): List<RaceCategoryEntity>

}
