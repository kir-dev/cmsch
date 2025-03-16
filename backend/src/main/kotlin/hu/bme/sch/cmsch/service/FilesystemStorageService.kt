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

    val objectServePath = "files"

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
        return Paths.get(getFileStoragePath(), fullName).deleteIfExists()
    }

    override fun getObjectUrl(fullName: String): Optional<String> {
        if (Paths.get(getFileStoragePath(), fullName).exists()) {
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
            val filePath = Paths.get(storagePath, path, name)
            Files.write(filePath, data)
            return Optional.of(constructObjectUrl(path, name))
        } catch (e: IOException) {
            log.error("Failed to write object to filesystem", e)
        }

        return Optional.empty()
    }

    override fun readObject(fullName: String): Optional<ByteArray> {
        try {
            return Optional.of(ClassPathResource(fullName).inputStream.readAllBytes())
        } catch (e: Throwable) {
            log.error("Failed to read object from classpath", e)
        }
        try {
            return Optional.of(Files.readAllBytes(Paths.get(getFileStoragePath(), fullName)))
        } catch (e: Throwable) {
            log.error("Failed to read object from filesystem", e)
        }
        return Optional.empty()
    }

    private fun constructObjectUrl(path: String, name: String) = constructObjectUrl(getObjectName(path, name))

    private fun constructObjectUrl(fullName: String): String =
        UriComponentsBuilder.fromHttpUrl(applicationComponent.adminSiteUrl.getValue())
            .pathSegment(objectServePath, fullName)
            .build()
            .toUriString()

    fun getFileStoragePath(): String = if (!startupPropertyConfig.filesystemStoragePath.startsWith("/")) {
        System.getProperty("user.dir") + "/" + startupPropertyConfig.filesystemStoragePath
    } else {
        startupPropertyConfig.filesystemStoragePath
    }
}
