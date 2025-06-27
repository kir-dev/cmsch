package hu.bme.sch.cmsch.component.tournament

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import kotlin.jvm.optionals.getOrNull
import kotlin.math.pow
import kotlin.random.Random

@Service
@ConditionalOnBean(TournamentComponent::class)
class KnockoutStageService(
    private val stageRepository: KnockoutStageRepository,
    private val tournamentService: TournamentService,
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
        for (i in firstRound+1 until gameCount+1){
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
        matchRepository.saveAll(matches)
    }

    @Transactional
    fun updateSeedsForStage(stageId: Int, seeds: List<SeededParticipantDto>) {
        val stage = findById(stageId) ?: throw IllegalArgumentException("No stage found with id $stageId")
        if (seeds.size != stage.participantCount) {

        }

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun transferTeamsForStage(stage: KnockoutStageEntity){
        val teamSeeds = (1..stage.participantCount).asIterable().shuffled().toList()
        var participants = getResultsForStage(stage)
        if (participants.size >= stage.participantCount) {
            participants = participants.subList(0, stage.participantCount).map { StageResultDto(it.teamId, it.teamName) }
        }
        for (i in 0 until stage.participantCount) {
            participants[i].initialSeed = teamSeeds[i]
        }
        stage.participants = participants.joinToString("\n") { objectMapper.writeValueAsString(it) }
        stageRepository.save(stage)
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
        val stages = stageRepository.findAllByTournamentIdAndLevel(stage.tournamentId, stage.level - 1)
        if (stages.isEmpty()) {
            return emptyList()
        }
        return stages.flatMap { it.participants.split("\n")
            .map { objectMapper.readValue(it, StageResultDto::class.java) } }
            .sorted()
    }

    @Transactional
    fun calculateTeamsFromSeeds(stage: KnockoutStageEntity) {
        val matches = matchRepository.findAllByStageId(stage.id)
        val seeds = getSeeds(stage)
        for (match in matches){
            val homeTeam = seeds[match.homeSeed]
            val awayTeam = seeds[match.awaySeed]
            match.homeTeamId = homeTeam?.teamId
            match.homeTeamName = homeTeam?.teamName ?: "TBD"
            match.awayTeamId = awayTeam?.teamId
            match.awayTeamName = awayTeam?.teamName ?: "TBD"
            matchRepository.save(match)
        }
    }

    fun getSeeds(stage: KnockoutStageEntity): Map<Int, ParticipantDto>{
        val seeds = mutableMapOf<Int, ParticipantDto>()
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
}