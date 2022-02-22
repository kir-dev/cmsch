package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.AchievementCategoryEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AchievementCategoryRepository : CrudRepository<AchievementCategoryEntity, Int> {
    override fun findAll(): List<AchievementCategoryEntity>
    fun findAllByCategoryId(categoryId: Int): List<AchievementCategoryEntity>
}