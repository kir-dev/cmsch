package hu.bme.sch.cmsch.component

import hu.bme.sch.cmsch.component.achievement.AchievementComponent
import hu.bme.sch.cmsch.component.ComponentSettingEntity
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ComponentSettingRepository : CrudRepository<ComponentSettingEntity, Int> {

    fun findByComponentAndProperty(component: String, property: String): Optional<ComponentSettingEntity>

}
