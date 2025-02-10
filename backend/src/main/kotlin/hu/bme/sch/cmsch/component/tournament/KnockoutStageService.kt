package hu.bme.sch.cmsch.component.tournament

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(TournamentComponent::class)
class KnockoutStageService(
    private val matchRepository: TournamentMatchRepository
): ApplicationContextAware {

    @Transactional
    fun createMatchesForStage(stage: KnockoutStageEntity) {
        for (i in 1..stage.matches()) {
            val match = TournamentMatchEntity(
                stage = stage,
                gameId = i,

            )
            matchRepository.save(match)
        }
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