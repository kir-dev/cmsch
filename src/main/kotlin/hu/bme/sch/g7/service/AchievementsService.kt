package hu.bme.sch.g7.service

import hu.bme.sch.g7.dao.AchievementCategoryRepository
import hu.bme.sch.g7.dao.AchievementRepository
import hu.bme.sch.g7.dao.SubmittedAchievementRepository
import hu.bme.sch.g7.dto.*
import hu.bme.sch.g7.model.*
import hu.bme.sch.g7.util.uploadFile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.lang.IllegalStateException
import java.util.*

const val HOUR = 60 * 60

@Suppress("RedundantModalityModifier") // Spring transactional proxy requires it not to be final
@Service
open class AchievementsService(
        val achievements: AchievementRepository,
        val submitted: SubmittedAchievementRepository,
        val categories: AchievementCategoryRepository,
        val clock: ClockService
) {

    companion object {
        private val target = "achievement"
    }

    @Transactional(readOnly = true)
    open fun getById(id: Int): Optional<AchievementEntity> {
        return achievements.findById(id)
    }

    @Transactional(readOnly = true)
    open fun getSubmissionOrNull(group: GroupEntity, achievement: Optional<AchievementEntity>): SubmittedAchievementEntity? {
        if (achievement.isEmpty)
            return null

        return submitted.findByAchievement_IdAndGroupId(achievement.get().id, group.id)
                .orElse(null)
    }

    @Transactional(readOnly = true)
    open fun getHighlightedOnes(group: GroupEntity): List<AchievementEntityWrapper> {
        return achievements.findAllByHighlightedTrueAndVisibleTrue()
                .map { findSubmissionStatus(it, group) }
    }

    @Transactional(readOnly = true)
    open fun getAllAchievements(group: GroupEntity): List<AchievementEntityWrapper> {
        return achievements.findAll()
                .map { findSubmissionStatus(it, group) }
    }

    @Transactional(readOnly = true)
    open fun getAllAchievementsForGuests(): List<AchievementEntityWrapper> {
        return achievements.findAll()
                .map { AchievementEntityWrapper(it, AchievementStatus.NOT_LOGGED_IN, "") }
    }

    private fun findSubmissionStatus(
            achievementEntity: AchievementEntity,
            group: GroupEntity
    ): AchievementEntityWrapper {
        val submission = submitted.findByAchievement_IdAndGroupId(achievementEntity.id, group.id)
        return when {
            submission.isEmpty -> AchievementEntityWrapper(achievementEntity, AchievementStatus.NOT_SUBMITTED, "")
            submission.get().rejected -> AchievementEntityWrapper(achievementEntity, AchievementStatus.REJECTED, submission.get().response)
            submission.get().approved -> AchievementEntityWrapper(achievementEntity, AchievementStatus.ACCEPTED, submission.get().response)
            else -> AchievementEntityWrapper(achievementEntity, AchievementStatus.SUBMITTED, submission.get().response)
        }
    }

    @Transactional
    open fun submitAchievement(
            answer: AchievementSubmissionDto,
            file: MultipartFile?,
            user: UserEntity
    ): AchievementSubmissionStatus {
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
            return newSubmission(achievement, answer, groupId, user, file)
        }

    }

    private fun newSubmission(
            achievement: AchievementEntity,
            answer: AchievementSubmissionDto,
            groupId: Int,
            user: UserEntity,
            file: MultipartFile?
    ): AchievementSubmissionStatus {

        if (achievement.type == AchievementType.TEXT) {
            if (answer.textAnswer.isBlank())
                return AchievementSubmissionStatus.EMPTY_ANSWER

            submitted.save(SubmittedAchievementEntity(
                    0, achievement, groupId, user.group?.name ?: "",
                    achievement.categoryId, answer.textAnswer, "",
                    "", false, false, 0
            ))
            return AchievementSubmissionStatus.OK

        } else if (achievement.type == AchievementType.IMAGE) {
            if (file == null || fileNameInvalid(file))
                return AchievementSubmissionStatus.INVALID_IMAGE

            val fileName = file.uploadFile(target)

            submitted.save(SubmittedAchievementEntity(
                    0, achievement, groupId, user.group?.name ?: "",
                    achievement.categoryId, "", "$target/$fileName",
                    "", false, false, 0
            ))
            return AchievementSubmissionStatus.OK

        } else if (achievement.type == AchievementType.BOTH) {
            val fileName = file?.uploadFile(target) ?: ""

            submitted.save(SubmittedAchievementEntity(
                    0, achievement, groupId, user.group?.name ?: "",
                    achievement.categoryId, answer.textAnswer, "$target/$fileName",
                    "", false, false, 0
            ))
            return AchievementSubmissionStatus.OK

        } else {
            throw IllegalStateException("Invalid achievement type: " + achievement.type)
        }
    }

    private fun updateSubmission(
            achievement: AchievementEntity,
            answer: AchievementSubmissionDto,
            file: MultipartFile?,
            submission: SubmittedAchievementEntity
    ): AchievementSubmissionStatus {

        if (achievement.type == AchievementType.TEXT) {
            if (answer.textAnswer.isBlank())
                return AchievementSubmissionStatus.EMPTY_ANSWER

            submission.textAnswer = answer.textAnswer
            submission.rejected = false
            submitted.save(submission)
            return AchievementSubmissionStatus.OK

        } else if (achievement.type == AchievementType.IMAGE) {
            if (file == null || fileNameInvalid(file))
                return AchievementSubmissionStatus.INVALID_IMAGE
            val fileName = file.uploadFile(target)

            submission.imageUrlAnswer = "$target/$fileName"
            submission.rejected = false
            submitted.save(submission)
            return AchievementSubmissionStatus.OK

        } else if (achievement.type == AchievementType.BOTH) {
            if (file != null && !fileNameInvalid(file)) {
                val fileName = file.uploadFile(target)
                submission.imageUrlAnswer = "$target/$fileName"
            }
            submission.textAnswer = answer.textAnswer
            submission.rejected = false
            submitted.save(submission)
            return AchievementSubmissionStatus.OK

        } else {
            throw IllegalStateException("Invalid achievement type: " + achievement.type)
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
    fun getCategories(groupId: Int): List<AchievementCategoryDto> {
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
                            approved = submissionByCategory[category.id]?.count { it.approved } ?: 0,
                            rejected = submissionByCategory[category.id]?.count { it.rejected } ?: 0,
                            notGraded = submissionByCategory[category.id]?.count { !it.approved && !it.rejected } ?: 0
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

}