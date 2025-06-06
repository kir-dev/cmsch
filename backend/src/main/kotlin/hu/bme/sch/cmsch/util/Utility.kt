package hu.bme.sch.cmsch.util

import hu.bme.sch.cmsch.admin.dashboard.DashboardComponent
import hu.bme.sch.cmsch.admin.dashboard.DashboardFormCard
import hu.bme.sch.cmsch.component.form.FormElementType
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
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.bodyToMono
import java.io.InputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.math.min


@Component
final class DI(
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

fun fetchFile(url: String): Result<ByteArray?> = runCatching {
    WebClient.create()
        .get().uri(url)
        .retrieve().bodyToMono<ByteArray>().block()
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
