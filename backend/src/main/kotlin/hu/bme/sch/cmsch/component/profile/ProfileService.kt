package hu.bme.sch.cmsch.component.profile

import hu.bme.sch.cmsch.component.debt.DebtDto
import hu.bme.sch.cmsch.component.debt.SoldProductRepository
import hu.bme.sch.cmsch.component.groupselection.GroupSelectionComponent
import hu.bme.sch.cmsch.component.leaderboard.LeaderBoardComponent
import hu.bme.sch.cmsch.component.leaderboard.LeaderBoardService
import hu.bme.sch.cmsch.component.leaderboard.TopListAbstractEntryDto
import hu.bme.sch.cmsch.component.location.LocationService
import hu.bme.sch.cmsch.component.login.LoginComponent
import hu.bme.sch.cmsch.component.riddle.RiddleService
import hu.bme.sch.cmsch.component.task.TasksService
import hu.bme.sch.cmsch.component.token.ALL_TOKEN_TYPE
import hu.bme.sch.cmsch.component.token.TokenCollectorService
import hu.bme.sch.cmsch.component.token.TokenComponent
import hu.bme.sch.cmsch.config.OwnershipType
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.service.TimeService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@ConditionalOnBean(ProfileService::class)
open class ProfileService(
    private val groupRepository: GroupRepository,
    private val profileComponent: ProfileComponent,
    private val debtsRepository: Optional<SoldProductRepository>,
    private val locationService: Optional<LocationService>,
    private val groupSelectionComponent: Optional<GroupSelectionComponent>,
    private val tokenService: Optional<TokenCollectorService>,
    private val tokenComponent: Optional<TokenComponent>,
    private val tasksService: Optional<TasksService>,
    private val riddleService: Optional<RiddleService>,
    private val leaderBoardService: Optional<LeaderBoardService>,
    private val leaderBoardComponent: Optional<LeaderBoardComponent>,
    private val loginComponent: Optional<LoginComponent>,
    private val clock: TimeService,
    private val startupPropertyConfig: StartupPropertyConfig,
) {

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    open fun getProfileForUser(user: UserEntity): ProfileView {
        val group = user.group
        val leavable = fetchWhetherGroupLeavable(group)
        val tokenCategoryToDisplay = tokenComponent.map { it.collectRequiredType.getValue() }.orElse(ALL_TOKEN_TYPE)

        return ProfileView(
            loggedIn = true,

            // App component
            fullName = profileComponent.showFullName.mapIfTrue { user.fullName },
            groupName = profileComponent.showGroup.mapIfTrue { group?.name ?: "nincs" },
            role = user.role,
            alias =  profileComponent.showAlias.mapIfTrue { user.alias },

            guild = profileComponent.showGuild.mapIfTrue { user.guild },
            email = profileComponent.showEmail.mapIfTrue { user.email },
            neptun = profileComponent.showNeptun.mapIfTrue { user.neptun },
            cmschId = if (profileComponent.showQr.isValueTrue() || profileComponent.showProfilePicture.isValueTrue()) { user.cmschId } else null,
            major = profileComponent.showMajor.mapIfTrue { user.major },

            groupLeaders = profileComponent.showGroupLeaders.mapIfTrue { fetchGroupLeaders(group) },

            // Group selection component
            groupSelectionAllowed = leavable,
            availableGroups = if (leavable) fetchSelectableGroups() else null,
            fallbackGroup = fetchFallbackGroup().orElse(null),

            // Token component
            tokens = tokenService.map { repo -> repo.getTokensForUser(user) }.orElse(null),
            collectedTokenCount = fetchCollectedTokenCount(user, tokenCategoryToDisplay).orElse(null),
            totalTokenCount = fetchTotalTokenCount(tokenCategoryToDisplay).orElse(null),
            minTokenToComplete = tokenComponent.map {it.collectRequiredTokens.getValue().toIntOrNull() ?: Int.MAX_VALUE }.orElse(null),

            // Task component
            totalTaskCount = tasksService.map { it.getTotalTasksForUser(user) }.orElse(null),
            submittedTaskCount = tasksService.map { it.getSubmittedTasksForUser(user) }.orElse(null),
            completedTaskCount = tasksService.map { it.getCompletedTasksForUser(user) }.orElse(null),

            // Riddle cmponent
            totalRiddleCount = riddleService.map { it.getTotalRiddleCount(user) }.orElse(null),
            completedRiddleCount = riddleService.map { it.getCompletedRiddleCount(user) }.orElse(null),

            // Locations component
            locations = profileComponent.showGroupLeadersLocations.mapIfTrue { fetchLocations(group).orElse(null) },

            // Debt controller
            debts = fetchDebts(user).orElse(null),

            // Leaderboard controller
            leaderboard = fetchLeaderboard()
        )
    }

    private fun fetchLeaderboard(): List<TopListAbstractEntryDto>? {
        if (!leaderBoardComponent.map { it.leaderboardEnabled.isValueTrue() }.orElse(false))
            return null

        return when (startupPropertyConfig.taskOwnershipMode) {
            OwnershipType.USER -> leaderBoardService.map { it.getBoardForUsers() }.orElse(null)
            OwnershipType.GROUP -> leaderBoardService.map { it.getBoardForGroups() }.orElse(null)
        }
    }

    private fun fetchWhetherGroupLeavable(group: GroupEntity?) =
        groupSelectionComponent.map { it.selectionEnabled.mapIfTrue { group?.leaveable ?: true } ?: false }.orElse(false)

    private fun fetchTotalTokenCount(tokenCategoryToDisplay: String) =
        tokenService.map { repo ->
            if (tokenCategoryToDisplay == ALL_TOKEN_TYPE) {
                repo.getTotalTokenCount()
            } else {
                repo.getTotalTokenCountWithCategory(tokenCategoryToDisplay)
            }
        }

    private fun fetchCollectedTokenCount(user: UserEntity, tokenCategoryToDisplay: String) =
        tokenService.map { repo ->
            if (tokenCategoryToDisplay == ALL_TOKEN_TYPE) {
                repo.getTokensForUser(user).size
            } else {
                repo.getTokensForUserWithCategory(user, tokenCategoryToDisplay)
            }
        }

    private fun fetchFallbackGroup() =
        groupSelectionComponent.map { repo ->
            repo.selectionEnabled.mapIfTrue {
                groupRepository
                    .findByName(loginComponent.map { it.fallbackGroupName.getValue() }.orElse("Vendég"))
                    .map { it.id }
                    .orElse(null)
            }
        }

    private fun fetchGroupLeaders(group: GroupEntity?) =
        group?.let { g ->
            listOf(
                GroupLeaderDto(g.staff1.split("|")),
                GroupLeaderDto(g.staff2.split("|")),
                GroupLeaderDto(g.staff3.split("|")),
                GroupLeaderDto(g.staff4.split("|"))
            ).filter { it.name.isNotBlank() }
        }

    private fun fetchLocations(group: GroupEntity?) =
        locationService.map { repo ->
            repo.findLocationsOfGroup(group?.name ?: "")
                .filter { it.timestamp + profileComponent.locationTimeout.getIntValue(0) > clock.getTimeInSeconds() }
                .map {
                    GroupMemberLocationDto(
                        it.alias.ifBlank { it.userName },
                        it.longitude,
                        it.latitude,
                        it.accuracy,
                        it.timestamp
                    )
                }
        }

    private fun fetchDebts(user: UserEntity) = debtsRepository
        .map { repo ->
            repo.findAllByOwnerId(user.id)
                .map {
                    DebtDto(
                        it.product,
                        it.price,
                        it.sellerName,
                        it.responsibleName,
                        it.payed,
                        it.shipped,
                        it.log,
                        it.materialIcon
                    )
                }
        }

    private fun fetchSelectableGroups(): Map<Int, String> {
        return groupRepository.findAllBySelectableTrue().associate { Pair(it.id, it.name) }
    }

}
