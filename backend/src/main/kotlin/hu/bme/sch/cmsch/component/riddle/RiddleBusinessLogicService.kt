package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.service.UserService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(RiddleComponent::class)
open class RiddleBusinessLogicService(
    private val riddleCacheManager: RiddleCacheManager,
    private val userService: UserService,
    private val clock: TimeService,
    private val riddleComponent: RiddleComponent
) : RiddleService {

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    override fun listRiddlesForUser(user: CmschUser): List<RiddleCategoryDto> {
        val categories = riddleCacheManager.findAllCategoriesByVisibleTrueAndMinRoleAtMost(user.role)
        val submissions = riddleCacheManager.findAllMappingByOwnerUserIdAndCompletedTrue(user.id)
            .groupBy { it.riddleCategoryId }
            .toMap()

        return mapRiddles(categories, submissions)
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    override fun listRiddlesForGroup(user: CmschUser, group: GroupEntity?): List<RiddleCategoryDto> {
        if (group == null)
            return listOf()

        val categories = riddleCacheManager.findAllCategoriesByVisibleTrueAndMinRoleAtMost(user.role)
        val submissions = riddleCacheManager.findAllMappingByOwnerGroupIdAndCompletedTrue(group.id)
            .groupBy { it.riddleCategoryId }
            .toMap()

        return mapRiddles(categories, submissions)
    }

    private fun mapRiddles(
        categories: List<RiddleCategoryEntity>,
        submissions: Map<Int, List<RiddleMappingEntity>>
    ) = categories.map { category ->
        val riddles = riddleCacheManager.findAllRiddleByCategoryId(category.categoryId)
        val total = riddles.size
        val nextId = riddles
            .filter { filter ->
                submissions[category.categoryId]?.none { it.riddleId == filter.id } ?: true
            }
            .minByOrNull { it.order }
            ?.id

        RiddleCategoryDto(
            categoryId = category.categoryId,
            title = category.title,
            nextRiddle = nextId,
            completed = submissions[category.categoryId]?.size ?: 0,
            total = total
        )
    }

    @Transactional(readOnly = true)
    override fun getRiddleForUser(user: CmschUser, riddleId: Int): RiddleView? {
        val riddle = riddleCacheManager.getRiddleById(riddleId) ?: return null
        riddleCacheManager.findCategoryByCategoryIdAndVisibleTrueAndMinRoleAtMost(riddle.categoryId, user.role)
            ?: return null

        val submissions = riddleCacheManager.findAllMappingByOwnerUserIdAndRiddleCategoryId(user.id, riddle.categoryId)
        return mapRiddleView(submissions, riddleId, riddle)
    }

    @Transactional(readOnly = true)
    override fun getRiddleForGroup(user: CmschUser, group: GroupEntity?, riddleId: Int): RiddleView? {
        if (group == null)
            return null

        val riddle = riddleCacheManager.getRiddleById(riddleId) ?: return null
        riddleCacheManager.findCategoryByCategoryIdAndVisibleTrueAndMinRoleAtMost(riddle.categoryId, user.role)
            ?: return null

        val submissions = riddleCacheManager.findAllMappingByGroupUserIdAndRiddleCategoryId(group.id, riddle.categoryId)
        return mapRiddleView(submissions, riddleId, riddle)
    }

    private fun mapRiddleView(
        submissions: List<RiddleMappingEntity>,
        riddleId: Int,
        riddle: RiddleEntity
    ): RiddleView? {
        val submission = submissions.find { it.riddleId == riddleId }
        if (submission != null)
            return RiddleView(
                imageUrl = riddle.imageUrl,
                title = riddle.title,
                hint = if (submission.hintUsed) riddle.hint else null,
                solved = submission.completed,
                skipped = submission.skipped,
                creator = riddle.creator,
                firstSolver = riddle.firstSolver,
                description = riddle.description,
                skipPermitted = !submission.completed && !cannotSkip(riddle)
            )

        val riddles = riddleCacheManager.findAllRiddleByCategoryId(riddle.categoryId)
        val nextId = riddles
            .filter { filter -> submissions.none { it.completed && it.riddleId == filter.id } }
            .minByOrNull { it.order }
            ?.id

        if (nextId != riddle.id)
            return null
        return RiddleView(
            imageUrl = riddle.imageUrl,
            title = riddle.title,
            hint = null,
            solved = false,
            skipped = false,
            creator = riddle.creator,
            firstSolver = riddle.firstSolver,
            description = riddle.description,
            skipPermitted = !cannotSkip(riddle)
        )
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    override fun unlockHintForUser(user: CmschUser, riddleId: Int): String? {
        val riddle = riddleCacheManager.getRiddleById(riddleId) ?: return null
        riddleCacheManager.findCategoryByCategoryIdAndVisibleTrueAndMinRoleAtMost(riddle.categoryId, user.role)
            ?: return null

        val submission = riddleCacheManager.findMappingByOwnerUserIdAndRiddleId(user.id, riddleId)
        if (submission != null) {
            submission.hintUsed = true
            riddleCacheManager.updateMapping(submission)
        } else {
            val nextId = getNextIdUser(user, riddle)
            if (nextId != riddle.id)
                return null

            val userEntity = userService.getById(user.internalId)
            riddleCacheManager.createNewMapping(
                RiddleMappingEntity(0, riddle.id, userEntity.id, 0,
                hintUsed = true, completed = false, skipped = false, attemptCount = 0)
            )
        }
        return riddle.hint
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    override fun unlockHintForGroup(user: CmschUser, group: GroupEntity?, riddleId: Int): String? {
        if (group == null)
            return null

        val riddle = riddleCacheManager.getRiddleById(riddleId) ?: return null
        riddleCacheManager.findCategoryByCategoryIdAndVisibleTrueAndMinRoleAtMost(riddle.categoryId, user.role)
            ?: return null

        val submission = riddleCacheManager.findMappingByOwnerGroupIdAndRiddleId(group.id, riddleId)
        if (submission != null) {
            submission.hintUsed = true
            riddleCacheManager.updateMapping(submission)
        } else {
            val nextId = getNextIdGroup(group, riddle)
            if (nextId != riddle.id)
                return null

            riddleCacheManager.createNewMapping(
                RiddleMappingEntity(0, riddle.id, 0, group.id,
                    hintUsed = true, completed = false, skipped = false, attemptCount = 0)
            )
        }
        return riddle.hint
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    override fun submitRiddleForUser(
        user: CmschUser,
        riddleId: Int,
        solution: String,
        skip: Boolean
    ): RiddleSubmissionView? {
        val riddle = riddleCacheManager.getRiddleById(riddleId) ?: return null
        riddleCacheManager.findCategoryByCategoryIdAndVisibleTrueAndMinRoleAtMost(riddle.categoryId, user.role)
            ?: return null

        if (skip && (cannotSkip(riddle) || !riddleComponent.skipEnabled.isValueTrue()))
            return RiddleSubmissionView(status = RiddleSubmissionStatus.CANNOT_SKIP, null)

        val submission = riddleCacheManager.findMappingByOwnerUserIdAndRiddleId(user.id, riddleId)
        if (submission != null) {
            if (!skip && checkSolutionIsWrong(solution, riddle)) {
                if (riddleComponent.saveFailedAttempts.isValueTrue()) {
                    submission.attemptCount += 1
                    submission.completedAt = clock.getTimeInSeconds()
                    riddleCacheManager.updateMapping(submission, lazyPersist = true)
                }
                return RiddleSubmissionView(status = RiddleSubmissionStatus.WRONG, null)
            }

            if (!submission.completed) {
                submission.completed = true
                submission.skipped = skip
                submission.attemptCount += 1
                submission.completedAt = clock.getTimeInSeconds()
                riddleCacheManager.updateMapping(submission)
                if (!skip && riddle.firstSolver.isBlank()) {
                    riddle.firstSolver = user.userName
                    riddleCacheManager.updateRiddle(riddle)
                }
            }
            return RiddleSubmissionView(status = RiddleSubmissionStatus.CORRECT, getNextIdUser(user, riddle))

        } else {
            val nextId = getNextIdUser(user, riddle)
            if (nextId != riddle.id)
                return null
            val userEntity = userService.getById(user.internalId)
            if (!skip && checkSolutionIsWrong(solution, riddle)) {
                if (riddleComponent.saveFailedAttempts.isValueTrue()) {
                    riddleCacheManager.createNewMapping(
                        RiddleMappingEntity(
                            0, riddle.id, userEntity.id, 0,
                            hintUsed = false, completed = false, skipped = false,
                            completedAt = clock.getTimeInSeconds(), attemptCount = 1
                        )
                    )
                }
                return RiddleSubmissionView(status = RiddleSubmissionStatus.WRONG, null)
            }

            riddleCacheManager.createNewMapping(
                RiddleMappingEntity(0, riddle.id, userEntity.id, 0,
                hintUsed = false, completed = true, skipped = skip,
                    completedAt = clock.getTimeInSeconds(), attemptCount = 1)
            )
            if (!skip && riddle.firstSolver.isBlank()) {
                riddle.firstSolver = user.userName
                riddleCacheManager.updateRiddle(riddle)
            }
            return RiddleSubmissionView(status = RiddleSubmissionStatus.CORRECT, getNextIdUser(user, riddle))
        }
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    override fun submitRiddleForGroup(
        user: CmschUser,
        group: GroupEntity?,
        riddleId: Int,
        solution: String,
        skip: Boolean
    ): RiddleSubmissionView? {
        if (group == null)
            return null

        val riddle = riddleCacheManager.getRiddleById(riddleId) ?: return null
        riddleCacheManager.findCategoryByCategoryIdAndVisibleTrueAndMinRoleAtMost(riddle.categoryId, user.role)
            ?: return null

        if (skip && (cannotSkip(riddle) || !riddleComponent.skipEnabled.isValueTrue()))
            return RiddleSubmissionView(status = RiddleSubmissionStatus.CANNOT_SKIP, null)

        val submission = riddleCacheManager.findMappingByOwnerGroupIdAndRiddleId(group.id, riddleId)
        if (submission != null) {
            if (!skip && checkSolutionIsWrong(solution, riddle)) {
                if (riddleComponent.saveFailedAttempts.isValueTrue()) {
                    submission.attemptCount += 1
                    submission.completedAt = clock.getTimeInSeconds()
                    riddleCacheManager.updateMapping(submission, lazyPersist = true)
                }
                return RiddleSubmissionView(status = RiddleSubmissionStatus.WRONG, null)
            }

            if (!submission.completed) {
                submission.completed = true
                submission.skipped = skip
                submission.attemptCount += 1
                submission.completedAt = clock.getTimeInSeconds()
                riddleCacheManager.updateMapping(submission)
                if (!skip && riddle.firstSolver.isBlank()) {
                    riddle.firstSolver = group.name
                    riddleCacheManager.updateRiddle(riddle)
                }
            }
            return RiddleSubmissionView(status = RiddleSubmissionStatus.CORRECT, getNextIdGroup(group, riddle))

        } else {
            val nextId = getNextIdGroup(group, riddle)
            if (nextId != riddle.id)
                return null
            if (!skip && checkSolutionIsWrong(solution, riddle)) {
                if (riddleComponent.saveFailedAttempts.isValueTrue()) {
                    riddleCacheManager.createNewMapping(
                        RiddleMappingEntity(
                            0, riddle.id, 0, group.id,
                            hintUsed = false, completed = false, skipped = false,
                            completedAt = clock.getTimeInSeconds(), attemptCount = 1
                        )
                    )
                }
                return RiddleSubmissionView(status = RiddleSubmissionStatus.WRONG, null)
            }

            riddleCacheManager.createNewMapping(
                RiddleMappingEntity(0, riddle.id, 0, group.id,
                    hintUsed = false, completed = true, skipped = skip,
                    completedAt = clock.getTimeInSeconds(), attemptCount = 1
                )
            )
            if (!skip && riddle.firstSolver.isBlank()) {
                riddle.firstSolver = group.name
                riddleCacheManager.updateRiddle(riddle)
            }
            return RiddleSubmissionView(status = RiddleSubmissionStatus.CORRECT, getNextIdGroup(group, riddle))
        }
    }

    private fun cannotSkip(riddle: RiddleEntity): Boolean {
        return riddleCacheManager.countAllMappingByCompletedNotSkippedAndRiddleId(riddle.id) <
                riddleComponent.skipAfterGroupsSolved.getIntValue(Int.MAX_VALUE)
    }

    private fun checkSolutionIsWrong(solution: String, riddle: RiddleEntity): Boolean {
        var transformedSubmittedSolution = solution
        var transformedRiddleSolution = riddle.solution

        if (riddleComponent.ignoreAccent.isValueTrue()) {
            transformedSubmittedSolution = replaceAccent(transformedSubmittedSolution)
            transformedRiddleSolution = replaceAccent(transformedRiddleSolution)
        }
        if (riddleComponent.ignoreCase.isValueTrue()) {
            transformedSubmittedSolution = transformedSubmittedSolution.lowercase()
            transformedRiddleSolution = transformedRiddleSolution.lowercase()
        }
        if (riddleComponent.ignoreWhitespace.isValueTrue()) {
            transformedSubmittedSolution = transformedSubmittedSolution
                .replace(" ", "")
                .replace("-", "")
                .replace("&", "")
                .replace("+", "")
                .replace(",", "")
            transformedRiddleSolution = transformedRiddleSolution
                .replace(" ", "")
                .replace("-", "")
                .replace("&", "")
                .replace("+", "")
                .replace(",", "")
        }

        return transformedSubmittedSolution != transformedRiddleSolution
    }

    private fun replaceAccent(transformedSubmittedSolution: String) = transformedSubmittedSolution
        .replace("Á", "A")
        .replace("É", "E")
        .replace("Í", "I")
        .replace("Ó", "O")
        .replace("Ö", "O")
        .replace("Ő", "O")
        .replace("Ú", "U")
        .replace("Ü", "U")
        .replace("Ű", "U")
        .replace("á", "a")
        .replace("é", "e")
        .replace("í", "i")
        .replace("ó", "o")
        .replace("ö", "o")
        .replace("ő", "o")
        .replace("ú", "u")
        .replace("ü", "u")
        .replace("ű", "u")

    private fun getNextIdUser(user: CmschUser, riddle: RiddleEntity): Int? {
        val submissions = riddleCacheManager.findAllMappingByOwnerUserIdAndRiddleCategoryId(user.id, riddle.categoryId)
        return findNextTo(riddle, submissions)
    }

    private fun getNextIdGroup(group: GroupEntity, riddle: RiddleEntity): Int? {
        val submissions = riddleCacheManager.findAllMappingByGroupUserIdAndRiddleCategoryId(group.id, riddle.categoryId)
        return findNextTo(riddle, submissions)
    }

    private fun findNextTo(
        riddle: RiddleEntity,
        submissions: List<RiddleMappingEntity>
    ): Int? {
        val riddles = riddleCacheManager.findAllRiddleByCategoryId(riddle.categoryId)
        return riddles
            .filter { filter -> submissions.none { it.completed && it.riddleId == filter.id } }
            .minByOrNull { it.order }
            ?.id
    }

    @Transactional(readOnly = true)
    override fun getCompletedRiddleCountUser(user: UserEntity): Int {
        val categories = riddleCacheManager.findAllCategoriesByVisibleTrueAndMinRoleAtMost(user.role)
                .map { it.categoryId }
        return riddleCacheManager.countAllMappingByCompletedTrueAndOwnerUserIdAndRiddleCategoryIdIn(user.id, categories)
    }

    @Transactional(readOnly = true)
    override fun getCompletedRiddleCountGroup(user: UserEntity, group: GroupEntity?): Int {
        if (group == null)
            return 0
        val categories = riddleCacheManager.findAllCategoriesByVisibleTrueAndMinRoleAtMost(user.role)
            .map { it.categoryId }
        return riddleCacheManager.countAllMappingByCompletedTrueAndOwnerGroupIdAndRiddleCategoryIdIn(group.id, categories)
    }

    @Transactional(readOnly = true)
    override fun getTotalRiddleCount(user: UserEntity): Int {
        val categories = riddleCacheManager.findAllCategoriesByVisibleTrueAndMinRoleAtMost(user.role)
            .map { it.categoryId }
        return riddleCacheManager.countAllRiddleByCategoryIdIn(categories)
    }

    @Transactional(readOnly = true)
    override fun listRiddleHistoryForUser(user: UserEntity): Map<String, List<RiddleViewWithSolution>> {
        val categories = riddleCacheManager.findAllCategoriesByVisibleTrueAndMinRoleAtMost(user.role)
        val submissions = riddleCacheManager.findAllMappingByOwnerUserIdAndCompletedTrue(user.id)
            .groupBy { riddleCacheManager.getRiddleById(it.riddleId)?.categoryId ?: 0 }
            .toMap()

        return categories.associate { category ->
            category.title to
                    submissions.getOrDefault(category.id, listOf())
                        .mapNotNull { riddle -> riddleCacheManager.getRiddleById(riddle.riddleId)?.let { mapRiddle(riddle, it) } }
                        .toList()
        }
    }

    @Transactional(readOnly = true)
    override fun listRiddleHistoryForGroup(user: UserEntity, group: GroupEntity?): Map<String, List<RiddleViewWithSolution>> {
        if (group == null)
            return mapOf()

        val categories = riddleCacheManager.findAllCategoriesByVisibleTrueAndMinRoleAtMost(user.role)
        val submissions = riddleCacheManager.findAllMappingByOwnerGroupIdAndCompletedTrue(group.id)
            .groupBy { riddleCacheManager.getRiddleById(it.riddleId)?.categoryId ?: 0 }
            .toMap()

        return categories.associate { category ->
            category.title to
                    submissions.getOrDefault(category.id, listOf())
                        .mapNotNull { riddle -> riddleCacheManager.getRiddleById(riddle.riddleId)?.let { mapRiddle(riddle, it) } }
                        .toList()
        }
    }

    private fun mapRiddle(mapping: RiddleMappingEntity, riddle: RiddleEntity): RiddleViewWithSolution {
        return RiddleViewWithSolution(
            imageUrl = riddle.imageUrl,
            title = riddle.title,
            hint = if (mapping.hintUsed) riddle.hint else "",
            solved = mapping.completed,
            skipped = mapping.skipped,
            creator = riddle.creator,
            firstSolver = riddle.firstSolver,
            solution = if (mapping.completed) riddle.solution else "",
            description = riddle.description
        )
    }

}
