package hu.bme.sch.g7.service

import hu.bme.sch.g7.dao.AchievementRepository
import hu.bme.sch.g7.dao.SubmittedAchievementRepository
import hu.bme.sch.g7.dto.TopListEntryDto
import hu.bme.sch.g7.model.GroupEntity
import org.springframework.stereotype.Service

@Service
class LeaderBoardService(
        val submissions: SubmittedAchievementRepository,
        val achievements: AchievementRepository,
        val config: RealtimeConfigService
) {

    fun getBoard(): List<TopListEntryDto> {
        // FIXME: only mocked for now
        return if (config.isLeaderBoardEnabled()) {
            listOf(TopListEntryDto("I16", 2000),
                    TopListEntryDto("I09", 1870),
                    TopListEntryDto("V10", 69))
        } else {
            listOf()
        }
    }

    fun getScoreOfGroup(group: GroupEntity): Int? {
        // FIXME: only mock for now
        return if (config.isLeaderBoardEnabled()) 300 else null
    }

}