package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.service.UserService
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
    private val userService: UserService,
    private val clock: TimeService
) {

    @Transactional(readOnly = false, isolation = Isolation.REPEATABLE_READ)
    open fun listRiddlesForUser(user: CmschUser): List<RiddleCategoryDto> {
        val categories = riddleCategoryRepository.findAllByVisibleTrueAndMinRoleIn(RoleType.atMost(user.role))
        val submissions = riddleMappingRepository.findAllByOwnerUser_IdAndCompletedTrue(user.id)
            .groupBy { it.riddle?.categoryId ?: 0 }
            .toMap()

        return mapRiddles(categories, submissions)
    }

    @Transactional(readOnly = false, isolation = Isolation.REPEATABLE_READ)
    open fun listRiddlesForGroup(user: CmschUser, group: GroupEntity?): List<RiddleCategoryDto> {
        if (group == null)
            return listOf()

        val categories = riddleCategoryRepository.findAllByVisibleTrueAndMinRoleIn(RoleType.atMost(user.role))
        val submissions = riddleMappingRepository.findAllByOwnerGroup_IdAndCompletedTrue(group.id)
            .groupBy { it.riddle?.categoryId ?: 0 }
            .toMap()

        return mapRiddles(categories, submissions)
    }

    private fun mapRiddles(
        categories: List<RiddleCategoryEntity>,
        submissions: Map<Int, List<RiddleMappingEntity>>
    ) = categories.map { category ->
        val riddles = riddleRepository.findAllByCategoryId(category.categoryId)
        val total = riddles.size
        val nextId = riddles
            .filter { filter ->
                submissions[category.categoryId]?.map { it.riddle?.id }?.contains(filter.id)?.not() ?: true
            }
            .minByOrNull { it.order }
            ?.id

        RiddleCategoryDto(category.categoryId, category.title, nextId, submissions[category.categoryId]?.size ?: 0, total)
    }

    @Transactional(readOnly = true)
    open fun getRiddleForUser(user: CmschUser, riddleId: Int): RiddleView? {
        val riddle = riddleRepository.findById(riddleId).orElse(null) ?: return null
        if (riddleCategoryRepository.findByCategoryIdAndVisibleTrueAndMinRoleIn(riddle.categoryId, RoleType.atMost(user.role)).isEmpty)
            return null

        val submissions = riddleMappingRepository.findAllByOwnerUser_IdAndRiddle_CategoryId(user.id, riddle.categoryId)
        return mapRiddleView(submissions, riddleId, riddle)
    }

    @Transactional(readOnly = true)
    open fun getRiddleForGroup(user: CmschUser, group: GroupEntity?, riddleId: Int): RiddleView? {
        if (group == null)
            return null

        val riddle = riddleRepository.findById(riddleId).orElse(null) ?: return null
        if (riddleCategoryRepository.findByCategoryIdAndVisibleTrueAndMinRoleIn(riddle.categoryId, RoleType.atMost(user.role)).isEmpty)
            return null

        val submissions = riddleMappingRepository.findAllByOwnerGroup_IdAndRiddle_CategoryId(group.id, riddle.categoryId)
        return mapRiddleView(submissions, riddleId, riddle)
    }

    private fun mapRiddleView(
        submissions: List<RiddleMappingEntity>,
        riddleId: Int,
        riddle: RiddleEntity
    ): RiddleView? {
        val submission = submissions.find { it.riddle?.id == riddleId }
        if (submission != null)
            return RiddleView(
                riddle.imageUrl,
                riddle.title,
                if (submission.hintUsed) riddle.hint else null,
                submission.completed
            )

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
    open fun unlockHintForUser(user: CmschUser, riddleId: Int): String? {
        val riddle = riddleRepository.findById(riddleId).orElse(null) ?: return null
        if (riddleCategoryRepository.findByCategoryIdAndVisibleTrueAndMinRoleIn(riddle.categoryId, RoleType.atMost(user.role)).isEmpty)
            return null

        val submission = riddleMappingRepository.findByOwnerUser_IdAndRiddle_Id(user.id, riddleId)
        if (submission.isPresent) {
            val submissionEntity = submission.get()
            submissionEntity.hintUsed = true
            riddleMappingRepository.save(submissionEntity)
        } else {
            val nextId = getNextIdUser(user, riddle)
            if (nextId != riddle.id)
                return null

            val userEntity = userService.getById(user.internalId)
            riddleMappingRepository.save(
                RiddleMappingEntity(0, riddle, userEntity, null,
                hintUsed = true, completed = false, attemptCount = 0)
            )
        }
        return riddle.hint
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun unlockHintForGroup(user: CmschUser, group: GroupEntity?, riddleId: Int): String? {
        if (group == null)
            return null

        val riddle = riddleRepository.findById(riddleId).orElse(null) ?: return null
        if (riddleCategoryRepository.findByCategoryIdAndVisibleTrueAndMinRoleIn(riddle.categoryId, RoleType.atMost(user.role)).isEmpty)
            return null

        val submission = riddleMappingRepository.findByOwnerGroup_IdAndRiddle_Id(group.id, riddleId)
        if (submission.isPresent) {
            val submissionEntity = submission.get()
            submissionEntity.hintUsed = true
            riddleMappingRepository.save(submissionEntity)
        } else {
            val nextId = getNextIdGroup(group, riddle)
            if (nextId != riddle.id)
                return null

            riddleMappingRepository.save(
                RiddleMappingEntity(0, riddle, null, group,
                    hintUsed = true, completed = false, attemptCount = 0)
            )
        }
        return riddle.hint
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun submitRiddleForUser(user: CmschUser, riddleId: Int, solution: String): RiddleSubmissionView? {
        val riddle = riddleRepository.findById(riddleId).orElse(null) ?: return null
        if (riddleCategoryRepository.findByCategoryIdAndVisibleTrueAndMinRoleIn(riddle.categoryId, RoleType.atMost(user.role)).isEmpty)
            return null

        val submission = riddleMappingRepository.findByOwnerUser_IdAndRiddle_Id(user.id, riddleId)
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
            return RiddleSubmissionView(status = RiddleSubmissionStatus.CORRECT, getNextIdUser(user, riddle))

        } else {
            val nextId = getNextIdUser(user, riddle)
            if (nextId != riddle.id)
                return null
            val userEntity = userService.getById(user.internalId)
            if (checkSolution(solution, riddle)) {
                riddleMappingRepository.save(
                    RiddleMappingEntity(0, riddle, userEntity, null,
                    hintUsed = false, completed = false, completedAt = clock.getTimeInSeconds(), attemptCount = 1)
                )
                return RiddleSubmissionView(status = RiddleSubmissionStatus.WRONG, null)
            }

            riddleMappingRepository.save(
                RiddleMappingEntity(0, riddle, userEntity, null,
                hintUsed = false, completed = true, completedAt = clock.getTimeInSeconds(), attemptCount = 1)
            )
            return RiddleSubmissionView(status = RiddleSubmissionStatus.CORRECT, getNextIdUser(user, riddle))
        }
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun submitRiddleForGroup(user: CmschUser, group: GroupEntity?, riddleId: Int, solution: String): RiddleSubmissionView? {
        if (group == null)
            return null

        val riddle = riddleRepository.findById(riddleId).orElse(null) ?: return null
        if (riddleCategoryRepository.findByCategoryIdAndVisibleTrueAndMinRoleIn(riddle.categoryId, RoleType.atMost(user.role)).isEmpty)
            return null

        val submission = riddleMappingRepository.findByOwnerGroup_IdAndRiddle_Id(group.id, riddleId)
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
            return RiddleSubmissionView(status = RiddleSubmissionStatus.CORRECT, getNextIdGroup(group, riddle))

        } else {
            val nextId = getNextIdGroup(group, riddle)
            if (nextId != riddle.id)
                return null
            if (checkSolution(solution, riddle)) {
                riddleMappingRepository.save(
                    RiddleMappingEntity(0, riddle, null, group,
                        hintUsed = false, completed = false, completedAt = clock.getTimeInSeconds(), attemptCount = 1)
                )
                return RiddleSubmissionView(status = RiddleSubmissionStatus.WRONG, null)
            }

            riddleMappingRepository.save(
                RiddleMappingEntity(0, riddle, null, group,
                    hintUsed = false, completed = true, completedAt = clock.getTimeInSeconds(), attemptCount = 1)
            )
            return RiddleSubmissionView(status = RiddleSubmissionStatus.CORRECT, getNextIdGroup(group, riddle))
        }
    }

    private fun checkSolution(solution: String, riddle: RiddleEntity) =
        solution.lowercase() != riddle.solution.lowercase()

    private fun getNextIdUser(user: CmschUser, riddle: RiddleEntity): Int? {
        val submissions = riddleMappingRepository.findAllByOwnerUser_IdAndRiddle_CategoryId(user.id, riddle.categoryId)
        return findNextTo(riddle, submissions)
    }

    private fun getNextIdGroup(group: GroupEntity, riddle: RiddleEntity): Int? {
        val submissions = riddleMappingRepository.findAllByOwnerGroup_IdAndRiddle_CategoryId(group.id, riddle.categoryId)
        return findNextTo(riddle, submissions)
    }

    private fun findNextTo(
        riddle: RiddleEntity,
        submissions: List<RiddleMappingEntity>
    ): Int? {
        val riddles = riddleRepository.findAllByCategoryId(riddle.categoryId)
        return riddles
            .filter { filter -> submissions.filter { it.completed }.map { it.riddle?.id }.contains(filter.id).not() }
            .minByOrNull { it.order }
            ?.id
    }

    @Transactional(readOnly = true)
    open fun getCompletedRiddleCountUser(user: UserEntity): Int {
        val categories = riddleCategoryRepository.findAllByVisibleTrueAndMinRoleIn(RoleType.atMost(user.role))
                .map { it.categoryId }
        return riddleMappingRepository.findAllByCompletedTrueAndOwnerUserAndRiddle_CategoryIdIn(user, categories).size
    }

    @Transactional(readOnly = true)
    open fun getCompletedRiddleCountGroup(user: UserEntity, group: GroupEntity?): Int {
        if (group == null)
            return 0
        val categories = riddleCategoryRepository.findAllByVisibleTrueAndMinRoleIn(RoleType.atMost(user.role))
            .map { it.categoryId }
        return riddleMappingRepository.findAllByCompletedTrueAndOwnerGroupAndRiddle_CategoryIdIn(group, categories).size
    }

    @Transactional(readOnly = true)
    open fun getTotalRiddleCount(user: UserEntity): Int {
        val categories = riddleCategoryRepository.findAllByVisibleTrueAndMinRoleIn(RoleType.atMost(user.role))
            .map { it.categoryId }
        return riddleRepository.findAllByCategoryIdIn(categories).size
    }

}
