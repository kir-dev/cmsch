package hu.bme.sch.cmsch.controller.api

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.*
import hu.bme.sch.cmsch.dto.config.OwnershipType
import hu.bme.sch.cmsch.dto.view.AchievementCategoryView
import hu.bme.sch.cmsch.dto.view.AchievementsView
import hu.bme.sch.cmsch.dto.view.SingleAchievementView
import hu.bme.sch.cmsch.service.AchievementsService
import hu.bme.sch.cmsch.service.ClockService
import hu.bme.sch.cmsch.service.LeaderBoardService
import hu.bme.sch.cmsch.service.RealtimeConfigService
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.beans.factory.annotation.Value
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
    private val clock: ClockService,
    @Value("\${cmsch.leaderboard.enabled:false}") private val leaderBoardAvailable: Boolean,
    @Value("\${cmsch.achievement.ownership:USER}") private val achievementOwnershipMode: OwnershipType
) {

    @JsonView(Preview::class)
    @GetMapping("/achievement")
    fun achievements(request: HttpServletRequest): AchievementsView {
        if (config.isSiteLowProfile()) {
            return AchievementsView(
                score = null,
                leaderBoard = listOf(),
                leaderBoardVisible = leaderBoardAvailable && config.isLeaderBoardEnabled(),
                leaderBoardFrozen = !config.isLeaderBoardUpdates())
        }

        val categories: List<AchievementCategoryDto>
        val score: Int?

        when (achievementOwnershipMode) {
            OwnershipType.USER -> {
                val user = request.getUserOrNull() ?: return AchievementsView(
                    score = null,
                    leaderBoard = if (leaderBoardAvailable) leaderBoardService.getBoardForUsers() else listOf(),
                    leaderBoardVisible = leaderBoardAvailable && config.isLeaderBoardEnabled(),
                    leaderBoardFrozen = !config.isLeaderBoardUpdates())
                categories = achievements.getCategoriesForUser(user.id)
                score = if (leaderBoardAvailable) leaderBoardService.getScoreOfUser(user) else null

                return AchievementsView(
                    score = score,
                    categories = categories
                        .filter { it.availableFrom < clock.getTimeInSeconds() && it.availableTo > clock.getTimeInSeconds() },
                    leaderBoard = if (leaderBoardAvailable) leaderBoardService.getBoardForUsers() else listOf(),
                    leaderBoardVisible = leaderBoardAvailable && config.isLeaderBoardEnabled(),
                    leaderBoardFrozen = !config.isLeaderBoardUpdates()
                )
            }
            OwnershipType.GROUP -> {
                val group = request.getUserOrNull()?.group ?: return AchievementsView(
                    score = null,
                    leaderBoard = if (leaderBoardAvailable) leaderBoardService.getBoardForGroups() else listOf(),
                    leaderBoardVisible = leaderBoardAvailable && config.isLeaderBoardEnabled(),
                    leaderBoardFrozen = !config.isLeaderBoardUpdates())
                categories = achievements.getCategoriesForGroup(group.id)
                score = if (leaderBoardAvailable) leaderBoardService.getScoreOfGroup(group) else null

                return AchievementsView(
                    score = score,
                    categories = categories
                        .filter { it.availableFrom < clock.getTimeInSeconds() && it.availableTo > clock.getTimeInSeconds() },
                    leaderBoard = if (leaderBoardAvailable) leaderBoardService.getBoardForGroups() else listOf(),
                    leaderBoardVisible = leaderBoardAvailable && config.isLeaderBoardEnabled(),
                    leaderBoardFrozen = !config.isLeaderBoardUpdates()
                )
            }
        }
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

        val achievements =  when (achievementOwnershipMode) {
            OwnershipType.USER -> {
                val user = request.getUserOrNull() ?: return AchievementCategoryView(
                    categoryName = "Nem található",
                    achievements = listOf()
                )
                achievements.getAllAchievementsForUser(user)
            }
            OwnershipType.GROUP -> {
                val group = request.getUserOrNull()?.group ?: return AchievementCategoryView(
                    categoryName = "Nem található",
                    achievements = listOf()
                )
                achievements.getAllAchievementsForGroup(group)
            }
        }

        return AchievementCategoryView(
            categoryName = category.name,
            achievements = achievements.filter { it.achievement.categoryId == categoryId }
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/achievement/submit/{achievementId}")
    fun achievement(@PathVariable achievementId: Int, request: HttpServletRequest): SingleAchievementView {
        val achievement = achievements.getById(achievementId)
        if (achievement.orElse(null)?.visible?.not() == true || config.isSiteLowProfile())
            return SingleAchievementView(achievement = null, submission = null)

        val submission = when (achievementOwnershipMode) {
            OwnershipType.USER -> {
                val user = request.getUserOrNull() ?: return SingleAchievementView(
                    achievement = achievement.orElse(null),
                    submission = null,
                    status = AchievementStatus.NOT_SUBMITTED
                )
                achievements.getSubmissionForUserOrNull(user, achievement)
            }
            OwnershipType.GROUP -> {
                val group = request.getUserOrNull()?.group ?: return SingleAchievementView(
                    achievement = achievement.orElse(null),
                    submission = null,
                    status = AchievementStatus.NOT_SUBMITTED
                )
                achievements.getSubmissionForGroupOrNull(group, achievement)
            }
        }

        return SingleAchievementView(
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

        val user = request.getUserOrNull()
            ?: return AchievementSubmissionResponseDto(AchievementSubmissionStatus.NO_PERMISSION)

        return when (achievementOwnershipMode) {
            OwnershipType.USER -> AchievementSubmissionResponseDto(achievements.submitAchievementForUser(answer, file, user))
            OwnershipType.GROUP -> AchievementSubmissionResponseDto(achievements.submitAchievementForGroup(answer, file, user))
        }
    }

}
