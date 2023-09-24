package hu.bme.sch.cmsch.component.bmejegy

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.Executors

@Service
@ConditionalOnBean(BmejegyComponent::class)
open class BmejegyTimer(
    private val bmejegyService: BmejegyService,
    private val bmejegyComponent: BmejegyComponent
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private var executor = Executors.newSingleThreadExecutor()

    private var ticks = 65535

    fun synchronize() {
        executor.submit {
            log.info("[BMEJEGY] Synchronizing started")
            try {
                val responseData = bmejegyService.fetchData()
                bmejegyService.updateTickets(responseData)
                bmejegyService.updateUserStatuses()
            } catch (e: Throwable) {
                log.error("[BMEJEGY] Exception during fetch", e)
            }
            log.info("[BMEJEGY] Synchronizing finished")
        }
    }

    fun clean() {
        log.info("Cleaning executor pool")
        try {
            executor.shutdownNow()
        } catch (e: Throwable) {
            log.error("Exception during executor cleanup")
        }
        executor = Executors.newSingleThreadExecutor()
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
            synchronize()
        }
    }

}
