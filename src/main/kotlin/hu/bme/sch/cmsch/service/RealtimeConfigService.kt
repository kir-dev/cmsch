package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.repository.RealtimeConfigRepository
import hu.bme.sch.cmsch.model.RealtimeConfigEntity
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct

const val WARNING_MESSAGE = "WARNING_MESSAGE"
const val WARNING_TYPE = "WARNING_TYPE"
@Deprecated("moved to another place")
const val LEADER_BOARD_ENABLED = "LEADER_BOARD_ENABLED"
@Deprecated("moved to another place")
const val LEADER_BOARD_UPDATES = "LEADER_BOARD_UPDATES"
@Deprecated("feature is not supported")
const val SITE_LOW_PROFILE = "SITE_LOW_PROFILE"
const val MESSAGE_OF_THE_DAY = "MESSAGE_OF_THE_DAY"
const val WEBSITE_URL = "WEBSITE_URL"
const val STAFF_MESSAGE = "STAFF_MESSAGE"
@Deprecated("feature is not supported")
const val EVENT_FINISHED = "EVENT_FINISHED"
@Deprecated("moved to another place")
const val REQUEST_FOR_NEPTUN = "REQUEST_FOR_NEPTUN"
@Deprecated("moved to another place")
const val REQUEST_FOR_EMAIL = "REQUEST_FOR_EMAIL"
@Deprecated("moved to another place")
const val MIN_TOKEN_TO_COMPLETE = "MIN_TOKEN_TO_COMPLETE"
@Deprecated("moved to another place")
const val HINT_SCORE_PERCENTAGE = "HINT_SCORE_PERCENTAGE"

@Service
class RealtimeConfigService(
        private val realtimeConfig: RealtimeConfigRepository,
        @Value("\${cmsch.website-default-url:http://127.0.0.1:8080/}") private val defaultWebsiteUrl: String,
        @Value("\${cmsch.config.warning-message-default:}") private val warningMessageDefault: String,
        @Value("\${cmsch.config.warning-type-default:warning}") private val warningTypeDefault: String,
        @Value("\${cmsch.config.leader-board-enabled-default:true}") private val leaderBoardEnabledDefault: String,
        @Value("\${cmsch.config.leader-board-updates-default:true}") private val leaderBoardUpdatesDefault: String,
        @Value("\${cmsch.config.event-finished-default:false}") private val eventFinishedDefault: String,
        @Value("\${cmsch.config.site-low-profile-default:false}") private val siteLowProfileDefault: String,
        @Value("\${cmsch.config.moth-default:Message of the day}") private val motdDefault: String,
        @Value("\${cmsch.config.staff-message-default:Szorgos népünk győzni fog!}") private val staffMessageDefault: String,
        @Value("\${cmsch.config.request-for-neptun-default:false}") private val requestForNeptunDefault: String,
        @Value("\${cmsch.config.request-for-email-default:false}") private val requestForEmailDefault: String,
        @Value("\${cmsch.config.min-token-to-complete-default:20}") private val minTokenToCompleteDefault: String,
        @Value("\${cmsch.config.hint-score-percentage-default:100}") private val hintScorePercentageDefault: String,
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val cache: ConcurrentHashMap<String, String> = ConcurrentHashMap()

    @PostConstruct
    fun init() {
        if (realtimeConfig.findByKey(WARNING_MESSAGE).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, WARNING_MESSAGE, warningMessageDefault, "Ez a szöveg jelenik meg az oldal tetején minden felhasználónak"))

        if (realtimeConfig.findByKey(WARNING_TYPE).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, WARNING_TYPE, warningTypeDefault, "success, info, warning, error"))

        if (realtimeConfig.findByKey(LEADER_BOARD_ENABLED).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, LEADER_BOARD_ENABLED, leaderBoardEnabledDefault, "true, false"))

        if (realtimeConfig.findByKey(LEADER_BOARD_UPDATES).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, LEADER_BOARD_UPDATES, leaderBoardUpdatesDefault, "true, false"))

        if (realtimeConfig.findByKey(EVENT_FINISHED).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, EVENT_FINISHED, eventFinishedDefault, "true, false"))

        if (realtimeConfig.findByKey(SITE_LOW_PROFILE).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, SITE_LOW_PROFILE, siteLowProfileDefault, "true, false"))

        if (realtimeConfig.findByKey(MESSAGE_OF_THE_DAY).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, MESSAGE_OF_THE_DAY, motdDefault, "Ez a szöveg jelenik meg a bejelentkezés után"))

        if (realtimeConfig.findByKey(STAFF_MESSAGE).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, STAFF_MESSAGE, staffMessageDefault, "Ez a szöveg jelenik meg az admin oldal tetején"))

        if (realtimeConfig.findByKey(WEBSITE_URL).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, WEBSITE_URL, defaultWebsiteUrl, "Átirányításoknál használt cím, mindig mutasson a frontendhez (teljes url)"))

        if (realtimeConfig.findByKey(REQUEST_FOR_NEPTUN).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, REQUEST_FOR_NEPTUN, requestForNeptunDefault, "true, false"))

        if (realtimeConfig.findByKey(REQUEST_FOR_EMAIL).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, REQUEST_FOR_EMAIL, requestForEmailDefault, "true, false"))

        if (realtimeConfig.findByKey(MIN_TOKEN_TO_COMPLETE).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, MIN_TOKEN_TO_COMPLETE, minTokenToCompleteDefault, "int"))

        if (realtimeConfig.findByKey(HINT_SCORE_PERCENTAGE).isEmpty)
            realtimeConfig.save(RealtimeConfigEntity(0, HINT_SCORE_PERCENTAGE, hintScorePercentageDefault, "int 0-100"))
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

    fun getWarningType(): String {
        return cache.computeIfAbsent(WARNING_TYPE) { key ->
            realtimeConfig.findByKey(key)
                .map { it.value }
                .orElse("warning")
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
        cache[LEADER_BOARD_UPDATES] = value.toString()
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

    fun isRequestForEmail(): Boolean {
        return cache.computeIfAbsent(REQUEST_FOR_EMAIL) { key ->
            realtimeConfig.findByKey(key)
                .map { it.value }
                .orElse("false")
        }.equals("true", ignoreCase = true)
    }

    fun getMinTokenToComplete(): Int {
        return cache.computeIfAbsent(MIN_TOKEN_TO_COMPLETE) { key ->
            realtimeConfig.findByKey(key)
                .map { it.value }
                .orElse("0")
        }.toIntOrNull() ?: 0
    }

    fun getHintScorePercentage(): Int {
        return cache.computeIfAbsent(HINT_SCORE_PERCENTAGE) { key ->
            realtimeConfig.findByKey(key)
                .map { it.value }
                .orElse("0")
        }.toIntOrNull() ?: 100
    }

}
