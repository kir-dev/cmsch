package hu.bme.sch.g7.controller

import hu.bme.sch.g7.model.SubmittedAchievementEntity
import hu.bme.sch.g7.service.AchievementsService
import hu.bme.sch.g7.util.getUser
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletRequest

@Controller
class AddonAchievementsController(
        private val achievements: AchievementsService,
) {

    @GetMapping("/export-bucketlist")
    fun achievements(request: HttpServletRequest, model: Model): String {
        val user = request.getUser()

        model.addAttribute("groupName", user.groupName)
        model.addAttribute("achievements", listOf<SubmittedAchievementEntity>())
        user.group?.also { model.addAttribute("achievements", achievements.getAllSubmissions(it).sortedBy { it.categoryId }) }
        model.addAttribute("categories", achievements.getAllCategories().groupBy { it.categoryId })

        return "addonAchievements"
    }

}
