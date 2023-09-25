package hu.bme.sch.cmsch.component.task

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(TaskComponent::class)
interface TaskCategoryRepository : CrudRepository<TaskCategoryEntity, Int>,
    EntityPageDataSource<TaskCategoryEntity, Int> {

    override fun findAll(): List<TaskCategoryEntity>
    fun findAllByAvailableFromLessThanAndAvailableToGreaterThan(availableFrom: Long, availableTo: Long): List<TaskCategoryEntity>
    fun findAllByCategoryId(categoryId: Int): List<TaskCategoryEntity>
    fun findAllByType(type: TaskCategoryType): List<TaskCategoryEntity>
    fun findAllByAdvertisedTrue(): List<TaskCategoryEntity>
    fun findAllByAdvertisedTrueAndAvailableFromLessThanAndAvailableToGreaterThan(availableFrom: Long, availableTo: Long): List<TaskCategoryEntity>

}
