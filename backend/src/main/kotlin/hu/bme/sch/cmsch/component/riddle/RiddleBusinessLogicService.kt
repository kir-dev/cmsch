package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.service.UserService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.resilience.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.sql.SQLException

@Service
@ConditionalOnBean(RiddleComponent::class)
class RiddleBusinessLogicService(
    private val riddleEntityRepository: RiddleEntityRepository,
    private val riddleCategoryRepository: RiddleCategoryRepository,
    private val riddleMappingRepository: RiddleMappingRepository,
    private val userService: UserService,
    private val clock: TimeService,
    private val riddleComponent: RiddleComponent,
    private val riddleModerationService: RiddleModerationService
) : RiddleService {

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    override fun listRiddlesForUser(user: CmschUser): List<RiddleCategoryDto> {
        val categories = riddleCategoryRepository.findAll()
            .filter { it.visible && it.minRole.value <= user.role.value }
        val submissionsList = riddleMappingRepository.findAllByOwnerUserIdAndCompletedTrue(user.id)
        val riddleIds = submissionsList.map { it.riddleId }.toSet()
        val riddlesById = riddleEntityRepository.findAllById(riddleIds).associateBy { it.id }
        val submissions = submissionsList
            .groupBy { riddlesById[it.riddleId]?.categoryId ?: 0 }
            .filterKeys { it != 0 }

        return mapRiddles(categories, submissions)
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    override fun listRiddlesForGroup(user: CmschUser, groupId: Int?): List<RiddleCategoryDto> {
        if (groupId == null)
            return listOf()

        val categories = riddleCategoryRepository.findAll()
            .filter { it.visible && it.minRole.value <= user.role.value }
        val submissionsList = riddleMappingRepository.findAllByOwnerGroupIdAndCompletedTrue(groupId)
        val riddleIds = submissionsList.map { it.riddleId }.toSet()
        val riddlesById = riddleEntityRepository.findAllById(riddleIds).associateBy { it.id }
        val submissions = submissionsList
            .groupBy { riddlesById[it.riddleId]?.categoryId ?: 0 }
            .filterKeys { it != 0 }

        return mapRiddles(categories, submissions)
    }

    private fun mapRiddles(
        categories: List<RiddleCategoryEntity>,
        submissions: Map<Int, List<RiddleMappingEntity>>
    ): List<RiddleCategoryDto> {
        val allRiddles = riddleEntityRepository.findAll().toList()
        val riddlesByCategory = allRiddles.groupBy { it.categoryId }

        return categories.map { category ->
            val riddles = riddlesByCategory[category.categoryId] ?: emptyList()
            val total = riddles.size
            val nextRiddleIds = findNextTo(category.categoryId, submissions[category.categoryId] ?: listOf(), allRiddles)

            RiddleCategoryDto(
                categoryId = category.categoryId,
                title = category.title,
                nextRiddles = nextRiddleIds,
                completed = submissions[category.categoryId]?.size ?: 0,
                total = total
            )
        }
    }

    @Transactional(readOnly = true)
    override fun getRiddleForUser(user: CmschUser, riddleId: Int): RiddleView? {
        val riddle = riddleEntityRepository.findById(riddleId).orElse(null) ?: return null
        val allCategories = riddleCategoryRepository.findAll().toList()
        val category = allCategories
            .firstOrNull { it.categoryId == riddle.categoryId && it.visible && it.minRole.value <= user.role.value }
            ?: return null

        val allRiddles = riddleEntityRepository.findAll().toList()
        val riddleIdsInCategory = allRiddles
            .filter { it.categoryId == riddle.categoryId }
            .map { it.id }
        val submissions = riddleMappingRepository.findAllByOwnerUserId(user.id)
            .filter { it.riddleId in riddleIdsInCategory }
        return getRiddleIfAllowedToRead(riddle, submissions)
    }

    @Transactional(readOnly = true)
    override fun getRiddleForGroup(user: CmschUser, groupId: Int?, riddleId: Int): RiddleView? {
        if (groupId == null)
            return null

        val riddle = riddleEntityRepository.findById(riddleId).orElse(null) ?: return null
        val allCategories = riddleCategoryRepository.findAll().toList()
        val category = allCategories
            .firstOrNull { it.categoryId == riddle.categoryId && it.visible && it.minRole.value <= user.role.value }
            ?: return null

        val allRiddles = riddleEntityRepository.findAll().toList()
        val riddleIdsInCategory = allRiddles
            .filter { it.categoryId == riddle.categoryId }
            .map { it.id }
        val submissions = riddleMappingRepository.findAllByOwnerGroupId(groupId)
            .filter { it.riddleId in riddleIdsInCategory }
        return getRiddleIfAllowedToRead(riddle, submissions)
    }

    private fun getRiddleIfAllowedToRead(riddle: RiddleEntity, submissions: List<RiddleMappingEntity>): RiddleView? {
        val isRiddleCompleted = submissions.filter { it.completed }.find { it.riddleId == riddle.id } != null
        if (isRiddleCompleted) {
            return mapRiddleView(submissions, riddle)
        }

        val nextRiddles = findNextTo(riddle.categoryId, submissions)
        return nextRiddles.find { it.id == riddle.id }
    }

    private fun mapRiddleView(
        submissions: List<RiddleMappingEntity>,
        riddle: RiddleEntity
    ): RiddleView {
        val submission = submissions.find { it.riddleId == riddle.id }
        if (submission != null)
            return RiddleView(
                id = riddle.id,
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

        return RiddleView(
            id = riddle.id,
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

    @Retryable(value = [SQLException::class], maxRetries = 5, delay = 500L, multiplier = 1.5)
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    override fun unlockHintForUser(user: CmschUser, riddleId: Int): String? {
        val riddle = riddleEntityRepository.findById(riddleId).orElse(null) ?: return null
        val allCategories = riddleCategoryRepository.findAll().toList()
        allCategories
            .firstOrNull { it.categoryId == riddle.categoryId && it.visible && it.minRole.value <= user.role.value }
            ?: return null

        val submission = riddleMappingRepository.findAllByOwnerUserId(user.id)
            .firstOrNull { it.riddleId == riddleId }
        if (submission != null) {
            submission.hintUsed = true
            riddleMappingRepository.save(submission)
        } else {
            val nextRiddles = getNextRiddlesUser(user, riddle)
            if (nextRiddles.find { it.id == riddle.id } == null)
                return null

            val userEntity = userService.getById(user.internalId)
            riddleMappingRepository.save(
                RiddleMappingEntity(0, riddle.id, userEntity.id, 0,
                    hintUsed = true, completed = false, skipped = false, attemptCount = 0)
            )
        }
        return riddle.hint
    }

    @Retryable(value = [SQLException::class], maxRetries = 5, delay = 500L, multiplier = 1.5)
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    override fun unlockHintForGroup(user: CmschUser, groupId: Int?, groupName: String, riddleId: Int): String? {
        if (groupId == null)
            return null

        val riddle = riddleEntityRepository.findById(riddleId).orElse(null) ?: return null
        val allCategories = riddleCategoryRepository.findAll().toList()
        allCategories
            .firstOrNull { it.categoryId == riddle.categoryId && it.visible && it.minRole.value <= user.role.value }
            ?: return null

        val submission = riddleMappingRepository.findAllByOwnerGroupId(groupId)
            .firstOrNull { it.riddleId == riddleId }
        if (submission != null) {
            submission.hintUsed = true
            riddleMappingRepository.save(submission)
        } else {
            val nextRiddles = getNextRiddlesGroup(groupId, riddle)
            if (nextRiddles.find { it.id == riddle.id } == null)
                return null

            riddleMappingRepository.save(
                RiddleMappingEntity(0, riddle.id, 0, groupId,
                    hintUsed = true, completed = false, skipped = false, attemptCount = 0)
            )
        }
        return riddle.hint
    }

    @Retryable(value = [SQLException::class], maxRetries = 5, delay = 500L, multiplier = 1.5)
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    override fun submitRiddleForUser(
        user: CmschUser,
        riddleId: Int,
        solution: String,
        skip: Boolean
    ): RiddleSubmissionView? {
        val banStatus = riddleModerationService.getUserAndGroupBanStatus(user.internalId, user.groupId?.toString())
        if (banStatus == SubmissionModerationStatus.HARD_BAN) {
            return RiddleSubmissionView(status = RiddleSubmissionStatus.SUBMITTER_BANNED)
        }
        val riddle = riddleEntityRepository.findById(riddleId).orElse(null) ?: return null
        val allCategories = riddleCategoryRepository.findAll().toList()
        allCategories
            .firstOrNull { it.categoryId == riddle.categoryId && it.visible && it.minRole.value <= user.role.value }
            ?: return null

        if (skip && (cannotSkip(riddle) || !riddleComponent.skipEnabled))
            return RiddleSubmissionView(status = RiddleSubmissionStatus.CANNOT_SKIP)

        if (banStatus == SubmissionModerationStatus.SHADOW_BAN) {
            return RiddleSubmissionView(status = RiddleSubmissionStatus.WRONG)
        }
        val submission = riddleMappingRepository.findAllByOwnerUserId(user.id)
            .firstOrNull { it.riddleId == riddleId }
        if (submission != null) {
            if (!skip && checkSolutionIsWrong(solution, riddle)) {
                if (riddleComponent.saveFailedAttempts) {
                    submission.attemptCount += 1
                    submission.completedAt = clock.getTimeInSeconds()
                    riddleMappingRepository.save(submission)
                }
                return RiddleSubmissionView(status = RiddleSubmissionStatus.WRONG)
            }

            if (!submission.completed) {
                submission.completed = true
                submission.skipped = skip
                submission.attemptCount += 1
                submission.completedAt = clock.getTimeInSeconds()
                riddleMappingRepository.save(submission)
                if (!skip && riddle.firstSolver.isBlank()) {
                    riddle.firstSolver = user.userName
                    riddleEntityRepository.save(riddle)
                }
            }
            return RiddleSubmissionView(status = RiddleSubmissionStatus.CORRECT, getNextRiddlesUser(user, riddle))

        } else {
            val nextRiddles = getNextRiddlesUser(user, riddle)
            if (nextRiddles.find { it.id == riddle.id } == null)
                return null
            val userEntity = userService.getById(user.internalId)
            if (!skip && checkSolutionIsWrong(solution, riddle)) {
                if (riddleComponent.saveFailedAttempts) {
                    riddleMappingRepository.save(
                        RiddleMappingEntity(
                            0, riddle.id, userEntity.id, 0,
                            hintUsed = false, completed = false, skipped = false,
                            completedAt = clock.getTimeInSeconds(), attemptCount = 1
                        )
                    )
                }
                return RiddleSubmissionView(status = RiddleSubmissionStatus.WRONG)
            }

            riddleMappingRepository.save(
                RiddleMappingEntity(0, riddle.id, userEntity.id, 0,
                    hintUsed = false, completed = true, skipped = skip,
                    completedAt = clock.getTimeInSeconds(), attemptCount = 1)
            )
            if (!skip && riddle.firstSolver.isBlank()) {
                riddle.firstSolver = user.userName
                riddleEntityRepository.save(riddle)
            }
            return RiddleSubmissionView(status = RiddleSubmissionStatus.CORRECT, getNextRiddlesUser(user, riddle))
        }
    }

    @Retryable(value = [SQLException::class], maxRetries = 5, delay = 500L, multiplier = 1.5)
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    override fun submitRiddleForGroup(
        user: CmschUser,
        groupId: Int?,
        groupName: String,
        riddleId: Int,
        solution: String,
        skip: Boolean
    ): RiddleSubmissionView? {
        if (groupId == null)
            return null

        val banStatus = riddleModerationService.getUserAndGroupBanStatus(user.internalId, groupId.toString())
        if (banStatus == SubmissionModerationStatus.HARD_BAN) {
            return RiddleSubmissionView(status = RiddleSubmissionStatus.SUBMITTER_BANNED)
        }

        val riddle = riddleEntityRepository.findById(riddleId).orElse(null) ?: return null
        val allCategories = riddleCategoryRepository.findAll().toList()
        allCategories
            .firstOrNull { it.categoryId == riddle.categoryId && it.visible && it.minRole.value <= user.role.value }
            ?: return null

        if (skip && (cannotSkip(riddle) || !riddleComponent.skipEnabled))
            return RiddleSubmissionView(status = RiddleSubmissionStatus.CANNOT_SKIP)

        if (banStatus == SubmissionModerationStatus.SHADOW_BAN) {
            return RiddleSubmissionView(status = RiddleSubmissionStatus.WRONG)
        }

        val submission = riddleMappingRepository.findAllByOwnerGroupId(groupId)
            .firstOrNull { it.riddleId == riddleId }
        if (submission != null) {
            if (!skip && checkSolutionIsWrong(solution, riddle)) {
                if (riddleComponent.saveFailedAttempts) {
                    submission.attemptCount += 1
                    submission.completedAt = clock.getTimeInSeconds()
                    riddleMappingRepository.save(submission)
                }
                return RiddleSubmissionView(status = RiddleSubmissionStatus.WRONG)
            }

            if (!submission.completed) {
                submission.completed = true
                submission.skipped = skip
                submission.attemptCount += 1
                submission.completedAt = clock.getTimeInSeconds()
                riddleMappingRepository.save(submission)
                if (!skip && riddle.firstSolver.isBlank()) {
                    riddle.firstSolver = groupName
                    riddleEntityRepository.save(riddle)
                }
            }
            return RiddleSubmissionView(status = RiddleSubmissionStatus.CORRECT, getNextRiddlesGroup(groupId, riddle))

        } else {
            val nextRiddles = getNextRiddlesGroup(groupId, riddle)
            if (nextRiddles.find { it.id == riddle.id } == null)
                return null
            if (!skip && checkSolutionIsWrong(solution, riddle)) {
                if (riddleComponent.saveFailedAttempts) {
                    riddleMappingRepository.save(
                        RiddleMappingEntity(
                            0, riddle.id, 0, groupId,
                            hintUsed = false, completed = false, skipped = false,
                            completedAt = clock.getTimeInSeconds(), attemptCount = 1
                        )
                    )
                }
                return RiddleSubmissionView(status = RiddleSubmissionStatus.WRONG)
            }

            riddleMappingRepository.save(
                RiddleMappingEntity(0, riddle.id, 0, groupId,
                    hintUsed = false, completed = true, skipped = skip,
                    completedAt = clock.getTimeInSeconds(), attemptCount = 1
                )
            )
            if (!skip && riddle.firstSolver.isBlank()) {
                riddle.firstSolver = groupName
                riddleEntityRepository.save(riddle)
            }
            return RiddleSubmissionView(status = RiddleSubmissionStatus.CORRECT, getNextRiddlesGroup(groupId, riddle))
        }
    }

    private fun cannotSkip(riddle: RiddleEntity): Boolean {
        return riddleMappingRepository.countAllByCompletedTrueAndSkippedFalseAndRiddleId(riddle.id) <
                riddleComponent.skipAfterGroupsSolved
    }

    private fun checkSolutionIsWrong(solution: String, riddle: RiddleEntity): Boolean {
        var transformedSubmittedSolution = solution
        var transformedRiddleSolution = riddle.solution

        if (riddleComponent.ignoreAccent) {
            transformedSubmittedSolution = replaceAccent(transformedSubmittedSolution)
            transformedRiddleSolution = replaceAccent(transformedRiddleSolution)
        }
        if (riddleComponent.ignoreCase) {
            transformedSubmittedSolution = transformedSubmittedSolution.lowercase()
            transformedRiddleSolution = transformedRiddleSolution.lowercase()
        }
        if (riddleComponent.ignoreWhitespace) {
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

        val solutionList = transformedRiddleSolution.split(";")

        return !(transformedSubmittedSolution in solutionList)
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

    private fun getNextRiddlesUser(user: CmschUser, riddle: RiddleEntity): List<RiddleView> {
        val allRiddles = riddleEntityRepository.findAll().toList()
        val riddleIdsInCategory = allRiddles
            .filter { it.categoryId == riddle.categoryId }
            .map { it.id }
        val submissions = riddleMappingRepository.findAllByOwnerUserId(user.id)
            .filter { it.riddleId in riddleIdsInCategory }
        return findNextTo(riddle.categoryId, submissions, allRiddles)
    }

    private fun getNextRiddlesGroup(groupId: Int, riddle: RiddleEntity): List<RiddleView> {
        val allRiddles = riddleEntityRepository.findAll().toList()
        val riddleIdsInCategory = allRiddles
            .filter { it.categoryId == riddle.categoryId }
            .map { it.id }
        val submissions = riddleMappingRepository.findAllByOwnerGroupId(groupId)
            .filter { it.riddleId in riddleIdsInCategory }
        return findNextTo(riddle.categoryId, submissions, allRiddles)
    }

    private fun findNextTo(
        categoryId: Int,
        submissions: List<RiddleMappingEntity>,
        allRiddles: List<RiddleEntity>
    ): List<RiddleView> =
        allRiddles
            .filter { it.categoryId == categoryId }
            .filter { filter -> submissions.none { it.completed && it.riddleId == filter.id } }
            .sortedBy { it.order }
            .take(riddleComponent.visibleRiddlesPerCategory.toInt())
            .map { mapRiddleView(submissions, it) }

    private fun findNextTo(
        categoryId: Int,
        submissions: List<RiddleMappingEntity>
    ): List<RiddleView> {
        val allRiddles = riddleEntityRepository.findAll().toList()
        return findNextTo(categoryId, submissions, allRiddles)
    }


    @Transactional(readOnly = true)
    override fun getCompletedRiddleCountUser(user: CmschUser): Int {
        val allCategories = riddleCategoryRepository.findAll().toList()
        val categoryIds = allCategories
            .filter { it.visible && it.minRole.value <= user.role.value }
            .map { it.categoryId }
        val allRiddles = riddleEntityRepository.findAll().toList()
        val riddleIds = allRiddles
            .filter { it.categoryId in categoryIds }
            .map { it.id }
        return riddleMappingRepository.countAllByCompletedTrueAndOwnerUserIdAndRiddleIdIn(user.id, riddleIds)
    }

    @Transactional(readOnly = true)
    override fun getCompletedRiddleCountGroup(user: CmschUser, groupId: Int?): Int {
        if (groupId == null)
            return 0
        val allCategories = riddleCategoryRepository.findAll().toList()
        val categoryIds = allCategories
            .filter { it.visible && it.minRole.value <= user.role.value }
            .map { it.categoryId }
        val allRiddles = riddleEntityRepository.findAll().toList()
        val riddleIds = allRiddles
            .filter { it.categoryId in categoryIds }
            .map { it.id }
        return riddleMappingRepository.countAllByCompletedTrueAndOwnerGroupIdAndRiddleIdIn(groupId, riddleIds)
    }

    @Transactional(readOnly = true)
    override fun getTotalRiddleCount(user: CmschUser): Int {
        val allCategories = riddleCategoryRepository.findAll().toList()
        val categoryIds = allCategories
            .filter { it.visible && it.minRole.value <= user.role.value }
            .map { it.categoryId }
        val allRiddles = riddleEntityRepository.findAll().toList()
        return allRiddles
            .count { it.categoryId in categoryIds }
    }

    @Transactional(readOnly = true)
    override fun listRiddleHistoryForUser(user: CmschUser): Map<String, List<RiddleViewWithSolution>> {
        val categories = riddleCategoryRepository.findAll().toList()
            .filter { it.visible && it.minRole.value <= user.role.value }
        val submissionsList = riddleMappingRepository.findAllByOwnerUserIdAndCompletedTrue(user.id)
        val riddleIds = submissionsList.map { it.riddleId }.toSet()
        val riddlesById = riddleEntityRepository.findAllById(riddleIds).associateBy { it.id }
        val submissions = submissionsList
            .groupBy { riddlesById[it.riddleId]?.categoryId ?: 0 }
            .filterKeys { it != 0 }

        return categories.associate { category ->
            category.title to submissions.getOrDefault(category.categoryId, listOf())
                        .map { mapping -> mapping to riddlesById[mapping.riddleId] }
                        .sortedBy { it.second?.order }
                        .mapNotNull { it.second?.let { riddle -> mapRiddle(it.first, riddle) } }
                        .toList()
        }
    }

    @Transactional(readOnly = true)
    override fun listRiddleHistoryForGroup(user: CmschUser, groupId: Int?): Map<String, List<RiddleViewWithSolution>> {
        if (groupId == null)
            return mapOf()

        val categories = riddleCategoryRepository.findAll().toList()
            .filter { it.visible && it.minRole.value <= user.role.value }
        val submissionsList = riddleMappingRepository.findAllByOwnerGroupIdAndCompletedTrue(groupId)
        val riddleIds = submissionsList.map { it.riddleId }.toSet()
        val riddlesById = riddleEntityRepository.findAllById(riddleIds).associateBy { it.id }
        val submissions = submissionsList
            .groupBy { riddlesById[it.riddleId]?.categoryId ?: 0 }
            .filterKeys { it != 0 }

        return categories.associate { category ->
            category.title to submissions.getOrDefault(category.categoryId, listOf())
                .map { mapping -> mapping to riddlesById[mapping.riddleId] }
                .sortedBy { it.second?.order }
                .mapNotNull { it.second?.let { riddle -> mapRiddle(it.first, riddle) } }
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