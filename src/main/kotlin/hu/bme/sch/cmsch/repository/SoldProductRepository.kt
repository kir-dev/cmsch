package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.SoldProductEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SoldProductRepository : CrudRepository<SoldProductEntity, Int> {
    fun findAllByOwnerId(id: Int): List<SoldProductEntity>

    fun findAllByResponsibleGroupId(id: Int): List<SoldProductEntity>
}