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
        val secondRoundGames = 2.0.pow(stage.rounds().toDouble() - 2).toInt()
        val firstRoundGames = stage.matches() - 2 * secondRoundGames + 1
        val byeWeekParticipantCount = stage.participantCount - firstRoundGames * 2

        val seedSpots = (1..2*secondRoundGames).asIterable().shuffled()
            .subList(0, byeWeekParticipantCount) //TODO bye week participant count wrong
        // TODO do better seeding, this is just random stuff
        val matches = mutableListOf<TournamentMatchEntity>()

        for (i in 0 until firstRoundGames) {
            matches.add(TournamentMatchEntity(
                gameId = i + 1,
                stageId = stage.id,
                homeSeed = i + 1 + byeWeekParticipantCount,
                awaySeed = i + 2 + byeWeekParticipantCount
            ))
        }
        var j = 1; var k = 1
        for (i in 1 until secondRoundGames + 1) {
            matches.add(TournamentMatchEntity(
                gameId = firstRoundGames + j,
                stageId = stage.id,
                homeSeed = if(seedSpots.contains(2*i-1)) j++ else -(k++),
                awaySeed = if(seedSpots.contains(2*i)) j++ else -(k++)
            ))
        }
        for (i in firstRoundGames + secondRoundGames until stage.matches()) {
            matches.add(TournamentMatchEntity(
                gameId = i + 1,
                stageId = stage.id,
                homeSeed = -(k++),
                awaySeed = -(k++)
            ))
        }
        for (match in matches) {
            matchRepository.save(match)
        }

        val teamSeeds = (1..stage.participantCount).asIterable().shuffled().toList()
        val participants = tournamentService.getResultsFromLevel(stage.tournamentId, stage.level - 1).subList(0, stage.participantCount)
            .map { StageResultDto(stage.id, stage.name, it.teamId, it.teamName) }
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