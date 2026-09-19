package hu.bme.sch.cmsch.component.bounty

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@ConditionalOnBean(BountyComponent::class)
class BountyTimer(
    private val bountyService: BountyService
) {

    @Scheduled(fixedRate = 1000 * 60, initialDelay = 1000 * 30)
    fun bountyTimer() {
        bountyService.tick()
    }

}
