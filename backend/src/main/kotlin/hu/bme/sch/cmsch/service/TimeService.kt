package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.config.StartupPropertyConfig
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class TimeService(startupPropertyConfig: StartupPropertyConfig) {

    val timeZone: ZoneId = Objects.requireNonNull(ZoneId.of(startupPropertyConfig.zoneId), "Invalid time zone")
    private val sqlDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun getTimeInSeconds() = ZonedDateTime.now(timeZone)?.toInstant()?.epochSecond ?: 0

    fun getZoneOffset() = ZonedDateTime.now(timeZone).offset.totalSeconds

    fun getTime() = ZonedDateTime.now(timeZone)?.toInstant()?.toEpochMilli() ?: 0

    fun inRange(availableFrom: Long, availableTo: Long, timeInSeconds: Long): Boolean =
        timeInSeconds in availableFrom..availableTo

    fun isTimePassed(timeToBeAfter: Long, timeInSeconds: Long): Boolean = timeInSeconds > timeToBeAfter

    fun todayInSqlFormat(): String = ZonedDateTime.now(timeZone).format(sqlDateFormatter)

}
