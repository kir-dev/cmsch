package hu.bme.sch.cmsch.component.errorlog

import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@ConditionalOnBean(ErrorLogComponent::class)
interface ErrorLogRepository : CrudRepository<ErrorLogEntity, Int>, EntityPageDataSource<ErrorLogEntity, Int> {

    @Query("select e from ErrorLogEntity e order by e.lastReportedAt desc")
    override fun findAll(): List<ErrorLogEntity>

    fun findByMessageAndStackAndUserAgentAndHrefAndRole(
        message: String,
        stack: String,
        userAgent: String,
        href: String,
        role: RoleType
    ): Optional<ErrorLogEntity>

}
