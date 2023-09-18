package hu.bme.sch.cmsch.component.riddle

import org.postgresql.util.PSQLException
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(RiddleComponent::class)
open class RiddlePersistenceService(
    private val riddleEntityRepository: RiddleEntityRepository,
    private val riddleMappingRepository: RiddleMappingRepository,
) {

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    open fun saveAllRiddleMapping(entities: MutableIterable<RiddleMappingEntity>) {
        riddleMappingRepository.saveAll(entities)
    }

    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    open fun saveRiddleMapping(entity: RiddleMappingEntity) {
        riddleMappingRepository.save(entity)
    }

    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    open fun saveRiddle(entity: RiddleEntity) {
        riddleEntityRepository.save(entity)
    }

}
