package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(DebtComponent::class)
interface SoldProductRepository : CrudRepository<SoldProductEntity, Int>,
    EntityPageDataSource<SoldProductEntity, Int> {

    fun findAllByOwnerId(id: Int): List<SoldProductEntity>

    fun findAllByResponsibleGroupId(id: Int): List<SoldProductEntity>
}
