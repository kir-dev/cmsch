package hu.bme.sch.cmsch.component.riddle

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(RiddleComponent::class)
class RiddleReadonlyService(
    private val riddleEntityRepository: RiddleEntityRepository,
    private val riddleMappingRepository: RiddleMappingRepository
) {

    data class RiddleDetails(val all: Int, val solved: Int, val skipped: Int)

    @Transactional(readOnly = true)
    fun getRiddleDetails(groupId: Int): RiddleDetails {
        return RiddleDetails(
            riddleEntityRepository.count().toInt(),
            riddleMappingRepository.countAllByOwnerGroupIdAndCompletedTrue(groupId),
            riddleMappingRepository.countAllByOwnerGroupIdAndCompletedTrueAndSkippedTrue(groupId),
        )
    }

}
