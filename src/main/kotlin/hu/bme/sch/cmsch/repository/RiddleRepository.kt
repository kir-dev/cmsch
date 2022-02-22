package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.RiddleEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RiddleRepository : CrudRepository<RiddleEntity, Int> {
    override fun findAll(): List<RiddleEntity>
    fun findAllByCategoryId(categoryId: Int): List<RiddleEntity>
    fun findAllByCategoryIdIn(categories: List<Int>): List<RiddleEntity>
}