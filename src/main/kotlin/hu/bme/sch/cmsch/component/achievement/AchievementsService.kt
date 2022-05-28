package hu.bme.sch.cmsch.component.achievement

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.ClockService
import hu.bme.sch.cmsch.util.uploadFile
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

const val HOUR = 60 * 60

@Service
@ConditionalOnBean(AchievementComponent::class)
open class AchievementsService(
    private val achievements: AchievementEntityRepository,
    private val submitted: SubmittedAchievementRepository,
    private val categories: AchievementCategoryRepository,
    private val clock: ClockService
) {

    companion object {
        private val target = "achievement"
    }

    @Transactional(readOnly = true)
    open fun getById(id: Int): Optional<AchievementEntity> {
        return achievements.findById(id)
    }

    @Transactional(readOnly = true)
    open fun getSubmissionForUserOrNull(user: UserEntity, achievement: Optional<AchievementEntity>): SubmittedAchievementEntity? {
        if (achievement.isEmpty)
            return null

        return submitted.findByAchievement_IdAndUserId(achievement.get().id, user.id)
            .orElse(null)
    }

    @Transactional(readOnly = true)
    open fun getSubmissionForGroupOrNull(group: GroupEntity, achievement: Optional<AchievementEntity>): SubmittedAchievementEntity? {
        if (achievement.isEmpty)
            return null

        return submitted.findByAchievement_IdAndGroupId(achievement.get().id, group.id)
                .orElse(null)
    }

    @Transactional(readOnly = true)
    open fun getHighlightedOnes(group: GroupEntity): List<AchievementEntityWrapperDto> {
        return achievements.findAllByHighlightedTrueAndVisibleTrue()
                .map { findSubmissionStatusForGroup(it, group) }
    }

    @Transactional(readOnly = true)
    open fun getAllAchievementsForGroup(group: GroupEntity): List<AchievementEntityWrapperDto> {
        return achievements.findAllByVisibleTrue()
                .map { findSubmissionStatusForGroup(it, group) }
    }

    @Transactional(readOnly = true)
    open fun getAllAchievementsForUser(user: CmschUser): List<AchievementEntityWrapperDto> {
        return achievements.findAllByVisibleTrue()
            .map { findSubmissionStatusForUser(it, user) }
    }

    @Transactional(readOnly = true)
    open fun getAllAchievementsForGuests(): List<AchievementEntityWrapperDto> {
        return achievements.findAllByVisibleTrue()
                .map { AchievementEntityWrapperDto(it, AchievementStatus.NOT_LOGGED_IN, "") }
    }

    private fun findSubmissionStatusForGroup(achievementEntity: AchievementEntity, group: GroupEntity): AchievementEntityWrapperDto {
        val submission = submitted.findByAchievement_IdAndGroupId(achievementEntity.id, group.id)
        return findSubmission(submission, achievementEntity)
    }

    private fun findSubmissionStatusForUser(achievementEntity: AchievementEntity, user: CmschUser): AchievementEntityWrapperDto {
        val submission = submitted.findByAchievement_IdAndUserId(achievementEntity.id, user.id)
        return findSubmission(submission, achievementEntity)
    }

    private fun findSubmission(submission: Optional<SubmittedAchievementEntity>, achievementEntity: AchievementEntity): AchievementEntityWrapperDto {
        return when {
            submission.isEmpty -> AchievementEntityWrapperDto(achievementEntity, AchievementStatus.NOT_SUBMITTED, "")
            submission.get().rejected -> AchievementEntityWrapperDto(achievementEntity, AchievementStatus.REJECTED, submission.get().response)
            submission.get().approved -> AchievementEntityWrapperDto(achievementEntity, AchievementStatus.ACCEPTED, submission.get().response)
            else -> AchievementEntityWrapperDto(achievementEntity, AchievementStatus.SUBMITTED, submission.get().response)
        }
    }


    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun submitAchievementForGroup(answer: AchievementSubmissionDto, file: MultipartFile?, user: UserEntity): AchievementSubmissionStatus {
        val groupId = user.group?.id ?: return AchievementSubmissionStatus.NO_ASSOCIATE_GROUP
        val achievement = achievements.findById(answer.achievementId).orElse(null)
                ?: return AchievementSubmissionStatus.INVALID_ACHIEVEMENT_ID

        if (achievement.availableFrom - (4 * HOUR) > clock.getTimeInSeconds()
                || achievement.availableTo + (4 * HOUR) < clock.getTimeInSeconds()) {
            return AchievementSubmissionStatus.TOO_EARLY_OR_LATE
        }

        val previous = submitted.findByAchievement_IdAndGroupId(answer.achievementId, groupId)
        if (previous.isPresent) {
            val submission = previous.get()
            if (submission.approved)
                return AchievementSubmissionStatus.ALREADY_APPROVED
            if (!submission.approved && !submission.rejected)
                return AchievementSubmissionStatus.ALREADY_SUBMITTED
            return updateSubmission(achievement, answer, file, submission)

        } else {
            return newSubmission(achievement, answer, groupId, null, user, file)
        }
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun submitAchievementForUser(answer: AchievementSubmissionDto, file: MultipartFile?, user: UserEntity): AchievementSubmissionStatus {
        val achievement = achievements.findById(answer.achievementId).orElse(null)
            ?: return AchievementSubmissionStatus.INVALID_ACHIEVEMENT_ID

        if (achievement.availableFrom - (4 * HOUR) > clock.getTimeInSeconds()
            || achievement.availableTo + (4 * HOUR) < clock.getTimeInSeconds()) {
            return AchievementSubmissionStatus.TOO_EARLY_OR_LATE
        }

        val previous = submitted.findByAchievement_IdAndUserId(answer.achievementId, user.id)
        if (previous.isPresent) {
            val submission = previous.get()
            if (submission.approved)
                return AchievementSubmissionStatus.ALREADY_APPROVED
            if (!submission.approved && !submission.rejected)
                return AchievementSubmissionStatus.ALREADY_SUBMITTED
            return updateSubmission(achievement, answer, file, submission)

        } else {
            return newSubmission(achievement, answer, null, user.id, user, file)
        }
    }

    private fun newSubmission(
        achievement: AchievementEntity,
        answer: AchievementSubmissionDto,
        groupId: Int?,
        userId: Int?,
        user: UserEntity,
        file: MultipartFile?
    ): AchievementSubmissionStatus {

        val groupName = if (groupId != null) user.group?.name else null
        val userName = if (userId != null) user.fullName else null

        when (achievement.type) {
            AchievementType.TEXT -> {
                if (answer.textAnswer.isBlank())
                    return AchievementSubmissionStatus.EMPTY_ANSWER

                submitted.save(SubmittedAchievementEntity(
                    0, achievement, groupId, groupName ?: "",
                    userId, userName ?: "",
                    achievement.categoryId, answer.textAnswer, "",
                    "", approved = false, rejected = false, score = 0
                ))
                return AchievementSubmissionStatus.OK

            }
            AchievementType.IMAGE -> {
                if (file == null || fileNameInvalid(file))
                    return AchievementSubmissionStatus.INVALID_IMAGE

                val fileName = file.uploadFile(target)

                submitted.save(SubmittedAchievementEntity(
                    0, achievement, groupId, groupName ?: "",
                    userId, userName ?: "",
                    achievement.categoryId, "", "$target/$fileName",
                    "", approved = false, rejected = false, score = 0
                ))
                return AchievementSubmissionStatus.OK

            }
            AchievementType.BOTH -> {
                val fileName = file?.uploadFile(target) ?: ""

                submitted.save(SubmittedAchievementEntity(
                    0, achievement, groupId, groupName ?: "",
                    userId, userName ?: "",
                    achievement.categoryId, answer.textAnswer, "$target/$fileName",
                    "", approved = false, rejected = false, score = 0
                ))
                return AchievementSubmissionStatus.OK

            }
            else -> {
                throw IllegalStateException("Invalid achievement type: " + achievement.type)
            }
        }
    }

    private fun updateSubmission(
        achievement: AchievementEntity,
        answer: AchievementSubmissionDto,
        file: MultipartFile?,
        submission: SubmittedAchievementEntity
    ): AchievementSubmissionStatus {

        when (achievement.type) {
            AchievementType.TEXT -> {
                if (answer.textAnswer.isBlank())
                    return AchievementSubmissionStatus.EMPTY_ANSWER

                submission.textAnswer = answer.textAnswer
                submission.rejected = false
                submitted.save(submission)
                return AchievementSubmissionStatus.OK

            }
            AchievementType.IMAGE -> {
                if (file == null || fileNameInvalid(file))
                    return AchievementSubmissionStatus.INVALID_IMAGE
                val fileName = file.uploadFile(target)

                submission.imageUrlAnswer = "$target/$fileName"
                submission.rejected = false
                submitted.save(submission)
                return AchievementSubmissionStatus.OK

            }
            AchievementType.BOTH -> {
                if (file != null && !fileNameInvalid(file)) {
                    val fileName = file.uploadFile(target)
                    submission.imageUrlAnswer = "$target/$fileName"
                }
                submission.textAnswer = answer.textAnswer
                submission.rejected = false
                submitted.save(submission)
                return AchievementSubmissionStatus.OK

            }
            else -> {
                throw IllegalStateException("Invalid achievement type: " + achievement.type)
            }
        }
    }

    private fun fileNameInvalid(file: MultipartFile): Boolean {
        return file.originalFilename == null || (
                !file.originalFilename!!.lowercase().endsWith(".png")
                        && !file.originalFilename!!.lowercase().endsWith(".jpg")
                        && !file.originalFilename!!.lowercase().endsWith(".jpeg")
                        && !file.originalFilename!!.lowercase().endsWith(".gif")
                )
    }

    @Transactional(readOnly = true)
    fun getCategoriesForGroup(groupId: Int): List<AchievementCategoryDto> {
        val submissionByCategory = submitted.findAllByGroupId(groupId)
                .groupBy { it.categoryId }

        val achievementByCategory = achievements.findAllByVisibleTrue()

        return categories.findAll()
                .map { category ->
                    return@map AchievementCategoryDto(
                            name = category.name,
                            categoryId = category.categoryId,
                            availableFrom = category.availableFrom,
                            availableTo = category.availableTo,

                            sum = achievementByCategory.count { it.categoryId == category.categoryId },
                            approved = submissionByCategory[category.categoryId]?.count { it.approved } ?: 0,
                            rejected = submissionByCategory[category.categoryId]?.count { it.rejected } ?: 0,
                            notGraded = submissionByCategory[category.categoryId]?.count { !it.approved && !it.rejected } ?: 0
                    )
                }
    }

    @Transactional(readOnly = true)
    fun getCategoriesForUser(userId: Int): List<AchievementCategoryDto> {
        val submissionByCategory = submitted.findAllByUserId(userId)
            .groupBy { it.categoryId }

        val achievementByCategory = achievements.findAllByVisibleTrue()

        return categories.findAll()
            .map { category ->
                return@map AchievementCategoryDto(
                    name = category.name,
                    categoryId = category.categoryId,
                    availableFrom = category.availableFrom,
                    availableTo = category.availableTo,

                    sum = achievementByCategory.count { it.categoryId == category.categoryId },
                    approved = submissionByCategory[category.categoryId]?.count { it.approved } ?: 0,
                    rejected = submissionByCategory[category.categoryId]?.count { it.rejected } ?: 0,
                    notGraded = submissionByCategory[category.categoryId]?.count { !it.approved && !it.rejected } ?: 0
                )
            }
    }

    @Transactional(readOnly = true)
    fun getCategoryName(categoryId: Int): String {
        return categories.findAllByCategoryId(categoryId).map { it.name }.firstOrNull() ?: "Nincs ilyen"
    }

    @Transactional(readOnly = true)
    fun getCategoryAvailableFrom(categoryId: Int): Long {
        return categories.findAllByCategoryId(categoryId).map { it.availableFrom }.firstOrNull() ?: 0
    }

    @Transactional(readOnly = true)
    fun getCategory(categoryId: Int): AchievementCategoryEntity? {
        return categories.findAllByCategoryId(categoryId).firstOrNull()
    }

    @Transactional(readOnly = true)
    fun getAllSubmissions(it: GroupEntity): List<SubmittedAchievementEntity> {
        return submitted.findAllByGroupId(it.id)
    }

    @Transactional(readOnly = true)
    fun getAllCategories(): List<AchievementCategoryEntity> {
        return categories.findAll()
    }

    @Transactional(readOnly = true)
    fun getTotalAchievementsForUser(user: UserEntity): Int {
        return achievements.findAllByVisibleTrue().size
    }

    @Transactional(readOnly = true)
    fun getSubmittedAchievementsForUser(user: UserEntity): Int {
        return submitted.findAllByUserIdAndRejectedFalseAndApprovedFalse(user.id).size
    }

    @Transactional(readOnly = true)
    fun getCompletedAchievementsForUser(user: UserEntity): Int {
        return submitted.findAllByUserIdAndRejectedFalseAndApprovedTrue(user.id).size
    }

}
