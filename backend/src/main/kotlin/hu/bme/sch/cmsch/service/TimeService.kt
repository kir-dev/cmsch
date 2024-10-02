package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.component.app.DebugComponent
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class TimeService(
    startupPropertyConfig: StartupPropertyConfig,
    private val debugComponent: DebugComponent
) {

    val timeZone: ZoneId = Objects.requireNonNull(ZoneId.of(startupPropertyConfig.zoneId), "Invalid time zone")
    private val sqlDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun getTimeInSeconds() = ZonedDateTime.now(timeZone)?.toInstant()?.epochSecond ?: 0

    fun getTime() = ZonedDateTime.now(timeZone)?.toInstant()?.toEpochMilli() ?: 0

    fun inRange(availableFrom: Long, availableTo: Long, timeInSeconds: Long): Boolean {
        val now = timeInSeconds + (debugComponent.submitDiff.getValue().toLongOrNull() ?: 0)
        return now in availableFrom..availableTo
    }

    fun isTimePassed(timeToBeAfter: Long, timeInSeconds: Long): Boolean {
        val now = timeInSeconds + (debugComponent.submitDiff.getValue().toLongOrNull() ?: 0)
        return now > timeToBeAfter
    }

    fun getNowInSeconds(): Long {
        return getTimeInSeconds() + (debugComponent.submitDiff.getValue().toLongOrNull() ?: 0)
    }

    fun todayInSqlFormat(): String = ZonedDateTime.now(timeZone).format(sqlDateFormatter)

}
