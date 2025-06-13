package hu.bme.sch.cmsch.component.riddle

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.sql.SQLException

@Service
@ConditionalOnBean(RiddleComponent::class)
class RiddlePersistenceService(
    private val riddleEntityRepository: RiddleEntityRepository,
    private val riddleMappingRepository: RiddleMappingRepository,
) {

    @Retryable(value = [ SQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    fun saveAllRiddleMapping(entities: MutableIterable<RiddleMappingEntity>) {
        riddleMappingRepository.saveAll(entities)
    }

    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    fun saveRiddleMapping(entity: RiddleMappingEntity) {
        riddleMappingRepository.save(entity)
    }

    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    fun saveRiddle(entity: RiddleEntity) {
        riddleEntityRepository.save(entity)
    }

}
