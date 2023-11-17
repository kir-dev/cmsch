package hu.bme.sch.cmsch.component.profile

import hu.bme.sch.cmsch.component.admission.AdmissionService
import hu.bme.sch.cmsch.component.bmejegy.BmejegyService
import hu.bme.sch.cmsch.component.debt.DebtDto
import hu.bme.sch.cmsch.component.debt.SoldProductRepository
import hu.bme.sch.cmsch.component.groupselection.GroupSelectionComponent
import hu.bme.sch.cmsch.component.location.LocationService
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.component.login.LoginComponent
import hu.bme.sch.cmsch.component.riddle.RiddleBusinessLogicService
import hu.bme.sch.cmsch.component.task.TasksService
import hu.bme.sch.cmsch.component.token.ALL_TOKEN_TYPE
import hu.bme.sch.cmsch.component.token.TokenCollectorService
import hu.bme.sch.cmsch.component.token.TokenComponent
import hu.bme.sch.cmsch.config.OwnershipType
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.TimeService
import org.postgresql.util.PSQLException
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
@ConditionalOnBean(ProfileService::class)
open class ProfileService(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val profileComponent: ProfileComponent,
    private val debtsRepository: Optional<SoldProductRepository>,
    private val locationService: Optional<LocationService>,
    private val groupSelectionComponent: Optional<GroupSelectionComponent>,
    private val tokenService: Optional<TokenCollectorService>,
    private val tokenComponent: Optional<TokenComponent>,
    private val tasksService: Optional<TasksService>,
    private val riddleService: Optional<RiddleBusinessLogicService>,
    private val loginComponent: Optional<LoginComponent>,
    private val bmejegyService: Optional<BmejegyService>,
    private val clock: TimeService,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val admissionService: Optional<AdmissionService>
) {

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    open fun getProfileForUser(user: UserEntity): ProfileView {
        val group = user.group
        val leavable = fetchWhetherGroupLeavable(group)
        val tokenCategoryToDisplay = tokenComponent.map { it.collectRequiredType.getValue() }.orElse(ALL_TOKEN_TYPE)
        val incompleteTasks = tasksService.map { it.getTasksThatNeedsToBeCompleted(user) }.orElse(null)

        return ProfileView(
            loggedIn = true,

            // App component
            fullName = profileComponent.showFullName.mapIfTrue { user.fullName },
            groupName = profileComponent.showGroup.mapIfTrue { group?.name ?: "nincs" },
            role = user.role,
            alias = profileComponent.showAlias.mapIfTrue { user.alias },

            guild = profileComponent.showGuild.mapIfTrue { user.guild },
            email = profileComponent.showEmail.mapIfTrue { user.email },
            neptun = profileComponent.showNeptun.mapIfTrue { user.neptun },
            cmschId = mapQr(user),
            major = profileComponent.showMajor.mapIfTrue { user.major },

            groupLeaders = profileComponent.showGroupLeaders.mapIfTrue { fetchGroupLeaders(group) },
            groupMessage = profileComponent.showGroupMessage.mapIfTrue { group?.profileTopMessage },
            userMessage = profileComponent.showUserMessage.mapIfTrue { user.profileTopMessage },

            // Group selection component
            groupSelectionAllowed = leavable,
            availableGroups = if (leavable) fetchSelectableGroups() else null,
            fallbackGroup = fetchFallbackGroup(),

            // Token component
            tokens = tokenService.map { repo -> repo.getTokensForUser(user) }.orElse(null),
            collectedTokenCount = fetchCollectedTokenCount(user, group, tokenCategoryToDisplay).orElse(null),
            totalTokenCount = fetchTotalTokenCount(tokenCategoryToDisplay).orElse(null),
            minTokenToComplete = tokenComponent.map { it.collectRequiredTokens.getValue().toIntOrNull() ?: Int.MAX_VALUE }
                .orElse(null),

            // Task component
            totalTaskCount = tasksService.map { it.getTotalTasksForUser(user) }.orElse(null),
            submittedTaskCount = tasksService.map { it.getSubmittedTasksForUser(user) }.orElse(null),
            completedTaskCount = tasksService.map { it.getCompletedTasksForUser(user) }.orElse(null),
            profileIsComplete = incompleteTasks?.isEmpty() ?: false,
            incompleteTasks = incompleteTasks,

            // Riddle component
            totalRiddleCount = riddleService.map { it.getTotalRiddleCount(user) }.orElse(null),
            completedRiddleCount = riddleService.map {
                when (startupPropertyConfig.riddleOwnershipMode) {
                    OwnershipType.USER -> it.getCompletedRiddleCountUser(user)
                    OwnershipType.GROUP -> it.getCompletedRiddleCountGroup(user, user.groupId)
                }
            }.orElse(null),

            // Locations component
            locations = profileComponent.showGroupLeadersLocations.mapIfTrue { fetchLocations(group).orElse(null) },

            // Debt controller
            debts = fetchDebts(user).orElse(null),

            // Leaderboard controller
            leaderboard = null
        )
    }

    private fun mapQr(user: UserEntity): String? {
        if (profileComponent.showQrOnlyIfTicketPresent.isValueTrue()
            && (profileComponent.showQr.isValueTrue() || profileComponent.showProfilePicture.isValueTrue())) {

            return if (admissionService.map { it.hasTicket(user.cmschId) }.orElse(false)) user.cmschId else null
        }

        return if (profileComponent.showQr.isValueTrue() || profileComponent.showProfilePicture.isValueTrue()) {
            if (profileComponent.bmejegyQrIfPresent.isValueTrue())
                fetchBmejegyTicket(user)
            else user.cmschId
        } else null
    }

    private fun fetchBmejegyTicket(user: UserEntity): String {
        return bmejegyService.flatMap { it.findVoucherByUser(user.id) }.orElse(user.cmschId)
    }

    private fun fetchWhetherGroupLeavable(group: GroupEntity?) =
        profileComponent.selectionEnabled.mapIfTrue { group?.leaveable ?: true } ?: false

    private fun fetchTotalTokenCount(tokenCategoryToDisplay: String) =
        tokenService.map { repo ->
            if (tokenCategoryToDisplay == ALL_TOKEN_TYPE) {
                repo.getTotalTokenCount()
            } else {
                repo.getTotalTokenCountWithCategory(tokenCategoryToDisplay)
            }
        }

    private fun fetchCollectedTokenCount(user: CmschUser, group: GroupEntity?, tokenCategoryToDisplay: String) =
        when (startupPropertyConfig.riddleOwnershipMode) {
            OwnershipType.USER -> tokenService.map { repo ->
                if (tokenCategoryToDisplay == ALL_TOKEN_TYPE) {
                    repo.countTokensForUser(user)
                } else {
                    repo.getTokensForUserWithCategory(user, tokenCategoryToDisplay)
                }
            }
            OwnershipType.GROUP -> if (group == null) Optional.of(0) else tokenService.map { repo ->
                if (tokenCategoryToDisplay == ALL_TOKEN_TYPE) {
                    repo.countTokensForGroup(group)
                } else {
                    repo.getTokensForGroupWithCategory(group, tokenCategoryToDisplay)
                }
            }
        }

    private fun fetchFallbackGroup() =
        profileComponent.selectionEnabled.mapIfTrue {
            groupRepository
                .findByName(loginComponent.map { it.fallbackGroupName.getValue() }.orElse("Vendég"))
                .map { it.id }
                .orElse(null)
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
            repo.findLocationsOfGroup(group?.id ?: 0)
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
        return groupRepository.findAllBySelectableTrue()
            .sortedBy { it.name }
            .associate { Pair(it.id, it.name) }
    }

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun changeAlias(user: UserEntity, newAlias: String): Boolean {
        return if (newAlias.matches(Regex(profileComponent.aliasRegex.getValue()))) {
            user.alias = newAlias.trim()
            userRepository.save(user)
            true
        } else {
            false
        }
    }

}
