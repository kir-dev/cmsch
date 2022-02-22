package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.RiddleCategoryEntity
import hu.bme.sch.cmsch.model.RoleType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RiddleCategoryRepository : CrudRepository<RiddleCategoryEntity, Int> {
    fun findAllByVisibleTrueAndMinRoleIn(roles: List<RoleType>): List<RiddleCategoryEntity>
    fun findByCategoryIdAndVisibleTrueAndMinRoleIn(categoryId: Int, roles: List<RoleType>): Optional<RiddleCategoryEntity>
}