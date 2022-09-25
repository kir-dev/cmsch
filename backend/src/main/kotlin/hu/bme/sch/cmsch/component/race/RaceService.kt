package hu.bme.sch.cmsch.component.race

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.UserRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.Throws

const val DEFAULT_CATEGORY = ""

@Suppress("RedundantModalityModifier") // Spring transactional proxy requires it not to be final
@Service
@ConditionalOnBean(RaceComponent::class)
open class RaceService(
    private val raceRecordRepository: RaceRecordRepository,
    private val raceCategoryRepository: RaceCategoryRepository,
    private val raceComponent: RaceComponent,
    private val userRepository: UserRepository,
) {

    @Transactional(readOnly = true)
    open fun getViewForGroups(user: UserEntity?): RaceView {
        val board = getBoardForGroups(DEFAULT_CATEGORY)

        return if (user == null) {
            RaceView(
                raceComponent.title.getValue(),
                raceComponent.defaultCategoryDescription.getValue(),
                null, null, board)
        } else {
            val groupId = user.group?.id ?: -1
            val place = board.indexOfFirst { it.id == groupId }
            RaceView(
                raceComponent.title.getValue(),
                raceComponent.defaultCategoryDescription.getValue(),
                if (place < 0) null else (place + 1),
                board.find { it.id == groupId }?.time,
                board
            )
        }
    }

    @Throws(NoSuchElementException::class)
    @Transactional(readOnly = true)
    open fun getViewForGroups(user: UserEntity?, slug: String): RaceView {
        val category = raceCategoryRepository.findByVisibleTrueAndSlug(slug).orElse(null)
            ?: throw NoSuchElementException()
        val board = getBoardForGroups(category.slug)

        return if (user == null) {
            RaceView(category.name, category.description, null, null, board)
        } else {
            val groupId = user.group?.id ?: -1
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

}
