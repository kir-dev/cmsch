package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.model.PermissionGroupEntity
import hu.bme.sch.cmsch.repository.PermissionGroupRepository
import hu.bme.sch.cmsch.util.transaction
import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityManager
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

private const val NO_OP_PERMISSION = ""

@Service
class PermissionGroupService(
    private val platformTransactionManager: PlatformTransactionManager,
    private val permissionGroupRepository: PermissionGroupRepository,
    private val entityManager: EntityManager
) : ApplicationContextAware {

    private val cacheForResolver = AtomicReference<Map<String, String>>(ConcurrentHashMap<String, String>())
    private val cacheForMenu = AtomicReference<List<PermissionGroupEntity>>(listOf())

    @PostConstruct
    fun init() {
        invalidatePermissionCaches()
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    fun automaticInvalidate() {
        invalidatePermissionCaches()
    }

    fun resolvePermissionGroups(permissionGroups: String): String {
        val cacheInstance = cacheForResolver.get()
        return permissionGroups.split(",")
            .joinToString(",") { cacheInstance.getOrDefault(it, NO_OP_PERMISSION) }
            .ifBlank { NO_OP_PERMISSION }
    }

    val allPermissionGroups
        get() = cacheForMenu.get()!!

    fun invalidatePermissionCaches() {
        val entities = platformTransactionManager.transaction(readOnly = true) {
            permissionGroupRepository.findAll()
        }
        entities.forEach { entityManager.detach(it) }

        cacheForResolver.set(ConcurrentHashMap(entities.associate { it.key to it.permissions }))
        cacheForMenu.set(entities.toList())
    }

    companion object {
        private var applicationContext: ApplicationContext? = null

        fun getBean(): PermissionGroupService = applicationContext?.getBean(PermissionGroupService::class.java)
            ?: throw IllegalStateException("Application context is not initialized.")
    }

    override fun setApplicationContext(context: ApplicationContext) {
        applicationContext = context
    }

}