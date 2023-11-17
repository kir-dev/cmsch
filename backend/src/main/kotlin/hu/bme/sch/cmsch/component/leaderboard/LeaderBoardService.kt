package hu.bme.sch.cmsch.component.leaderboard

import hu.bme.sch.cmsch.component.challenge.ChallengeSubmissionRepository
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.component.riddle.RiddleComponent
import hu.bme.sch.cmsch.component.riddle.RiddleEntityRepository
import hu.bme.sch.cmsch.component.riddle.RiddleMappingRepository
import hu.bme.sch.cmsch.component.task.SubmittedTaskRepository
import hu.bme.sch.cmsch.component.task.TaskComponent
import hu.bme.sch.cmsch.component.token.TokenComponent
import hu.bme.sch.cmsch.component.token.TokenPropertyRepository
import hu.bme.sch.cmsch.config.OwnershipType
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.util.CombinedKey
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*
import jakarta.annotation.PostConstruct
import org.postgresql.util.PSQLException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable

@Suppress("RedundantModalityModifier") // Spring transactional proxy requires it not to be final
@Service
@ConditionalOnBean(LeaderBoardComponent::class)
open class LeaderBoardService(
    private val groups: GroupRepository,
    private val users: UserRepository,
    private val leaderBoardComponent: LeaderBoardComponent,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val taskSubmissions: Optional<SubmittedTaskRepository>,
    private val riddleSubmissions: Optional<RiddleMappingRepository>,
    private val riddleRepository: Optional<RiddleEntityRepository>,
    private val riddleComponent: Optional<RiddleComponent>,
    private val challengeSubmissions: Optional<ChallengeSubmissionRepository>,
    private val tokenSubmissions: Optional<TokenPropertyRepository>,
    private val taskComponent: Optional<TaskComponent>,
    private val tokenComponent: Optional<TokenComponent>
) {

    class TopListDetails(
        var id: Int,
        var name: String,
        var items: MutableMap<String, Int> = mutableMapOf()
    ) {
        val total: Int
            get() = items.values.sumOf { it }
    }

    class TopListByCategoryDetails(
        var name: String,
        var items: Map<String, Int> = mapOf()
    )

    private val log = LoggerFactory.getLogger(javaClass)
    private var cachedTopListForGroups: List<LeaderBoardAsGroupEntryDto> = listOf()
    private var cachedTopListForUsers: List<LeaderBoardAsUserEntryDto> = listOf()
    private var cachedDetailsForGroups: List<TopListDetails> = listOf()
    private var cachedDetailsForUsers: List<TopListDetails> = listOf()

    @PostConstruct
    fun init() {
        recalculate()
    }

    fun getBoardForGroups(): List<LeaderBoardAsGroupEntryDto> {
        if (leaderBoardComponent.leaderboardEnabled.isValueTrue())
            return cachedTopListForGroups
        return listOf()
    }

    fun getBoardForUsers(): List<LeaderBoardAsUserEntryDto> {
        if (leaderBoardComponent.leaderboardEnabled.isValueTrue())
            return cachedTopListForUsers
        return listOf()
    }

    fun getBoardDetailsForGroups(): List<TopListDetails> {
        if (leaderBoardComponent.leaderboardDetailsEnabled.isValueTrue())
            return cachedDetailsForGroups
        return listOf()
    }

    fun getBoardDetailsForUsers(): List<TopListDetails> {
        if (leaderBoardComponent.leaderboardDetailsEnabled.isValueTrue())
            return cachedDetailsForUsers
        return listOf()
    }

    fun getBoardDetailsByCategoryForGroups(): List<TopListByCategoryDetails> {
        if (leaderBoardComponent.leaderboardDetailsByCategoryEnabled.isValueTrue())
            return transpose(cachedDetailsForGroups)
        return listOf()
    }

    private fun transpose(cachedDetailsForGroups: List<TopListDetails>): List<TopListByCategoryDetails> {
        return cachedDetailsForGroups
            .flatMap { record -> record.items.entries.map { Triple(it.key, record.name, it.value) } }
            .groupBy { it.first }
            .map { category -> TopListByCategoryDetails(category.key, category.value.associate { it.second to it.third }) }
            .sortedBy { it.name }
    }

    fun getBoardDetailsByCategoryForUsers(): List<TopListByCategoryDetails> {
        if (leaderBoardComponent.leaderboardDetailsByCategoryEnabled.isValueTrue())
            return transpose(cachedDetailsForUsers)
        return listOf()
    }

    fun getBoardAnywaysForGroups(): List<LeaderBoardAsGroupEntryDto> {
        return cachedTopListForGroups
    }

    fun getBoardAnywaysForUsers(): List<LeaderBoardAsUserEntryDto> {
        return cachedTopListForUsers
    }

    fun getScoreOfGroup(groupName: String): Int? {
        if (leaderBoardComponent.leaderboardEnabled.isValueTrue())
            return cachedTopListForGroups.find { it.name == groupName }?.totalScore ?: 0
        return null
    }

    fun getScoreOfUser(user: CmschUser): Int? {
        if (leaderBoardComponent.leaderboardEnabled.isValueTrue())
            return cachedTopListForUsers.find { it.id == user.id }?.totalScore ?: 0
        return null
    }

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    @Scheduled(fixedRate = 1000L * 60 * 60 * 10)
    open fun recalculate() {
        if (leaderBoardComponent.leaderboardFrozen.isValueTrue()) {
            log.info("Recalculating is disabled now")
            return
        }
        forceRecalculateForGroups()
        forceRecalculateForUsers()
    }

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    open fun forceRecalculateForGroups() {
        val hintPercentage: Float = riddleComponent.map { it.hintScorePercent.getValue().toIntOrNull() ?: 0 }.orElse(0) / 100f
        val details = mutableMapOf<Int, TopListDetails>()
        log.info("Recalculating group top list cache; hint:{}", hintPercentage)

        val tasksPercent = leaderBoardComponent.tasksPercent.getIntValue(100) / 100.0f
        val tasks = when (startupPropertyConfig.taskOwnershipMode) {
            OwnershipType.GROUP -> {
                taskSubmissions.map { it.findAll() }.orElse(listOf())
                    .groupBy { CombinedKey(it.groupId ?: 0, it.groupName) }
                    .filter { groups.findByName(it.key.name).map { m -> m.races }.orElse(false) }
                    .map {
                        LeaderBoardAsGroupEntryDto(
                            it.key.id,
                            it.key.name,
                            taskScore = (it.value.sumOf { s -> s.score } * tasksPercent).toInt())
                    }
            }
            OwnershipType.USER -> listOf()
        }
        val tasksTitle = taskComponent.map { it.menuDisplayName.getValue() }.orElse("")
        tasks.forEach { task ->
            details.computeIfAbsent(task.id) { key -> TopListDetails(key, task.name) }
                .items.compute(tasksTitle) { _, value -> (value ?: 0) + task.taskScore }
        }

        val riddlesPercent = leaderBoardComponent.riddlesPercent.getIntValue(100) / 100.0f
        val riddleCache = riddleRepository.map { repo -> repo.findAll().associateBy { it.id } }.orElse(mapOf())
        val riddles = when (startupPropertyConfig.riddleOwnershipMode) {
            OwnershipType.GROUP -> {
                riddleSubmissions.map { it.findAll() }.orElse(listOf())
                    .groupBy { groups.findById(it.ownerGroupId).orElseThrow() }
                    .filter { it.key?.races ?: false }
                    .map { riddleGroup ->
                        LeaderBoardAsGroupEntryDto(
                            riddleGroup.key?.id ?: 0,
                            riddleGroup.key?.name ?: "n/a",
                            riddleScore = (riddleGroup.value
                                .filter { it.completed && !it.skipped }
                                .sumOf { s ->
                                    ((riddleCache[s.riddleId]?.score?.toFloat() ?: 0f) * (if (s.hintUsed) hintPercentage else 1f)).toInt()
                                } * riddlesPercent).toInt())
                    }
            }
            OwnershipType.USER -> listOf()
        }
        val riddleTitle = riddleComponent.map { it.menuDisplayName.getValue() }.orElse("")
        riddles.forEach { riddle ->
            details.computeIfAbsent(riddle.id) { key -> TopListDetails(key, riddle.name) }
                .items.compute(riddleTitle) { _, value -> (value ?: 0) + riddle.riddleScore }
        }

        val challengesPercent = leaderBoardComponent.challengesPercent.getIntValue(100) / 100.0f
        val challenges = when (startupPropertyConfig.challengeOwnershipMode) {
            OwnershipType.GROUP -> {
                challengeSubmissions.map { it.findAll() }.orElse(listOf())
                    .groupBy { CombinedKey(it.groupId ?: 0, it.groupName) }
                    .filter { groups.findByName(it.key.name).map { m -> m.races }.orElse(false) }
                    .map { entity ->
                        entity.value.forEach { challenge ->
                            details.computeIfAbsent(entity.key.id) {
                                key -> TopListDetails(key, entity.value[0].groupName)
                            }.items.compute(challenge.category) {
                                _, value -> (value ?: 0) + (challenge.score * challengesPercent).toInt()
                            }
                        }
                        LeaderBoardAsGroupEntryDto(
                            entity.key.id,
                            entity.key.name,
                            challengeScore = entity.value.sumOf { s -> (s.score * challengesPercent).toInt() })
                    }
            }
            OwnershipType.USER -> listOf()
        }

        val tokenPercent = leaderBoardComponent.tokenPercent.getIntValue(100) / 100.0f
        val tokens = when (startupPropertyConfig.tokenOwnershipMode) {
            OwnershipType.GROUP -> {
                tokenSubmissions.map { it.findAll() }.orElse(listOf())
                    .groupBy { it.ownerGroup }
                    .map { entity ->
                        LeaderBoardAsGroupEntryDto(
                            entity.key?.id ?: 0,
                            entity.key?.name ?: "n/a",
                            tokenScore = (entity.value.sumOf { s -> s.token?.score ?: 0 } * tokenPercent).toInt()
                        )
                    }
            }
            OwnershipType.USER -> listOf()
        }

        val tokenTitle = tokenComponent.map { it.menuDisplayName.getValue() }.orElse("")
        tokens.forEach { token ->
            details.computeIfAbsent(token.id) { key -> TopListDetails(key, token.name) }
                .items.compute(tokenTitle) { _, value -> (value ?: 0) + token.tokenScore }
        }

        cachedTopListForGroups = sequenceOf(tasks, riddles, challenges, tokens)
            .flatMap { it }
            .groupBy { CombinedKey(it.id, it.name) }
            .map { entries ->
                val taskScore = entries.value.maxOf { it.taskScore }
                val riddleScore = entries.value.maxOf { it.riddleScore }
                val challengeScore = entries.value.maxOf { it.challengeScore }
                val tokenScore = entries.value.maxOf { it.tokenScore }
                LeaderBoardAsGroupEntryDto(entries.key.id,
                    entries.key.name,
                    taskScore = taskScore,
                    riddleScore = riddleScore,
                    challengeScore = challengeScore,
                    tokenScore = tokenScore,
                    totalScore = taskScore + riddleScore + challengeScore + tokenScore)
            }
            .sortedByDescending { it.totalScore }

        details.forEach { it.value.items.entries.removeAll { (k, v) -> k.isBlank() || v == 0 } }
        cachedDetailsForGroups = details.values.sortedByDescending { it.total }
        log.info("Recalculating finished")
    }

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    open fun forceRecalculateForUsers() {
        val hintPercentage: Float = riddleComponent.map { it.hintScorePercent.getValue().toIntOrNull() ?: 0 }.orElse(0) / 100f
        log.info("Recalculating user top list cache; hint:{}", hintPercentage)
        val details = mutableMapOf<Int, TopListDetails>()

        val tasksPercent = leaderBoardComponent.tasksPercent.getIntValue(100) / 100.0f
        val tasks = when (startupPropertyConfig.taskOwnershipMode) {
            OwnershipType.GROUP -> listOf()
            OwnershipType.USER -> {
                taskSubmissions.map { it.findAll() }.orElse(listOf())
                    .groupBy { it.userId }
                    .map { entity ->
                        val user = users.findById(entity.key ?: 0)
                        LeaderBoardAsUserEntryDto(
                            entity.key ?: 0,
                            user.map { it.fullName }.orElse("n/a"),
                            user.map { it.groupName }.orElse("-"),
                            taskScore = (entity.value.sumOf { s -> s.score } * tasksPercent).toInt()
                        )
                    }
            }
        }
        val tasksTitle = taskComponent.map { it.menuDisplayName.getValue() }.orElse("")
        tasks.forEach {
            details.computeIfAbsent(it.id) { key -> TopListDetails(key, it.name) }.items[tasksTitle] = it.taskScore
        }

        val riddlesPercent = leaderBoardComponent.riddlesPercent.getIntValue(100) / 100.0f
        val riddleCache = riddleRepository.map { repo -> repo.findAll().associateBy { it.id } }.orElse(mapOf())
        val riddles = when (startupPropertyConfig.riddleOwnershipMode) {
            OwnershipType.GROUP -> listOf()
            OwnershipType.USER -> {
                riddleSubmissions.map { it.findAll() }.orElse(listOf())
                    .groupBy { users.findById(it.ownerUserId).orElseThrow() }
                    .map { riddleGroup ->
                        LeaderBoardAsUserEntryDto(riddleGroup.key?.id ?: 0,
                            riddleGroup.key?.fullName ?: "n/a",
                            riddleGroup.key?.groupName ?: "-",
                            riddleScore = (riddleGroup.value
                                .filter { it.completed && !it.skipped }
                                .sumOf { s ->
                                    ((riddleCache[s.riddleId]?.score?.toFloat() ?: 0f) * (if (s.hintUsed) hintPercentage else 1f)).toInt()
                                } * riddlesPercent).toInt()
                        )
                    }
            }
        }
        val riddleTitle = riddleComponent.map { it.menuDisplayName.getValue() }.orElse("")
        riddles.forEach {
            details.computeIfAbsent(it.id) { key -> TopListDetails(key, it.name) }.items[riddleTitle] = it.riddleScore
        }

        val challengesPercent = leaderBoardComponent.challengesPercent.getIntValue(100) / 100.0f
        val challenges = when (startupPropertyConfig.challengeOwnershipMode) {
            OwnershipType.GROUP -> listOf()
            OwnershipType.USER -> {
                challengeSubmissions.map { it.findAll() }.orElse(listOf())
                    .groupBy { it.userId }
                    .map { entity ->
                        entity.value.forEach { challenge ->
                            details.computeIfAbsent(entity.key ?: 0) { key ->
                                TopListDetails(
                                    key,
                                    entity.value[0].userName
                                )
                            }.items[challenge.category] = (challenge.score * challengesPercent).toInt()
                        }
                        LeaderBoardAsUserEntryDto(
                            entity.key ?: 0,
                            entity.value[0].userName,
                            entity.value[0].groupName,
                            challengeScore = entity.value.sumOf { s -> (s.score * challengesPercent).toInt() }
                        )
                    }
            }
        }

        val tokenPercent = leaderBoardComponent.tokenPercent.getIntValue(100) / 100.0f
        val tokens = when (startupPropertyConfig.tokenOwnershipMode) {
            OwnershipType.GROUP -> listOf()
            OwnershipType.USER -> {
                tokenSubmissions.map { it.findAll() }.orElse(listOf())
                    .groupBy { it.ownerUser?.id ?: 0 }
                    .map { entity ->
                        LeaderBoardAsUserEntryDto(
                            entity.key,
                            entity.value[0].ownerUser?.fullName ?: "n/a",
                            entity.value[0].ownerGroup?.name ?: "n/a",
                            tokenScore = (entity.value.sumOf { s -> s.token?.score ?: 0 } * tokenPercent).toInt()
                        )
                    }
            }
        }
        val tokenTitle = tokenComponent.map { it.menuDisplayName.getValue() }.orElse("")
        tokens.forEach {
            details.computeIfAbsent(it.id) { key -> TopListDetails(key, it.name) }.items[tokenTitle] = it.tokenScore
        }

        cachedTopListForUsers = sequenceOf(tasks, riddles, challenges, tokens)
            .flatMap { it }
            .groupBy { it.id }
            .map { entries ->
                val taskScore = entries.value.maxOf { it.taskScore }
                val riddleScore = entries.value.maxOf { it.riddleScore }
                val challengeScore = entries.value.maxOf { it.challengeScore }
                val tokenScore = entries.value.maxOf { it.tokenScore }
                LeaderBoardAsUserEntryDto(entries.key, entries.value.firstOrNull()?.name ?: "n/a",
                    entries.value.firstOrNull()?.groupName ?: "n/a",
                    taskScore = taskScore,
                    riddleScore = riddleScore,
                    challengeScore = challengeScore,
                    tokenScore = tokenScore,
                    totalScore = taskScore + riddleScore + challengeScore + tokenScore)
            }
            .sortedByDescending { it.totalScore }
        details.forEach { it.value.items.entries.removeAll { (k, v) -> k.isBlank() || v == 0 } }
        cachedDetailsForUsers = details.values.sortedByDescending { it.total }

        log.info("Recalculating finished")
    }

    fun getPlaceOfGroup(group: GroupEntity): Int {
        return getBoardForGroups().indexOfFirst { it.name == group.name } + 1
    }

}
