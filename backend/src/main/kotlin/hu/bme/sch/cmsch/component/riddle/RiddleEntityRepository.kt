package hu.bme.sch.cmsch.component.riddle

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(RiddleComponent::class)
interface RiddleEntityRepository : CrudRepository<RiddleEntity, Int> {
    override fun findAll(): List<RiddleEntity>
    fun findAllByCategoryId(categoryId: Int): List<RiddleEntity>
    fun findAllByCategoryIdIn(categories: List<Int>): List<RiddleEntity>
}
