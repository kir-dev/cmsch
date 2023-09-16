package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.UserEntity
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service

@Service
@ConditionalOnBean(RiddleComponent::class)
class ConcurrentRiddleService(
    private val cacheManager: RiddleCacheManager,
    private val riddleService: RiddleBusinessLogicService
) : RiddleService {

    override fun listRiddlesForUser(user: CmschUser): List<RiddleCategoryDto> {
        return riddleService.listRiddlesForUser(user)
    }

    override fun listRiddlesForGroup(user: CmschUser, group: GroupEntity?): List<RiddleCategoryDto> {
        return riddleService.listRiddlesForGroup(user, group)
    }

    override fun getRiddleForUser(user: CmschUser, riddleId: Int): RiddleView? {
        return riddleService.getRiddleForUser(user, riddleId)
    }

    override fun getRiddleForGroup(user: CmschUser, group: GroupEntity?, riddleId: Int): RiddleView? {
        return riddleService.getRiddleForGroup(user, group, riddleId)
    }

    override fun unlockHintForUser(user: CmschUser, riddleId: Int): String? {
        val lock = cacheManager.getLockForUser(user.id)
        lock.lock()
        try {
            return riddleService.unlockHintForUser(user, riddleId)
        } finally {
            lock.unlock()
        }
    }

    override fun unlockHintForGroup(user: CmschUser, group: GroupEntity?, riddleId: Int): String? {
        if (group == null)
            return null

        val lock = cacheManager.getLockForGroup(group.id)
        lock.lock()
        try {
            return riddleService.unlockHintForGroup(user, group, riddleId)
        } finally {
            lock.unlock()
        }
    }

    override fun submitRiddleForUser(
        user: CmschUser,
        riddleId: Int,
        solution: String,
        skip: Boolean
    ): RiddleSubmissionView? {
        val lock = cacheManager.getLockForUser(user.id)
        lock.lock()
        try {
            return riddleService.submitRiddleForUser(user, riddleId, solution, skip)
        } finally {
            lock.unlock()
        }
    }

    override fun submitRiddleForGroup(
        user: CmschUser,
        group: GroupEntity?,
        riddleId: Int,
        solution: String,
        skip: Boolean
    ): RiddleSubmissionView? {
        if (group == null)
            return null

        val lock = cacheManager.getLockForGroup(group.id)
        lock.lock()
        try {
            return riddleService.submitRiddleForGroup(user, group, riddleId, solution, skip)
        } finally {
            lock.unlock()
        }
    }

    override fun getCompletedRiddleCountUser(user: UserEntity): Int {
        return riddleService.getCompletedRiddleCountUser(user)
    }

    override fun getCompletedRiddleCountGroup(user: UserEntity, group: GroupEntity?): Int {
        return riddleService.getCompletedRiddleCountGroup(user, group)
    }

    override fun getTotalRiddleCount(user: UserEntity): Int {
        return riddleService.getTotalRiddleCount(user)
    }

    override fun listRiddleHistoryForUser(user: UserEntity): Map<String, List<RiddleViewWithSolution>> {
        return riddleService.listRiddleHistoryForUser(user)
    }

    override fun listRiddleHistoryForGroup(
        user: UserEntity,
        group: GroupEntity?
    ): Map<String, List<RiddleViewWithSolution>> {
        return riddleService.listRiddleHistoryForGroup(user, group)
    }


}