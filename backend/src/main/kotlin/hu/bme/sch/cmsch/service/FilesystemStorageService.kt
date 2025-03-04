package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.config.StartupPropertyConfig
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.exists

@Service
@ConditionalOnExpression("'\${hu.bme.sch.cmsch.startup.storage-implementation}'.equalsIgnoreCase(T(hu.bme.sch.cmsch.config.StorageImplementation).FILESYSTEM.name)")
class FilesystemStorageService(
    private val startupPropertyConfig: StartupPropertyConfig
) : StorageService {

    private val log = LoggerFactory.getLogger(javaClass)

    init {
        log.info("Using filesystem for storage")
    }

    override fun getObjectUrl(fullName: String): Optional<String> {
        val url = if (fullName.startsWith("/cdn/"))
            fullName
        else
            "/cdn${if (fullName.startsWith("/")) "" else "/"}$fullName"

        if (Paths.get(getFileStoragePath(), url.replace("/cdn/", "/")).exists()) {
            return Optional.of(url)
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
        } catch (e: IOException) {
            log.error("Failed to write object to filesystem", e)
            return Optional.empty()
        }
        return Optional.of(name)
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

    fun getFileStoragePath(): String = if (!startupPropertyConfig.external.startsWith("/")) {
        System.getProperty("user.dir") + "/" + startupPropertyConfig.external
    } else {
        startupPropertyConfig.external
    }
}
