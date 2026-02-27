package hu.bme.sch.cmsch.service

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Service
class RateLimiterService {

    private val loginAttempts = ConcurrentHashMap<String, MutableList<Long>>()

    fun isAllowed(ip: String, limitPerMinute: Long): Boolean {
        if (limitPerMinute <= 0) return true
        
        val now = System.currentTimeMillis()
        val oneMinuteAgo = now - 60000
        
        val attempts = loginAttempts.computeIfAbsent(ip) { mutableListOf() }
        
        synchronized(attempts) {
            attempts.removeIf { it < oneMinuteAgo }
            if (attempts.size < limitPerMinute) {
                attempts.add(now)
                return true
            }
            return false
        }
    }

}
