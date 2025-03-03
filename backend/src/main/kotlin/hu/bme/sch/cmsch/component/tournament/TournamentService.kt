package hu.bme.sch.cmsch.component.tournament

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.repository.GroupRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

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


    @Transactional
    fun teamRegister(tournamentId: Int, teamId: Int, teamName: String): Boolean {
        val tournament = tournamentRepository.findById(tournamentId)
        if (tournament.isEmpty) {
            return false
        }
        val group = groupRepository.findById(teamId)
        if (group.isEmpty) {
            return false
        }
        val participants = tournament.get().participants
        val parsed = mutableListOf<ParticipantDto>()
        parsed.addAll(participants.split("\n").map { objectMapper.readValue(it, ParticipantDto::class.java) })

        parsed.add(ParticipantDto(teamId, teamName))

        tournament.get().participants = parsed.joinToString("\n") { objectMapper.writeValueAsString(it) }
        tournament.get().participantCount = parsed.size

        tournamentRepository.save(tournament.get())
        return true
    }

}
