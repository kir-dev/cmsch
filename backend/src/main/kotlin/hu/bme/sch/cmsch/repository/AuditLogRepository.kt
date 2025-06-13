package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.AuditLogByDayEntry
import hu.bme.sch.cmsch.model.AuditLogEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface AuditLogRepository : JpaRepository<AuditLogEntity, Int> {

    @Query("select new hu.bme.sch.cmsch.model.AuditLogByDayEntry(floor(e.timestamp / 86400) * 86400, count(*)) " +
            "from AuditLogEntity e " +
            "group by floor(e.timestamp / 86400) * 86400 " +
            "order by floor(e.timestamp / 86400) * 86400")
    fun findAllDaysWithLogs(): List<AuditLogByDayEntry>

    @Query("select e from AuditLogEntity e where e.timestamp between :day and :day + 86400 order by e.timestamp")
    fun findAllLogsOnDay(day: Long): List<AuditLogEntity>

    @Query("select e from AuditLogEntity e order by e.timestamp")
    fun findAllOrderByTimestamp(): List<AuditLogEntity>
}
