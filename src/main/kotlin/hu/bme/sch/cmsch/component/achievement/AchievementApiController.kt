package hu.bme.sch.cmsch.component.achievement

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.component.leaderboard.LeaderBoardComponent
import hu.bme.sch.cmsch.component.leaderboard.LeaderBoardService
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.dto.config.OwnershipType
import hu.bme.sch.cmsch.service.ClockService
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(AchievementComponent::class)
class AchievementApiController(
    private val leaderBoardService: Optional<LeaderBoardService>,
    private val leaderBoardComponent: Optional<LeaderBoardComponent>,
    private val achievements: AchievementsService,
    private val clock: ClockService,
    @Value("\${cmsch.achievement.ownership:USER}") private val achievementOwnershipMode: OwnershipType
) {

    @JsonView(Preview::class)
    @GetMapping("/achievement")
    fun achievements(request: HttpServletRequest): AchievementsView {
        val categories: List<AchievementCategoryDto>
        val score: Int?
        val leaderBoardAvailable = leaderBoardComponent.map { it.leaderboardEnabled.isValueTrue() }.orElse(false)
        val leaderBoardFrozen = leaderBoardComponent.map { it.leaderboardFrozen.isValueTrue() }.orElse(true)

        when (achievementOwnershipMode) {
            OwnershipType.USER -> {
                val user = request.getUserOrNull() ?: return AchievementsView(
                    score = null,
                    leaderBoard = if (leaderBoardAvailable) leaderBoardService.map { it.getBoardForUsers() }.orElse(listOf()) else listOf(),
                    leaderBoardVisible = leaderBoardAvailable,
                    leaderBoardFrozen = leaderBoardFrozen
                )
                categories = achievements.getCategoriesForUser(user.id)
                score = if (leaderBoardAvailable) leaderBoardService.map { it.getScoreOfUser(user) }.orElse(null) else null

                return AchievementsView(
                    score = score,
                    categories = categories
                        .filter { it.availableFrom < clock.getTimeInSeconds() && it.availableTo > clock.getTimeInSeconds() },
                    leaderBoard = if (leaderBoardAvailable) leaderBoardService.map { it.getBoardForUsers() }.orElse(listOf()) else listOf(),
                    leaderBoardVisible = leaderBoardAvailable,
                    leaderBoardFrozen = leaderBoardFrozen
                )
            }
            OwnershipType.GROUP -> {
                val group = request.getUserOrNull()?.group ?: return AchievementsView(
                    score = null,
                    leaderBoard = if (leaderBoardAvailable) leaderBoardService.map { it.getBoardForGroups() }.orElse(listOf()) else listOf(),
                    leaderBoardVisible = leaderBoardAvailable,
                    leaderBoardFrozen = leaderBoardFrozen
                )
                categories = achievements.getCategoriesForGroup(group.id)
                score = if (leaderBoardAvailable) leaderBoardService.map { it.getScoreOfGroup(group) }.orElse(null) else null

                return AchievementsView(
                    score = score,
                    categories = categories
                        .filter { it.availableFrom < clock.getTimeInSeconds() && it.availableTo > clock.getTimeInSeconds() },
                    leaderBoard = if (leaderBoardAvailable) leaderBoardService.map { it.getBoardForGroups() }.orElse(listOf()) else listOf(),
                    leaderBoardVisible = leaderBoardAvailable,
                    leaderBoardFrozen = leaderBoardFrozen
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
        if (achievement.orElse(null)?.visible?.not() == true)
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
        val user = request.getUserOrNull()
            ?: return AchievementSubmissionResponseDto(AchievementSubmissionStatus.NO_PERMISSION)

        return when (achievementOwnershipMode) {
            OwnershipType.USER -> AchievementSubmissionResponseDto(achievements.submitAchievementForUser(answer, file, user))
            OwnershipType.GROUP -> AchievementSubmissionResponseDto(achievements.submitAchievementForGroup(answer, file, user))
        }
    }

}
