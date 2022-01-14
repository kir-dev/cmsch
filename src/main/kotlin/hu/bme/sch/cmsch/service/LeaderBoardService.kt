package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.dao.AchievementRepository
import hu.bme.sch.cmsch.dao.GroupRepository
import hu.bme.sch.cmsch.dao.SubmittedAchievementRepository
import hu.bme.sch.cmsch.dto.TopListEntryDto
import hu.bme.sch.cmsch.model.GroupEntity
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.annotation.PostConstruct

@Suppress("RedundantModalityModifier") // Spring transactional proxy requires it not to be final
@Service
open class LeaderBoardService(
        private val submissions: SubmittedAchievementRepository,
        private val achievements: AchievementRepository,
        private val groups: GroupRepository,
        private val config: RealtimeConfigService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private var cachedTopList: List<TopListEntryDto> = listOf()

    @PostConstruct
    fun init() {
        recalculate()
    }

    fun getBoard(): List<TopListEntryDto> {
        if (config.isLeaderBoardEnabled())
            return cachedTopList
        return listOf()
    }

    fun getBoardAnyways(): List<TopListEntryDto> {
        return cachedTopList
    }

    fun getScoreOfGroup(group: GroupEntity): Int? {
        if (config.isLeaderBoardEnabled())
            return cachedTopList.find { it.name == group.name }?.score ?: 0
        return null
    }

    @Transactional(readOnly = true)
    @Scheduled(fixedRate = 1000L * 60 * 60 * 10)
    open fun recalculate() {
        if (!config.isLeaderBoardUpdates()) {
            log.info("Recalculating is disabled now")
            return
        }
        forceRecalculate()
    }

    @Transactional(readOnly = true)
    open fun forceRecalculate() {
        log.info("Recalculating top list cache")
        cachedTopList = submissions.findAll()
                .asSequence()
                .groupBy { it.groupName }
                .filter { groups.findByName(it.key).map { it.races }.orElse(false) }
                .map { TopListEntryDto(it.key, it.value.sumOf { it.score }) }
                .sortedByDescending { it.score }
    }

}
