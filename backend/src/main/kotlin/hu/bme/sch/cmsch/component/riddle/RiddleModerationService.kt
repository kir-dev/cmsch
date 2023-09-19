package hu.bme.sch.cmsch.component.riddle

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service

@Service
@ConditionalOnBean(RiddleComponent::class)
class RiddleModerationService(private val riddleComponent: RiddleComponent) {
    private val userShadowBanList = BanList { riddleComponent.userShadowBanList.getValue() }
    private val groupShadowBanList = BanList { riddleComponent.groupShadowBanList.getValue() }
    private val userBanList = BanList { riddleComponent.userBanList.getValue() }
    private val groupBanList = BanList { riddleComponent.groupBanList.getValue() }

    fun getUserAndGroupBanStatus(userId: String, groupId: String?): SubmissionModerationStatus =
        if (userBanList.isOnList(userId) || (groupId != null && groupBanList.isOnList(groupId))) {
            SubmissionModerationStatus.HARD_BAN
        } else if (userShadowBanList.isOnList(userId) || (groupId != null && groupShadowBanList.isOnList(groupId))) {
            SubmissionModerationStatus.SHADOW_BAN
        } else
            SubmissionModerationStatus.NONE

}

enum class SubmissionModerationStatus { NONE, SHADOW_BAN, HARD_BAN }

private class BanList(private val rawListSupplier: () -> String) {
    private var banList: Set<String> = emptySet()
    private var rawList: String = ""

    fun isOnList(entry: String): Boolean {
        checkForListChange();
        return banList.contains(entry)
    }

    private fun checkForListChange() {
        synchronized(this) {
            val newList = rawListSupplier()
            if (newList != rawList) {
                rawList = newList
                banList = parseBanList(rawList)
            }
        }
    }

    private fun parseBanList(banList: String): Set<String> =
        banList.lineSequence().flatMap { line -> line.split(",").map { it.trim() } }.filter { it.isNotBlank() }.toSet()
}