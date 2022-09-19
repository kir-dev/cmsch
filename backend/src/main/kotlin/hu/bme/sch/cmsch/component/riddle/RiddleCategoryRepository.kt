package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.model.RoleType
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@ConditionalOnBean(RiddleComponent::class)
interface RiddleCategoryRepository : CrudRepository<RiddleCategoryEntity, Int> {
    fun findAllByVisibleTrueAndMinRoleIn(roles: List<RoleType>): List<RiddleCategoryEntity>
    fun findByCategoryIdAndVisibleTrueAndMinRoleIn(categoryId: Int, roles: List<RoleType>): Optional<RiddleCategoryEntity>
}
