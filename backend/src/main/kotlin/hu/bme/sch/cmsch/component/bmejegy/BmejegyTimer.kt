package hu.bme.sch.cmsch.component.bmejegy

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.Executors
import javax.annotation.PostConstruct

@Service
@ConditionalOnBean(BmejegyComponent::class)
open class BmejegyTimer(
    private val bmejegyService: BmejegyService,
    private val bmejegyComponent: BmejegyComponent
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val executor = Executors.newSingleThreadExecutor()

    private var ticks = 65535

    fun syncronize() {
        executor.submit {
            log.info("[BMEJEGY] Synchronizing started")
            val responseData = bmejegyService.fetchData()
            bmejegyService.updateTickets(responseData)
            bmejegyService.updateUserStatuses()
            log.info("[BMEJEGY] Synchronizing finished")
        }
    }

    @Scheduled(fixedRate = 1000 * 60, initialDelay = 1000 * 10)
    open fun tick() {
        ++ticks
        if (ticks > (bmejegyComponent.syncInterval.getIntValue(0))) {
            ticks = 0
            if (!bmejegyComponent.syncEnabled.isValueTrue()) {
                log.info("[BMEJEGY] Synchronizing currently disabled")
                return
            }
            log.info("[BMEJEGY] Pushing event to executor pool")
            syncronize()
        }
    }

}
