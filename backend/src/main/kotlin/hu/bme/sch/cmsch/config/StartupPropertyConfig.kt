package hu.bme.sch.cmsch.config

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "hu.bme.sch.cmsch.startup")
data class StartupPropertyConfig @ConstructorBinding constructor(
    val sysadmins: String,
    val external: String,
    val auditLog: String,
    val zoneId: String,

    // JWT
    val jwtEnabled: Boolean,
    @Deprecated(message = "JWT generated automatically")
    val secretKey: String,
    val sessionValidityInMilliseconds: Long,

    // Profile Qr
    val profileQrEnabled: Boolean,
    val profileQrPrefix: String,
    val profileSalt: String,
    val profileGenerationTarget: String,
    val profileQrCodeSize: Int,

    // Strategies
    val taskOwnershipMode: OwnershipType,
    val riddleOwnershipMode: OwnershipType,
    val tokenOwnershipMode: OwnershipType,
    val formOwnershipMode: OwnershipType,
    val challengeOwnershipMode: OwnershipType,
    val raceOwnershipMode: OwnershipType,

    // Increased session
    val increasedSessionTime: Int
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun onInit() {
        log.info("StartupPropertyConfig settings: {}", this.toString())
    }

}
