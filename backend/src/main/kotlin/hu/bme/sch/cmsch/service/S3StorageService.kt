package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.config.StartupPropertyConfig
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.util.UriComponentsBuilder
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.exists
import kotlin.io.path.isReadable

@Service
@ConditionalOnExpression("'\${hu.bme.sch.cmsch.startup.storage-implementation}'.equalsIgnoreCase(T(hu.bme.sch.cmsch.config.StorageImplementation).S3.name)")
class S3StorageService(
    val startupPropertyConfig: StartupPropertyConfig
) : StorageService {

    private val log = LoggerFactory.getLogger(javaClass)

    private val s3 = with(startupPropertyConfig) {
        val client = S3Client.builder()
            .forcePathStyle(true)
            .region(Region.of(s3Region))
            .credentialsProvider { AwsBasicCredentials.create(s3AccessKey, s3SecretKey) }
            .endpointOverride(URI(s3Endpoint))
            .build()
        log.info("Initialized S3 client {}", client)
        return@with client
    }

    init {
        log.info("Using S3 bucket for storage")
    }

    private fun getS3PublicUrl(fullName: String) =
        UriComponentsBuilder.fromHttpUrl(startupPropertyConfig.s3PublicEndpoint)
            .pathSegment(startupPropertyConfig.s3Bucket, fullName)
            .build()
            .toUriString()

    override fun listObjects(): List<Pair<String, Long>> {
        try {
            val request = ListObjectsRequest.builder()
                .bucket(startupPropertyConfig.s3Bucket)
                .build()
            val list = s3.listObjects(request)
            return list.contents().map { it.key() to it.size() }
        } catch (error: Throwable) {
            log.error("Error listing S3 bucket", error)
        }
        return listOf()
    }

    override fun deleteObject(fullName: String): Boolean {
        try {
            val request = DeleteObjectRequest.builder()
                .bucket(startupPropertyConfig.s3Bucket)
                .key(fullName)
                .build()
            s3.deleteObject(request)
            return true
        } catch (error: Throwable) {
            log.error("Error deleting S3 object", error)
        }
        return false
    }

    override fun getObjectUrl(fullName: String): Optional<String> {
        try {
            val request = HeadObjectRequest.builder()
                .bucket(startupPropertyConfig.s3Bucket)
                .key(fullName)
                .build()

            // We use headObject because we are not interested in the actual data, we just want to check if it exists
            s3.headObject(request)
            return Optional.of(getS3PublicUrl(fullName))
        } catch (_: NoSuchKeyException) {
            // If NoSuchKeyException is thrown, the object doesn't exist
        } catch (error: Throwable) {
            log.error("Error checking if S3 object exists", error)
        }

        return Optional.empty()
    }

    override fun saveNamedObject(
        path: String,
        name: String,
        file: MultipartFile
    ): Optional<String> {
        if (file.isEmpty || file.contentType == null)
            return Optional.empty()

        return saveNamedObject(path, name, file.contentType ?: defaultContentType, file.bytes)
    }

    override fun saveNamedObject(
        path: String,
        name: String,
        contentType: String,
        data: ByteArray
    ): Optional<String> {
        if (data.isEmpty()) return Optional.empty()

        try {
            val fullName = getObjectName(path, name)
            val request = PutObjectRequest.builder()
                .bucket(startupPropertyConfig.s3Bucket)
                .key(fullName)
                .contentType(contentType)
                .build()
            s3.putObject(request, RequestBody.fromBytes(data))
            return Optional.of(getS3PublicUrl(fullName))
        } catch (error: Throwable) {
            log.error("Failed to upload object {}/{} to S3 bucket", path, name, error)
        }
        return Optional.empty()
    }

    override fun saveNamedObject(
        path: String,
        name: String,
        filesystemPath: Path
    ): Optional<String> {
        if (!filesystemPath.exists() || !filesystemPath.isReadable()) return Optional.empty()

        try {
            val fullName = getObjectName(path, name)
            val contentType = runCatching { Files.probeContentType(filesystemPath) }.getOrNull()

            val request = PutObjectRequest.builder()
                .bucket(startupPropertyConfig.s3Bucket)
                .key(fullName)
                .contentType(contentType)
                .build()
            s3.putObject(request, RequestBody.fromFile(filesystemPath))
            return Optional.of(getS3PublicUrl(fullName))
        } catch (error: Throwable) {
            log.error("Failed to upload file {} {}/{} to S3 bucket", filesystemPath, path, name, error)
        }
        return Optional.empty()
    }

    override fun readObject(fullName: String): Optional<ByteArray> {
        try {
            val request = GetObjectRequest.builder()
                .bucket(startupPropertyConfig.s3Bucket)
                .key(fullName)
                .build()
            return Optional.of(s3.getObject(request).readBytes())
        } catch (_: NoSuchKeyException) {
            // If NoSuchKeyException is thrown, the object doesn't exist
        } catch (error: Throwable) {
            log.error("Error reading S3 object", error)
        }

        return Optional.empty()
    }

}
