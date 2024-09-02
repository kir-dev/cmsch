package hu.bme.sch.cmsch.component.task

import com.google.common.util.concurrent.ThreadFactoryBuilder
import hu.bme.sch.cmsch.admin.dashboard.*
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ImplicitPermissions
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.transaction
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.io.File
import java.nio.file.Path
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.LongAdder
import javax.imageio.ImageIO
import kotlin.jvm.optionals.getOrNull

private const val SELECTOR_TYPE = "selectorType"

private const val VIEW = "task-disc-optimize"

const val EXPORT_PATH = "export"

@Controller
@RequestMapping("/admin/control/$VIEW")
@ConditionalOnBean(value = [TaskComponent::class])
class TaskDiscOptimizerDashboard(
    adminMenuService: AdminMenuService,
    applicationComponent: TaskComponent,
    auditLogService: AuditLogService,
    private val platformTransactionManager: PlatformTransactionManager,
    private val taskSubmissionsRepository: Optional<SubmittedTaskRepository>,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val imageRescaleService: TaskImageRescaleService
) : DashboardPage(
    view = VIEW,
    title = "Disc Optimize",
    description = "",
    wide = false,
    adminMenuService = adminMenuService,
    component = applicationComponent,
    auditLog = auditLogService,
    showPermission = ImplicitPermissions.PERMISSION_SUPERUSER_ONLY,
    adminMenuCategory = ApplicationComponent.DEVELOPER_CATEGORY,
    adminMenuIcon = "delete",
    adminMenuPriority = 21
) {

    companion object {
        @JvmStatic
        val lock = Any()
    }

    private val log = LoggerFactory.getLogger(javaClass)

    internal val processStatus = AtomicReference("nincs exportálva")
    internal val processCount = LongAdder()
    internal val processRunning = AtomicBoolean(false)

    private val threadFactory = ThreadFactoryBuilder()
        .setUncaughtExceptionHandler { _, e ->
            log.error("Uncaught exception during Souvenir export", e)
            processStatus.set("Error: ${e.message}")
        }
        .setNameFormat("optimizer-pool-%d")
        .build()
    private val executorService = Executors.newThreadPerTaskExecutor(threadFactory)

    override fun getComponents(user: CmschUser, requestParams: Map<String, String>): List<DashboardComponent> {
        return listOf(
            exportStatus(),
            exportPanel(),
            permissionCard
        )
    }

    private fun exportStatus(): DashboardTableCard {
        return DashboardTableCard(
            id = 2,
            title = "Export eredménye",
            description = "Ha üres, akkor még nem volt exportálva",
            header = listOf("Kulcs", "Érték"),
            content = listOf(
                listOf("Exportálás folyamatban", if (processRunning.get()) "igen" else "nem"),
                listOf("Exportálás státusza", processStatus.get()),
                listOf("Counter", processCount.sum().toString()),
            ),
            wide = wide,
            exportable = false
        )
    }

    fun exportPanel(): DashboardFormCard {
        return DashboardFormCard(
            id = 3,
            wide = wide,
            title = "Exportálás indítása",
            description = "",
            content = listOf(
            ),
            buttonCaption = "Indítás",
            buttonIcon = "play_arrow",
            action = "optimize",
            method = "post",
            multipartForm = false,
        )
    }

    @PostMapping("/optimize")
    fun optimize(auth: Authentication, @RequestParam allRequestParams: Map<String, String>): String {
        val user = auth.getUser()
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        synchronized(lock) {
            if (processRunning.get())
                return dashboardPage(view = VIEW, card = 2, message = "Már fut exportálás")
            processRunning.set(true)
            executorService.submit(TaskImageOptimizerTask(
                platformTransactionManager = platformTransactionManager,
                taskSubmissionsRepository = taskSubmissionsRepository,
                processStatus = processStatus,
                processRunning = processRunning,
                processCount = processCount,
                startupPropertyConfig = startupPropertyConfig,
                imageRescaleService = imageRescaleService,
            ))
        }

        return dashboardPage(view = VIEW, card = 2, message = "Exportálás beütemezve")
    }


    private val permissionCard = DashboardPermissionCard(
        id = 1,
        permission = showPermission.permissionString,
        description = "Ez a jog szükséges ennek az oldalnak az olvasásához.",
        wide = false
    )

}

class TaskImageOptimizerTask(
    private val platformTransactionManager: PlatformTransactionManager,
    taskSubmissionsRepository: Optional<SubmittedTaskRepository>,

    private val processStatus: AtomicReference<String>,
    private val processRunning: AtomicBoolean,
    private val processCount: LongAdder,
    private val startupPropertyConfig: StartupPropertyConfig,

    private val imageRescaleService: TaskImageRescaleService,
) : Runnable {

    private val taskSubmissionsRepository = taskSubmissionsRepository.getOrNull()

    override fun run() {
        processStatus.set("Optimizer elindult")

        try {
            val tasks = platformTransactionManager.transaction(readOnly = true, isolation = TransactionDefinition.ISOLATION_READ_COMMITTED) {
                taskSubmissionsRepository!!.findAll()
            }
            imgonnakillmyself@for (selectedTask in tasks) {
                if (selectedTask.imageUrlAnswer.isNotBlank()) {
                    println("NOT BLANK ${selectedTask.id} for ${selectedTask.task?.id}")
                    val newName = resizeImage(selectedTask.imageUrlAnswer)

                    if (newName != null) {
                        selectedTask.imageUrlAnswer = newName
                        platformTransactionManager.transaction(readOnly = false) {
                            taskSubmissionsRepository!!.save(selectedTask)
                            println(" - SAVED $newName")
                        }
                        processCount.increment()
                        break@imgonnakillmyself
                    } else {
                        println(" - SMALL ENOUGH")
                    }
                } else {
                    println("BLANK ${selectedTask.id}")
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        processStatus.set("Optimizer kész")
        processRunning.set(false)
    }

    private fun resizeImage(fileFromCdn: String): String? {
        val inputFile = File(startupPropertyConfig.external, fileFromCdn)
        println("RESCALE $fileFromCdn ${inputFile.length()}")

        val megaByte = 1024 * 1024
        if (inputFile.length() < (1 * megaByte)) {
            val inputImage = ImageIO.read(inputFile)
            val resizedImage = imageRescaleService.resizeImage(inputImage, 800, 800)
            var outputPath = Path.of(startupPropertyConfig.external, fileFromCdn).toString()
            outputPath = outputPath.substringBeforeLast(".") + ".jpg"
            val outputFile = File(outputPath)

            println(" - OUTPUT: $outputPath")
            // TODO: DELETE original
            ImageIO.write(resizedImage, "jpg", outputFile)
            println(" - LENGTH: ${outputFile.length()}")
            return fileFromCdn.substringBeforeLast(".") + ".jpg"
        } else {
            println(" - SMALL ENOUGH")
            return null
        }
    }

}