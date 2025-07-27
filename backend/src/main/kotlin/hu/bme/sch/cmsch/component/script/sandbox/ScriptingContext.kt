package hu.bme.sch.cmsch.component.script.sandbox

import hu.bme.sch.cmsch.model.Duplicatable
import org.springframework.data.repository.CrudRepository
import kotlin.jvm.java
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KClass

class ScriptingContext(
    val readOnlyDb: ReadOnlyScriptingDbContext,
    val modifyingDb: ModifyingScriptingDbContext
) {

    val artifacts = mutableListOf<ScriptArtifact>()

    fun publishArtifact(artifact: ScriptArtifact) {
        artifacts.add(artifact)
    }

}

@Suppress("UNCHECKED_CAST")
class ModifyingScriptingDbContext(private val supportedRepos: List<CrudRepository<*, *>>, private val readOnly: Boolean) {

    fun <T, ID> repository(selectedRepo: KClass<out CrudRepository<T, ID>>): CrudRepository<T, ID> {
        if (readOnly)
            error("modifyingDb cannot be used in a read-only context")

        return (
            supportedRepos.firstOrNull { repo ->
                repo.javaClass.interfaces.any { selectedRepo.java.isAssignableFrom(it) }
            } as? CrudRepository<T, ID>
        ) ?: error("cannot find repository for ${selectedRepo.java.canonicalName}")
    }

}

@Suppress("UNCHECKED_CAST")
class ReadOnlyScriptingDbContext(private val supportedRepos: List<CrudRepository<*, *>>) {

    fun <T : Duplicatable, ID : Any> repository(selectedRepo: KClass<out CrudRepository<T, ID>>): ReadOnlyRepositoryProxy<T, ID> {
        val proxied = (
            supportedRepos.firstOrNull { repo ->
                repo.javaClass.interfaces.any { selectedRepo.java.isAssignableFrom(it) }
            } as? CrudRepository<T, ID>
        ) ?: error("cannot find repository for ${selectedRepo.java.canonicalName}")

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

//fun asd() {
//    val scriptingContext = ScriptingContext(db = ScriptingReadOnlyDbContext())
//    println(scriptingContext.readOnlyDb.repository(UserRepository::class).findAll().first().neptun)
//    println(scriptingContext.readOnlyDb.repository(UserRepository::class).findById(12)?.neptun)
//    println(scriptingContext.modifyingDb.repository(UserRepository::class).findAll().first().neptun)
//    println(scriptingContext.modifyingDb.repository(UserRepository::class).findById(12)?.neptun)
//}