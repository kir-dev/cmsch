package hu.bme.sch.cmsch.component.tournament

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.service.StaffPermissions
import hu.bme.sch.cmsch.service.TimeService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import kotlin.collections.map
import kotlin.jvm.optionals.getOrNull
import kotlin.math.pow
import kotlin.random.Random

@Service
@ConditionalOnBean(TournamentComponent::class)
class KnockoutStageService(
    private val stageRepository: KnockoutStageRepository,
    private val tournamentService: TournamentService,
    private val clock: TimeService,
    private val matchRepository: TournamentMatchRepository,
    val objectMapper: ObjectMapper
): ApplicationContextAware {

    @Transactional
    fun createMatchesForStage(stage: KnockoutStageEntity) {
        val firstRound = 2.0.pow(stage.rounds() - 1).toInt()
        val gameCount = 2*firstRound
        val byeSlotCount = 2*firstRound-stage.participantCount

        val byeGames = (1..firstRound).asIterable().shuffled().subList(0,byeSlotCount).sorted()
        val matches = mutableListOf<TournamentMatchEntity>()

        var j = 1; var k = 0
        for (i in 1 until firstRound+1){
            matches.add(TournamentMatchEntity(
                gameId = i,
                stageId = stage.id,
                level = 1,
                homeSeed = j++,
                awaySeed = if (byeGames.size>k && byeGames[k] == i) {
                    k++
                    0
                } else {
                    j++
                }
            ))
        }
        var roundMatches = firstRound; j = 1; k = 2
        for (i in firstRound+1 until gameCount){
            matches.add(TournamentMatchEntity(
                gameId = i,
                stageId = stage.id,
                level = k,
                homeSeed = -(j++),
                awaySeed = -(j++)
            ))
            if (j == roundMatches+1) {
                k++
                roundMatches += roundMatches/2
            }
        }
        //calculateTeamsFromSeeds(stage, matches)
        matchRepository.saveAll(matches)
    }

    @Transactional
    fun setInitialSeeds(stage: KnockoutStageEntity, seeds: List<StageResultDto>, user: CmschUser) {
        if (StaffPermissions.PERMISSION_SET_SEEDS.validate(user).not()){
            throw IllegalArgumentException("User does not have permission to set seeds.")
        }
        if (seeds.size != stage.participantCount) {
            throw IllegalArgumentException("Number of seeds must match the participant count of the stage.")
        }

        stage.participants = seeds.joinToString("\n") { objectMapper.writeValueAsString(it) }
        stageRepository.save(stage)
    }

    fun transferTeamsForStage(stage: KnockoutStageEntity): String {
        val teamSeeds = (1..stage.participantCount).asIterable().toList()
        var participants = getResultsForStage(stage)
        if (participants.size >= stage.participantCount) {
            participants = participants.subList(0, stage.participantCount).map { StageResultDto(it.teamId, it.teamName) }
        }
        for (i in 0 until stage.participantCount) {
            participants[i].initialSeed = teamSeeds[i]
        }
        stage.participants = participants.joinToString("\n") { objectMapper.writeValueAsString(it) }
        return stage.participants
    }

    @Transactional(readOnly = true)
    fun getResultsForStage(stage: KnockoutStageEntity): List<StageResultDto> {
        if (stage.level <= 1) {
            return tournamentService.getParticipants(stage.tournamentId)
                .mapIndexed { index, participant ->
                    StageResultDto(
                        participant.teamId,
                        participant.teamName,
                        stage.id,
                        initialSeed = index + 1,
                        detailedStats = Optional.empty()
                    )
                }
        }
        val prevStage = stageRepository.findByTournamentIdAndLevel(stage.tournamentId, stage.level - 1).getOrNull()?:return emptyList()
        if (prevStage.participants == "") {
            return emptyList()
        }
        return prevStage.participants.split("\n").map { objectMapper.readValue(it, StageResultDto::class.java) }
    }


    fun calculateTeamsFromSeeds(stage: KnockoutStageEntity) {
        val actualMatches = matchRepository.findAllByStageId(stage.id)
        val seeds = getSeeds(stage)
        for (match in actualMatches) {
            val homeTeam = seeds[match.homeSeed]
            val awayTeam = seeds[match.awaySeed]
            match.homeTeamId = homeTeam?.teamId
            match.homeTeamName = homeTeam?.teamName ?: "TBD"
            match.awayTeamId = awayTeam?.teamId
            match.awayTeamName = awayTeam?.teamName ?: "TBD"
        }
        matchRepository.saveAll(actualMatches)
    }


    fun setSeeds(stage: KnockoutStageEntity): String {
        val matches = matchRepository.findAllByStageId(stage.id)
        val seeds = mutableListOf<SeededParticipantDto>()
        seeds.addAll(
            stage.participants.split("\n").map { objectMapper.readValue(it, StageResultDto::class.java) }
            .map { SeededParticipantDto(
                seed = it.initialSeed,
                teamId = it.teamId,
                teamName = it.teamName
            ) } )
        seeds.add(SeededParticipantDto(
            seed = 0,
            teamId = 0,
            teamName = "ByeGame"
        )) // Placeholder for bye slots))
        for (match in matches) {
            val winner = match.winner()
            if (winner != null) {
                seeds.add(winner.let { SeededParticipantDto(
                    seed = -match.gameId,
                    teamId = it.teamId,
                    teamName = it.teamName
                ) })
            }
        }
        return seeds.joinToString("\n") { objectMapper.writeValueAsString(it) }
    }

    fun getSeeds(stage: KnockoutStageEntity): Map<Int, ParticipantDto>{
        val seeds = mutableMapOf<Int, ParticipantDto>()
        if (stage.seeds == ""){
            return emptyMap()
        }
        stage.seeds.split("\n").forEach {
            val participant = objectMapper.readValue(it, SeededParticipantDto::class.java)
            seeds[participant.seed] = ParticipantDto(
                teamId = participant.teamId,
                teamName = participant.teamName
            )
        }
        return seeds
    }

    fun findById(id: Int): KnockoutStageEntity? {
        //return stageRepository.findById(id).orElseThrow { IllegalArgumentException("No stage found with id $id") }
        return stageRepository.findById(id).getOrNull()
    }

    fun getParticipants(stageId: Int): List<StageResultDto> {
        val stage = findById(stageId)
        if (stage == null || stage.participants.isEmpty()) {
            return emptyList()
        }
        return stage!!.participants.split("\n").map { objectMapper.readValue(it, StageResultDto::class.java) }
    }


    fun getMatchesByStageTournamentId(tournamentId: Int): List<TournamentMatchEntity> {
        val stages = stageRepository.findAllByTournamentId(tournamentId)
        val matches = mutableListOf<TournamentMatchEntity>()
        for (stage in stages) {
            matches.addAll(matchRepository.findAllByStageId(stage.id))
        }
        return matches
    }

    @Transactional
    fun deleteMatchesForStage(stage: KnockoutStageEntity) {
        matchRepository.deleteAllByStageId(stage.id)
    }

    companion object {
        private var applicationContext: ApplicationContext? = null

        fun getBean(): KnockoutStageService = applicationContext?.getBean(KnockoutStageService::class.java)
            ?: throw IllegalStateException("Application context is not initialized.")
    }

    override fun setApplicationContext(context: ApplicationContext) {
        applicationContext = context
    }

    fun findStagesByTournamentId(tournamentId: Int): List<KnockoutStageEntity> {
        return stageRepository.findAllByTournamentId(tournamentId)
    }

    fun findMatchesByStageId(stageId: Int): List<TournamentMatchEntity> {
        return matchRepository.findAllByStageId(stageId)
    }

    @Scheduled(fixedRate = 1000 * 60 * 10)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun updateSeeds(){
        val stages = stageRepository.findAll()
        for (stage in stages) {
            if (stage.status == StageStatus.ONGOING) {
                val matches = matchRepository.findAllByStageId(stage.id)
                if (matches.isEmpty()) {
                    continue
                }
                val updatedSeeds = setSeeds(stage)
                stage.seeds = updatedSeeds
                calculateTeamsFromSeeds(stage)
                stageRepository.save(stage)
            }
        }
    }
}