package hu.bme.sch.cmsch.component.script.sandbox

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.script.ScriptLogLineDto
import hu.bme.sch.cmsch.model.Duplicatable
import org.springframework.core.ResolvableType
import org.springframework.data.repository.CrudRepository
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KClass
import kotlin.script.experimental.api.ScriptDiagnostic.Severity

class ScriptingContext(
    val readOnlyDb: ReadOnlyScriptingDbContext,
    val modifyingDb: ModifyingScriptingDbContext,
    val modifyingComponents: ModifyingScriptingComponentContext,
    private val updateLogs: (logs: List<ScriptLogLineDto>) -> Unit
) {

    val artifacts = mutableListOf<ScriptArtifact>()
    val logs = mutableListOf<ScriptLogLineDto>()

    fun publishArtifact(artifact: ScriptArtifact) {
        artifacts.add(artifact)
    }

    fun println(message: String) {
        info(message)
    }

    fun info(message: String) {
        logs.add(ScriptLogLineDto(
            message = message,
            severity = Severity.INFO,
            exception = null,
        ))
        updateLogs(logs)
    }

    fun info(message: Any?) {
        info(message.toString())
    }

    fun debug(message: String) {
        logs.add(ScriptLogLineDto(
            message = message,
            severity = Severity.DEBUG,
            exception = null,
        ))
        updateLogs(logs)
    }

    fun debug(message: Any?) {
        debug(message.toString())
    }

    fun warn(message: String) {
        logs.add(ScriptLogLineDto(
            message = message,
            severity = Severity.WARNING,
            exception = null,
        ))
        updateLogs(logs)
    }

    fun warn(message: Any?) {
        warn(message.toString())
    }

    fun error(message: String) {
        logs.add(ScriptLogLineDto(
            message = message,
            severity = Severity.ERROR,
            exception = null,
        ))
        updateLogs(logs)
    }

    fun error(message: Any?) {
        error(message.toString())
    }

    fun error(message: String, exception: Exception?) {
        logs.add(ScriptLogLineDto(
            message = message,
            severity = Severity.ERROR,
            exception = exception?.message,
        ))
        updateLogs(logs)
    }

    fun printStackTrace(exception: Exception) {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        exception.printStackTrace(pw)
        pw.flush()
        error(sw.toString())
    }

}

@Suppress("UNCHECKED_CAST")
class ModifyingScriptingDbContext(private val supportedRepos: List<CrudRepository<*, *>>, private val readOnly: Boolean) {

    fun <T, ID, R : CrudRepository<T, ID>> repository(selectedRepo: KClass<out R>): R {
        if (readOnly)
            error("modifyingDb cannot be used in a read-only context")

        return (
            supportedRepos.firstOrNull { repo ->
                repo.javaClass.interfaces.any { selectedRepo.java.isAssignableFrom(it) }
            } as? R
        ) ?: error("Cannot find repository for ${selectedRepo.java.canonicalName}")
    }

}

@Suppress("UNCHECKED_CAST")
class ReadOnlyScriptingDbContext(private val supportedRepos: List<CrudRepository<*, *>>) {

    fun <T, ID : Any> repository(selectedRepo: KClass<out CrudRepository<T, ID>>): ReadOnlyRepositoryProxy<T, ID> where T : Duplicatable {
        val proxied = (
            supportedRepos.firstOrNull { repo ->
                repo.javaClass.interfaces.any { selectedRepo.java.isAssignableFrom(it) }
            } as? CrudRepository<T, ID>
        ) ?: error("Cannot find repository for ${selectedRepo.java.canonicalName}")

        return ReadOnlyRepositoryProxy(proxied)
    }
}

@Suppress("UNCHECKED_CAST")
class ReadOnlyRepositoryProxy<T : Duplicatable, ID : Any>(private val proxy: CrudRepository<T, ID>) {

    fun findAll(): List<T> {
        return proxy.findAll().mapNotNull { if (it is T) { it.duplicate() as T } else null }.toList()
    }

    fun findById(id: ID): T? {
        return proxy.findById(id).map { it.duplicate() }.getOrNull() as T?
    }

    fun findAllById(id: List<ID>): List<T> {
        return proxy.findAllById(id).map { it.duplicate() as T }.toList()
    }

}

@Suppress("UNCHECKED_CAST")
class ModifyingScriptingComponentContext(
    components: List<ComponentBase>
) {

    private val components: Map<KClass<out ComponentBase>, ComponentBase> = components.associateBy { it::class }

    fun <R : ComponentBase> component(selectedComponent: KClass<out R>): R {
        return components[selectedComponent] as R?
            ?: error("Cannot find component for $selectedComponent")
    }

}