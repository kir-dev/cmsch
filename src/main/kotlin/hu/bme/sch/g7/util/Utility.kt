package hu.bme.sch.g7.util

import hu.bme.sch.g7.controller.USER_ENTITY_DTO_SESSION_ATTRIBUTE_NAME
import hu.bme.sch.g7.model.UserEntity
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.util.*
import javax.servlet.http.HttpServletRequest

@Component
class DI {
    companion object {
        lateinit var instance: DI
    }

    init {
        instance = this
    }

    @Value("\${g7web.external:/etc/g7web/external}")
    lateinit var uploadPath: String
}

fun MultipartFile.uploadFile(target: String): String? {
    if (this.isEmpty || this.contentType == null)
        return null

    var path = if (!DI.instance.uploadPath.startsWith("/")) {
        System.getProperty("user.dir") + "/" + DI.instance.uploadPath
    } else {
        DI.instance.uploadPath
    }
    val dir = File(path, target)
    dir.mkdirs()
    val originalFilename = this.originalFilename ?: ""
    val fileName = (UUID(System.currentTimeMillis(), Random().nextLong()).toString()
            + originalFilename.substring(if (originalFilename.contains(".")) originalFilename.lastIndexOf('.') else 0))

    path += (if (path.endsWith("/")) "" else "/") + "$target/$fileName"
    try {
        this.transferTo(File(path))
    } catch (e: IOException) {
        return null
    }
    return fileName
}

fun HttpServletRequest.getUser(): UserEntity {
    return this.getSession(true).getAttribute(USER_ENTITY_DTO_SESSION_ATTRIBUTE_NAME) as UserEntity
}

fun HttpServletRequest.getUserOrNull(): UserEntity? {
    return this.getSession(true).getAttribute(USER_ENTITY_DTO_SESSION_ATTRIBUTE_NAME) as UserEntity?
}