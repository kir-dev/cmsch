package hu.bme.sch.cmsch.component.tournament

import hu.bme.sch.cmsch.repository.GroupRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(TournamentComponent::class)
open class TournamentService(
    private val tournamentRepository: TournamentRepository,
    private val groupRepository: GroupRepository,
    private val tournamentComponent: TournamentComponent
) {
    @Transactional(readOnly = true)
    open fun findAll(): List<TournamentEntity> {
        return tournamentRepository.findAll()
    }
}
