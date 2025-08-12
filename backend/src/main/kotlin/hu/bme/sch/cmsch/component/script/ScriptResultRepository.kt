package hu.bme.sch.cmsch.component.script

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(ScriptComponent::class)
interface ScriptResultRepository : CrudRepository<ScriptResultEntity, Int>, EntityPageDataSource<ScriptResultEntity, Int> {

    @Modifying
    @Query("UPDATE ScriptResultEntity SET logs = :logs WHERE id = :id")
    fun updateLogsById(
        @Param("id") id: Int,
        @Param("logs") logs: String
    ): Int

}