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
import java.util.concurrent.atomic.AtomicReference

@Service
class ComponentHandlerService(
    val components: List<ComponentBase>
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val writer = ObjectMapper().writerFor(object : TypeReference<Map<String, Map<String, Any>>>() {})
    private val cache = AtomicReference(EnumMap<RoleType, String>(RoleType::class.java))

    fun getComponentConstantsForRole(role: RoleType): Map<String, Map<String, Any>> {
        return components
            .filter { it.minRole.isAvailableForRole(role) || role.isAdmin }
            .associate { it.component to it.attachConstants() }
    }

    fun getComponentConstantsForRoleFast(role: RoleType): String {
        return cache.get()[role] ?: "{}"
    }

    @Async
    @EventListener
    open fun handlePersistEvents(event: ComponentSettingsPersistedEvent) {
        invalidateCaches()
    }

    @PostConstruct
    fun invalidateCaches() {
        log.info("Invalidating ComponentHandlerService cache")
        val newCache = EnumMap<RoleType, String>(RoleType::class.java)
        for (role in RoleType.values()) {
            newCache[role] = writer.writeValueAsString(getComponentConstantsForRole(role))
        }
        cache.set(newCache)
        log.info("ComponentHandlerService cache regenerated")
    }

}
