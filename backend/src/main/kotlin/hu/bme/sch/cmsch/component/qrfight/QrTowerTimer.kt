package hu.bme.sch.cmsch.component.qrfight

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@ConditionalOnBean(QrFightComponent::class)
class QrTowerTimer(
    private val towerService: QrFightService
) {

    @Scheduled(fixedRate = 1000 * 60 * 10)
    fun towerTimer() {
        towerService.executeTowerTimer()
    }

}
