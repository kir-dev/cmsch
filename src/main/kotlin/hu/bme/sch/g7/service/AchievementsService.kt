package hu.bme.sch.g7.service

import hu.bme.sch.g7.dao.AchievementRepository
import hu.bme.sch.g7.dao.SubmittedAchievementRepository
import hu.bme.sch.g7.dto.AchievementEntityWrapper
import hu.bme.sch.g7.dto.AchievementStatus
import hu.bme.sch.g7.model.AchievementEntity
import hu.bme.sch.g7.model.GroupEntity
import hu.bme.sch.g7.model.SubmittedAchievementEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Suppress("RedundantModalityModifier") // Spring transactional proxy requires it not to be final
@Service
open class AchievementsService(
        val achievements: AchievementRepository,
        val submitted: SubmittedAchievementRepository
) {

    @Transactional(readOnly = true)
    fun getById(id: Int): Optional<AchievementEntity> {
        return achievements.findById(id)
    }

    @Transactional(readOnly = true)
    fun getSubmissionOrNull(group: GroupEntity, achievement: Optional<AchievementEntity>): SubmittedAchievementEntity? {
        if (achievement.isEmpty)
            return null

        return submitted.findByAchievement_IdAndGroupId(achievement.get().id, group.id)
                .orElse(null)
    }

    // FIXME: Optimize
    @Transactional(readOnly = true)
    fun getHighlightedOnes(group: GroupEntity): List<AchievementEntityWrapper> {
        return achievements.findAllByHighlightedTrue()
                .map { AchievementEntityWrapper(it, findSubmissionStatus(it, group)) }
    }

    // FIXME: Optimize
    @Transactional(readOnly = true)
    fun getAllAchievements(group: GroupEntity): List<AchievementEntityWrapper> {
        return achievements.findAll()
                .map { AchievementEntityWrapper(it, findSubmissionStatus(it, group)) }
    }

    private fun findSubmissionStatus(
            achievementEntity: AchievementEntity,
            group: GroupEntity
    ): AchievementStatus {
        val submission = submitted.findByAchievement_IdAndGroupId(achievementEntity.id, group.id)
        return when {
            submission.isEmpty -> AchievementStatus.NOT_SUBMITTED
            submission.get().rejected -> AchievementStatus.REJECTED
            submission.get().approved -> AchievementStatus.ACCEPTED
            else -> AchievementStatus.SUBMITTED
        }
    }

}