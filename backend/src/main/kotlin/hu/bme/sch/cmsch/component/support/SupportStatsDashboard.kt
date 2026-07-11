package hu.bme.sch.cmsch.component.support

import hu.bme.sch.cmsch.admin.dashboard.*
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.StaffPermissions
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.service.UserService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import java.time.Instant
import java.time.ZonedDateTime

private const val VIEW = "support-stats"

@Controller
@RequestMapping("/admin/control/$VIEW")
@ConditionalOnBean(SupportComponent::class)
class SupportStatsDashboard(
    adminMenuService: AdminMenuService,
    private val supportComponent: SupportComponent,
    auditLogService: AuditLogService,
    private val threadRepository: SupportThreadRepository,
    private val scheduleRepository: SupportScheduleRepository,
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val clock: TimeService,
) : DashboardPage(
    VIEW,
    "Ügyfélszolgálat statisztikák",
    "Lezárt megkeresések és fellelőseik statisztikák.",
    false,
    adminMenuService,
    supportComponent,
    auditLogService,
    StaffPermissions.PERMISSION_SHOW_SUPPORT_THREADS,
    adminMenuCategory = null,
    "bar_chart",
    2
) {

    private val permissionCard = DashboardPermissionCard(
        1,
        permission = showPermission.permissionString,
        description = "Ez a jog szükséges ennek az oldalnak az olvasásához.",
        wide = wide
    )

    override fun getComponents(user: CmschUser, requestParams: Map<String, String>): List<DashboardComponent> {
        if (!showPermission.validate(user)) return listOf(permissionCard)

        val doneThreads = threadRepository.findByStatus(SupportThreadStatus.DONE)

        val components = mutableListOf(
            buildCurrentScheduleCard(),
            buildCompletedBySolverTable(doneThreads),
            buildFastCompletedBySolverTable(doneThreads),
            buildOverallStatsTable(doneThreads),
            permissionCard
        )
        return components
    }

    private fun resolveDisplayName(internalId: String): String {
        val user = userRepository.findByInternalId(internalId).orElse(null) ?: return internalId
        val configName = userService.resolveConfig(user.config).supportDefaultName
        return configName.ifBlank { user.fullName }.ifBlank { internalId }
    }

    private fun buildCurrentScheduleCard(): DashboardCard {
        val nowTime = ZonedDateTime.now(clock.timeZone).toLocalTime()
        val schedules = scheduleRepository.findAll().toList()

        val inSchedule = schedules.filter { schedule ->
            if (schedule.supportUserId.isBlank() || schedule.from <= 0 || schedule.to <= 0) return@filter false
            val fromTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(schedule.from), clock.timeZone).toLocalTime()
            val toTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(schedule.to), clock.timeZone).toLocalTime()
            nowTime >= fromTime && nowTime <= toTime
        }

        val names: List<String> = when {
            inSchedule.isNotEmpty() -> inSchedule.map { resolveDisplayName(it.supportUserId) }
            else -> {
                val defaults = supportComponent.defaultSupportUserIds
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                if (defaults.isNotEmpty())
                    defaults.map { resolveDisplayName(it) + " (default)" }
                else
                    listOf("Jelenleg senki sem elérhető")
            }
        }

        return DashboardCard(
            id = 2,
            wide = wide,
            title = "Jelenlegi ügyeletes",
            description = "Az aktuálisan beosztásban lévő, vagy alapértelmezett ügyfélszolgálatosok.",
            content = names
        )
    }

    private fun buildCompletedBySolverTable(doneThreads: List<SupportThreadEntity>): DashboardTableCard {
        val bySolver = doneThreads
            .groupBy { it.solver.ifBlank { "–" } }
            .map { (solver, threads) -> Pair(threads.size, listOf(solver, threads.size.toString())) }
            .sortedByDescending { it.first }
            .map { it.second }

        return DashboardTableCard(
            id = 3,
            title = "Lezárt megkeresések felelősönként",
            description = "Az egyes felelősök által lezárt megkeresések száma, csökkenő sorrendben.",
            header = listOf("Felelős", "Lezárt megkeresések"),
            content = bySolver,
            wide = wide,
            exportable = true
        )
    }

    private fun buildFastCompletedBySolverTable(doneThreads: List<SupportThreadEntity>): DashboardTableCard {
        val eightHoursInSeconds = 8 * 3600L
        val fastBySolver = doneThreads
            .filter { it.updatedAt - it.createdAt <= eightHoursInSeconds }
            .groupBy { it.solver.ifBlank { "–" } }
            .map { (solver, threads) -> Pair(threads.size, listOf(solver, threads.size.toString())) }
            .sortedByDescending { it.first }
            .map { it.second }

        return DashboardTableCard(
            id = 4,
            title = "8 órán belül lezárt megkeresések felelősönként",
            description = "Azok a lezárt megkeresések, amelyek a nyitástól számított 8 órán belül lezárásra kerültek.",
            header = listOf("Felelős", "Gyors lezárások"),
            content = fastBySolver,
            wide = wide,
            exportable = true
        )
    }

    private fun buildOverallStatsTable(doneThreads: List<SupportThreadEntity>): DashboardTableCard {
        val total = doneThreads.size
        val eightHoursInSeconds = 8 * 3600L
        val fast = doneThreads.count { it.updatedAt - it.createdAt <= eightHoursInSeconds }
        val avgSeconds = if (total > 0) doneThreads.sumOf { it.updatedAt - it.createdAt } / total else 0L
        val avgHours = avgSeconds / 3600
        val avgMinutes = (avgSeconds % 3600) / 60

        return DashboardTableCard(
            id = 5,
            title = "Összesített statisztikák",
            description = "Az összes lezárt szál összesített adatai.",
            header = listOf("Mutató", "Érték"),
            content = listOf(
                listOf("Összes lezárt szál", total.toString()),
                listOf("8 órán belül lezárva", fast.toString()),
                listOf("Átlagos lezárási idő", "%d:%02d".format(avgHours, avgMinutes))
            ),
            wide = wide,
            exportable = false
        )
    }
}
