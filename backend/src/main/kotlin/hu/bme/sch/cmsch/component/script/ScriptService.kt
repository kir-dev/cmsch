package hu.bme.sch.cmsch.component.script

import hu.bme.sch.cmsch.component.script.sandbox.ModifyingScriptingDbContext
import hu.bme.sch.cmsch.component.script.sandbox.ReadOnlyScriptingDbContext
import hu.bme.sch.cmsch.component.script.sandbox.ScriptingContext
import hu.bme.sch.cmsch.component.script.sandbox.ScriptingCsvUtil
import hu.bme.sch.cmsch.component.script.sandbox.ScriptingJsonUtil
import hu.bme.sch.cmsch.model.Duplicatable
import jakarta.annotation.PostConstruct
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import kotlin.script.experimental.api.KotlinType
import kotlin.script.experimental.api.ResultWithDiagnostics

@Service
class ScriptService(
    private val repositories: List<CrudRepository<*, *>>
) {

    fun executeScript(scriptEntity: ScriptEntity) {

        val context = if (scriptEntity.readOnly) {
            ScriptingContext(
                modifyingDb = ModifyingScriptingDbContext(listOf(), true),
                readOnlyDb = ReadOnlyScriptingDbContext(repositories),
            )
        } else {
            ScriptingContext(
                modifyingDb = ModifyingScriptingDbContext(repositories, false),
                readOnlyDb = ReadOnlyScriptingDbContext(repositories),
            )
        }

        val result = ScriptRunnerService().runScript(scriptEntity.script,
            mapOf(
                "context" to KotlinType(ScriptingContext::class, false),
                "csv" to KotlinType(ScriptingCsvUtil::class, false),
                "json" to KotlinType(ScriptingJsonUtil::class, false),
            ),
            mapOf(
                "context" to context,
                "csv" to ScriptingCsvUtil(),
                "json" to ScriptingJsonUtil()
            )
        )

        when (result) {
            is ResultWithDiagnostics.Success -> {
                println("Success: ${result.value.returnValue}")
            }
            is ResultWithDiagnostics.Failure -> {
                println("Failed:")
                result.reports.forEach { println(it.message) }
            }
        }

        context.artifacts.forEach {
            println(it)
        }
    }

}