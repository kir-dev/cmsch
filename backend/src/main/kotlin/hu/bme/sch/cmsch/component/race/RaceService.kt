package hu.bme.sch.cmsch.component.race

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.component.team.TeamIntroductionRepository
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.UserRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull
import kotlin.math.pow
import kotlin.math.sqrt

const val DEFAULT_CATEGORY = ""

@Suppress("RedundantModalityModifier") // Spring transactional proxy requires it not to be final
@Service
@ConditionalOnBean(RaceComponent::class)
open class RaceService(
    private val raceRecordRepository: RaceRecordRepository,
    private val raceCategoryRepository: RaceCategoryRepository,
    private val freestyleRaceRecordRepository: FreestyleRaceRecordRepository,
    private val raceComponent: RaceComponent,
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val teamIntroductionRepository: TeamIntroductionRepository,
) {

    @Transactional(readOnly = true)
    open fun getViewForGroups(user: CmschUser?): RaceView {
        val board = getBoardForGroups(DEFAULT_CATEGORY)

        return if (user == null) {
            RaceView(
                categoryName = raceComponent.title,
                description = raceComponent.defaultCategoryDescription,
                place = null, bestTime = null, board = board
            )
        } else {
            val groupId = user.groupId ?: -1
            val place = board.indexOfFirst { it.id == groupId }
            RaceView(
                categoryName = raceComponent.title,
                description = raceComponent.defaultCategoryDescription,
                place = if (place < 0) null else (place + 1),
                bestTime = board.find { it.id == groupId }?.time,
                board = board
            )
        }
    }

    @Transactional(readOnly = true)
    open fun getFreestyleViewForGroups(): FreestyleRaceView {
        val board = getFreestyleBoardForGroups()
        return FreestyleRaceView(
            raceComponent.title,
            raceComponent.defaultCategoryDescription,
            board
        )
    }

    @Throws(NoSuchElementException::class)
    @Transactional(readOnly = true)
    open fun getViewForGroups(user: CmschUser?, slug: String): RaceView {
        val category = raceCategoryRepository.findByVisibleTrueAndSlug(slug).orElse(null)
            ?: throw NoSuchElementException()
        val board = getBoardForGroups(category.slug)

        return if (user == null) {
            RaceView(category.name, category.description, null, null, board)
        } else {
            val groupId = user.groupId ?: -1
            val place = board.indexOfFirst { it.id == groupId }
            RaceView(
                category.name,
                category.description,
                if (place < 0) null else (place + 1),
                board.find { it.id == groupId }?.time,
                board
            )
        }
    }

    @Throws(NoSuchElementException::class)
    @Transactional(readOnly = true)
    open fun getRaceByTeam(teamId: Int, userId: Int): RaceView {
        val team = groupRepository.findById(teamId).getOrNull()
            ?: return RaceView("Nem található", "", null, null, listOf())
        val introduction = teamIntroductionRepository.findApprovedIntroductionsForGroup(teamId).firstOrNull()
        val board = getBoardForUsers(DEFAULT_CATEGORY, false)
            .filter { it.groupName == team.name }

        val place = board.indexOfFirst { it.id == teamId }
        return RaceView(
            team.name,
            introduction?.introduction ?: "",
            if (place < 0) null else (place + 1),
            board.find { it.id == userId }?.time,
            board
        )
    }

    @Transactional(readOnly = true)
    open fun getBoardForGroups(category: String) = if (raceComponent.ascendingOrder) {
        raceRecordRepository.findAllByCategory(category)
            .groupBy { it.groupId }
            .map { submission ->
                RaceEntryDto(
                    submission.key ?: 0,
                    submission.value.first().groupName,
                    submission.value.first().groupName,
                    submission.value.minOf { it.time },
                    email = ""
                )
            }
            .sortedBy { it.time }
    } else {
        raceRecordRepository.findAllByCategory(category)
            .groupBy { it.groupId }
            .map { submission ->
                RaceEntryDto(
                    submission.key ?: 0,
                    submission.value.first().groupName,
                    submission.value.first().groupName,
                    submission.value.maxOf { it.time },
                    email = ""
                )
            }
            .sortedByDescending { it.time }
    }

    @Transactional(readOnly = true)
    open fun getFreestyleBoardForGroups() = if (raceComponent.ascendingOrder) {
        freestyleRaceRecordRepository.findAll()
            .map { submission ->
                FreestyleRaceEntryDto(
                    submission.groupId ?: 0,
                    submission.groupName,
                    submission.groupName,
                    submission.time,
                    label = submission.label
                )
            }
            .sortedBy { it.time }
    } else {
        freestyleRaceRecordRepository.findAll()
            .map { submission ->
                FreestyleRaceEntryDto(
                    submission.groupId ?: 0,
                    submission.groupName,
                    submission.groupName,
                    submission.time,
                    label = submission.label,
                )
            }
            .sortedByDescending { it.time }
    }

    @Transactional(readOnly = true)
    open fun getViewForUsers(user: CmschUser?): RaceView {
        val board = getBoardForUsers(DEFAULT_CATEGORY, false)

        return if (user == null) {
            RaceView(
                raceComponent.title,
                raceComponent.defaultCategoryDescription,
                null, null, board)
        } else {
            val place = board.indexOfFirst { it.id == user.id }
            RaceView(
                raceComponent.title,
                raceComponent.defaultCategoryDescription,
                if (place < 0) null else (place + 1),
                board.find { it.id == user.id }?.time,
                board
            )
        }
    }

    @Throws(NoSuchElementException::class)
    @Transactional(readOnly = true)
    open fun getViewForUsers(user: CmschUser?, slug: String): RaceView {
        val category = raceCategoryRepository.findByVisibleTrueAndSlug(slug).orElse(null)
            ?: throw NoSuchElementException()
        val board = getBoardForUsers(category.slug, false)

        return if (user == null) {
            RaceView(category.name, category.description, null, null, board)
        } else {
            val place = board.indexOfFirst { it.id == user.id }
            RaceView(
                category.name,
                category.description,
                if (place < 0) null else (place + 1),
                board.find { it.id == user.id }?.time,
                board
            )
        }
    }

    @Transactional(readOnly = true)
    open fun getFreestyleViewForUsers(): FreestyleRaceView {
        val board = getFreestyleBoardForUsers()
        return FreestyleRaceView(
            categoryName = raceComponent.freestyleCategoryName,
            description = raceComponent.freestyleCategoryDescription,
            board = board
        )
    }

    @Transactional(readOnly = true)
    open fun getBoardForUsers(category: String, fetchEmail: Boolean) = if (raceComponent.ascendingOrder) {
        raceRecordRepository.findAllByCategory(category)
            .groupBy { it.userId }
            .map { submission ->
                val email = if (fetchEmail) userRepository.findById(submission.key ?: 0)
                    .map { it.email }
                    .orElse("n/a") else ""
                RaceEntryDto(
                    submission.key ?: 0,
                    submission.value.first().userName,
                    submission.value.first().groupName,
                    submission.value.minOf { it.time },
                    email
                )
            }
            .sortedBy { it.time }
    } else {
        raceRecordRepository.findAllByCategory(category)
            .groupBy { it.userId }
            .map { submission ->
                val email = if (fetchEmail) userRepository.findById(submission.key ?: 0)
                    .map { it.email }
                    .orElse("n/a") else ""
                RaceEntryDto(
                    submission.key ?: 0,
                    submission.value.first().userName,
                    submission.value.first().groupName,
                    submission.value.maxOf { it.time },
                    email
                )
            }
            .sortedByDescending { it.time }
    }

    @Transactional(readOnly = true)
    open fun getFreestyleBoardForUsers() = if (raceComponent.ascendingOrder) {
        freestyleRaceRecordRepository.findAll()
            .map { submission ->
                FreestyleRaceEntryDto(
                    submission.userId ?: 0,
                    submission.userName,
                    submission.groupName,
                    submission.time,
                    submission.description,
                    label = submission.label
                )
            }
            .sortedBy { it.time }
    } else {
        freestyleRaceRecordRepository.findAll()
            .map { submission ->
                FreestyleRaceEntryDto(
                    submission.userId ?: 0,
                    submission.userName,
                    submission.groupName,
                    submission.time,
                    submission.description,
                    label = submission.label
                )
            }
            .sortedByDescending { it.time }
    }

    /***
     * Gets relevant statistics about race entries of the given user
     * @param CmschUser whose stats we want to get
     * @return RaceStatsView of the user, or null if the user has no submissions
     */
    @Transactional(readOnly = true)
    open fun getRaceStats(user: CmschUser): RaceStatsView? {

        // All records
        val records: List<RaceRecordEntity> = raceRecordRepository.findAll().sortedBy { it.time }

        // All the records of this user
        var userRecords = records.filter { it.userId == user.id }
        if( userRecords.isEmpty() ) return null
        val bestTime = userRecords.minBy { it.time }.time

        // Best records of all users.
        val bestTimes = records.groupBy { it.userId }.map { it.value.first() }.sortedBy { it.time } // Sort might be unnecessary, but just to be safe
        val placement = bestTimes.indexOfFirst { it.userId == user.id } + 1

        val timesParticipated = userRecords.count()

        val averageTime = userRecords.map { it.time }.average().toFloat()

        val deviation = sqrt(userRecords.map { it.time }.map { (it - averageTime).pow(2) }.average()).toFloat()

        // kcal of 0.5l of Soproni Lager
        val kcalPerPortion = 164
        val kCaloriesPerSecond = kcalPerPortion / bestTime

        return RaceStatsView(
            bestTime = bestTime,
            placement = placement,
            timesParticipated = timesParticipated,
            averageTime = averageTime,
            deviation = deviation,
            kCaloriesPerSecond = kCaloriesPerSecond
        )
    }
}
