package hu.bme.sch.cmsch.component.race

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.UserRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

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
    private val groupRepository: GroupRepository
) {

    @Transactional(readOnly = true)
    open fun getViewForGroups(user: CmschUser?): RaceView {
        val board = getBoardForGroups(DEFAULT_CATEGORY)

        return if (user == null) {
            RaceView(
                categoryName = raceComponent.title.getValue(),
                description = raceComponent.defaultCategoryDescription.getValue(),
                place = null, bestTime = null, board = board
            )
        } else {
            val groupId = user.groupId ?: -1
            val place = board.indexOfFirst { it.id == groupId }
            RaceView(
                categoryName = raceComponent.title.getValue(),
                description = raceComponent.defaultCategoryDescription.getValue(),
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
            raceComponent.title.getValue(),
            raceComponent.defaultCategoryDescription.getValue(),
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
        val board = getBoardForUsers(DEFAULT_CATEGORY, false)
            .filter { it.groupName == team.name }

        val place = board.indexOfFirst { it.id == teamId }
        return RaceView(
            team.name,
            team.description,
            if (place < 0) null else (place + 1),
            board.find { it.id == userId }?.time,
            board
        )
    }

    @Transactional(readOnly = true)
    open fun getBoardForGroups(category: String) = if (raceComponent.ascendingOrder.isValueTrue()) {
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
    open fun getFreestyleBoardForGroups() = if (raceComponent.ascendingOrder.isValueTrue()) {
        freestyleRaceRecordRepository.findAll()
            .map { submission ->
                FreestyleRaceEntryDto(
                    submission.groupId ?: 0,
                    submission.groupName,
                    submission.groupName,
                    submission.time
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
                    submission.time
                )
            }
            .sortedByDescending { it.time }
    }

    @Transactional(readOnly = true)
    open fun getViewForUsers(user: CmschUser?): RaceView {
        val board = getBoardForUsers(DEFAULT_CATEGORY, false)

        return if (user == null) {
            RaceView(
                raceComponent.title.getValue(),
                raceComponent.defaultCategoryDescription.getValue(),
                null, null, board)
        } else {
            val place = board.indexOfFirst { it.id == user.id }
            RaceView(
                raceComponent.title.getValue(),
                raceComponent.defaultCategoryDescription.getValue(),
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
            categoryName = raceComponent.freestyleCategoryName.getValue(),
            description = raceComponent.freestyleCategoryDescription.getValue(),
            board = board
        )
    }

    @Transactional(readOnly = true)
    open fun getBoardForUsers(category: String, fetchEmail: Boolean) = if (raceComponent.ascendingOrder.isValueTrue()) {
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
    open fun getFreestyleBoardForUsers() = if (raceComponent.ascendingOrder.isValueTrue()) {
        freestyleRaceRecordRepository.findAll()
            .map { submission ->
                FreestyleRaceEntryDto(
                    submission.userId ?: 0,
                    submission.userName,
                    submission.groupName,
                    submission.time,
                    submission.description
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
                    submission.description
                )
            }
            .sortedByDescending { it.time }
    }

}
