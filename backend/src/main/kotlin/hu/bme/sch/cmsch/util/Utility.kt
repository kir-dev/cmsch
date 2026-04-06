package hu.bme.sch.cmsch.util

import hu.bme.sch.cmsch.admin.dashboard.DashboardComponent
import hu.bme.sch.cmsch.admin.dashboard.DashboardFormCard
import hu.bme.sch.cmsch.component.form.FormElementType
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.UserService
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.plugins.jpeg.JPEGImageWriteParam
import javax.imageio.stream.MemoryCacheImageOutputStream
import kotlin.jvm.optionals.getOrNull
import kotlin.math.min

fun Authentication.getUser(): CmschUser {
    return this.principal as CmschUser
}

fun Authentication?.getUserOrNull(): CmschUser? {
    return if (this == null) null else (this.principal as? CmschUser)
}

fun Authentication.getUserEntityFromDatabase(userService: UserService): UserEntity {
    return userService.getById(this.name)
}

fun Authentication?.getUserEntityFromDatabaseOrNull(userService: UserService): UserEntity? {
    return if (this == null) null else userService.findByInternalId(this.name).getOrNull()
}

fun Map<String, String>.urlEncode(): String =
    this.entries.joinToString("&") { it.key.urlEncode() + "=" + it.value.urlEncode() }

fun fetchFile(url: String): Result<ByteArray?> = runCatching {
    val webClient = WebClient.builder()
        .codecs { configurer: ClientCodecConfigurer ->
            configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)
        }
        .build()

    webClient.get().uri(url)
        .retrieve().bodyToMono<ByteArray>().block()
}


fun String.urlEncode(): String {
    return URLEncoder.encode(this, StandardCharsets.UTF_8)
}

fun String.urlDecode(): String {
    return URLDecoder.decode(this, StandardCharsets.UTF_8)
}

fun <T> Boolean.mapIfTrue(mapper: () -> T?): T? = if (this) mapper.invoke() else null

fun Set<RoleType>.isAvailableForRole(role: RoleType): Boolean = role in this

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

    fun dashboardCardTypes(components: List<DashboardComponent>) = components.map { it.type }.distinct().toList()

    fun dashboardFormTypes(components: List<DashboardComponent>) = components
        .asSequence()
        .filterIsInstance<DashboardFormCard>()
        .flatMap { it.content }
        .map { it.type.templateName }
        .distinct()
        .toList()

    fun dashboardFormCustomTypes(components: List<DashboardComponent>) = components
        .asSequence()
        .filterIsInstance<DashboardFormCard>()
        .flatMap { it.content }
        .filter { it.type == FormElementType.CUSTOM_BACKEND_ONLY }
        .map { it.customType }
        .distinct()
        .toList()

    fun <T> partition(list: List<T>, size: Int): List<List<T>> {
        val partitioned = mutableListOf<List<T>>()
        for (i in list.indices step size) {
            partitioned.add(list.subList(i, min(i + size, list.size)))
        }
        return partitioned
    }

}

fun BufferedImage.resizeImage(maxWidth: Int, maxHeight: Int): BufferedImage {
    val width = this.width
    val height = this.height

    val scalingFactor = minOf(maxWidth.toDouble() / width, maxHeight.toDouble() / height)

    val newWidth = (width * scalingFactor).toInt()
    val newHeight = (height * scalingFactor).toInt()

    val resizedImage = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB)

    val graphics2D = resizedImage.createGraphics()
    graphics2D.drawImage(this.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null)
    graphics2D.dispose()

    return resizedImage
}

fun BufferedImage.optimizeImage(extension: String): ByteArray {
    val baos = ByteArrayOutputStream()
    if (extension.lowercase() in listOf("jpg", "jpeg")) {
        val writer = ImageIO.getImageWritersByFormatName("jpeg").next()
        val param = JPEGImageWriteParam(null).apply {
            compressionMode = ImageWriteParam.MODE_EXPLICIT
            compressionQuality = 0.85f
            progressiveMode = ImageWriteParam.MODE_DEFAULT
        }

        // JPEG does not support alpha/transparency, so transparent images must be converted to RGB
        // with a white background. Otherwise, OpenJDK's ImageIO cannot write JPEG files with transparency.
        val noAlpha = if (colorModel.hasAlpha()) {
            val rgb = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
            val g = rgb.createGraphics()
            g.drawImage(this, 0, 0, Color.WHITE, null)
            g.dispose()
            rgb
        } else {
            this
        }

        val output = MemoryCacheImageOutputStream(baos)
        writer.output = output
        writer.write(null, IIOImage(noAlpha, null, null), param)
        output.close()
        writer.dispose()
    } else {
        ImageIO.write(this, extension, baos)
    }
    return baos.toByteArray()
}
