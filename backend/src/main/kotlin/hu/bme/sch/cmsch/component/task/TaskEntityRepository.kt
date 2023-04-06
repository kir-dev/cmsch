package hu.bme.sch.cmsch.component.task

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(TaskComponent::class)
interface TaskEntityRepository : CrudRepository<TaskEntity, Int>,
    EntityPageDataSource<TaskEntity, Int> {

    fun findAllByHighlightedTrueAndVisibleTrue(): List<TaskEntity>
    fun findAllByVisibleTrue(): List<TaskEntity>
    fun findAllByCategoryIdAndVisibleTrue(categoryId: Int): List<TaskEntity>
    fun findAllByTag(tag: String): List<TaskEntity>
}
