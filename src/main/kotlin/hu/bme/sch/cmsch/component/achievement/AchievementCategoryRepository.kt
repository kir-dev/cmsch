package hu.bme.sch.cmsch.component.achievement

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(AchievementComponent::class)
interface AchievementCategoryRepository : CrudRepository<AchievementCategoryEntity, Int> {
    override fun findAll(): List<AchievementCategoryEntity>
    fun findAllByCategoryId(categoryId: Int): List<AchievementCategoryEntity>
}
