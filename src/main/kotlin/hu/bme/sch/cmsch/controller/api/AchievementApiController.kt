package hu.bme.sch.cmsch.controller.api

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.*
import hu.bme.sch.cmsch.dto.view.AchievementCategoryView
import hu.bme.sch.cmsch.dto.view.AchievementsView
import hu.bme.sch.cmsch.dto.view.SingleAchievementView
import hu.bme.sch.cmsch.service.AchievementsService
import hu.bme.sch.cmsch.service.ClockService
import hu.bme.sch.cmsch.service.LeaderBoardService
import hu.bme.sch.cmsch.service.RealtimeConfigService
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
class AchievementApiController(
    private val config: RealtimeConfigService,
    private val leaderBoardService: LeaderBoardService,
    private val achievements: AchievementsService,
    private val clock: ClockService
) {

    @JsonView(Preview::class)
    @GetMapping("/achievement")
    fun achievements(request: HttpServletRequest): AchievementsView {
        if (config.isSiteLowProfile()) {
            return AchievementsView(
                groupScore = null,
                leaderBoard = listOf(),
                leaderBoardVisible = config.isLeaderBoardEnabled(),
                leaderBoardFrozen = !config.isLeaderBoardUpdates())
        }

        val group = request.getUserOrNull()?.group ?: return AchievementsView(
            groupScore = null,
            leaderBoard = leaderBoardService.getBoard(),
            leaderBoardVisible = config.isLeaderBoardEnabled(),
            leaderBoardFrozen = !config.isLeaderBoardUpdates())

        return AchievementsView(
            groupScore = leaderBoardService.getScoreOfGroup(group),
            categories = achievements.getCategories(group.id)
                .filter { it.availableFrom < clock.getTimeInSeconds() && it.availableTo > clock.getTimeInSeconds() },
            leaderBoard = leaderBoardService.getBoard(),
            leaderBoardVisible = config.isLeaderBoardEnabled(),
            leaderBoardFrozen = !config.isLeaderBoardUpdates()
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/achievement/category/{categoryId}")
    fun achievementCategory(@PathVariable categoryId: Int, request: HttpServletRequest): AchievementCategoryView {
        val category = achievements.getCategory(categoryId) ?: return AchievementCategoryView(
            categoryName = "Nem található O.o",
            achievements = listOf()
        )

        if (config.isSiteLowProfile() || category.availableFrom > clock.getTimeInSeconds() || category.availableTo < clock.getTimeInSeconds()) {
            return AchievementCategoryView(
                categoryName = "Még nem publikus O.o",
                achievements = listOf()
            )
        }

        val group = request.getUserOrNull()?.group ?: return AchievementCategoryView(
            categoryName = "Nem található",
            achievements = listOf()
        )

        return AchievementCategoryView(
            categoryName = category.name,
            achievements = achievements.getAllAchievements(group).filter { it.achievement.categoryId == categoryId }
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/achievement/submit/{achievementId}")
    fun achievement(@PathVariable achievementId: Int, request: HttpServletRequest): SingleAchievementView {
        val achievement = achievements.getById(achievementId)
        if (achievement.orElse(null)?.visible?.not() == true || config.isSiteLowProfile())
            return SingleAchievementView(warningMessage = config.getWarningMessage(), achievement = null, submission = null)

        val group = request.getUserOrNull()?.group ?: return SingleAchievementView(
            warningMessage = config.getWarningMessage(),
            achievement = achievement.orElse(null),
            submission = null,
            status = AchievementStatus.NOT_SUBMITTED
        )

        val submission = achievements.getSubmissionOrNull(group, achievement)
        return SingleAchievementView(
            warningMessage = config.getWarningMessage(),
            achievement = achievement.orElse(null),
            submission = submission,
            status = if (submission?.approved == true) AchievementStatus.ACCEPTED
            else if (submission?.rejected == true) AchievementStatus.REJECTED
            else if (submission?.approved == false && !submission.rejected) AchievementStatus.SUBMITTED
            else AchievementStatus.NOT_SUBMITTED
        )
    }

    @ResponseBody
    @PostMapping("/achievement/submit")
    fun submitAchievement(
        @ModelAttribute(binding = false) answer: AchievementSubmissionDto,
        @RequestParam(required = false) file: MultipartFile?,
        request: HttpServletRequest
    ): AchievementSubmissionResponseDto {
        if (config.isSiteLowProfile())
            return AchievementSubmissionResponseDto(AchievementSubmissionStatus.NO_PERMISSION)

        val user = request.getUserOrNull() ?:
            return AchievementSubmissionResponseDto(AchievementSubmissionStatus.NO_PERMISSION)
        return AchievementSubmissionResponseDto(achievements.submitAchievement(answer, file, user))
    }

}
