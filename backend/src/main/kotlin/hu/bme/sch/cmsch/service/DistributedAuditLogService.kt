package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.component.login.CmschUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

class DistributedAuditLogService {

    private val logs: MutableMap<Class<out Any>, Logger> = mutableMapOf()

    private fun getLog(cls: KClass<*>): Logger {
        return logs.computeIfAbsent(cls.java) { javaClass -> LoggerFactory.getLogger(javaClass) }
    }

    fun systemInfo(info: String) {

    }

    fun create(actor: CmschUser, component: KClass<*>, action: String, params: Map<String, String>, textualDescription: String) {
        getLog(component).info("CREATE $textualDescription")
        auditLog(ActionCategory.CREATE, actor, component, action, params)
    }

    fun update(actor: CmschUser, component: KClass<*>, action: String, params: Map<String, String>, textualDescription: String) {
        getLog(component).info("UPDATE $textualDescription")
        auditLog(ActionCategory.UPDATE, actor, component, action, params)
    }

    fun delete(actor: CmschUser, component: KClass<*>, action: String, params: Map<String, String>, textualDescription: String) {
        getLog(component).info("DELETE $textualDescription")
        auditLog(ActionCategory.DELETE, actor, component, action, params)
    }

    fun configure(actor: CmschUser, component: KClass<*>, action: String, params: Map<String, String>, textualDescription: String) {
        getLog(component).info("CONFIGURE $textualDescription")
        auditLog(ActionCategory.CONFIGURE, actor, component, action, params)
    }

    private fun auditLog(category: ActionCategory, actor: CmschUser?, component: KClass<*>, action: String, params: Map<String, String>) {

    }

    enum class ActionCategory(
        reversible: Boolean,
    ) {
        SYSTEM(false),
        EVENT(false),
        CREATE(true),
        UPDATE(true),
        DELETE(true),
        CONFIGURE(true),
        SHOW_CONFIDENTIAL(false),
        AUTH(false),
        IMPORT(true),
        EXPORT(true),
        PURGE(true),
    }

    data class AuditLogEntry(
        val timestamp: String,
        val actorId: String?,
        val actorName: String,
        val category: ActionCategory,
        val action: String,
        val params: Map<String, String>,
        val textualDescription: String,
    )

}