package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.dao.*
import hu.bme.sch.cmsch.dto.TopListAsGroupEntryDto
import hu.bme.sch.cmsch.dto.TopListAsUserEntryDto
import hu.bme.sch.cmsch.dto.config.OwnershipType
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.UserEntity
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import javax.annotation.PostConstruct

@Suppress("RedundantModalityModifier") // Spring transactional proxy requires it not to be final
@Service
open class LeaderBoardService(
    private val achievementSubmissions: SubmittedAchievementRepository,
    private val riddleSubmissions: RiddleMappingRepository,
    private val groups: GroupRepository,
    private val users: UserRepository,
    private val config: RealtimeConfigService,
    @Value("\${cmsch.achievement.ownership:USER}") private val achievementOwnershipMode: OwnershipType,
    @Value("\${cmsch.riddle.ownership:USER}") private val riddleOwnershipMode: OwnershipType
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private var cachedTopListForGroups: List<TopListAsGroupEntryDto> = listOf()
    private var cachedTopListForUsers: List<TopListAsUserEntryDto> = listOf()

    @PostConstruct
    fun init() {
        recalculate()
    }

    fun getBoardForGroups(): List<TopListAsGroupEntryDto> {
        if (config.isLeaderBoardEnabled())
            return cachedTopListForGroups
        return listOf()
    }

    fun getBoardForUsers(): List<TopListAsUserEntryDto> {
        if (config.isLeaderBoardEnabled())
            return cachedTopListForUsers
        return listOf()
    }

    fun getBoardAnywaysForGroups(): List<TopListAsGroupEntryDto> {
        return cachedTopListForGroups
    }

    fun getBoardAnywaysForUsers(): List<TopListAsUserEntryDto> {
        return cachedTopListForUsers
    }

    fun getScoreOfGroup(group: GroupEntity): Int? {
        if (config.isLeaderBoardEnabled())
            return cachedTopListForGroups.find { it.name == group.name }?.totalScore ?: 0
        return null
    }

    fun getScoreOfUser(user: UserEntity): Int? {
        if (config.isLeaderBoardEnabled())
            return cachedTopListForUsers.find { it.id == user.id }?.totalScore ?: 0
        return null
    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    @Scheduled(fixedRate = 1000L * 60 * 60 * 10)
    open fun recalculate() {
        if (!config.isLeaderBoardUpdates()) {
            log.info("Recalculating is disabled now")
            return
        }
        forceRecalculateForGroups()
        forceRecalculateForUsers()
    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    open fun forceRecalculateForGroups() {
        val hintPercentage: Float = config.getHintScorePercentage() / 100f
        log.info("Recalculating group top list cache; hint:{}", hintPercentage)
        val achievements = when (achievementOwnershipMode) {
            OwnershipType.GROUP -> {
                achievementSubmissions.findAll()
                    .groupBy { it.groupName }
                    .filter { groups.findByName(it.key).map { m -> m.races }.orElse(false) }
                    .map { TopListAsGroupEntryDto(it.key, it.value.sumOf { s -> s.score }, 0, 0) }
            }
            OwnershipType.USER -> listOf()
        }

        val riddles = when (riddleOwnershipMode) {
            OwnershipType.GROUP -> {
                riddleSubmissions.findAll()
                    .groupBy { it.ownerGroup }
                    .filter { it.key?.races ?: false }
                    .map {
                        TopListAsGroupEntryDto(it.key?.name ?: "n/a", 0, it.value
                            .sumOf { s ->
                                ((s.riddle?.score?.toFloat() ?: 0f) * (if (s.hintUsed) hintPercentage else 1f)).toInt()
                            }, 0
                        )
                    }
            }
            OwnershipType.USER -> listOf()
        }

        cachedTopListForGroups = sequenceOf(achievements, riddles)
            .flatMap { it }
            .groupBy { it.name }
            .map { entries ->
                val achievementScore = entries.value.maxOf { it.achievementScore }
                val riddleScore = entries.value.maxOf { it.riddleScore }
                TopListAsGroupEntryDto(entries.key,
                    achievementScore,
                    riddleScore,
                    achievementScore + riddleScore)
            }
            .sortedByDescending { it.totalScore }
        log.info("Recalculating finished")
    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    open fun forceRecalculateForUsers() {
        val hintPercentage: Float = config.getHintScorePercentage() / 100f
        log.info("Recalculating user top list cache; hint:{}", hintPercentage)
        val achievements = when (achievementOwnershipMode) {
            OwnershipType.GROUP -> listOf()
            OwnershipType.USER -> {
                achievementSubmissions.findAll()
                    .groupBy { it.userId }
                    .map {
                        TopListAsUserEntryDto(
                            it.key ?: 0,
                            users.findById(it.key ?: 0).map { it.fullName }.orElse("n/a"),
                            it.value.sumOf { s -> s.score }, 0, 0
                        )
                    }
            }
        }

        val riddles = when (riddleOwnershipMode) {
            OwnershipType.GROUP -> listOf()
            OwnershipType.USER -> {
                riddleSubmissions.findAll()
                    .groupBy { it.ownerUser }
                    .map {
                        TopListAsUserEntryDto(it.key?.id ?: 0,
                            it.key?.fullName ?: "n/a", 0,
                            it.value.sumOf { s ->
                                ((s.riddle?.score?.toFloat() ?: 0f) * (if (s.hintUsed) hintPercentage else 1f)).toInt()
                            }, 0
                        )
                    }
            }
        }

        cachedTopListForUsers = sequenceOf(achievements, riddles)
            .flatMap { it }
            .groupBy { it.id }
            .map { entries ->
                val achievementScore = entries.value.maxOf { it.achievementScore }
                val riddleScore = entries.value.maxOf { it.riddleScore }
                TopListAsUserEntryDto(entries.key, entries.value.firstOrNull()?.name ?: "n/a",
                    achievementScore,
                    riddleScore,
                    achievementScore + riddleScore)
            }
            .sortedByDescending { it.totalScore }
        log.info("Recalculating finished")
    }

}
