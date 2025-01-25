package hu.bme.sch.cmsch.component.app

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ComponentSettingRepository : CrudRepository<ComponentSettingEntity, Int> {

    fun findByComponentAndProperty(component: String, property: String): Optional<ComponentSettingEntity>

    fun findAllByComponent(component: String): List<ComponentSettingEntity>

}
