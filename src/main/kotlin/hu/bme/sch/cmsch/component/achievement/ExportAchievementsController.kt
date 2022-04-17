package hu.bme.sch.cmsch.component.achievement

import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletRequest

@Controller
@ConditionalOnBean(AchievementComponent::class)
class ExportAchievementsController(
    private val achievements: AchievementsService,
    private val achievementComponent: AchievementComponent
) {

    @GetMapping("/export-bucketlist")
    fun achievements(request: HttpServletRequest, model: Model): String {
        if (!achievementComponent.exportEnabled.isValueTrue())
            return "redirect:/"

        val user = request.getUser()
        model.addAttribute("groupName", user.groupName)
        model.addAttribute("achievements", listOf<SubmittedAchievementEntity>())
        user.group?.also { group -> model.addAttribute("achievements", achievements.getAllSubmissions(group).sortedBy { it.categoryId }) }
        model.addAttribute("categories", achievements.getAllCategories().groupBy { it.categoryId })

        return "exportAchievements"
    }

}
