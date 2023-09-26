package hu.bme.sch.cmsch.component.task

import hu.bme.sch.cmsch.admin.dashboard.DashboardComponent
import hu.bme.sch.cmsch.admin.dashboard.DashboardFormCard
import hu.bme.sch.cmsch.admin.dashboard.DashboardPage
import hu.bme.sch.cmsch.admin.dashboard.DashboardPermissionCard
import hu.bme.sch.cmsch.component.form.FormElement
import hu.bme.sch.cmsch.component.form.FormElementType
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.config.OwnershipType
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import org.apache.catalina.util.URLEncoder
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.nio.charset.StandardCharsets

private const val VIEW = "task-review"
private const val ENDPOINT = "/admin/control/$VIEW"

@Controller
@RequestMapping(ENDPOINT)
@ConditionalOnBean(TaskComponent::class)
class SubmittedTaskReviewDashboard(
    adminMenuService: AdminMenuService,
    applicationComponent: TaskComponent,
    private val auditLogService: AuditLogService,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val tasksService: TasksService,
    private val userService: UserService,
    private val groupRepository: GroupRepository
) : DashboardPage(
    VIEW,
    "Személyes beadás értékelése",
    "Személyes beadás értékelése",
    false,
    adminMenuService,
    applicationComponent,
    auditLogService,
    StaffPermissions.PERMISSION_TASK_MANUAL_SUBMIT,
    adminMenuIcon = "task_alt",
    adminMenuPriority = 7
) {
    val ownershipType get() = startupPropertyConfig.taskOwnershipMode
    val isUserOwnership get() = ownershipType == OwnershipType.USER

    private val permissionCard = DashboardPermissionCard(
        1,
        permission = showPermission.permissionString,
        description = "Ez a jog szükséges ennek az oldalnak az olvasásához.",
        wide = false
    )

    override fun getComponents(user: CmschUser): List<DashboardComponent> {
        return listOf(
            permissionCard,
            getForm(),
        )
    }

    fun getForm(): DashboardFormCard {
        return DashboardFormCard(
            2,
            false,
            "Személyes beadás értékelése",
            "Válaszd ki a ${if (isUserOwnership) "felhasználót" else "csoportot"}, akinek a beadását értékelni akarod, majd a feladatot!",
            listOf(
                FormElement(
                    "author", if (isUserOwnership) "Felhasználó" else "Csoport", FormElementType.SEARCHABLE_SELECT,
                    ".*", "", getAuthorList(isUserOwnership),
                    "Akinek értékelni szeretnéd a beadott feladatát",
                    required = true
                ),
                FormElement(
                    "task", "Feladat", FormElementType.SEARCHABLE_SELECT,
                    ".*", "", getTaskList(),
                    "Az értékelendő feladat (automatikusan frissül miután a ${if (isUserOwnership) "felhasználót" else "csoportot"} kiválasztottad)",
                    required = true
                ),
                FormElement(
                    "response", "Megjegyzés", FormElementType.TEXT,
                    ".*", "", "",
                    "Megjegyzés az értékeléshez"
                ),
                FormElement(
                    "score", "Pontszám", FormElementType.NUMBER,
                    ".*", "", "",
                    required = true
                ),
                FormElement(
                    "decision", "Értékelés", FormElementType.SELECT,
                    ".*", "", "Elfogad,Elutasít", "A beadás értékelése",
                    required = true
                )
            ),
            buttonCaption = "Küldés",
            buttonIcon = "send",
            action = "submit-review",
            method = "post"
        )
    }


    @PostMapping("/submit-review")
    fun send(
        auth: Authentication,
        @RequestParam(required = true) author: String,
        @RequestParam(required = true) task: String,
        @RequestParam(defaultValue = "") response: String,
        @RequestParam(required = true) decision: String,
        @RequestParam(required = true) score: Int
    ): String {
        val user = auth.getUser()
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        val authorId = author.split(":")[0].toIntOrNull() ?: throw IllegalStateException("Invalid authorId format")
        val taskId = task.split(":")[0].toIntOrNull() ?: throw IllegalStateException("Invalid userId format")

        val userId = if (isUserOwnership) authorId else null
        val groupId = if (!isUserOwnership) authorId else null
        val isApproved = decision.lowercase() == "elfogad"
        val savedSuccessfully =
            tasksService.submitTaskReview(taskId, userId, groupId, response, isApproved, score, user.userName)
        if (savedSuccessfully) {
            auditLogService.fine(
                user,
                "task",
                "Submit review for task with id: $task, groupId: $groupId, userId: $userId, approved: $isApproved, score: $score"
            )
        } else {
            auditLogService.error(
                user,
                "task",
                "Submit review failed for task with id: $task, groupId: $groupId, userId: $userId, approved: $isApproved, score: $score"
            )
        }
        val message = if (savedSuccessfully) "A mentés sikeres" else "Sikertelen volt a mentés"
        val encodedMessage = URLEncoder.QUERY.encode(message, StandardCharsets.UTF_8)
        return "redirect:$ENDPOINT?card=2&message=$encodedMessage"
    }


    fun getAuthorList(isUserOwnership: Boolean): String =
        if (isUserOwnership) {
            userService.findAllUserSelectorView()
                .sortedBy { it.name }
                .joinToString(",") {
                    "${it.id}: ${it.fullNameWithAlias.replace(',', ' ')} [${it.provider.firstOrNull() ?: 'n'}]"
                }
        } else {
            groupRepository.findAllThatExists()
                .sortedBy { it.name }
                .joinToString(",") {
                    "${it.id}: ${it.name.replace(',', ' ')}"
                }
        }

    fun getTaskList(): String =
        tasksService.getAllTasksNameView()
            .sortedBy { it.title }
            .joinToString(",") {
                "${it.id}: ${it.title.replace(',', ' ')}"
            }
}