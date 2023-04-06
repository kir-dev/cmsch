package hu.bme.sch.cmsch.addon.nova

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/nova")
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.ext",
    name = ["nova"],
    havingValue = "true",
    matchIfMissing = false
)
class NovaIntegrationController(
    @Value("\${hu.bme.sch.cmsch.token.nova-in:}") private val validTokenIn: String,
    private val service: NovaIntegrationService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping("/valid-users")
    fun updateValidSignups(
        @RequestHeader(defaultValue = "none") token: String,
        @RequestBody emails: List<String>
    ): ResponseEntity<String> {
        if (token == "none") {
            log.info("[NOVA/VALID-USERS] No token presents")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        if (token != validTokenIn) {
            log.info("[NOVA/VALID-USERS] Invalid token")
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token")
        }

        val count = service.updateSubmissions(emails)
        log.info("[NOVA/VALID-USERS] Updated {} users", count)
        return ResponseEntity.ok("OK")
    }

    @PostMapping("/validate/{mode}/{email}/{value}")
    fun changeUserValidation(
        @RequestHeader(defaultValue = "none") token: String,
        @PathVariable mode: String,
        @PathVariable email: String,
        @PathVariable value: String,
        @RequestBody(required = false) body: String?
    ): ResponseEntity<Any> {
        if (token == "none") {
            log.info("[NOVA/VALID-USERS] No token presents")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        if (token != validTokenIn) {
            log.info("[NOVA/VALID-USERS] Invalid token")
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token")
        }

        log.info("[NOVA/VALID-USERS] Setting status {} of {} to '{}'", mode, email, value)

        return when (mode) {
            "payment" -> {
                service.setPaymentStatus(email, value.equals("ok", ignoreCase = true), body)
                ResponseEntity.ok("ok")
            }
            "avatar" -> {
                service.setAvatarStatus(email, value.equals("ok", ignoreCase = true), body)
                ResponseEntity.ok("ok")
            }
            "cv" -> {
                service.setCvStatus(email, value.equals("ok", ignoreCase = true), body)
                ResponseEntity.ok("ok")
            }
            "details" -> {
                service.setDetailsStatus(email, value.equals("ok", ignoreCase = true), body)
                ResponseEntity.ok("ok")
            }
            else -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid mode! Use: payment, avatar, cv, details")
        }
    }

    @GetMapping("/users")
    fun fetchSubmissions(
        @RequestHeader(defaultValue = "none") token: String,
    ): ResponseEntity<Any> {
        if (token == "none") {
            log.info("[NOVA/VALID-USERS] No token presents")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        if (token != validTokenIn) {
            log.info("[NOVA/VALID-USERS] Invalid token")
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token")
        }

        val users = service.fetchSubmissions()
            .filter { it.email.isNotBlank() }
        log.info("[NOVA/VALID-USERS] Listing {} users", users.size)
        return ResponseEntity.ok(users)
    }
    
}
