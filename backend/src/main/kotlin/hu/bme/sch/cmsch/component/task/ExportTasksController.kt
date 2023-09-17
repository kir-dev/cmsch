package hu.bme.sch.cmsch.component.task

import hu.bme.sch.cmsch.component.leaderboard.LeaderBoardService
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.markdownToHtml
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.util.*

@Controller
@ConditionalOnBean(TaskComponent::class)
class ExportTasksController(
    private val tasks: TasksService,
    private val taskComponent: TaskComponent,
    private val leaderBoardService: Optional<LeaderBoardService>
) {

    @GetMapping("/export-tasks")
    fun tasks(auth: Authentication, model: Model): String {
        if (!taskComponent.exportEnabled.isValueTrue())
            return "redirect:/"

        val user = auth.getUser()
        model.addAttribute("groupName", user.groupName)
        model.addAttribute("place", leaderBoardService
            .map { service -> service.getBoardForGroups()
                .indexOfFirst { it.name == user.groupName } + 1 }
            .orElse(0))

        model.addAttribute("notes", markdownToHtml(taskComponent.leadOrganizerQuote.getValue()))
        model.addAttribute("logoUrl", taskComponent.logoUrl.getValue())
        model.addAttribute("tasks", listOf<SubmittedTaskEntity>())
        user.groupId?.also { groupId -> model.addAttribute("tasks", tasks.getAllSubmissions(groupId).sortedBy { it.categoryId }) }
        model.addAttribute("categories", tasks.getAllCategories().groupBy { it.categoryId })

        return "exportTasks"
    }

}
