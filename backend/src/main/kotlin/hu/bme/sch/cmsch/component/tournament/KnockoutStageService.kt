package hu.bme.sch.cmsch.component.tournament

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.pow
import kotlin.random.Random

@Service
@ConditionalOnBean(TournamentComponent::class)
class KnockoutStageService(
    private val tournamentService: TournamentService,
    private val stageRepository: KnockoutStageRepository,
    private val matchRepository: TournamentMatchRepository,
    private val objectMapper: ObjectMapper
): ApplicationContextAware {

    @Transactional
    fun createMatchesForStage(stage: KnockoutStageEntity) {
        val firstRound = 2.0.pow(stage.rounds() - 1).toInt()
        val gameCount = 2*firstRound
        val byeSlotCount = 2*firstRound-stage.participantCount

        val byeGames = (1..firstRound).asIterable().shuffled().subList(0,byeSlotCount).sorted()
        val matches = mutableListOf<TournamentMatchEntity>()

        var j = 0; var k = 0
        for (i in 0 until firstRound){
            matches.add(TournamentMatchEntity(
                gameId = i,
                stageId = stage.id,
                level = 1,
                homeSeed = j++,
                awaySeed = if (byeGames[k] == i) {
                    k++
                    0
                } else {
                    j++
                }
            ))
        }
        var roundMatches = firstRound; j = 0; k = 2
        for (i in firstRound until gameCount){
            matches.add(TournamentMatchEntity(
                gameId = i,
                stageId = stage.id,
                level = k,
                homeSeed = -(j++),
                awaySeed = -(j++)
            ))
            if (j == roundMatches) {
                j = 0
                k++
                roundMatches /= 2
            }
        }

        for (match in matches) {
            matchRepository.save(match)
        }

        //placeholder seeding for testing
        val teamSeeds = (1..stage.participantCount).asIterable().shuffled().toList()
        val participants = tournamentService.getResultsFromLevel(stage.tournamentId, stage.level - 1).subList(0, stage.participantCount)
            .map { StageResultDto(it.teamId, it.teamName) }
        for (i in 0 until stage.participantCount) {
            participants[i].seed = teamSeeds[i]
        }
        stage.participants = participants.joinToString("\n") { objectMapper.writeValueAsString(it) }
        stageRepository.save(stage)
    }

    @Transactional
    fun calculateTeamsFromSeeds(stage: KnockoutStageEntity) {
        val matches = matchRepository.findAllByStageId(stage.id)
        val teams = stage.participants.split("\n").map { objectMapper.readValue(it, StageResultDto::class.java) }
        for (match in matches){
            val homeTeam = teams.find { it.seed == match.homeSeed }
            val awayTeam = teams.find { it.seed == match.awaySeed }
            match.homeTeamId = homeTeam?.teamId
            match.homeTeamName = homeTeam?.teamName ?: "TBD"
            match.awayTeamId = awayTeam?.teamId
            match.awayTeamName = awayTeam?.teamName ?: "TBD"
            matchRepository.save(match)
        }
    }

    fun getTournamentService(): TournamentService = tournamentService

    fun findById(id: Int): KnockoutStageEntity {
        return stageRepository.findById(id).orElseThrow { IllegalArgumentException("No stage found with id $id") }
    }

    fun getParticipants(stageId: Int): List<StageResultDto> {
        val stage = findById(stageId)
        return stage.participants.split("\n").map { objectMapper.readValue(it, StageResultDto::class.java) }
    }


    fun getMatchesByStageTournamentId(tournamentId: Int): List<TournamentMatchEntity> {
        val stages = stageRepository.findAllByTournamentId(tournamentId)
        val matches = mutableListOf<TournamentMatchEntity>()
        for (stage in stages) {
            matches.addAll(matchRepository.findAllByStageId(stage.id))
        }
        return matches
    }

    fun getAggregatedMatchesByTournamentId(): List<MatchGroupDto> {
        val tournaments = tournamentService.findAll().associateBy { it.id }
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