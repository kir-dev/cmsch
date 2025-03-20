package hu.bme.sch.cmsch.service

import com.fasterxml.uuid.Generators
import org.springframework.http.MediaType
import org.springframework.web.multipart.MultipartFile
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

    fun saveObjectWithHashedName(path: String, file: MultipartFile): Optional<String> =
        saveNamedObject(path, hashName(file.originalFilename ?: ""), file)

    fun saveObjectWithHashedName(
        path: String,
        fileName: String,
        contentType: String,
        data: ByteArray
    ): Optional<String> = saveNamedObject(path, hashName(fileName), contentType, data)

    fun saveNamedObject(path: String, name: String, file: MultipartFile): Optional<String>

    fun saveNamedObject(path: String, name: String, contentType: String, data: ByteArray): Optional<String>

    fun readObject(path: String, name: String): Optional<ByteArray> = readObject(getObjectName(path, name))

    fun readObject(fullName: String): Optional<ByteArray>

    private fun hashName(name: String) = (Generators.timeBasedEpochRandomGenerator().generate().toString()
            + name.substring(if (name.contains(".")) name.lastIndexOf('.') else 0))
}
