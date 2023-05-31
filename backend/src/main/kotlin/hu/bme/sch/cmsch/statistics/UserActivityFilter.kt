package hu.bme.sch.cmsch.statistics

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.LongAdder
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest

@Component
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["stats"],
    havingValue = "true",
    matchIfMissing = false
)
class UserActivityFilter : GenericFilterBean() {

    private val log = LoggerFactory.getLogger(javaClass)

    private val currentMinuteRequests = LongAdder()
    private val lastFiveMinutesRequests = LinkedList<Int>()
    private val userActivities = ConcurrentHashMap<String, Instant>()

    private val storedRpm = AtomicInteger(0)
    private val storedUsersIn5Minutes = AtomicInteger(0)
    private val storedUsersIn30Minutes = AtomicInteger(0)

    val rpm: Int
        get() = storedRpm.get()

    val usersIn5Minutes: Int
        get() = storedUsersIn5Minutes.get()

    val usersIn30Minutes: Int
        get() = storedUsersIn30Minutes.get()

    override fun initFilterBean() {
        log.info("User activity filter registered")
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        currentMinuteRequests.increment()

        if (request is HttpServletRequest) {
            val ip = request.remoteAddr
            userActivities[ip] = Instant.now()
        }

        chain.doFilter(request, response)
    }

    @Scheduled(cron = "0 * * * * *")
    fun onEveryMinute() {
        updateLastFiveMinutes()
        removeOldEntries()
        updateStatistics()
    }

    private fun updateLastFiveMinutes() {
        if (lastFiveMinutesRequests.size >= 5) {
            lastFiveMinutesRequests.removeFirst()
        }
        lastFiveMinutesRequests.add(currentMinuteRequests.sum().toInt())
        currentMinuteRequests.reset()
    }

    private fun removeOldEntries() {
        val thirtyMinutesAgo = Instant.now().minus(30, ChronoUnit.MINUTES)
        userActivities.entries.removeIf { (_, lastActionTime) ->
            lastActionTime.isBefore(thirtyMinutesAgo)
        }
    }

    private fun updateStatistics() {
        storedRpm.set(getRpmLastFiveMinutes())
        storedUsersIn5Minutes.set(countFiveMinutesEntries())
        storedUsersIn30Minutes.set(userActivities.size)
    }

    private fun getRpmLastFiveMinutes(): Int {
        return (lastFiveMinutesRequests.sum().toDouble() / lastFiveMinutesRequests.size).toInt()
    }

    private fun countFiveMinutesEntries(): Int {
        val fiveMinutesAgo = Instant.now().minus(5, ChronoUnit.MINUTES)
        return userActivities.values.count { lastActionTime ->
            !lastActionTime.isBefore(fiveMinutesAgo)
        }
    }

}