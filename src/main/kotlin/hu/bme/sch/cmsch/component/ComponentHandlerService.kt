package hu.bme.sch.cmsch.component

import hu.bme.sch.cmsch.model.RoleType
import org.springframework.stereotype.Service

@Service
class ComponentHandlerService(
    val components: List<ComponentBase>
) {

    fun getComponentConstantsForRole(role: RoleType): Map<String, Map<String, String>> {
        return components
            .filter { it.minRole.isAvailableForRole(role) || role.isAdmin }
            .associate { it.component to it.attachConstants() }
    }

}
