package hu.bme.sch.cmsch.component.tournament

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.config.OwnershipType
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.util.isAvailableForRole
import org.hibernate.internal.util.collections.CollectionHelper.listOf
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.http.ResponseEntity
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.sql.SQLException
import java.util.*
import kotlin.collections.listOf
import kotlin.jvm.optionals.getOrNull

@Service
@ConditionalOnBean(TournamentComponent::class)
open class TournamentService(
    private val tournamentRepository: TournamentRepository,
    private val stageRepository: KnockoutStageRepository,
    private val groupRepository: GroupRepository,
    private val tournamentComponent: TournamentComponent,
    private val objectMapper: ObjectMapper,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val matchRepository: TournamentMatchRepository
): ApplicationContextAware {
    @Transactional(readOnly = true)
    open fun findAll(): List<TournamentEntity> {
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

        var tournaments = tournamentRepository.findAll()
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
        val participants = if (tournament.participants != "") tournament.participants.split("\n").map { objectMapper.readValue(it, ParticipantDto::class.java) } else listOf()

        val playerId = when (startupPropertyConfig.tournamentOwnershipMode){
            OwnershipType.GROUP -> user?.groupId ?: null
            OwnershipType.USER -> user?.id ?: null
        }
        val isJoined = participants.any { it.teamId == playerId }
        val joinEnabled = tournament.joinable && !isJoined &&
            ((user?.role ?: RoleType.GUEST) >= RoleType.PRIVILEGED ||
            (startupPropertyConfig.tournamentOwnershipMode == OwnershipType.USER && (user?.role ?: RoleType.GUEST) >= RoleType.BASIC))

        val stages = stageRepository.findAllByTournamentId(tournament.id)
            .sortedBy { it.level }

        return TournamentDetailedView(
            TournamentWithParticipants(
                tournament.id,
                tournament.title,
                tournament.description,
                tournament.location,
                joinEnabled,
                isJoined,
                tournament.participantCount,
                getParticipants(tournament.id),
                tournament.status
            ), stages.map { KnockoutStageDetailedView(
                it.id,
                it.name,
                it.level,
                it.participantCount,
                it.nextRound,
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
        if(participants != "") parsed.addAll(participants.split("\n").map { objectMapper.readValue(it, ParticipantDto::class.java) })
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

    override fun setApplicationContext(context: ApplicationContext) {
        applicationContext = context
    }

    @Transactional
    fun deleteStagesForTournament(tournament: TournamentEntity) {
        stageRepository.deleteAllByTournamentId(tournament.id)
    }

}
