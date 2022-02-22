package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.ProductEntity
import hu.bme.sch.cmsch.model.ProductType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : CrudRepository<ProductEntity, Int> {
    override fun findAll(): List<ProductEntity>
    fun findAllByType(type: ProductType): List<ProductEntity>
    fun findAllByTypeAndVisibleTrue(type: ProductType): List<ProductEntity>
}