package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.model.RoleType
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@ConditionalOnBean(ApplicationComponent::class)
interface MenuRepository : CrudRepository<MenuEntity, Int> {

    fun findByRole(role: RoleType): Optional<MenuEntity>

}
