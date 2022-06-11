package hu.bme.sch.cmsch.component.task

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(TaskComponent::class)
interface TaskCategoryRepository : CrudRepository<TaskCategoryEntity, Int> {
    override fun findAll(): List<TaskCategoryEntity>
    fun findAllByCategoryId(categoryId: Int): List<TaskCategoryEntity>
}
