package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.component.login.CmschUser
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

    override fun listRiddlesForGroup(user: CmschUser, groupId: Int?): List<RiddleCategoryDto> {
        return riddleService.listRiddlesForGroup(user, groupId)
    }

    override fun getRiddleForUser(user: CmschUser, riddleId: Int): RiddleView? {
        return riddleService.getRiddleForUser(user, riddleId)
    }

    override fun getRiddleForGroup(user: CmschUser, groupId: Int?, riddleId: Int): RiddleView? {
        return riddleService.getRiddleForGroup(user, groupId, riddleId)
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

    override fun unlockHintForGroup(user: CmschUser, groupId: Int?, groupName: String, riddleId: Int): String? {
        if (groupId == null)
            return null

        val lock = cacheManager.getLockForGroup(groupId)
        lock.lock()
        try {
            return riddleService.unlockHintForGroup(user, groupId, groupName, riddleId)
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
        groupId: Int?,
        groupName: String,
        riddleId: Int,
        solution: String,
        skip: Boolean
    ): RiddleSubmissionView? {
        if (groupId == null)
            return null

        val lock = cacheManager.getLockForGroup(groupId)
        lock.lock()
        try {
            return riddleService.submitRiddleForGroup(user, groupId, groupName, riddleId, solution, skip)
        } finally {
            lock.unlock()
        }
    }

    override fun getCompletedRiddleCountUser(user: CmschUser): Int {
        return riddleService.getCompletedRiddleCountUser(user)
    }

    override fun getCompletedRiddleCountGroup(user: CmschUser, groupId: Int?): Int {
        return riddleService.getCompletedRiddleCountGroup(user, groupId)
    }

    override fun getTotalRiddleCount(user: CmschUser): Int {
        return riddleService.getTotalRiddleCount(user)
    }

    override fun listRiddleHistoryForUser(user: CmschUser): Map<String, List<RiddleViewWithSolution>> {
        return riddleService.listRiddleHistoryForUser(user)
    }

    override fun listRiddleHistoryForGroup(
        user: CmschUser,
        groupId: Int?
    ): Map<String, List<RiddleViewWithSolution>> {
        return riddleService.listRiddleHistoryForGroup(user, groupId)
    }


}