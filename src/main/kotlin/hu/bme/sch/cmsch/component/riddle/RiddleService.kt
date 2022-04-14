package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.ClockService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(RiddleService::class)
open class RiddleService(
    private val riddleRepository: RiddleEntityRepository,
    private val riddleCategoryRepository: RiddleCategoryRepository,
    private val riddleMappingRepository: RiddleMappingRepository,
    private val clock: ClockService
) {

    @Transactional(readOnly = false, isolation = Isolation.REPEATABLE_READ)
    open fun listRiddlesForUser(user: UserEntity): List<RiddleCategoryDto> {
        val categories = riddleCategoryRepository.findAllByVisibleTrueAndMinRoleIn(RoleType.atMost(user.role))
        val submissions = riddleMappingRepository.findAllByOwnerUserAndCompletedTrue(user)
            .groupBy { it.riddle?.categoryId ?: 0 }
            .toMap()

        return categories.map { category ->
            val riddles = riddleRepository.findAllByCategoryId(category.categoryId)
            val total = riddles.size
            val nextId = riddles
                .filter { filter -> submissions[category.categoryId]?.map { it.riddle?.id }?.contains(filter.id)?.not() ?: true }
                .minByOrNull { it.order }
                ?.id

            RiddleCategoryDto(category.categoryId, category.title, nextId, submissions[category.categoryId]?.size ?: 0, total)
        }
    }

    @Transactional(readOnly = true)
    open fun getRiddleForUser(user: UserEntity, riddleId: Int): RiddleView? {
        val riddle = riddleRepository.findById(riddleId).orElse(null) ?: return null
        if (riddleCategoryRepository.findByCategoryIdAndVisibleTrueAndMinRoleIn(riddle.categoryId, RoleType.atMost(user.role)).isEmpty)
            return null

        val submissions = riddleMappingRepository.findAllByOwnerUserAndRiddle_CategoryId(user, riddle.categoryId)
        val submission = submissions.find { it.riddle?.id == riddleId }
        if (submission != null)
            return RiddleView(riddle.imageUrl, riddle.title, if (submission.hintUsed) riddle.hint else null, submission.completed)

        val riddles = riddleRepository.findAllByCategoryId(riddle.categoryId)
        val nextId = riddles
            .filter { filter -> submissions.filter { it.completed }.map { it.riddle?.id }.contains(filter.id).not() }
            .minByOrNull { it.order }
            ?.id

        if (nextId != riddle.id)
            return null
        return RiddleView(riddle.imageUrl, riddle.title, null, false)
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun unlockHintForUser(user: UserEntity, riddleId: Int): String? {
        val riddle = riddleRepository.findById(riddleId).orElse(null) ?: return null
        if (riddleCategoryRepository.findByCategoryIdAndVisibleTrueAndMinRoleIn(riddle.categoryId, RoleType.atMost(user.role)).isEmpty)
            return null

        val submission = riddleMappingRepository.findByOwnerUserAndRiddle_Id(user, riddleId)
        if (submission.isPresent) {
            val submissionEntity = submission.get()
            submissionEntity.hintUsed = true
            riddleMappingRepository.save(submissionEntity)
        } else {
            val nextId = getNextId(user, riddle)
            if (nextId != riddle.id)
                return null

            riddleMappingRepository.save(
                RiddleMappingEntity(0, riddle, user, null,
                hintUsed = true, completed = false, attemptCount = 0)
            )
        }
        return riddle.hint
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun submitRiddleForUser(user: UserEntity, riddleId: Int, solution: String): RiddleSubmissionView? {
        val riddle = riddleRepository.findById(riddleId).orElse(null) ?: return null
        if (riddleCategoryRepository.findByCategoryIdAndVisibleTrueAndMinRoleIn(riddle.categoryId, RoleType.atMost(user.role)).isEmpty)
            return null

        val submission = riddleMappingRepository.findByOwnerUserAndRiddle_Id(user, riddleId)
        if (submission.isPresent) {
            val submissionEntity = submission.get()
            if (checkSolution(solution, riddle)) {
                submissionEntity.attemptCount += 1
                submissionEntity.completedAt = clock.getTimeInSeconds()
                riddleMappingRepository.save(submissionEntity)
                return RiddleSubmissionView(status = RiddleSubmissionStatus.WRONG, null)
            }

            submissionEntity.completed = true
            submissionEntity.attemptCount += 1
            submissionEntity.completedAt = clock.getTimeInSeconds()
            riddleMappingRepository.save(submissionEntity)
            return RiddleSubmissionView(status = RiddleSubmissionStatus.CORRECT, getNextId(user, riddle))

        } else {
            val nextId = getNextId(user, riddle)
            if (nextId != riddle.id)
                return null
            if (checkSolution(solution, riddle)) {
                riddleMappingRepository.save(
                    RiddleMappingEntity(0, riddle, user, null,
                    hintUsed = false, completed = false, completedAt = clock.getTimeInSeconds(), attemptCount = 1)
                )
                return RiddleSubmissionView(status = RiddleSubmissionStatus.WRONG, null)
            }

            riddleMappingRepository.save(
                RiddleMappingEntity(0, riddle, user, null,
                hintUsed = false, completed = true, completedAt = clock.getTimeInSeconds(), attemptCount = 1)
            )
            return RiddleSubmissionView(status = RiddleSubmissionStatus.CORRECT, getNextId(user, riddle))

        }
    }

    private fun checkSolution(solution: String, riddle: RiddleEntity) =
        solution.lowercase() != riddle.solution.lowercase()

    private fun getNextId(user: UserEntity, riddle: RiddleEntity): Int? {
        val submissions = riddleMappingRepository.findAllByOwnerUserAndRiddle_CategoryId(user, riddle.categoryId)
        val riddles = riddleRepository.findAllByCategoryId(riddle.categoryId)
        return riddles
            .filter { filter -> submissions.filter { it.completed }.map { it.riddle?.id }.contains(filter.id).not() }
            .minByOrNull { it.order }
            ?.id
    }

    @Transactional(readOnly = true)
    open fun getCompletedRiddleCount(user: UserEntity): Int {
        val categories = riddleCategoryRepository.findAllByVisibleTrueAndMinRoleIn(RoleType.atMost(user.role))
                .map { it.categoryId }
        return riddleMappingRepository.findAllByCompletedTrueAndOwnerUserAndRiddle_CategoryIdIn(user, categories).size
    }

    @Transactional(readOnly = true)
    open fun getTotalRiddleCount(user: UserEntity): Int {
        val categories = riddleCategoryRepository.findAllByVisibleTrueAndMinRoleIn(RoleType.atMost(user.role))
            .map { it.categoryId }
        return riddleRepository.findAllByCategoryIdIn(categories).size
    }

}
