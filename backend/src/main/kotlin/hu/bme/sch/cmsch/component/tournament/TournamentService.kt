package hu.bme.sch.cmsch.component.tournament

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.repository.GroupRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.ApplicationContext
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.sql.SQLException
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
@ConditionalOnBean(TournamentComponent::class)
open class TournamentService(
    private val tournamentRepository: TournamentRepository,
    private val stageRepository: KnockoutStageRepository,
    private val groupRepository: GroupRepository,
    private val tournamentComponent: TournamentComponent,
    private val objectMapper: ObjectMapper
) {
    @Transactional(readOnly = true)
    open fun findAll(): List<TournamentEntity> {
        return tournamentRepository.findAll()
    }

    @Transactional(readOnly = true)
    fun findById(tournamentId: Int): Optional<TournamentEntity> {
        return tournamentRepository.findById(tournamentId)
    }

    @Transactional(readOnly = true)
    fun getParticipants(tournamentId: Int): List<ParticipantDto> {
        val tournament = tournamentRepository.findById(tournamentId)
        if (tournament.isEmpty || tournament.get().participants.isEmpty()) {
            return emptyList()
        }
        return tournament.get().participants.split("\n").map { objectMapper.readValue(it, ParticipantDto::class.java) }
    }

    @Transactional(readOnly = true)
    fun getResultsInStage(tournamentId: Int, stageId: Int): List<StageResultDto> {
        val stage = stageRepository.findById(stageId)
        if (stage.isEmpty || stage.get().tournamentId != tournamentId) {
            return emptyList()
        }
        return stage.get().participants.split("\n").map { objectMapper.readValue(it, StageResultDto::class.java) }
    }


    @Transactional(readOnly = true)
    fun getResultsFromLevel(tournamentId: Int, level: Int): List<StageResultDto> {
        if (level < 1) {
            return getParticipants(tournamentId).map { StageResultDto(it.teamId, it.teamName) }
        }
        val stages = stageRepository.findAllByTournamentIdAndLevel(tournamentId, level)
        if (stages.isEmpty()) {
            return emptyList()
        }
        return stages.flatMap { it.participants.split("\n").map { objectMapper.readValue(it, StageResultDto::class.java) } }.sortedWith(
            compareBy(
                { it.position },
                { it.points },
                { it.won },
                { it.goalDifference },
                { it.goalsFor }
            )
        )
    }

    @Retryable(value = [ SQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun teamRegister(tournamentId: Int, user: CmschUser): TournamentJoinStatus {
        val tournament = tournamentRepository.findById(tournamentId).getOrNull()
            ?: return TournamentJoinStatus.TOURNAMENT_NOT_FOUND

        val groupId = user.groupId
            ?: return TournamentJoinStatus.INSUFFICIENT_PERMISSIONS
        val team = groupRepository.findById(groupId).getOrNull()
            ?: return TournamentJoinStatus.INSUFFICIENT_PERMISSIONS

        val participants = tournament.participants
        val parsed = mutableListOf<ParticipantDto>()
        parsed.addAll(participants.split("\n").map { objectMapper.readValue(it, ParticipantDto::class.java) })
        if (parsed.any { it.teamId == groupId }) {
            return TournamentJoinStatus.ALREADY_JOINED
        }

        parsed.add(ParticipantDto(groupId, team.name))

        tournament.participants = parsed.joinToString("\n") { objectMapper.writeValueAsString(it) }
        tournament.participantCount = parsed.size

        tournamentRepository.save(tournament)
        return TournamentJoinStatus.OK
    }

    @Retryable(value = [ SQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun userRegister(tournamentId: Int, user: CmschUser): TournamentJoinStatus {
        val tournament = tournamentRepository.findById(tournamentId).getOrNull()
            ?: return TournamentJoinStatus.TOURNAMENT_NOT_FOUND

        val participants = tournament.participants
        val parsed = mutableListOf<ParticipantDto>()
        parsed.addAll(participants.split("\n").map { objectMapper.readValue(it, ParticipantDto::class.java) })
        if (parsed.any { it.teamId == user.id }) {
            return TournamentJoinStatus.ALREADY_JOINED
        }
        parsed.add(ParticipantDto(user.id, user.userName))

        tournament.participants = parsed.joinToString("\n") { objectMapper.writeValueAsString(it) }
        tournament.participantCount = parsed.size

        tournamentRepository.save(tournament)
        return TournamentJoinStatus.OK
    }

    companion object{
        private var applicationContext: ApplicationContext? = null
        fun getBean(): TournamentService = applicationContext?.getBean(TournamentService::class.java)
            ?: throw IllegalStateException("TournamentService is not initialized. Make sure TournamentComponent is enabled and application context is set.")
    }

    @Transactional
    fun deleteStagesForTournament(tournament: TournamentEntity) {
        stageRepository.deleteAllByTournamentId(tournament.id)
    }

}
