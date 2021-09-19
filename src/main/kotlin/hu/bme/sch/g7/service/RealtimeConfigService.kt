package hu.bme.sch.g7.service

import hu.bme.sch.g7.dao.RealtimeConfigRepository
import hu.bme.sch.g7.model.RealtimeConfigEntity
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.annotation.PostConstruct

const val WARNING_MESSAGE = "WARNING_MESSAGE"
const val LEADER_BOARD_ENABLED = "LEADER_BOARD_ENABLED"
const val LEADER_BOARD_UPDATES = "LEADER_BOARD_UPDATES"
const val SITE_LOW_PROFILE = "SITE_LOW_PROFILE"
const val MESSAGE_OF_THE_DAY = "MESSAGE_OF_THE_DAY"
const val WEBSITE_URL = "WEBSITE_URL"
const val STAFF_MESSAGE = "STAFF_MESSAGE"
const val EVENT_FINISHED = "EVENT_FINISHED"
const val REQUEST_FOR_NEPTUN = "REQUEST_FOR_NEPTUN"

@Service
class RealtimeConfigService(
        private val realtimeConfig: RealtimeConfigRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val cache: MutableMap<String, String> = mutableMapOf()

    @PostConstruct
    fun init() {
        if (realtimeConfig.findByKey(WARNING_MESSAGE).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, WARNING_MESSAGE, ""))

        if (realtimeConfig.findByKey(LEADER_BOARD_ENABLED).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, LEADER_BOARD_ENABLED, "true"))

        if (realtimeConfig.findByKey(LEADER_BOARD_UPDATES).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, LEADER_BOARD_UPDATES, "true"))

        if (realtimeConfig.findByKey(EVENT_FINISHED).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, EVENT_FINISHED, "false"))

        if (realtimeConfig.findByKey(SITE_LOW_PROFILE).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, SITE_LOW_PROFILE, "false"))

        if (realtimeConfig.findByKey(MESSAGE_OF_THE_DAY).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, MESSAGE_OF_THE_DAY, "Jobb ma egy túzok, mint holnap egy veréb!"))

        if (realtimeConfig.findByKey(STAFF_MESSAGE).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, STAFF_MESSAGE, "Szorgos népünk győzni fog!"))

        if (realtimeConfig.findByKey(WEBSITE_URL).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, WEBSITE_URL, "https://g7.sch.bme.hu/"))

        if (realtimeConfig.findByKey(REQUEST_FOR_NEPTUN).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, REQUEST_FOR_NEPTUN, "false"))
    }

    fun resetCache() {
        log.info("Clearing realtime config cache")
        cache.clear()
    }

    fun getWarningMessage(): String {
        return cache.computeIfAbsent(WARNING_MESSAGE) { key ->
            realtimeConfig.findByKey(key)
                    .map { it.value }
                    .orElse("")
        }
    }

    fun getMotd(): String {
        return cache.computeIfAbsent(MESSAGE_OF_THE_DAY) { key ->
            realtimeConfig.findByKey(key)
                    .map { it.value }
                    .orElse("")
        }
    }

    fun getWebsiteUrl(): String {
        return cache.computeIfAbsent(WEBSITE_URL) { key ->
            realtimeConfig.findByKey(key)
                    .map { it.value }
                    .orElse("")
        }
    }

    fun getStaffMessage(): String {
        return cache.computeIfAbsent(STAFF_MESSAGE) { key ->
            realtimeConfig.findByKey(key)
                    .map { it.value }
                    .orElse("")
        }
    }

    fun isLeaderBoardEnabled(): Boolean {
        return cache.computeIfAbsent(LEADER_BOARD_ENABLED) { key ->
            realtimeConfig.findByKey(key)
                    .map { it.value }
                    .orElse("false")
        }.equals("true", ignoreCase = true)
    }

    fun isLeaderBoardUpdates(): Boolean {
        return cache.computeIfAbsent(LEADER_BOARD_UPDATES) { key ->
            realtimeConfig.findByKey(key)
                    .map { it.value }
                    .orElse("false")
        }.equals("true", ignoreCase = true)
    }

    fun isEventFinished(): Boolean {
        return cache.computeIfAbsent(EVENT_FINISHED) { key ->
            realtimeConfig.findByKey(key)
                    .map { it.value }
                    .orElse("false")
        }.equals("true", ignoreCase = true)
    }

    fun isSiteLowProfile(): Boolean {
        return cache.computeIfAbsent(SITE_LOW_PROFILE) { key ->
            realtimeConfig.findByKey(key)
                    .map { it.value }
                    .orElse("false")
        }.equals("true", ignoreCase = true)
    }

    @Transactional
    fun setLeaderboardUpdates(value: Boolean) {
        cache.put(LEADER_BOARD_UPDATES, value.toString())
        realtimeConfig.findByKey(LEADER_BOARD_UPDATES)
                .map {
                    it.value = value.toString()
                    return@map it
                }
                .ifPresent { realtimeConfig.save(it) }
    }

    fun isRequestForNeptun(): Boolean {
        return cache.computeIfAbsent(REQUEST_FOR_NEPTUN) { key ->
            realtimeConfig.findByKey(key)
                    .map { it.value }
                    .orElse("false")
        }.equals("true", ignoreCase = true)
    }

}