package hu.bme.sch.cmsch.component.tournament

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.config.OwnershipType
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.util.isAvailableForRole
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.resilience.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.ObjectMapper
import kotlin.jvm.optionals.getOrNull

@Service
@ConditionalOnBean(TournamentComponent::class)
class TournamentService(
    private val tournamentRepository: TournamentRepository,
    private val stageRepository: TournamentStageRepository,
    private val groupRepository: GroupRepository,
    private val tournamentComponent: TournamentComponent,
    private val matchRepository: TournamentMatchRepository,
    private val objectMapper: ObjectMapper,
    private val clock: TimeService
){
    @Transactional(readOnly = true)
    fun findAll(): List<TournamentEntity> {
        return tournamentRepository.findAll()
    }

    @Transactional(readOnly = true)
    fun findById(tournamentId: Int): TournamentEntity? {
        return tournamentRepository.findById(tournamentId).getOrNull()
    }


    @Transactional(readOnly = true)
    fun listAllTournaments(): List<TournamentPreviewView> {
        if (!tournamentComponent.showTournamentsAtAll) {
            return listOf()
        }

        val tournaments = tournamentRepository.findAll()
            .filter { it.visible }.map {
            TournamentPreviewView(
                it.id,
                it.title,
                it.description,
                it.location,
                it.status
            )
        }
        return tournaments
    }


    @Transactional(readOnly = false)
    fun showTournament(tournamentId: Int, user: CmschUser?): OptionalTournamentView? {
        val tournament = tournamentRepository.findById(tournamentId).getOrNull()
            ?: return null

        return if (tournament.visible && tournamentComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST)){
            OptionalTournamentView(true, mapTournament(tournament, user))
        } else {
            OptionalTournamentView(false, null)
        }
    }

    private fun mapTournament(tournament: TournamentEntity, user: CmschUser?): TournamentDetailedView {
        val participants = getParticipants(tournament)

        val now = clock.getTimeInSeconds()

        val playerId = when (tournament.participantType) {
            OwnershipType.GROUP -> user?.groupId
            OwnershipType.USER -> user?.id
        }
        val joined = participants.any { it.teamId == playerId }
        val joinChangable = tournament.joinable && tournament.joinDeadline > now &&
                (
                    (user?.role ?: RoleType.GUEST) >= RoleType.PRIVILEGED ||
                    (tournament.participantType == OwnershipType.USER && (user?.role ?: RoleType.GUEST) >= RoleType.BASIC)
                )


        val stages = stageRepository.findAllByTournamentId(tournament.id)
            .sortedBy { it.level }

        return TournamentDetailedView(
            TournamentWithParticipants(
                tournament.id,
                tournament.title,
                tournament.description,
                tournament.location,
                joinChangable && !joined && participants.size < tournament.participantMaxCount,
                joined,
                joinChangable && joined,
                tournament.joinDeadline,
                tournament.participantMaxCount,
                participants,
                tournament.status
            ), stages.map { KnockoutStageDetailedView(
                it.id,
                it.type,
                it.name,
                it.level,
                it.participantCount,
                it.participants.split("\n").filter { p -> p.isNotEmpty() }.map { p -> objectMapper.readValue(p, SeededParticipantDto::class.java) },
                it.nextRound,
                it.groupCount,
                it.status,
                matchRepository.findAllByStageId(it.id).map { MatchDto(
                    it.id,
                    it.gameId,
                    it.kickoffTime,
                    it.level,
                    it.location,
                    it.homeSeed,
                    it.awaySeed,
                    if(it.homeTeamId!=null) ParticipantDto(it.homeTeamId!!, it.homeTeamName) else null,
                    if(it.awayTeamId!=null) ParticipantDto(it.awayTeamId!!, it.awayTeamName) else null,
                    it.homeTeamScore,
                    it.awayTeamScore,
                    it.status
                ) }
            ) })
    }

    @Transactional(readOnly = true)
    fun getParticipants(tournamentId: Int): List<ParticipantDto> {
        val tournament = tournamentRepository.findById(tournamentId)
        if (tournament.isEmpty || tournament.get().participants.isEmpty()) {
            return emptyList()
        }
        return tournament.get().participants.split("\n")
            .filter { it.isNotEmpty() }
            .map { objectMapper.readValue(it, ParticipantDto::class.java) }
    }

    fun getParticipants(tournament: TournamentEntity): List<ParticipantDto> {
        if (tournament.participants.isEmpty()) {
            return emptyList()
        }
        return tournament.participants.split("\n")
            .filter { it.isNotEmpty() }
            .map { objectMapper.readValue(it, ParticipantDto::class.java) }
    }

    @Transactional(readOnly = true)
    fun getResultsInStage(tournamentId: Int, stageId: Int): List<StageResultDto> {
        val stage = stageRepository.findById(stageId)
        if (stage.isEmpty || stage.get().tournamentId != tournamentId) {
            return emptyList()
        }
        return stage.get().participants.split("\n").map { objectMapper.readValue(it, StageResultDto::class.java) }
    }

    fun getAggregatedMatchesByTournamentId(): List<MatchGroupDto> {
        val tournaments = findAll().associateBy { it.id }
        val stages = stageRepository.findAll().associateBy { it.id }
        val aggregatedByStageId = matchRepository.findAllAggregated()
        val aggregated = mutableMapOf<Int, Long>()
        for(aggregatedStage in aggregatedByStageId) {
            aggregated[stages[aggregatedStage.stageId]!!.tournamentId] = aggregated.getOrDefault(stages[aggregatedStage.stageId]!!.tournamentId, 0) + aggregatedStage.matchCount
        }
        return aggregated.map {
            MatchGroupDto(
                it.key,
                tournaments[it.key]?.title ?:"",
                tournaments[it.key]?.location ?:"",
                it.value.toInt()
            )
        }.sortedByDescending { it.matchCount }
    }

    @Retryable
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun register(tournamentId: Int, user: CmschUser): TournamentJoinStatus {
        val now = clock.getTimeInSeconds()
        val tournament = findById(tournamentId)
            ?: return TournamentJoinStatus.TOURNAMENT_NOT_FOUND
        if (tournament.joinDeadline < now) return TournamentJoinStatus.NOT_JOINABLE
        if (!tournament.joinable) return TournamentJoinStatus.NOT_JOINABLE
        return when (tournament.participantType) {
            OwnershipType.GROUP -> teamRegister(tournament, user)
            OwnershipType.USER -> userRegister(tournament, user)
        }
    }


    fun teamRegister(tournament: TournamentEntity, user: CmschUser): TournamentJoinStatus {
        val groupId = user.groupId
            ?: return TournamentJoinStatus.INSUFFICIENT_PERMISSIONS
        val team = groupRepository.findById(groupId).getOrNull()
            ?: return TournamentJoinStatus.INSUFFICIENT_PERMISSIONS
        if (user.role < RoleType.PRIVILEGED) return TournamentJoinStatus.INSUFFICIENT_PERMISSIONS

        val parsed = getParticipants(tournament).toMutableList()
        if (parsed.any { it.teamId == groupId }) {
            return TournamentJoinStatus.ALREADY_JOINED
        }
        if (parsed.size >= tournament.participantMaxCount && tournament.participantMaxCount != -1) {
            return TournamentJoinStatus.NOT_JOINABLE
        }
        parsed.add(ParticipantDto(groupId, team.name))

        tournament.participants = parsed.joinToString("\n") { objectMapper.writeValueAsString(it) }
        tournament.participantCount = parsed.size

        tournamentRepository.save(tournament)
        return TournamentJoinStatus.OK
    }

    @Retryable
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun userRegister(tournament: TournamentEntity, user: CmschUser): TournamentJoinStatus {
        if (user.role < RoleType.BASIC) return TournamentJoinStatus.INSUFFICIENT_PERMISSIONS
        val parsed = getParticipants(tournament).toMutableList()
        if (parsed.any { it.teamId == user.id }) {
            return TournamentJoinStatus.ALREADY_JOINED
        }
        if (parsed.size >= tournament.participantMaxCount && tournament.participantMaxCount != -1) {
            return TournamentJoinStatus.NOT_JOINABLE
        }
        parsed.add(ParticipantDto(user.id, user.userName))

        tournament.participants = parsed.joinToString("\n") { objectMapper.writeValueAsString(it) }
        tournament.participantCount = parsed.size

        tournamentRepository.save(tournament)
        return TournamentJoinStatus.OK
    }

    @Retryable
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun unregister(tournamentId: Int, user: CmschUser): TournamentCancelStatus {
        val now = clock.getTimeInSeconds()
        val tournament = findById(tournamentId)
            ?: return TournamentCancelStatus.TOURNAMENT_NOT_FOUND
        if (tournament.joinDeadline < now) return TournamentCancelStatus.NOT_CANCELABLE
        return when (tournament.participantType) {
            OwnershipType.GROUP -> teamUnregister(tournament, user)
            OwnershipType.USER -> userUnregister(tournament, user)
        }
    }


    fun teamUnregister(tournament: TournamentEntity, user: CmschUser): TournamentCancelStatus {
        val groupId = user.groupId
            ?: return TournamentCancelStatus.INSUFFICIENT_PERMISSIONS
        val team = groupRepository.findById(groupId).getOrNull()
            ?: return TournamentCancelStatus.INSUFFICIENT_PERMISSIONS
        if (user.role < RoleType.PRIVILEGED) return TournamentCancelStatus.INSUFFICIENT_PERMISSIONS

        val parsed = getParticipants(tournament).toMutableList()
        if (parsed.none { it.teamId == groupId }) {
            return TournamentCancelStatus.NOT_PLAYING
        }
        parsed.removeIf { it.teamId == groupId }

        tournament.participants = parsed.joinToString("\n") { objectMapper.writeValueAsString(it) }
        tournament.participantCount = parsed.size

        tournamentRepository.save(tournament)
        return TournamentCancelStatus.OK
    }

    fun userUnregister(tournament: TournamentEntity, user: CmschUser): TournamentCancelStatus {
        if (user.role < RoleType.BASIC) return TournamentCancelStatus.INSUFFICIENT_PERMISSIONS
        val parsed = getParticipants(tournament).toMutableList()
        if (parsed.none { it.teamId == user.id }) {
            return TournamentCancelStatus.NOT_PLAYING
        }
        parsed.removeIf { it.teamId == user.id }

        tournament.participants = parsed.joinToString("\n") { objectMapper.writeValueAsString(it) }
        tournament.participantCount = parsed.size

        tournamentRepository.save(tournament)
        return TournamentCancelStatus.OK
    }

    @Transactional
    fun deleteStagesForTournament(tournament: TournamentEntity) {
        stageRepository.deleteAllByTournamentId(tournament.id)
    }
}
