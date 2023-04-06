package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(DebtComponent::class)
interface ProductRepository : CrudRepository<ProductEntity, Int>,
    EntityPageDataSource<ProductEntity, Int> {

    override fun findAll(): List<ProductEntity>
    fun findAllByType(type: ProductType): List<ProductEntity>
    fun findAllByTypeAndVisibleTrue(type: ProductType): List<ProductEntity>
}
