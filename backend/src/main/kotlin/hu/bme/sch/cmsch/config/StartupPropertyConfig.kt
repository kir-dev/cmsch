package hu.bme.sch.cmsch.config

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import javax.annotation.PostConstruct

@ConstructorBinding
@ConfigurationProperties(prefix = "hu.bme.sch.cmsch.startup")
data class StartupPropertyConfig(
    val sysadmins: String = "",
    val external: String = "/etc/cmsch/external",
    val zoneId: String = "CET",

    // JWT
    val jwtEnabled: Boolean = true,
    val secretKey: String = "secret",
    val sessionValidityInMilliseconds: Long = 172800000,

    // Profile Qr
    val profileQrEnabled: Boolean = false,
    val profileQrPrefix: String = "X_",
    val profileSalt: String = "RANDOM_STRING",
    val profileGenerationTarget: String = "/etc/cmsch/external/profiles",
    val profileQrCodeSize: Int = 360,

    // Strategies
    val taskOwnershipMode: OwnershipType = OwnershipType.USER,
    val riddleOwnershipMode: OwnershipType = OwnershipType.USER,
    val tokenOwnershipMode: OwnershipType = OwnershipType.USER,
    val formOwnershipMode: OwnershipType = OwnershipType.USER,
    val challengeOwnershipMode: OwnershipType = OwnershipType.USER,
    val raceOwnershipMode: OwnershipType = OwnershipType.USER,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun onInit() {
        log.info("StartupPropertyConfig settings: {}", this.toString())
    }

}
