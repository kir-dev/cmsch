package hu.bme.sch.cmsch.component.support

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
@ConditionalOnBean(SupportComponent::class)
interface SupportThreadRepository : CrudRepository<SupportThreadEntity, Int>, EntityPageDataSource<SupportThreadEntity, Int> {
    fun findByUuid(uuid: String): Optional<SupportThreadEntity>
    fun findByUserInternalId(internalId: String): List<SupportThreadEntity>
    fun findByUserEmail(email: String): List<SupportThreadEntity>
    fun findByStatusNot(status: SupportThreadStatus): List<SupportThreadEntity>

    @Query("SELECT t FROM SupportThreadEntity t ORDER BY t.updatedAt DESC")
    override fun findAll(): Iterable<SupportThreadEntity>

    @Query("SELECT COUNT(t) FROM SupportThreadEntity t WHERE t.userInternalId = :internalId AND t.status <> hu.bme.sch.cmsch.component.support.SupportThreadStatus.DONE")
    fun countOpenByUserInternalId(internalId: String): Long

    @Query("SELECT COUNT(t) FROM SupportThreadEntity t WHERE t.userEmail = :email AND t.userInternalId = '' AND t.status <> hu.bme.sch.cmsch.component.support.SupportThreadStatus.DONE")
    fun countOpenByAnonymousEmail(email: String): Long

    fun findByStatus(status: SupportThreadStatus): List<SupportThreadEntity>
}
