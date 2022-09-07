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
    "race", "Mérés", "Mérések",
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
            results.addAll(users.findAll().map {
                if (it.alias.isNotBlank()) "${it.fullName} (${it.alias})" else it.fullName
            }.sorted().toList())
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
        if (entity.userName.isNotBlank()) {
            val username: String
            val alias: String
            if (entity.userName.contains(" (") && entity.userName.endsWith(")")) {
                val nameParts = entity.userName.split(Regex(" \\("), 2)
                username = nameParts[0]
                alias = if (nameParts.isEmpty()) "" else nameParts[1]
            } else {
                username = entity.userName
                alias = ""
            }

            val users = users.findAllByFullName(username)
            if (users.size > 1) {
                tryToMatchByAlias(users, alias, entity)

            } else if (users.size == 1) {
                entity.userName = users.first().fullName
                entity.userId = users.first().id
                entity.groupId = users.first().group?.id
                entity.groupName = users.first().groupName
                entity.timestamp = clock.getTimeInSeconds()
            } else {
                log.error("User not found: {} so rejected", entity.userName)
                return false
            }
        }
        return true
    }

    private fun tryToMatchByAlias(
        users: List<UserEntity>,
        alias: String,
        entity: RaceRecordEntity
    ) {
        val now = clock.getTimeInSeconds()
        val matchingUsers = users.filter { it.alias == alias }
        if (matchingUsers.isEmpty()) {
            log.warn(
                "Name duplication found: {} (but alias did not match: {}) using first user {}",
                entity.userName,
                alias,
                users.first().id
            )
            entity.userName = users.first().fullName
            entity.userId = users.first().id
            entity.groupId = users.first().group?.id
            entity.groupName = users.first().groupName
            entity.timestamp = now
        } else if (matchingUsers.size > 1) {
            log.warn(
                "Name duplication found: {} (alias duplicate as well: {}) using first user {}",
                entity.userName,
                alias,
                matchingUsers.first().id
            )
            entity.userName = matchingUsers.first().fullName
            entity.userId = matchingUsers.first().id
            entity.groupId = matchingUsers.first().group?.id
            entity.groupName = matchingUsers.first().groupName
            entity.timestamp = now
        } else {
            entity.userName = matchingUsers.first().fullName
            entity.userId = matchingUsers.first().id
            entity.groupId = matchingUsers.first().group?.id
            entity.groupName = matchingUsers.first().groupName
            entity.timestamp = now
        }
    }
}
