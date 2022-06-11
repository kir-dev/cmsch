package hu.bme.sch.cmsch.component

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ComponentSettingRepository : CrudRepository<ComponentSettingEntity, Int> {

    fun findByComponentAndProperty(component: String, property: String): Optional<ComponentSettingEntity>

}
