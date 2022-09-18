package hu.bme.sch.cmsch.component.qrfight

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.component.token.TokenCollectorStatus
import hu.bme.sch.cmsch.component.token.TokenEntity
import hu.bme.sch.cmsch.component.token.TokenPropertyEntity
import hu.bme.sch.cmsch.component.token.TokenPropertyRepository
import hu.bme.sch.cmsch.config.OwnershipType
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.TimeService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*

const val TIMER_OCCURRENCE = 10

@Service
@ConditionalOnBean(QrFightComponent::class)
open class QrFightService(
    private val qrTowerRepository: QrTowerRepository,
    private val qrLevelRepository: QrLevelRepository,
    private val userRepository: UserRepository,
    private val tokenPropertyRepository: Optional<TokenPropertyRepository>,
    private val clock: TimeService,
    private val objectMapper: ObjectMapper,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val indulaschIntegrationService: IndulaschIntegrationService,
    private val qrFightComponent: QrFightComponent
) {

    private val log = LoggerFactory.getLogger(javaClass)

    open fun getLevelsForGroups(group: GroupEntity?): QrFightOverviewView {
        val levels = qrLevelRepository.findAllByVisibleTrueAndEnabledTrue()
            .sortedBy { it.order }

        return QrFightOverviewView(
            levels.filter { !it.extraLevel }.map { mapLevel(it, group) },
            levels.filter { it.extraLevel }.map { mapLevel(it, group) }
        )
    }

    private fun mapLevel(level: QrLevelEntity, group: GroupEntity?): QrFightLevelView {
        val outOfDate = !clock.inRange(level.availableFrom, level.availableTo, clock.getTimeInSeconds())
        val isOpen = group != null && isLevelOpenForGroup(level, group)
        val isCompleted = group != null && isLevelCompletedForGroup(level, group)

        val status = if (group == null) {
            LevelStatus.NOT_LOGGED_IN
        } else if (!level.enabled || outOfDate) {
            LevelStatus.NOT_ENABLED
        } else if (!isOpen) {
            LevelStatus.NOT_UNLOCKED
        } else if (isCompleted) {
            LevelStatus.COMPLETED
        } else {
            LevelStatus.OPEN
        }

        val teams = tokenPropertyRepository.orElseThrow().findAllByToken_Type(level.category)
            .groupBy { it.ownerGroup?.name ?: "..." }
            .map { it.key to it.value.count() }
            .toMap()

        val towers = qrTowerRepository.findAllByCategory(level.category)
            .map {
                TowerView(
                    it.displayName,
                    if (group?.name == it.ownerGroupName) it.ownerMessage else it.publicMessage,
                    it.ownerGroupName,
                    it.holder,
                    it.holderFor
                )
            }

        val maxCollected = teams.maxOfOrNull { it.value } ?: 0
        return QrFightLevelView(
            name = level.displayName,
            description = when (status) {
                LevelStatus.OPEN -> level.hintWhileOpen
                LevelStatus.COMPLETED -> level.hintAfterCompleted
                else -> level.hintBeforeEnabled
            },
            status = status,
            owners = teams.filterValues { it == maxCollected }.map { it.key }.joinToString(", "),
            teams = teams,
            towers = towers
        )
    }

    open fun getLevelsForUsers(user: UserEntity?): QrFightOverviewView {
        val levels = qrLevelRepository.findAllByVisibleTrueAndEnabledTrue()
            .sortedBy { it.order }

        return QrFightOverviewView(
            levels.filter { !it.extraLevel }.map { mapLevel(it, user) },
            levels.filter { it.extraLevel }.map { mapLevel(it, user) }
        )
    }

    private fun mapLevel(level: QrLevelEntity, user: UserEntity?): QrFightLevelView {
        val outOfDate = !clock.inRange(level.availableFrom, level.availableTo, clock.getTimeInSeconds())
        val isOpen = user != null && isLevelOpenForUser(level, user)
        val isCompleted = user != null && isLevelCompletedForUser(level, user)

        val status = if (user == null) {
            LevelStatus.NOT_LOGGED_IN
        } else if (!level.enabled || outOfDate) {
            LevelStatus.NOT_ENABLED
        } else if (!isOpen) {
            LevelStatus.NOT_UNLOCKED
        } else if (isCompleted) {
            LevelStatus.COMPLETED
        } else {
            LevelStatus.OPEN
        }

        val teams = tokenPropertyRepository.orElseThrow().findAllByToken_Type(level.category)
            .groupBy { it.ownerUser?.id ?: -1 }
            .map { (it.value.firstOrNull()?.ownerUser?.fullNameWithAlias ?: "...") to it.value.count() }
            .toMap()

        val towers = qrTowerRepository.findAllByCategory(level.category)
            .map { t ->
                TowerView(
                    t.displayName,
                    if (user?.id == t.ownerUserId) t.ownerMessage else t.publicMessage,
                    t.ownerUserName,
                    userRepository.findById(t.holder?.toIntOrNull() ?: 0).map { it.fullNameWithAlias }.orElse(null),
                    t.holderFor
                )
            }

        val maxCollected = teams.maxOfOrNull { it.value } ?: 0
        return QrFightLevelView(
            name = level.displayName,
            description = when (status) {
                LevelStatus.OPEN -> level.hintWhileOpen
                LevelStatus.COMPLETED -> level.hintAfterCompleted
                else -> level.hintBeforeEnabled
            },
            status = status,
            owners = teams.filterValues { it == maxCollected }.map { it.key }.joinToString(", "),
            teams = teams,
            towers = towers
        )
    }

    fun onTokenScanForGroup(user: UserEntity, group: GroupEntity, token: TokenEntity): Pair<String?, TokenCollectorStatus> {
        if (tokenPropertyRepository.isEmpty)
            return Pair(null, TokenCollectorStatus.QR_FIGHT_INTERNAL_ERROR)
        val repo = tokenPropertyRepository.orElseThrow()

        // Check Level INFO
        val levels = qrLevelRepository.findAllByCategory(token.type)
        if (levels.isNotEmpty()) {
            val level = levels.first()
            val outOfDate = !clock.inRange(level.availableFrom, level.availableTo, clock.getTimeInSeconds())
            if (!level.enabled || outOfDate) {
                log.info("Level '{}' out disabled:{} or out-of-date:{} for user '{}'",
                    level.displayName, !level.enabled, outOfDate, user.fullName)
                return Pair(token.title, TokenCollectorStatus.QR_FIGHT_LEVEL_LOCKED)
            }
            if (!isLevelOpenForGroup(level, group)) {
                log.info("Level '{}' is not unlocked for user '{}'", level.displayName, user.fullName)
                return Pair(token.title, TokenCollectorStatus.QR_FIGHT_LEVEL_LOCKED)
            }
        }

        // Check for TOWERS
        val action = token.action ?: ""
        if (action.startsWith("capture:")) {
            val tower = action.split(":")[1]
            val towerEntity = qrTowerRepository.findAllBySelector(tower).firstOrNull()
                ?: return Pair(token.title, TokenCollectorStatus.QR_FIGHT_TOWER_LOCKED)

            val outOfDate = !clock.inRange(towerEntity.availableFrom, towerEntity.availableTo, clock.getTimeInSeconds())
            if (towerEntity.locked || outOfDate) {
                log.info("Tower '{}' out locked:{} or out-of-date:{}", towerEntity.selector, towerEntity.locked, outOfDate)
                return Pair(token.title, TokenCollectorStatus.QR_FIGHT_TOWER_LOCKED)
            }

            towerEntity.ownerGroupId = group.id
            towerEntity.ownerGroupName = group.name
            towerEntity.history += "${user.fullNameWithAlias};${user.id};${group.name};${group.id};${clock.getTimeInSeconds()};\n"
            qrTowerRepository.save(towerEntity)
            log.info("Tower '{}' captured by by group:{} (user:{})", token.title, group.name, user.fullName)
            return Pair(token.title, TokenCollectorStatus.QR_TOWER_CAPTURED)

        } else if (action.startsWith("history:")) {
            val tower = action.split(":")[1]
            val towerEntity = qrTowerRepository.findAllBySelector(tower).firstOrNull()
                ?: return Pair(token.title, TokenCollectorStatus.QR_FIGHT_TOWER_LOCKED)

            val outOfDate = !clock.inRange(towerEntity.availableFrom, towerEntity.availableTo, clock.getTimeInSeconds())
            if (towerEntity.locked || outOfDate) {
                log.info("Tower '{}' out locked:{} or out-of-date:{} for user '{}'",
                    towerEntity.selector, towerEntity.locked, outOfDate, user.fullName)
                return Pair(token.title, TokenCollectorStatus.QR_FIGHT_TOWER_LOCKED)
            }

            towerEntity.history += "${user.fullNameWithAlias};${user.id};${group.name};${group.id};${clock.getTimeInSeconds()};\n"
            qrTowerRepository.save(towerEntity)
            log.info("Tower '{}' logged by by group:{} (user:{})", token.title, group.name, user.fullName)
            return Pair(token.title, TokenCollectorStatus.QR_TOWER_LOGGED)
        }

        if (repo.findByToken_TokenAndOwnerGroup(token.token, group).isPresent) {
            log.info("Token '{}' already collected by group:{} (user:{})", token.title, group.name, user.fullName)
            return Pair(token.title, TokenCollectorStatus.ALREADY_SCANNED)
        }

        log.info("Token '{}' collected for user:{} group:{}", token.title, user.fullName, group.name)
        repo.save(TokenPropertyEntity(0, null, group, token, clock.getTimeInSeconds()))
        return Pair(token.title, TokenCollectorStatus.SCANNED)
    }

    private fun isLevelOpenForGroup(level: QrLevelEntity, group: GroupEntity): Boolean {
        if (level.dependsOn.isBlank())
            return true
        val dependencyLevels = qrLevelRepository.findAllByCategory(level.dependsOn)
        if (dependencyLevels.isEmpty())
            return true
        val dependencyLevel = dependencyLevels.first()

        val completedTokens = tokenPropertyRepository.orElseThrow()
            .findAllByOwnerGroup_IdAndToken_TypeAndToken_ActiveTargetTrue(group.id, dependencyLevel.category).size
        return completedTokens >= dependencyLevel.minAmountToComplete
    }

    private fun isLevelCompletedForGroup(level: QrLevelEntity, group: GroupEntity): Boolean {
        val completedTokens = tokenPropertyRepository.orElseThrow()
            .findAllByOwnerGroup_IdAndToken_TypeAndToken_ActiveTargetTrue(group.id, level.category).size
        return completedTokens >= level.minAmountToComplete
    }

    fun onTokenScanForUser(user: UserEntity, token: TokenEntity): Pair<String?, TokenCollectorStatus> {
        if (tokenPropertyRepository.isEmpty)
            return Pair(null, TokenCollectorStatus.QR_FIGHT_INTERNAL_ERROR)
        val repo = tokenPropertyRepository.orElseThrow()

        // Check Level INFO
        val levels = qrLevelRepository.findAllByCategory(token.type)
        if (levels.isNotEmpty()) {
            val level = levels.first()
            val outOfDate = !clock.inRange(level.availableFrom, level.availableTo, clock.getTimeInSeconds())
            if (!level.enabled || outOfDate) {
                log.info("Level '{}' out disabled:{} or out-of-date:{} for user '{}'",
                    level.displayName, !level.enabled, outOfDate, user.fullName)
                return Pair(token.title, TokenCollectorStatus.QR_FIGHT_LEVEL_LOCKED)
            }
            if (!isLevelOpenForUser(level, user)) {
                log.info("Level '{}' is not unlocked for user '{}'", level.displayName, user.fullName)
                return Pair(token.title, TokenCollectorStatus.QR_FIGHT_LEVEL_LOCKED)
            }
        }

        // Check for TOWERS
        val action = token.action ?: ""
        if (action.startsWith("capture:")) {
            val tower = action.split(":")[1]
            val towerEntity = qrTowerRepository.findAllBySelector(tower).firstOrNull()
                ?: return Pair(token.title, TokenCollectorStatus.QR_FIGHT_TOWER_LOCKED)

            val outOfDate = !clock.inRange(towerEntity.availableFrom, towerEntity.availableTo, clock.getTimeInSeconds())
            if (towerEntity.locked || outOfDate) {
                log.info("Tower '{}' out locked:{} or out-of-date:{}", towerEntity.selector, towerEntity.locked, outOfDate)
                return Pair(token.title, TokenCollectorStatus.QR_FIGHT_TOWER_LOCKED)
            }

            towerEntity.ownerUserId = user.id
            towerEntity.ownerUserName = user.fullNameWithAlias
            towerEntity.history += "${user.fullNameWithAlias};${user.id};n/a;0;${clock.getTimeInSeconds()};\n"

            qrTowerRepository.save(towerEntity)
            log.info("Tower '{}' captured by by user:{}", token.title, user.fullName)
            return Pair(token.title, TokenCollectorStatus.QR_TOWER_CAPTURED)

        } else if (action.startsWith("history:")) {
            val tower = action.split(":")[1]
            val towerEntity = qrTowerRepository.findAllBySelector(tower).firstOrNull()
                ?: return Pair(token.title, TokenCollectorStatus.QR_FIGHT_TOWER_LOCKED)

            val outOfDate = !clock.inRange(towerEntity.availableFrom, towerEntity.availableTo, clock.getTimeInSeconds())
            if (towerEntity.locked || outOfDate) {
                log.info("Tower '{}' out locked:{} or out-of-date:{} for user '{}'",
                    towerEntity.selector, towerEntity.locked, outOfDate, user.fullName)
                return Pair(token.title, TokenCollectorStatus.QR_FIGHT_TOWER_LOCKED)
            }

            towerEntity.history += "${user.fullNameWithAlias};${user.id};n/a;0;${clock.getTimeInSeconds()};\n"
            qrTowerRepository.save(towerEntity)
            log.info("Tower '{}' logged by by user:{}", token.title, user.fullName)
            return Pair(token.title, TokenCollectorStatus.QR_TOWER_LOGGED)
        }

        if (repo.findByToken_TokenAndOwnerUser_Id(token.token, user.id).isPresent) {
            log.info("Token '{}' already collected by user:{}", token.title, user.fullName)
            return Pair(token.title, TokenCollectorStatus.ALREADY_SCANNED)
        }

        log.info("Token '{}' collected for user:{}", token.title, user.fullName)
        repo.save(TokenPropertyEntity(0, user, null, token, clock.getTimeInSeconds()))
        return Pair(token.title, TokenCollectorStatus.SCANNED)
    }

    private fun isLevelOpenForUser(level: QrLevelEntity, user: UserEntity): Boolean {
        if (level.dependsOn.isBlank())
            return true
        val dependencyLevels = qrLevelRepository.findAllByCategory(level.dependsOn)
        if (dependencyLevels.isEmpty())
            return true
        val dependencyLevel = dependencyLevels.first()

        val completedTokens = tokenPropertyRepository.orElseThrow()
            .findAllByOwnerUser_IdAndToken_TypeAndToken_ActiveTargetTrue(user.id, dependencyLevel.category).size
        return completedTokens >= dependencyLevel.minAmountToComplete
    }

    private fun isLevelCompletedForUser(level: QrLevelEntity, user: UserEntity): Boolean {
        val completedTokens = tokenPropertyRepository.orElseThrow()
            .findAllByOwnerUser_IdAndToken_TypeAndToken_ActiveTargetTrue(user.id, level.category).size
        return completedTokens >= level.minAmountToComplete
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun executeTowerTimer() {
        log.info("Tower time!")
        val now = clock.getTimeInSeconds()
        val towers = qrTowerRepository.findAllByRecordTimeTrue()
            .filter { !it.locked && clock.inRange(it.availableFrom, it.availableTo, now) }

        when (startupPropertyConfig.tokenOwnershipMode) {
            OwnershipType.USER -> {
                val updated = towers.map { tower ->
                    val state = mutableMapOf<String, Int>()
                    if (tower.state.isNotBlank())
                        state.putAll(parseState(tower.state))
                    state[tower.ownerUserId.toString()] = (state[tower.ownerUserId.toString()] ?: 0) + 10
                    tower.state = serializeState(state)
                    tower.holder = state.entries.maxByOrNull { it.value }?.key ?: "0"
                    tower.holderFor = (state.maxOfOrNull { it.value }?.toInt() ?: 0) * TIMER_OCCURRENCE
                    tower
                }
                qrTowerRepository.saveAll(updated)
                log.info("Tower for USER timer executed, and updated {}/{} towers", updated.size, towers.size)
            }
            OwnershipType.GROUP -> {
                val updated = towers.map { tower ->
                    val state = mutableMapOf<String, Int>()
                    if (tower.state.isNotBlank())
                        state.putAll(parseState(tower.state))
                    state[tower.ownerGroupName] = (state[tower.ownerGroupName] ?: 0) + 1
                    tower.state = serializeState(state)
                    tower.holder = state.entries.maxByOrNull { it.value }?.key ?: ""
                    tower.holderFor = (state.maxOfOrNull { it.value }?.toInt() ?: 0) * TIMER_OCCURRENCE
                    tower
                }
                qrTowerRepository.saveAll(updated)
                log.info("Tower for GROUP timer executed, and updated {}/{} towers", updated.size, towers.size)
            }
        }
        replaceIndulaschMessage()
    }

    private fun serializeState(state: MutableMap<String, Int>): String {
        return objectMapper.writeValueAsString(state)
    }

    private fun parseState(state: String): MutableMap<String, Int> {
        return objectMapper.readValue(state, object : TypeReference<MutableMap<String, Int>>() {})
    }

    @Transactional(readOnly = true)
    open fun getTowerDetails(selector: String): QrFightTowerDto {
        val tower = qrTowerRepository.findAllBySelector(selector).firstOrNull()
            ?: return QrFightTowerDto("TOWER NOT FOUND")
        return when (startupPropertyConfig.tokenOwnershipMode) {
            OwnershipType.GROUP -> QrFightTowerDto(tower.displayName, tower.ownerGroupName, tower.holder ?: "")
            OwnershipType.USER -> QrFightTowerDto(
                tower.displayName,
                tower.ownerUserName,
                userRepository.findById(tower.holder?.toIntOrNull() ?: 0).map { it.fullNameWithAlias }.orElse(null)
            )
        }
    }

    fun replaceIndulaschMessage() {
        if (!qrFightComponent.indulaschTowerEnabled.isValueTrue())
            return

        indulaschIntegrationService.fetchIndulasch().forEach {
            if (it.text.startsWith(qrFightComponent.indulaschMessagePrefix.getValue())) {
                indulaschIntegrationService.deleteMessage(it.id)
            }
        }

        val tower = qrTowerRepository.findAllBySelector(qrFightComponent.indulaschTowerSelector.getValue())
            .firstOrNull()
            ?: return

        when (startupPropertyConfig.tokenOwnershipMode) {
            OwnershipType.USER -> indulaschIntegrationService.insertMessage(
                IndulaschNewMessageDto(
                    IndulaschMessageType.getByNameOrNull(qrFightComponent.indulaschMessageLevel.getValue())
                        ?: IndulaschMessageType.INFO,
                    qrFightComponent.indulaschMessageFormat.getValue()
                        .replace("{HOLDER}",
                            userRepository.findById(tower.holder?.toIntOrNull() ?: 0).map { it.fullNameWithAlias }
                                .orElse("senki")
                        )
                        .replace("{TIME}", (tower.holderFor ?: 0).toString())
                        .replace("{OWNER}", tower.ownerUserName.ifBlank { "senki" })
                )
            )
            OwnershipType.GROUP -> indulaschIntegrationService.insertMessage(
                IndulaschNewMessageDto(
                    IndulaschMessageType.getByNameOrNull(qrFightComponent.indulaschMessageLevel.getValue())
                        ?: IndulaschMessageType.INFO,
                    qrFightComponent.indulaschMessageFormat.getValue()
                        .replace("{HOLDER}", tower.holder ?: "senki")
                        .replace("{TIME}", (tower.holderFor ?: 0).toString())
                        .replace("{OWNER}", tower.ownerGroupName.ifBlank { "senki" })
                )
            )
        }
    }

}
