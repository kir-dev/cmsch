package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.model.RoleType
import jakarta.annotation.PostConstruct
import org.postgresql.util.PSQLException
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock


/**
 * This service is only designed to work for the frontend direction.
 * Admin page actions still requires using the Repositories.
 */
@Service
@ConditionalOnBean(RiddleComponent::class)
open class RiddleCacheManager(
    private val riddleEntityRepository: RiddleEntityRepository,
    private val riddleCategoryRepository: RiddleCategoryRepository,
    private val riddleMappingRepository: RiddleMappingRepository,
    private val riddlePersistenceService: RiddlePersistenceService,
    private val riddleComponent: RiddleComponent,
    private val config: StartupPropertyConfig
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val riddles = ConcurrentHashMap<Int, RiddleEntity>()
    private val categories = ConcurrentHashMap<Int, RiddleCategoryEntity>()
    private val mappings = ConcurrentHashMap<Int, RiddleMappingEntity>()
    private val lazyPersists = Collections.synchronizedSet(HashSet<RiddleMappingEntity>())

    private val groupLocks = ConcurrentHashMap<Int, ReentrantLock>()
    private val userLocks = ConcurrentHashMap<Int, ReentrantLock>()

    fun getLockForGroup(groupId: Int): ReentrantLock {
        return groupLocks.computeIfAbsent(groupId) { ReentrantLock() }
    }

    fun getLockForUser(userId: Int): ReentrantLock {
        return userLocks.computeIfAbsent(userId) { ReentrantLock() }
    }

    @PostConstruct
    fun init() {
        if (config.masterRole && config.riddleMicroserviceEnabled) {
            log.info("Riddle periodic save is disabled")
            return
        }
        resetCache(persistMapping = false, overrideMappings = true)
    }

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    open fun resetCache(persistMapping: Boolean, overrideMappings: Boolean) {
        log.info("Getting all locks for 'resetCache({}, {})'", persistMapping, overrideMappings)
        groupLocks.forEach { (_, lock) -> lock.lock() }
        userLocks.forEach { (_, lock) -> lock.lock() }
        log.info("Got all locks for 'resetCache({}, {})'", persistMapping, overrideMappings)
        try {
            riddles.clear()
            categories.clear()
            if (persistMapping) {
                riddleMappingRepository.saveAll(mappings.values)
            }
            if (overrideMappings) {
                mappings.clear()
            }

            riddles.putAll(riddleEntityRepository.findAll().associateBy { it.id })
            categories.putAll(riddleCategoryRepository.findAll().associateBy { it.id })
            if (overrideMappings) {
                mappings.putAll(riddleMappingRepository.findAll().associateBy { it.id })
            }
            mappings.forEach {
                it.value.riddleCategoryId = riddles[it.value.riddleId]?.categoryId ?: 0
            }
        } finally {
            groupLocks.forEach { (_, lock) -> lock.unlock() }
            userLocks.forEach { (_, lock) -> lock.unlock() }
            log.info("All locks released by 'resetCache({}, {})'", persistMapping, overrideMappings)
        }
    }

    fun forceUnlock() {
        groupLocks.forEach { (_, lock) -> lock.unlock() }
        userLocks.forEach { (_, lock) -> lock.unlock() }
        log.info("All locks released by 'forceUnlock'")
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    open fun periodicSave() {
        if (config.masterRole && config.riddleMicroserviceEnabled) {
            log.info("Riddle periodic save is disabled")
            return
        }

        log.info("Getting all locks for 'periodicSave'")
        groupLocks.forEach { (_, lock) -> lock.lock() }
        userLocks.forEach { (_, lock) -> lock.lock() }
        log.info("Got all locks for 'periodicSave'")
        try {
            riddlePersistenceService.saveAllRiddleMapping(lazyPersists)
            lazyPersists.clear()
        } finally {
            groupLocks.forEach { (_, lock) -> lock.unlock() }
            userLocks.forEach { (_, lock) -> lock.unlock() }
            log.info("All locks released by 'periodicSave'")
            groupLocks.clear()
            userLocks.clear()
        }
    }

    fun createNewMapping(mapping: RiddleMappingEntity) {
        riddlePersistenceService.saveRiddleMapping(mapping)
        mappings[mapping.id] = mapping
        mapping.riddleCategoryId = riddles[mapping.riddleId]?.categoryId ?: 0
    }

    fun updateMapping(mapping: RiddleMappingEntity, lazyPersist: Boolean = false) {
        if (lazyPersist) {
            lazyPersists.add(mapping)
        } else {
            riddlePersistenceService.saveRiddleMapping(mapping)
        }
    }

    fun updateRiddle(riddle: RiddleEntity) {
        riddlePersistenceService.saveRiddle(riddle)
    }

    fun getRiddleById(riddleId: Int) = riddles[riddleId]

    fun findAllCategoriesByVisibleTrueAndMinRoleAtMost(role: RoleType) =
        categories.values.asSequence()
            .filter { it.visible && it.minRole.value <= role.value }
            .toList()

    fun findAllMappingByOwnerUserIdAndCompletedTrue(userId: Int) =
        mappings.values.asSequence()
            .filter { it.ownerUserId == userId && it.completed }
            .toList()

    fun findAllMappingByOwnerGroupIdAndCompletedTrue(groupId: Int) =
        mappings.values.asSequence()
            .filter { it.ownerGroupId == groupId && it.completed }
            .toList()

    fun findAllRiddleByCategoryId(categoryId: Int) =
        riddles.values.asSequence()
            .filter { it.categoryId == categoryId }
            .toList()

    fun findCategoryByCategoryIdAndVisibleTrueAndMinRoleAtMost(categoryId: Int, role: RoleType) =
        categories.values
            .firstOrNull { it.visible && it.categoryId == categoryId && it.minRole.value <= role.value }

    fun findAllMappingByOwnerUserIdAndRiddleCategoryId(userId: Int, categoryId: Int) =
        mappings.values.asSequence()
            .filter { it.ownerUserId == userId && it.riddleCategoryId == categoryId }
            .toList()

    fun findAllMappingByGroupUserIdAndRiddleCategoryId(groupId: Int, categoryId: Int) =
        mappings.values.asSequence()
            .filter { it.ownerGroupId == groupId && it.riddleCategoryId == categoryId }
            .toList()

    fun findMappingByOwnerUserIdAndRiddleId(userId: Int, riddleId: Int) =
        mappings.values
            .firstOrNull { it.ownerUserId == userId && it.riddleId == riddleId }

    fun findMappingByOwnerGroupIdAndRiddleId(groupId: Int, riddleId: Int) =
        mappings.values
            .firstOrNull { it.ownerGroupId == groupId && it.riddleId == riddleId }

    fun countAllMappingByCompletedNotSkippedAndRiddleId(riddleId: Int) =
        mappings.values
            .count { it.completed && !it.skipped && it.riddleId == riddleId }

    fun countAllMappingByCompletedTrueAndOwnerUserIdAndRiddleCategoryIdIn(userId: Int, categories: List<Int>) =
        mappings.values
            .count { it.completed && it.ownerUserId == userId && it.riddleCategoryId in categories }

    fun countAllMappingByCompletedTrueAndOwnerGroupIdAndRiddleCategoryIdIn(groupId: Int, categories: List<Int>) =
        mappings.values
            .count { it.completed && it.ownerGroupId == groupId && it.riddleCategoryId in categories }

    fun countAllRiddleByCategoryIdIn(categories: List<Int>) =
        riddles.values
            .count { it.categoryId in categories }

}