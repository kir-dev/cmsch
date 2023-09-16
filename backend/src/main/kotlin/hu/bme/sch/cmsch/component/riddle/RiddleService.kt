package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.UserEntity

interface RiddleService {

    fun listRiddlesForUser(user: CmschUser): List<RiddleCategoryDto>

    fun listRiddlesForGroup(user: CmschUser, group: GroupEntity?): List<RiddleCategoryDto>

    fun getRiddleForUser(user: CmschUser, riddleId: Int): RiddleView?

    fun getRiddleForGroup(user: CmschUser, group: GroupEntity?, riddleId: Int): RiddleView?

    fun unlockHintForUser(user: CmschUser, riddleId: Int): String?

    fun unlockHintForGroup(user: CmschUser, group: GroupEntity?, riddleId: Int): String?

    fun submitRiddleForUser(
        user: CmschUser,
        riddleId: Int,
        solution: String,
        skip: Boolean = false
    ): RiddleSubmissionView?

    fun submitRiddleForGroup(
        user: CmschUser,
        group: GroupEntity?,
        riddleId: Int,
        solution: String,
        skip: Boolean = false
    ): RiddleSubmissionView?

    fun getCompletedRiddleCountUser(user: UserEntity): Int

    fun getCompletedRiddleCountGroup(user: UserEntity, group: GroupEntity?): Int

    fun getTotalRiddleCount(user: UserEntity): Int

    fun listRiddleHistoryForUser(user: UserEntity): Map<String, List<RiddleViewWithSolution>>

    fun listRiddleHistoryForGroup(user: UserEntity, group: GroupEntity?): Map<String, List<RiddleViewWithSolution>>

}