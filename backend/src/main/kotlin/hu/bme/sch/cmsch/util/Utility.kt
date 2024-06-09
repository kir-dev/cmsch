package hu.bme.sch.cmsch.util

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.UserService
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.springframework.core.io.ClassPathResource
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
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

private fun getFileStoragePath(): String = if (!DI.instance.startupPropertyConfig.external.startsWith("/")) {
    System.getProperty("user.dir") + "/" + DI.instance.startupPropertyConfig.external
} else {
    DI.instance.startupPropertyConfig.external
}

fun MultipartFile.uploadFile(target: String, overrideName: String? = null): String? {
    if (this.isEmpty || this.contentType == null)
        return null

    var path = getFileStoragePath()
    val dir = File(path, target)
    dir.mkdirs()
    val originalFilename = this.originalFilename ?: ""
    val fileName = overrideName
        ?: (UUID(System.currentTimeMillis(), Random().nextLong()).toString()
                + originalFilename.substring(if (originalFilename.contains(".")) originalFilename.lastIndexOf('.') else 0))

    path += (if (path.endsWith("/")) "" else "/") + "$target/$fileName"
    try {
        this.transferTo(File(path))
    } catch (e: IOException) {
        return null
    }
    return fileName
}

fun Authentication.getUser(): CmschUser {
    return this.principal as CmschUser
}

fun Authentication?.getUserOrNull(): CmschUser? {
    return if (this == null) null else (this.principal as CmschUser?)
}

fun Authentication.getUserEntityFromDatabase(): UserEntity {
    return DI.instance.userService.getById(this.name)
}

fun Authentication?.getUserEntityFromDatabaseOrNull(): UserEntity? {
    return if (this == null) null else DI.instance.userService.findById(this.name).orElse(null)
}

fun Map<String, String>.urlEncode(): String = this.entries.joinToString("&") {
    URLEncoder.encode(it.key, StandardCharsets.UTF_8) + "=" + URLEncoder.encode(it.value, StandardCharsets.UTF_8)
}

fun readAsset(assetName: String): Optional<ByteArray> {
    try {
        return Optional.of(ClassPathResource(assetName).inputStream.readAllBytes())
    } catch (ignored: Throwable) {
    }
    try {
        return Optional.of(Files.readAllBytes(Paths.get(getFileStoragePath(), assetName)))
    } catch (ignored: Throwable) {
    }
    return Optional.empty()
}

private val markdownExtensions = listOf(TablesExtension.create())
private val markdownParser: Parser = Parser.builder().extensions(markdownExtensions).build()
private val markdownRenderer = HtmlRenderer.builder().extensions(markdownExtensions).build()

fun markdownToHtml(markdown: String): String {
    val document: Node = markdownParser.parse(markdown)
    return markdownRenderer.render(document)
}

@Service
class ThymeleafUtility {

    fun convertMarkdownToHtml(markdown: String): String = markdownToHtml(markdown)

    fun convertMarkdownToHtmlAndTrimIdent(markdown: String): String = markdownToHtml(markdown.trimIndent())

}