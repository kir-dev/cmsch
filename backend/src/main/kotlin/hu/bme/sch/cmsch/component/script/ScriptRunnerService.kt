package hu.bme.sch.cmsch.component.script

import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.dependencies.CompoundDependenciesResolver
import kotlin.script.experimental.dependencies.DependsOn
import kotlin.script.experimental.dependencies.Repository
import kotlin.script.experimental.dependencies.maven.MavenDependenciesResolver
import kotlin.script.experimental.dependencies.resolveFromScriptSourceAnnotations
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.JvmDependency
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.jvmTarget
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

object ScriptHelper {
    val resolver = CompoundDependenciesResolver(MavenDependenciesResolver())

    fun configureMavenDepsOnAnnotations(context: ScriptConfigurationRefinementContext): ResultWithDiagnostics<ScriptCompilationConfiguration> {
        val annotations =
            context.collectedData?.get(ScriptCollectedData.collectedAnnotations)?.takeIf { it.isNotEmpty() }
                ?: return context.compilationConfiguration.asSuccess()
        return runBlocking {
            resolver.resolveFromScriptSourceAnnotations(annotations)
        }.onSuccess {
            context.compilationConfiguration.with {
                dependencies.append(JvmDependency(it))
            }.asSuccess()
        }
    }
}

class ScriptWithMavenDepsConfiguration : ScriptCompilationConfiguration(
    {
        defaultImports(DependsOn::class, Repository::class)
        defaultImports(
            "kotlin.math.*",
            "hu.bme.sch.cmsch.model.*",
            "hu.bme.sch.cmsch.repository.*",
            "hu.bme.sch.cmsch.util.*",
            "hu.bme.sch.cmsch.component.script.*",
            "hu.bme.sch.cmsch.component.script.sandbox.*",
            "hu.bme.sch.cmsch.component.bmejegy.*",
            "hu.bme.sch.cmsch.component.challenge.*",
            "hu.bme.sch.cmsch.component.communities.*",
            "hu.bme.sch.cmsch.component.conference.*",
            "hu.bme.sch.cmsch.component.debt.*",
            "hu.bme.sch.cmsch.component.email.*",
            "hu.bme.sch.cmsch.component.script.*",
            "hu.bme.sch.cmsch.component.event.*",
            "hu.bme.sch.cmsch.component.form.*",
            "hu.bme.sch.cmsch.component.gallery.*",
            "hu.bme.sch.cmsch.component.key.*",
            "hu.bme.sch.cmsch.component.location.*",
            "hu.bme.sch.cmsch.component.news.*",
            "hu.bme.sch.cmsch.component.proto.*",
            "hu.bme.sch.cmsch.component.qrfight.*",
            "hu.bme.sch.cmsch.component.race.*",
            "hu.bme.sch.cmsch.component.riddle.*",
            "hu.bme.sch.cmsch.component.staticpage.*",
            "hu.bme.sch.cmsch.component.task.*",
            "hu.bme.sch.cmsch.component.team.*",
            "hu.bme.sch.cmsch.component.token.*",
        )
        jvm {
            dependenciesFromCurrentContext(wholeClasspath = true)
            jvmTarget(Runtime.version().version().first().toString())
        }
        refineConfiguration {
            onAnnotations(DependsOn::class, Repository::class, handler = ScriptHelper::configureMavenDepsOnAnnotations)
        }
    }
)

@KotlinScript(
    fileExtension = "runtime.kts",
    compilationConfiguration = ScriptWithMavenDepsConfiguration::class
)
abstract class ScriptWithMavenDeps

class ScriptRunnerService {

    fun makeEvalConfig(context: Map<String, Any?>): ScriptEvaluationConfiguration {
        return ScriptEvaluationConfiguration {
            providedProperties(context)
            constructorArgs()
            scriptsInstancesSharing(false)
        }
    }

    fun runScript(code: String, contextTypes: Map<String, KotlinType>, context: Map<String, Any?>): ResultWithDiagnostics<EvaluationResult> {
        val host = BasicJvmScriptingHost()
        val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<ScriptWithMavenDeps> {
            providedProperties(contextTypes)
        }
        val result = host.eval(
            code.toScriptSource(),
            compilationConfiguration,
            makeEvalConfig(context)
        )
        return result
    }

}