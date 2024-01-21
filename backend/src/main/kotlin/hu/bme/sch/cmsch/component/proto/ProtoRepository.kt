package hu.bme.sch.cmsch.component.proto

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(ProtoComponent::class)
interface ProtoRepository : CrudRepository<ProtoEntity, Int>, EntityPageDataSource<ProtoEntity, Int> {

    fun findAllByPathAndEnabledTrue(path: String): List<ProtoEntity>

}