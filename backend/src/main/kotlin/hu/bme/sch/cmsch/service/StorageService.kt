package hu.bme.sch.cmsch.service

import org.springframework.http.MediaType
import org.springframework.web.multipart.MultipartFile
import java.util.*

interface StorageService {

    val defaultContentType get() = MediaType.APPLICATION_OCTET_STREAM_VALUE

    fun exists(fullName: String): Boolean = getObjectUrl(fullName).isPresent

    fun exists(path: String, name: String): Boolean = getObjectUrl(path, name).isPresent

    fun getObjectUrl(fullName: String): Optional<String>

    fun getObjectName(path: String, name: String): String = "${path}/${name}"

    fun getObjectUrl(path: String, name: String): Optional<String> = getObjectUrl(getObjectName(path, name))

    /**
     * Commit a multipart file into storage
     * @return hashed file name if successful
     */
    fun saveObject(path: String, file: MultipartFile): Optional<String> =
        saveNamedObject(path, hashName(file.originalFilename ?: ""), file)

    /**
     * Commit an object into storage
     * @return hashed file name if successful
     */
    fun saveObject(path: String, fileName: String, contentType: String, data: ByteArray): Optional<String> =
        saveNamedObject(path, hashName(fileName), contentType, data)

    /**
     * Commit a multipart file into storage with a predefined name
     * @return file name if successful
     */
    fun saveNamedObject(path: String, name: String, file: MultipartFile): Optional<String>

    /**
     * Commit an object into storage with a predefined name
     * @return file name if successful
     */
    fun saveNamedObject(path: String, name: String, contentType: String, data: ByteArray): Optional<String>

    fun readObject(path: String, name: String): Optional<ByteArray> = readObject(getObjectName(path, name))

    fun readObject(fullName: String): Optional<ByteArray>

    private fun hashName(name: String) = (UUID(System.currentTimeMillis(), Random().nextLong()).toString()
            + name.substring(if (name.contains(".")) name.lastIndexOf('.') else 0))
}
