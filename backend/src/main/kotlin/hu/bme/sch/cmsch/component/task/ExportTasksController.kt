package hu.bme.sch.cmsch.component.task

import hu.bme.sch.cmsch.util.getUserFromDatabase
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
@ConditionalOnBean(TaskComponent::class)
class ExportTasksController(
    private val tasks: TasksService,
    private val taskComponent: TaskComponent
) {

    @GetMapping("/export-tasks")
    fun tasks(auth: Authentication, model: Model): String {
        if (!taskComponent.exportEnabled.isValueTrue())
            return "redirect:/"

        val user = auth.getUserFromDatabase()
        model.addAttribute("groupName", user.groupName)
        model.addAttribute("tasks", listOf<SubmittedTaskEntity>())
        user.group?.also { group -> model.addAttribute("tasks", tasks.getAllSubmissions(group).sortedBy { it.categoryId }) }
        model.addAttribute("categories", tasks.getAllCategories().groupBy { it.categoryId })

        return "exportTasks"
    }

}
