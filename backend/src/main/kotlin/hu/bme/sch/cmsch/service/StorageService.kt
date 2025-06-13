package hu.bme.sch.cmsch.service

import com.fasterxml.uuid.Generators
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path
import java.util.*

interface StorageService {

    companion object {
        const val OBJECT_SERVE_PATH = "cdn"
    }

    val defaultContentType get() = MediaType.APPLICATION_OCTET_STREAM_VALUE

    fun exists(fullName: String): Boolean = getObjectUrl(fullName).isPresent

    fun exists(path: String, name: String): Boolean = getObjectUrl(path, name).isPresent

    fun listObjects(): List<Pair<String, Long>>

    fun delete(path: String, name: String): Boolean = deleteObject(getObjectName(path, name))

    fun deleteObject(fullName: String): Boolean

    fun getObjectUrl(fullName: String): Optional<String>

    fun getObjectName(path: String, name: String): String = "${path}/${name}"

    fun getObjectUrl(path: String, name: String): Optional<String> = getObjectUrl(getObjectName(path, name))

    fun saveObjectWithRandomName(path: String, file: MultipartFile): Optional<String> =
        saveNamedObject(path, generateName(file.originalFilename ?: ""), file)

    fun saveObjectWithRandomName(
        path: String,
        fileName: String,
        contentType: String,
        data: ByteArray
    ): Optional<String> = saveNamedObject(path, generateName(fileName), contentType, data)

    fun saveObjectWithRandomName(path: String, fileName: String, filesystemPath: Path): Optional<String> =
        saveNamedObject(path, generateName(fileName), filesystemPath)

    fun saveNamedObject(path: String, name: String, file: MultipartFile): Optional<String>

    fun saveNamedObject(path: String, name: String, contentType: String, data: ByteArray): Optional<String>

    fun saveNamedObject(path: String, name: String, filesystemPath: Path): Optional<String>

    fun readObject(path: String, name: String): Optional<ByteArray> = readObject(getObjectName(path, name))

    fun readObject(fullName: String): Optional<ByteArray>

    private fun generateName(name: String) = (Generators.timeBasedEpochRandomGenerator().generate().toString()
            + name.substring(if (name.contains(".")) name.lastIndexOf('.') else 0))

    fun readBundledAsset(assetName: String): Optional<ByteArray> {
        try {
            return Optional.of(ClassPathResource(assetName).inputStream.readAllBytes())
        } catch (error: Throwable) {
            LoggerFactory.getLogger(javaClass).error("Failed to read jar bundled asset '{}'!", assetName, error)
        }
        return Optional.empty()
    }
}
