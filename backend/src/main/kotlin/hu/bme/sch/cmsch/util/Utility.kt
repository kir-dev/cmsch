package hu.bme.sch.cmsch.util

import hu.bme.sch.cmsch.component.login.CmschUserPrincipal
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.UserService
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.util.*

@Component
class DI(
    val userService: UserService,
    val startupPropertyConfig: StartupPropertyConfig
) {
    companion object {
        lateinit var instance: DI
    }

    init {
        instance = this
    }
}

fun MultipartFile.uploadFile(target: String): String? {
    if (this.isEmpty || this.contentType == null)
        return null

    var path = if (!DI.instance.startupPropertyConfig.external.startsWith("/")) {
        System.getProperty("user.dir") + "/" + DI.instance.startupPropertyConfig.external
    } else {
        DI.instance.startupPropertyConfig.external
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

fun Authentication.getUser(): CmschUserPrincipal {
    return this.principal as CmschUserPrincipal
}

fun Authentication?.getUserOrNull(): CmschUserPrincipal? {
    return if (this == null) null else (this.principal as CmschUserPrincipal?)
}

fun Authentication.getUserFromDatabase(): UserEntity {
    return DI.instance.userService.getById(this.name)
}

fun Authentication?.getUserFromDatabaseOrNull(): UserEntity? {
    return if (this == null) null else DI.instance.userService.findById(this.name).orElse(null)
}
