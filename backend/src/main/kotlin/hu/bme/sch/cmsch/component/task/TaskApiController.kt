package hu.bme.sch.cmsch.component.task

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.component.leaderboard.LeaderBoardComponent
import hu.bme.sch.cmsch.component.leaderboard.LeaderBoardService
import hu.bme.sch.cmsch.config.OwnershipType
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(TaskComponent::class)
class TaskApiController(
    private val leaderBoardService: Optional<LeaderBoardService>,
    private val leaderBoardComponent: Optional<LeaderBoardComponent>,
    private val tasks: TasksService,
    private val clock: TimeService,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val taskComponent: TaskComponent
) {

    @JsonView(Preview::class)
    @GetMapping("/task")
    fun tasks(auth: Authentication?): TasksView {
        val categories: List<TaskCategoryDto>

        val user = auth?.getUserOrNull()
        when (startupPropertyConfig.taskOwnershipMode) {
            OwnershipType.USER -> {
                user ?: return TasksView()
                categories = tasks.getCategoriesForUserInTimeRange(user.id, clock.getNowInSeconds())

                return TasksView(
                    categories = categories
                        .filter { clock.inRange(it.availableFrom, it.availableTo, clock.getNowInSeconds()) },
                )
            }
            OwnershipType.GROUP -> {
                val groupId = user?.groupId ?: return TasksView( )
                categories = tasks.getCategoriesForGroupInRange(groupId, clock.getNowInSeconds())

                return TasksView(
                    categories = categories
                        .filter { clock.inRange(it.availableFrom, it.availableTo, clock.getTimeInSeconds()) },
                )
            }
        }
    }

    @JsonView(FullDetails::class)
    @GetMapping("/task/category/{categoryId}")
    fun taskCategory(@PathVariable categoryId: Int, auth: Authentication?): TaskCategoryView {
        val category = tasks.getCategory(categoryId) ?: return TaskCategoryView(
            categoryName = "Nem található O.o",
            tasks = listOf()
        )

        val taskList = when (startupPropertyConfig.taskOwnershipMode) {
            OwnershipType.USER -> {
                val user = auth?.getUserOrNull() ?: return TaskCategoryView(
                    categoryName = "Nem található",
                    tasks = listOf()
                )
                tasks.getAllTasksForUser(user, categoryId)
            }
            OwnershipType.GROUP -> {
                val groupId = auth?.getUserOrNull()?.groupId ?: return TaskCategoryView(
                    categoryName = "Nem található",
                    tasks = listOf()
                )
                tasks.getAllTasksForGroup(groupId, categoryId)
            }
        }

        return TaskCategoryView(
            categoryName = category.name,
            tasks = taskList,
            availableFrom = category.availableFrom,
            availableTo = category.availableTo,
            type = category.type
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/task/submit/{taskId}")
    fun task(@PathVariable taskId: Int, auth: Authentication?): SingleTaskView {
        val task = tasks.getById(taskId)
        val now = clock.getTimeInSeconds()
        if (task.orElse(null)?.visible?.not() == true)
            return SingleTaskView(task = null, submission = null)

        val categoryName = task.map { tasks.getCategoryName(it.categoryId) }.orElse(null)
        val submission = when (startupPropertyConfig.taskOwnershipMode) {
            OwnershipType.USER -> {
                val user = auth?.getUserOrNull() ?: return SingleTaskView(
                    task = task.map { TaskEntityDto(it, now, categoryName) }.orElse(null),
                    submission = null,
                    status = TaskStatus.NOT_SUBMITTED
                )
                tasks.getSubmissionForUserOrNull(user, task)
            }
            OwnershipType.GROUP -> {
                val group = auth?.getUserOrNull()?.groupId ?: return SingleTaskView(
                    task = task.map { TaskEntityDto(it, now, categoryName) }.orElse(null),
                    submission = null,
                    status = TaskStatus.NOT_SUBMITTED
                )
                tasks.getSubmissionForGroupOrNull(group, task)
            }
        }

        val taskOptional = task.map { TaskEntityDto(it, now, categoryName) }.orElse(null)
        return SingleTaskView(
            task = taskOptional,
            submission = submission?.let { sub -> mapSubmittedTaskEntityDto(sub, task, now) },
            status = resolveTaskStatus(submission)
        )
    }

    private fun mapSubmittedTaskEntityDto(
        sub: SubmittedTaskEntity,
        task: Optional<TaskEntity>,
        now: Long
    ) =
        SubmittedTaskEntityDto(
            sub,
            taskComponent.scoreVisibleAtAll.isValueTrue()
                    && (taskComponent.scoreVisible.isValueTrue()
                    || (sub.approved && task.map { it.availableTo + (TWO_HOURS) < now }.orElse(false)))
        )

    @ResponseBody
    @PostMapping("/task/submit")
    fun submitTask(
        @ModelAttribute(binding = false) answer: TaskSubmissionDto,
        @RequestParam(required = false) file: MultipartFile?,
        auth: Authentication?
    ): TaskSubmissionResponseDto {
        val user = auth?.getUserOrNull()
            ?: return TaskSubmissionResponseDto(TaskSubmissionStatus.NO_PERMISSION)

        return when (startupPropertyConfig.taskOwnershipMode) {
            OwnershipType.USER -> TaskSubmissionResponseDto(tasks.submitTaskForUser(answer, file, user))
            OwnershipType.GROUP -> TaskSubmissionResponseDto(tasks.submitTaskForGroup(answer, file, user))
        }
    }

}
