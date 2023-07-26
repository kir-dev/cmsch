package hu.bme.sch.cmsch.component.key

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(AccessKeyComponent::class)
interface AccessKeyRepository : CrudRepository<AccessKeyEntity, Int>, EntityPageDataSource<AccessKeyEntity, Int> {

    override fun findAll(): List<AccessKeyEntity>

    fun findTop1ByUsedByUserId(usedByUserId: Int): List<AccessKeyEntity>

    fun findTop1ByAccessKey(accessKey: String): List<AccessKeyEntity>

}