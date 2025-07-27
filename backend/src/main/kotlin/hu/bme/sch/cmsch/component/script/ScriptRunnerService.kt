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


@Service
class ScriptRunnerService {

    object ScriptWithMavenDepsConfiguration : ScriptCompilationConfiguration(
        {
            defaultImports(DependsOn::class, Repository::class)
            defaultImports("kotlin.math.*", "hu.bme.sch.cmsch.component.script.sandbox.*", )
            jvm {
                dependenciesFromCurrentContext(wholeClasspath = true)
                jvmTarget("24")
            }
            refineConfiguration {
                onAnnotations(DependsOn::class, Repository::class, handler = ::configureMavenDepsOnAnnotations)
            }
        }
    )

    companion object {
        fun configureMavenDepsOnAnnotations(context: ScriptConfigurationRefinementContext): ResultWithDiagnostics<ScriptCompilationConfiguration> {
            val annotations = context.collectedData?.get(ScriptCollectedData.collectedAnnotations)?.takeIf { it.isNotEmpty() }
                ?: return context.compilationConfiguration.asSuccess()
            return runBlocking {
                resolver.resolveFromScriptSourceAnnotations(annotations)
            }.onSuccess {
                context.compilationConfiguration.with {
                    dependencies.append(JvmDependency(it))
                }.asSuccess()
            }
        }

        private val resolver = CompoundDependenciesResolver(MavenDependenciesResolver())
    }

    @KotlinScript(
        fileExtension = "runtime.kts",
        compilationConfiguration = ScriptWithMavenDepsConfiguration::class
    )
    abstract class ScriptWithMavenDeps

    private final fun makeEvalConfig(context: Map<String, Any?>): ScriptEvaluationConfiguration {
        return ScriptEvaluationConfiguration {
            providedProperties(context)
            constructorArgs()
            scriptsInstancesSharing(false)
        }
    }

    final fun runScript(code: String, contextTypes: Map<String, KotlinType>, context: Map<String, Any?>): ResultWithDiagnostics<EvaluationResult> {
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