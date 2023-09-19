package hu.bme.sch.cmsch.component.riddle

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service

enum class SubmissionModerationStatus { NONE, SHADOW_BAN, HARD_BAN }

@Service
@ConditionalOnBean(RiddleComponent::class)
class RiddleModerationService {
    private var userShadowBans = emptySet<String>()
    private var groupShadowBans = emptySet<String>()
    private var userBans = emptySet<String>()
    private var groupBans = emptySet<String>()

    fun getUserAndGroupBanStatus(userId: String, groupId: String?): SubmissionModerationStatus =
        if (userBans.contains(userId) || (groupId != null && groupBans.contains(groupId)))
            SubmissionModerationStatus.HARD_BAN
        else if (userShadowBans.contains(userId) || (groupId != null && groupShadowBans.contains(groupId)))
            SubmissionModerationStatus.SHADOW_BAN
        else
            SubmissionModerationStatus.NONE

    fun setUserShadowBans(rawList: String) = parseBanList(rawList).also { userShadowBans = it }
    fun setGroupShadowBans(rawList: String) = parseBanList(rawList).also { groupShadowBans = it }
    fun setUserBans(rawList: String) = parseBanList(rawList).also { userBans = it }
    fun setGroupBans(rawList: String) = parseBanList(rawList).also { groupBans = it }

    private fun parseBanList(banList: String): Set<String> =
        banList.lineSequence().flatMap { line -> line.split(",").map { it.trim() } }.filter { it.isNotBlank() }.toSet()
}
