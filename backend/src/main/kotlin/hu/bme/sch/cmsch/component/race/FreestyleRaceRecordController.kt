package hu.bme.sch.cmsch.component.race

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.config.OwnershipType
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.transaction
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/freestyle-race")
@ConditionalOnBean(RaceComponent::class)
class FreestyleRaceRecordController(
    repo: RaceRecordRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: RaceComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    private val groups: GroupRepository,
    private val users: UserRepository,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val clock: TimeService,
    transactionManager: PlatformTransactionManager,
    env: Environment
) : OneDeepEntityPage<RaceRecordEntity>(
    "freestyle-race",
    RaceRecordEntity::class, ::RaceRecordEntity,
    "Eredmény", "Funky mérések",
    "Időmérő eredmények nyers időeredményei. Ennél a kategóriánál minden beadás látszik.",

    transactionManager,
    repo,
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,
    env,

    entitySourceMapping = mapOf(
        "GroupEntity" to {
            val results = mutableListOf<String>()
            results.add("-")
            results.addAll(transactionManager.transaction(readOnly = true) { groups.findAll() }
                .map { it.name }
                .sorted()
                .toList())
            return@to results
        },
        "UserEntity" to {
            val results = mutableListOf<String>()
            results.add("-")
            results.addAll(transactionManager.transaction(readOnly = true) { users.findAll() }
                .sortedBy { it.fullName }
                .map { mapUsername(it) }
                .toList())
            return@to results
        },
    ),

    showPermission = StaffPermissions.PERMISSION_SHOW_RACE,
    createPermission = StaffPermissions.PERMISSION_CREATE_RACE,
    editPermission = StaffPermissions.PERMISSION_EDIT_RACE,
    deletePermission = StaffPermissions.PERMISSION_DELETE_RACE,

    createEnabled = true,
    editEnabled   = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "time_auto",
    adminMenuPriority = 4,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun onEntityPreSave(entity: RaceRecordEntity, auth: Authentication): Boolean {
        return when (startupPropertyConfig.raceOwnershipMode) {
            OwnershipType.USER -> processUserSubmission(entity)
            OwnershipType.GROUP -> processGroupSubmission(entity)
        }
    }

    private fun processGroupSubmission(entity: RaceRecordEntity): Boolean {
        if (entity.groupName.isNotBlank()) {
            val groupEntity = groups.findByName(entity.groupName)
            if (groupEntity.isPresent) {
                entity.groupId = groupEntity.orElseThrow().id
                entity.groupName = groupEntity.orElseThrow().name
                entity.userName = ""
                entity.userId = 0
                entity.timestamp = clock.getTimeInSeconds()
            } else {
                log.error("Group not found: {} so rejected", entity.userName)
                return false
            }
        }
        return true
    }

    private fun processUserSubmission(entity: RaceRecordEntity): Boolean {
        if (entity.userName.isNotBlank() && entity.userName != "-") {
            val user = users.findById(entity.userName.split("|")[0].trim().toIntOrNull() ?: 0)

            if (user.isPresent) {
                entity.userName = user.orElseThrow().fullName
                entity.userId = user.orElseThrow().id
                entity.groupId = user.orElseThrow().group?.id
                entity.groupName = user.orElseThrow().group?.name ?: ""
                entity.timestamp = clock.getTimeInSeconds()
            } else {
                log.error("User not found: {} so rejected", entity.userName)
                return false
            }
        }
        return true
    }

    override fun onPreEdit(actualEntity: RaceRecordEntity): RaceRecordEntity {
        val userId = actualEntity.userId ?: return actualEntity
        val copy = actualEntity.copy()
        val user = users.findById(userId).orElse(null) ?: return actualEntity
        copy.userName = mapUsername(user)
        return copy
    }

}

private fun mapUsername(it: UserEntity) =
    "${it.id}| ${it.fullNameWithAlias} [${it.provider.firstOrNull() ?: 'n'}] ${it.email}"