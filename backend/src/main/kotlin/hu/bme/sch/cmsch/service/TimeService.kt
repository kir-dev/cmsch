package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@Service
class TimeService(
    private val startupPropertyConfig: StartupPropertyConfig,
    private val applicationComponent: ApplicationComponent
) {

    val timeZone = ZoneId.of(startupPropertyConfig.zoneId)!!

    fun getTimeInSeconds() = LocalDateTime.now(timeZone).atZone(ZoneOffset.UTC)?.toInstant()?.epochSecond ?: 0
    fun getTime() = LocalDateTime.now(timeZone).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli() ?: 0

    fun inRange(availableFrom: Long, availableTo: Long, timeInSeconds: Long): Boolean {
        val now = timeInSeconds + (applicationComponent.submitDiff.getValue().toLongOrNull() ?: 0)
        return now in availableFrom..availableTo
    }

}
