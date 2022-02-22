package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.AchievementEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AchievementRepository : CrudRepository<AchievementEntity, Int> {
    fun findAllByHighlightedTrueAndVisibleTrue(): List<AchievementEntity>
    fun findAllByVisibleTrue(): List<AchievementEntity>
}