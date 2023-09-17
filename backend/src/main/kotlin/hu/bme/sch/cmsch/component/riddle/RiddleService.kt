package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.component.login.CmschUser

interface RiddleService {

    fun listRiddlesForUser(user: CmschUser): List<RiddleCategoryDto>

    fun listRiddlesForGroup(user: CmschUser, groupId: Int?): List<RiddleCategoryDto>

    fun getRiddleForUser(user: CmschUser, riddleId: Int): RiddleView?

    fun getRiddleForGroup(user: CmschUser, groupId: Int?, riddleId: Int): RiddleView?

    fun unlockHintForUser(user: CmschUser, riddleId: Int): String?

    fun unlockHintForGroup(user: CmschUser, groupId: Int?, groupName: String, riddleId: Int): String?

    fun submitRiddleForUser(
        user: CmschUser,
        riddleId: Int,
        solution: String,
        skip: Boolean = false
    ): RiddleSubmissionView?

    fun submitRiddleForGroup(
        user: CmschUser,
        groupId: Int?,
        groupName: String,
        riddleId: Int,
        solution: String,
        skip: Boolean = false
    ): RiddleSubmissionView?

    fun getCompletedRiddleCountUser(user: CmschUser): Int

    fun getCompletedRiddleCountGroup(user: CmschUser, groupId: Int?): Int

    fun getTotalRiddleCount(user: CmschUser): Int

    fun listRiddleHistoryForUser(user: CmschUser): Map<String, List<RiddleViewWithSolution>>

    fun listRiddleHistoryForGroup(user: CmschUser, groupId: Int?): Map<String, List<RiddleViewWithSolution>>

}