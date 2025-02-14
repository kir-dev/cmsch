package hu.bme.sch.cmsch.component.tournament

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.pow

@Service
@ConditionalOnBean(TournamentComponent::class)
class KnockoutStageService(
    private val matchRepository: TournamentMatchRepository
): ApplicationContextAware {

    @Transactional
    fun createMatchesForStage(stage: KnockoutStageEntity) {
        val secondRoundGames = 2.0.pow(stage.rounds().toDouble() - 2).toInt()
        val firstRoundGames = stage.matches() - 2 * secondRoundGames + 1
        val byeWeekParticipants = stage.participantCount - firstRoundGames * 2

    }

    companion object {
        private var applicationContext: ApplicationContext? = null

        fun getBean(): KnockoutStageService = applicationContext?.getBean(KnockoutStageService::class.java)
            ?: throw IllegalStateException("Application context is not initialized.")
    }

    override fun setApplicationContext(context: ApplicationContext) {
        applicationContext = context
    }
}