package hu.bme.sch.g7.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@Service
class ClockService(
    @Value("\${g7-web.zone-id:CET}") zoneId: String
) {
    private val timeZone = ZoneId.of(zoneId)

    fun getTimeInSeconds() = LocalDateTime.now(timeZone).atZone(ZoneOffset.UTC)?.toInstant()?.epochSecond ?: 0

}