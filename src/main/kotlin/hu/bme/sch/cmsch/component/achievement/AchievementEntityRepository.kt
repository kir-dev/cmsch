package hu.bme.sch.cmsch.component.achievement

import hu.bme.sch.cmsch.component.achievement.AchievementEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AchievementEntityRepository : CrudRepository<AchievementEntity, Int> {
    fun findAllByHighlightedTrueAndVisibleTrue(): List<AchievementEntity>
    fun findAllByVisibleTrue(): List<AchievementEntity>
}
