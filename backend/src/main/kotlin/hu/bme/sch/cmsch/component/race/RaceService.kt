package hu.bme.sch.cmsch.component.race

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.UserEntity
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Suppress("RedundantModalityModifier") // Spring transactional proxy requires it not to be final
@Service
@ConditionalOnBean(RaceComponent::class)
open class RaceService(
    private val raceRecordRepository: RaceRecordRepository,
    private val raceComponent: RaceComponent
) {

    @Transactional(readOnly = true)
    open fun getViewForGroups(user: UserEntity?): RaceView {
        val board = getBoardForGroups()

        return if (user == null) {
            RaceView(null, null, board)
        } else {
            val groupId = user.group?.id ?: -1
            val place = board.indexOfFirst { it.id == groupId }
            RaceView(
                if (place < 0) null else (place + 1),
                board.find { it.id == groupId }?.time,
                board
            )
        }
    }

    @Transactional(readOnly = true)
    open fun getBoardForGroups() = if (raceComponent.ascendingOrder.isValueTrue()) {
        raceRecordRepository.findAll()
            .groupBy { it.groupId }
            .map { submission ->
                RaceEntryDto(
                    submission.key ?: 0,
                    submission.value.first().groupName,
                    submission.value.first().groupName,
                    submission.value.minOf { it.time }
                )
            }
            .sortedBy { it.time }
    } else {
        raceRecordRepository.findAll()
            .groupBy { it.groupId }
            .map { submission ->
                RaceEntryDto(
                    submission.key ?: 0,
                    submission.value.first().groupName,
                    submission.value.first().groupName,
                    submission.value.maxOf { it.time }
                )
            }
            .sortedByDescending { it.time }
    }

    @Transactional(readOnly = true)
    open fun getViewForUsers(user: CmschUser?): RaceView {
        val board = getBoardForUsers()

        return if (user == null) {
            RaceView(null, null, board)
        } else {
            val place = board.indexOfFirst { it.id == user.id }
            RaceView(
                if (place < 0) null else (place + 1),
                board.find { it.id == user.id }?.time,
                board
            )
        }
    }

    @Transactional(readOnly = true)
    open fun getBoardForUsers() = if (raceComponent.ascendingOrder.isValueTrue()) {
        raceRecordRepository.findAll()
            .groupBy { it.userId }
            .map { submission ->
                RaceEntryDto(
                    submission.key ?: 0,
                    submission.value.first().userName,
                    submission.value.first().groupName,
                    submission.value.minOf { it.time }
                )
            }
            .sortedBy { it.time }
    } else {
        raceRecordRepository.findAll()
            .groupBy { it.userId }
            .map { submission ->
                RaceEntryDto(
                    submission.key ?: 0,
                    submission.value.first().userName,
                    submission.value.first().groupName,
                    submission.value.maxOf { it.time }
                )
            }
            .sortedByDescending { it.time }
    }

}
