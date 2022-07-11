package hu.bme.sch.cmsch.component.task

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(TaskComponent::class)
interface TaskEntityRepository : CrudRepository<TaskEntity, Int> {
    fun findAllByHighlightedTrueAndVisibleTrue(): List<TaskEntity>
    fun findAllByVisibleTrue(): List<TaskEntity>
    fun findAllByCategoryIdAndVisibleTrue(categoryId: Int): List<TaskEntity>
}
