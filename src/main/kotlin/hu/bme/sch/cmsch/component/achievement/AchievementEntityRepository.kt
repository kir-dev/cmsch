package hu.bme.sch.cmsch.component.achievement

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(AchievementComponent::class)
interface AchievementEntityRepository : CrudRepository<AchievementEntity, Int> {
    fun findAllByHighlightedTrueAndVisibleTrue(): List<AchievementEntity>
    fun findAllByVisibleTrue(): List<AchievementEntity>
}
