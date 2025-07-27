package hu.bme.sch.cmsch.component.script

import hu.bme.sch.cmsch.component.script.sandbox.ScriptingContext
import hu.bme.sch.cmsch.component.script.sandbox.ScriptingCsvUtil
import hu.bme.sch.cmsch.component.script.sandbox.ScriptingJsonUtil
import org.junit.jupiter.api.Test
import kotlin.script.experimental.api.KotlinType
import kotlin.script.experimental.api.ResultWithDiagnostics

class ScriptRunnerServiceTest {

    @Test
    fun name() {
//        val context = ScriptingContext()
//
//        val result = ScriptRunnerService().runScript(
//            """
//            println("Hello world")
//            println(xyz)
//
//            data class TestDto(
//                val a: String,
//                val testB: String,
//            )
//            val listOf = listOf(TestDto("asdasd", "b"), TestDto("asdasdcccc", "c"))
//            println(json.generatePrettyJson(listOf))
//            println(csv.generateTypedCsv(listOf))
//
//        """.trimIndent(),
//            mapOf(
//                "xyz" to KotlinType(String::class, false),
//                "context" to KotlinType(ScriptingContext::class, false),
//                "csv" to KotlinType(ScriptingCsvUtil::class, false),
//                "json" to KotlinType(ScriptingJsonUtil::class, false),
//            ),
//            mapOf(
//                "xyz" to "asd",
//                "context" to context,
//                "csv" to ScriptingCsvUtil(),
//                "json" to ScriptingJsonUtil()
//            )
//        )
//
//
//        when (result) {
//            is ResultWithDiagnostics.Success -> {
//                println("Success: ${result.value.returnValue}")
//            }
//            is ResultWithDiagnostics.Failure -> {
//                println("Failed:")
//                result.reports.forEach { println(it.message) }
//            }
//        }
    }
}