package hu.bme.sch.cmsch.config

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "hu.bme.sch.cmsch.startup")
data class StartupPropertyConfig @ConstructorBinding constructor(
    val distributedMode: Boolean,

    val sysadmins: String,
    val zoneId: String,

    // JWT
    val secretKey: String,
    val sessionValidityInMilliseconds: Long,

    // Tokens
    val mailgunToken: String,

    // Profile Qr
    val profileQrPrefix: String,
    val profileSalt: String,

    // Strategies
    val taskOwnershipMode: OwnershipType,
    val riddleOwnershipMode: OwnershipType,
    val tokenOwnershipMode: OwnershipType,
    val challengeOwnershipMode: OwnershipType,
    val raceOwnershipMode: OwnershipType,

    // Increased session
    val increasedSessionTime: Int,

    // Microservice
    val masterRole: Boolean,
    val riddleMicroserviceEnabled: Boolean,
    val managementToken: String,
    val nodeName: String,

    // Storage
    val storageImplementation: StorageImplementation,
    val storageCacheMaxAge: Long,

    val filesystemStoragePath: String,

    // S3
    val s3AccessKey: String = "",
    val s3SecretKey: String = "",
    val s3Region: String = "",
    val s3Bucket: String = "",
    val s3Endpoint: String = "",
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun onInit() {
        log.info("StartupPropertyConfig settings: {}", this.toString())
    }

}
