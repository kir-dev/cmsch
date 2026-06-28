package hu.bme.sch.cmsch.component.support

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(SupportComponent::class)
interface SupportMessageRepository : CrudRepository<SupportMessageEntity, Int> {
    fun findByThreadUuidOrderByCreatedAtAsc(threadUuid: String): List<SupportMessageEntity>
    fun findByThreadUuidAndInternalOnlyFalseOrderByCreatedAtAsc(threadUuid: String): List<SupportMessageEntity>
}
