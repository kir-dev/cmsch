package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.dao.RiddleCategoryRepository
import hu.bme.sch.cmsch.dao.RiddleMappingRepository
import hu.bme.sch.cmsch.dao.RiddleRepository
import hu.bme.sch.cmsch.dto.RiddleCategoryDto
import hu.bme.sch.cmsch.dto.view.RiddleSubmissionStatus
import hu.bme.sch.cmsch.dto.view.RiddleSubmissionView
import hu.bme.sch.cmsch.dto.view.RiddleView
import hu.bme.sch.cmsch.model.RiddleEntity
import hu.bme.sch.cmsch.model.RiddleMappingEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
open class RiddleService(
    private val riddleRepository: RiddleRepository,
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

        println(submissions)

        return categories.map { category ->
            val riddles = riddleRepository.findAllByCategoryId(category.categoryId)
            val total = riddles.size
            val nextId = riddles
                .filter { filter -> submissions[category.id]?.none { it.id == filter.id } ?: true }
                .minByOrNull { it.order }
                ?.id

            RiddleCategoryDto(category.categoryId, category.title, nextId, submissions.size, total)
        }
    }

    @Transactional(readOnly = true)
    open fun getRiddleForUser(user: UserEntity, riddleId: Int): RiddleView? {
        val riddle = riddleRepository.findById(riddleId).orElse(null) ?: return null
        if (riddleCategoryRepository.findByCategoryIdAndVisibleTrueAndMinRoleIn(riddle.categoryId, RoleType.atMost(user.role)).isEmpty)
            return null

        val submissions = riddleMappingRepository.findAllByOwnerUserAndRiddle_CategoryId(user, riddle.categoryId)
        val submission = submissions.find { it.id == riddleId }
        if (submission != null)
            return RiddleView(riddle.imageUrl, riddle.title, if (submission.hintUsed) riddle.hint else null, submission.completed)

        val riddles = riddleRepository.findAllByCategoryId(riddle.categoryId)
        val nextId = riddles
            .filter { filter -> submissions.none { it.completed && it.id == filter.id } }
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
            submission.get().hintUsed = true
            riddleMappingRepository.save(submission.get())
        } else {
            val nextId = getNextId(user, riddle)
            if (nextId != riddle.id)
                return null

            riddleMappingRepository.save(RiddleMappingEntity(0, riddle, user, null,
                hintUsed = true, completed = false))
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
            if (checkSolution(solution, riddle))
                return RiddleSubmissionView(status = RiddleSubmissionStatus.WRONG, null)

            val submissionEntity = submission.get()
            submissionEntity.completed = true
            submissionEntity.completedAt = clock.getTimeInSeconds()
            riddleMappingRepository.save(submissionEntity)
            return RiddleSubmissionView(status = RiddleSubmissionStatus.CORRECT, getNextId(user, riddle))
        } else {
            val nextId = getNextId(user, riddle)
            if (nextId != riddle.id)
                return null
            if (checkSolution(solution, riddle))
                return RiddleSubmissionView(status = RiddleSubmissionStatus.WRONG, null)

            riddleMappingRepository.save(RiddleMappingEntity(0, riddle, user, null,
                hintUsed = false, completed = true, completedAt = clock.getTimeInSeconds()))
            return RiddleSubmissionView(status = RiddleSubmissionStatus.CORRECT, getNextId(user, riddle))
        }
    }

    private fun checkSolution(solution: String, riddle: RiddleEntity) =
        solution.lowercase() != riddle.solution.lowercase()

    private fun getNextId(user: UserEntity, riddle: RiddleEntity): Int? {
        val submissions = riddleMappingRepository.findAllByOwnerUserAndRiddle_CategoryId(user, riddle.categoryId)
        val riddles = riddleRepository.findAllByCategoryId(riddle.categoryId)
        return riddles
            .filter { filter -> submissions.none { it.completed && it.id == filter.id } }
            .minByOrNull { it.order }
            ?.id
    }

}
