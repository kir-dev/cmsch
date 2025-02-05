package hu.bme.sch.cmsch.component.tournament

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(TournamentComponent::class)
class KnockoutStageService(
    private val matchRepository: TournamentMatchRepository
) {

    @Transactional
    fun createMatchesForStage(stage: KnockoutStageEntity) {
        for (i in 1..stage.matches()) {
            val match = TournamentMatchEntity(
                stageId = stage.id,
                id = i,
                // Set other necessary fields TODO
            )
            matchRepository.save(match)
        }
    }
}