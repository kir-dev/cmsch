package hu.bme.sch.cmsch.component.script

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(ScriptComponent::class)
interface ScriptRepository : CrudRepository<ScriptEntity, Int>, EntityPageDataSource<ScriptEntity, Int> {
}