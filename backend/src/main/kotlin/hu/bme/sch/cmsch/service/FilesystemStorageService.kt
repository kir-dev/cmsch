package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.util.UriComponentsBuilder
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.fileSize

@Service
@ConditionalOnExpression("'\${hu.bme.sch.cmsch.startup.storage-implementation}'.equalsIgnoreCase(T(hu.bme.sch.cmsch.config.StorageImplementation).FILESYSTEM.name)")
class FilesystemStorageService(
    private val applicationComponent: ApplicationComponent,
    private val startupPropertyConfig: StartupPropertyConfig
) : StorageService {

    private val log = LoggerFactory.getLogger(javaClass)

    init {
        log.info("Using filesystem for storage")
    }

    override fun listObjects(): List<Pair<String, Long>> {
        val objectRoot = Paths.get(getFileStoragePath())
        return objectRoot.toFile().walkTopDown()
            .asSequence()
            .filter { it.isFile }
            .map {
                objectRoot.relativize(it.toPath()).toString().replace(File.separator, "/") to it.toPath().fileSize()
            }
            .toList()
    }

    override fun deleteObject(fullName: String): Boolean {
        return getSanitizedPath(fullName)?.deleteIfExists() == true
    }

    override fun getObjectUrl(fullName: String): Optional<String> {
        if (getSanitizedPath(fullName)?.exists() == true) {
            return Optional.of(constructObjectUrl(fullName))
        }
        return Optional.empty()
    }


    override fun saveNamedObject(path: String, name: String, file: MultipartFile): Optional<String> {
        if (file.isEmpty || file.contentType == null)
            return Optional.empty()

        return saveNamedObject(path, name, file.contentType ?: defaultContentType, file.bytes)
    }

    override fun saveNamedObject(path: String, name: String, contentType: String, data: ByteArray): Optional<String> {
        val storagePath = getFileStoragePath()
        val dir = File(storagePath, path)
        dir.mkdirs()

        try {
            val filePath = getSanitizedPath(getObjectName(path, name))
            if (filePath != null) {
                Files.write(filePath, data)
                return Optional.of(constructObjectUrl(path, name))
            }
        } catch (e: IOException) {
            log.error("Failed to write object to filesystem", e)
        }

        return Optional.empty()
    }

    override fun readObject(fullName: String): Optional<ByteArray> {
        try {
            val file = Optional.ofNullable(getSanitizedPath(fullName)?.let { Files.readAllBytes(it) })
            if (file.isPresent) return file
        } catch (e: Throwable) {
            log.error("Failed to read object from filesystem", e)
        }
        try {
            return Optional.of(ClassPathResource(fullName).inputStream.readAllBytes())
        } catch (e: Throwable) {
            log.error("Failed to read object from classpath", e)
        }
        return Optional.empty()
    }

    private fun getSanitizedPath(fullName: String): Path? {
        val storagePath = getFileStoragePath()
        val path = Paths.get(storagePath, fullName)

        // This means we are reading outside the storage folder, which might be malicious
        if (!path.startsWith(storagePath)) {
            log.warn(
                "Got a relative path that escapes the parent folder '{}', possibly malicious: '{}'",
                storagePath,
                fullName
            )
            return null
        }

        return path
    }

    private fun constructObjectUrl(path: String, name: String) = constructObjectUrl(getObjectName(path, name))

    private fun constructObjectUrl(fullName: String): String =
        UriComponentsBuilder.fromHttpUrl(applicationComponent.adminSiteUrl.getValue())
            .pathSegment(StorageService.OBJECT_SERVE_PATH, fullName)
            .build()
            .toUriString()

    fun getFileStoragePath(): String = if (!startupPropertyConfig.filesystemStoragePath.startsWith("/")) {
        System.getProperty("user.dir") + "/" + startupPropertyConfig.filesystemStoragePath
    } else {
        startupPropertyConfig.filesystemStoragePath
    }
}
