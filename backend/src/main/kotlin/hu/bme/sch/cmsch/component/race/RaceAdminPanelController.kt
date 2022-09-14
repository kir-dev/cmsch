package hu.bme.sch.cmsch.component.race

import hu.bme.sch.cmsch.config.OwnershipType
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_RACE
import hu.bme.sch.cmsch.service.TimeService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/race")
@ConditionalOnBean(RaceComponent::class)
class RaceAdminPanelController(
    repo: RaceRecordRepository,
    private val groups: GroupRepository,
    private val users: UserRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: RaceComponent,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val clock: TimeService
) : AbstractAdminPanelController<RaceRecordEntity>(
    repo,
    "race", "Eredmény", "Mérések",
    "Időmérő eredmények nyers időeredményei",
    RaceRecordEntity::class, ::RaceRecordEntity, importService, adminMenuService, component,
    mapOf(
        "GroupEntity" to {
            val results = mutableListOf<String>()
            results.add("-")
            results.addAll(groups.findAll().map { it.name }.sorted().toList())
            return@to results
        },
        "UserEntity" to {
            val results = mutableListOf<String>()
            results.add("-")
            results.addAll(users.findAll().sortedBy { it.fullName }.map { mapUsername(it) }.toList())
            return@to results
        },
    ),
    permissionControl = PERMISSION_EDIT_RACE,
    importable = true, adminMenuPriority = 1, adminMenuIcon = "timer"
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
