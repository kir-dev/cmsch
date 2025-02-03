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
    open fun findTournamentById(id: Int) : Optional<TournamentEntity> {
        return tournamentRepository.findById(id)
    }

    @Transactional(readOnly = true)
    open fun findStagesByTournamentId(tournamentId: Int) : List<KnockoutStageEntity> {
        return stageRepository.findAllByTournamentId(tournamentId)
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
        val parsed = mutableListOf(ParticipantDto(teamId, teamName))
        parsed.addAll(participants.split("\n").map { objectMapper.readValue(it, ParticipantDto::class.java) })

        parsed.add(ParticipantDto(teamId, teamName))

        tournament.get().participants = parsed.joinToString("\n") { objectMapper.writeValueAsString(it) }
        tournament.get().participantCount = parsed.size

        tournamentRepository.save(tournament.get())
        return true
    }


}
