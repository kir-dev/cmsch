package hu.bme.sch.cmsch.component.script

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.script.sandbox.*
import hu.bme.sch.cmsch.model.Duplicatable
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.util.transaction
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.ResolvableType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.script.experimental.api.KotlinType
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.time.measureTimedValue

@Service
@ConditionalOnBean(ScriptComponent::class)
class ScriptService(
    private val repositories: List<CrudRepository<*, *>>,
    private val components: List<ComponentBase>,
    private val scriptResultRepository: ScriptResultRepository,
    private val clock: TimeService,
    private val transactionManager: PlatformTransactionManager
) {

    private final val log = LoggerFactory.getLogger(javaClass)

    private final val objectMapper = jacksonObjectMapper()
    private final val artifactWriter = objectMapper.writerFor(object : TypeReference<List<ScriptArtifact>>() {})
    private final val logWriter = objectMapper.writerFor(object : TypeReference<List<ScriptLogLineDto>>() {})

    private final val executorService = Executors.newVirtualThreadPerTaskExecutor()

    fun executeScript(userEntity: UserEntity? = null, scriptEntity: ScriptEntity): Pair<Int, Future<*>> {
        val (resultEntity, context) = transactionManager.transaction(
            readOnly = false,
            isolation = TransactionDefinition.ISOLATION_READ_COMMITTED,
        ) {
            return@transaction prepareScript(scriptEntity, userEntity)
        }

        val task = executorService.submit {
            val resultEntity = resultEntity.copy()
            log.info("Script runner {} | Executing task", scriptEntity.id)

            val timedResult = try {
                measureTimedValue {
                    ScriptRunnerService().runScript(
                        scriptEntity.script,
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
                }
            } catch (e: Exception) {
                resultEntity.success = false
                resultEntity.running = false
                resultEntity.result = e.message ?: ""
                log.error("Failed to execute script", e)

                transactionManager.transaction(
                    readOnly = false,
                    isolation = TransactionDefinition.ISOLATION_READ_COMMITTED,
                ) {
                    scriptResultRepository.save(resultEntity)
                }
                System.gc()
                return@submit
            }
            System.gc()

            val result = timedResult.value
            resultEntity.duration = timedResult.duration.inWholeMilliseconds
            when (result) {
                is ResultWithDiagnostics.Success -> {
                    log.info("Script runner {} | Finished successfully ${result.value.returnValue}", scriptEntity.id)
                    resultEntity.result = result.value.returnValue.toString()
                    resultEntity.success = true
                    resultEntity.running = false
                }

                is ResultWithDiagnostics.Failure -> {
                    log.info("Script runner {} | Failed", scriptEntity.id)
                    resultEntity.success = false
                    resultEntity.running = false
                }
            }

            resultEntity.artifacts = artifactWriter.writeValueAsString(context.artifacts)
            resultEntity.logs = logWriter.writeValueAsString(result.reports.map {
                ScriptLogLineDto(
                    it.message,
                    it.severity,
                    it.sourcePath,
                )
            } + context.logs)
            transactionManager.transaction(
                readOnly = false,
                isolation = TransactionDefinition.ISOLATION_READ_COMMITTED,
            ) {
                scriptResultRepository.save(resultEntity)
            }

            context.artifacts.forEach {
                println(it)
            }
            log.info("Script runner {} | Result saved", scriptEntity.id)
        }

        return resultEntity.id to task
    }

    private final fun prepareScript(
        scriptEntity: ScriptEntity,
        userEntity: UserEntity?
    ): Pair<ScriptResultEntity, ScriptingContext> {
        log.info("Script runner {} | Start (triggered by {} {})", scriptEntity.id, userEntity?.id, userEntity?.fullName)

        val supportedDependencies = scriptEntity.entities
            .split(", *".toRegex())
            .filter { it.isNotBlank() }
            .map { it.lowercase() }

        val filteredRepositories = if (scriptEntity.entities.trim() == "*") {
            repositories.filter { repo ->
                val type = ResolvableType.forClass(repo.javaClass).`as`(CrudRepository::class.java)
                val entityType = type.generics[0].resolve()
                    ?: return@filter false
                return@filter Duplicatable::class.java.isAssignableFrom(entityType)
            }
        } else {

            repositories.filter { repo ->
                val type = ResolvableType.forClass(repo.javaClass).`as`(CrudRepository::class.java)
                val entityType = type.generics[0].resolve()
                    ?: return@filter false
                if (!Duplicatable::class.java.isAssignableFrom(entityType))
                    return@filter false
                val entityName = entityType.simpleName.lowercase()
                entityName in supportedDependencies
            }
        }

        val filteredComponents = if (scriptEntity.entities.trim() == "*") {
            components
        } else {
            components.filter { it::class.simpleName in supportedDependencies }
        }

        log.info("Script runner {} | Entities available: {}", scriptEntity.id, repositories.mapNotNull { repo ->
            val type = ResolvableType.forClass(repo.javaClass).`as`(CrudRepository::class.java)
            val entityType = type.generics[0].resolve()
                ?: return@mapNotNull null
            if (!Duplicatable::class.java.isAssignableFrom(entityType))
                return@mapNotNull null
            val entityName = entityType.simpleName
            entityName
        })
        log.info("Script runner {} | Entities filtered: {}", scriptEntity.id, filteredRepositories.map { repo ->
            val type = ResolvableType.forClass(repo.javaClass).`as`(CrudRepository::class.java)
            val entityType = type.generics[0].resolve()
            val entityName = entityType?.simpleName
            entityName
        })
        log.info("Script runner {} | Entities components: {}", scriptEntity.id, filteredComponents.map { component -> component::class.simpleName })

        log.info("Script runner {} | Result created", scriptEntity.id)
        val resultEntity = ScriptResultEntity(
            success = false,
            running = true,
            scriptId = scriptEntity.id,
            userId = userEntity?.id ?: -1,
            userName = userEntity?.fullName ?: "system",
            timestamp = clock.getTimeInSeconds(),
            artifacts = "[]",
            logs = "[]",
        )
        scriptResultRepository.save(resultEntity)

        // This one is required to sync log messages without waiting the script to stop
        val updateLogsCallback: (List<ScriptLogLineDto>) -> Unit = { logs ->
            transactionManager.transaction(readOnly = false) {
                scriptResultRepository.updateLogsById(
                    resultEntity.id,
                    logWriter.writeValueAsString(logs)
                )
            }
        }

        val context = if (scriptEntity.readOnly) {
            ScriptingContext(
                modifyingDb = ModifyingScriptingDbContext(listOf(), true),
                modifyingComponents = ModifyingScriptingComponentContext(listOf()),
                readOnlyDb = ReadOnlyScriptingDbContext(filteredRepositories),
                updateLogs = updateLogsCallback,
            )
        } else {
            ScriptingContext(
                modifyingDb = ModifyingScriptingDbContext(filteredRepositories, false),
                modifyingComponents = ModifyingScriptingComponentContext(components),
                readOnlyDb = ReadOnlyScriptingDbContext(filteredRepositories),
                updateLogs = updateLogsCallback
            )
        }

        return Pair(resultEntity, context)
    }

}
