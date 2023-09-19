package hu.bme.sch.cmsch.component

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.model.RoleType
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

@Service
class ComponentHandlerService(
    val components: List<ComponentBase>
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val writer = ObjectMapper().writerFor(object : TypeReference<Map<String, Map<String, Any>>>() {})
    private val cache = EnumMap<RoleType, String>(RoleType::class.java)
    private val lock = ReentrantReadWriteLock()

    fun getComponentConstantsForRole(role: RoleType): Map<String, Map<String, Any>> {
        return components
            .filter { it.minRole.isAvailableForRole(role) || role.isAdmin }
            .associate { it.component to it.attachConstants() }
    }

    fun getComponentConstantsForRoleFast(role: RoleType): String {
        return lock.read {
            cache[role] ?: "{}"
        }
    }

    @Async
    @EventListener
    open fun handlePersistEvents(event: ComponentSettingsPersistedEvent) {
        invalidateCaches()
    }

    @PostConstruct
    fun invalidateCaches() {
        log.info("Invalidating ComponentHandlerService cache")
        lock.write {
            for (role in RoleType.values()) {
                cache[role] = writer.writeValueAsString(getComponentConstantsForRole(role))
            }
        }
        log.info("ComponentHandlerService cache regenerated")
    }

}
