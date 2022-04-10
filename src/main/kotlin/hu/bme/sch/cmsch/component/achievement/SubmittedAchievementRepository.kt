package hu.bme.sch.cmsch.component.achievement

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SubmittedAchievementRepository : CrudRepository<SubmittedAchievementEntity, Int> {
    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findByAchievement_IdAndGroupId(achievementId: Int, groupId: Int): Optional<SubmittedAchievementEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findByAchievement_IdAndUserId(achievementId: Int, groupId: Int): Optional<SubmittedAchievementEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findByAchievement_Id(achievementId: Int): List<SubmittedAchievementEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findByAchievement_IdAndRejectedIsFalseAndApprovedIsFalse(achievementId: Int): List<SubmittedAchievementEntity>

    fun findAllByScoreGreaterThanAndApprovedIsTrue(zero: Int): List<SubmittedAchievementEntity>

    fun findAllByGroupId(groupId: Int): List<SubmittedAchievementEntity>

    fun findAllByUserId(userId: Int): List<SubmittedAchievementEntity>

    fun findAllByUserIdAndRejectedFalseAndApprovedFalse(userId: Int): List<SubmittedAchievementEntity>

    fun findAllByUserIdAndRejectedFalseAndApprovedTrue(userId: Int): List<SubmittedAchievementEntity>
}
