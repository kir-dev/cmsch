package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.config.StartupPropertyConfig
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@Service
class ClockService(
    private val startupPropertyConfig: StartupPropertyConfig
) {

    val timeZone = ZoneId.of(startupPropertyConfig.zoneId)!!

    fun getTimeInSeconds() = LocalDateTime.now(timeZone).atZone(ZoneOffset.UTC)?.toInstant()?.epochSecond ?: 0

}
