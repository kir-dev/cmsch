package hu.bme.sch.cmsch.component.task

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

data class TaskCountByCategory(val categoryId: Int, val count: Long)

data class TaskNameView(val id: Int, val title: String)

@Repository
@ConditionalOnBean(TaskComponent::class)
interface TaskEntityRepository : CrudRepository<TaskEntity, Int>,
    EntityPageDataSource<TaskEntity, Int> {

    fun findAllByHighlightedTrueAndVisibleTrue(): List<TaskEntity>
    fun findAllByVisibleTrue(): List<TaskEntity>

    @Query("SELECT NEW hu.bme.sch.cmsch.component.task.TaskNameView(e.id, e.title) FROM TaskEntity e")
    fun findAllTaskNameView(): List<TaskNameView>

    fun findAllByVisibleTrueAndAvailableFromLessThanAndAvailableToGreaterThan(availableFrom: Long, availableTo: Long): List<TaskEntity>
    fun countAllByVisibleTrue(): Int
    fun findAllByCategoryIdAndVisibleTrue(categoryId: Int): List<TaskEntity>
    fun findAllByTag(tag: String): List<TaskEntity>
    fun findTop1ByTag(tag: String): List<TaskEntity>

    @Query("SELECT NEW hu.bme.sch.cmsch.component.task.TaskCountByCategory(t.categoryId, COUNT(t)) FROM TaskEntity t WHERE t.visible = true AND t.availableFrom < :now AND t.availableTo > :now GROUP BY t.categoryId")
    fun findTaskCountByCategory(now: Long): List<TaskCountByCategory>

    fun findAllByVisibleTrueAndCategoryId(categoryId: Int): List<TaskEntity>

    @Query("""SELECT NEW hu.bme.sch.cmsch.component.task.TaskEntity(
        t.id, 
        t.title, 
        t.categoryId, 
        '', 
        '', 
        '', 
        t.type,
        t.format, 
        '',
        t.availableFrom,
        t.availableTo, 
        t.maxScore, 
        t.visible, 
        t.highlighted, 
        t.order, 
        t.tag) 
        FROM TaskEntity t
    """)
    fun findAllWithoutLobs(): List<TaskEntity>

}
