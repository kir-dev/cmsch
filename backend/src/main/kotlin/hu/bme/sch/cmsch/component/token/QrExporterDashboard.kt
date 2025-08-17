package hu.bme.sch.cmsch.component.token

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import hu.bme.sch.cmsch.admin.dashboard.DashboardComponent
import hu.bme.sch.cmsch.admin.dashboard.DashboardFormCard
import hu.bme.sch.cmsch.admin.dashboard.DashboardPage
import hu.bme.sch.cmsch.admin.dashboard.DashboardPermissionCard
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.form.FormElement
import hu.bme.sch.cmsch.component.form.FormElementType
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.StaffPermissions
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.imageio.ImageIO
import kotlin.math.max

private const val VIEW = "token-qr-export"

private const val ADD_BASE_URL = "addBaseUrl"
private const val CORRECTION_LEVEL = "correctionLevel"
private const val EXTENSION = "extension"
private const val SIZE = "size"
private const val EXTRA_TEXT = "extraText"

@Controller
@RequestMapping("/admin/control/$VIEW")
@ConditionalOnBean(TokenComponent::class)
class QrExporterDashboard(
    adminMenuService: AdminMenuService,
    auditLogService: AuditLogService,
    private val tokenComponent: TokenComponent,
    private val tokenRepository: TokenRepository,
    private val applicationComponent: ApplicationComponent,
) : DashboardPage(
    view = VIEW,
    title = "QR export",
    description = "Az tokenek QR kódjának exportálása egyben",
    wide = false,
    adminMenuService = adminMenuService,
    component = tokenComponent,
    auditLog = auditLogService,

    showPermission = StaffPermissions.PERMISSION_SHOW_TOKENS,
    adminMenuIcon = "qr_code",
    adminMenuPriority = 3
) {
    private val permissionCard = DashboardPermissionCard(
        id = 1,
        permission = showPermission.permissionString,
        description = "Ez a jog szükséges ennek az oldalnak az olvasásához.",
        wide = false
    )

    override fun getComponents(user: CmschUser, requestParams: Map<String, String>): List<DashboardComponent> {
        return listOf(
            downloadQr(),
            permissionCard,
        )
    }

    private fun downloadQr(): DashboardComponent {
        return DashboardFormCard(
            id = 3,
            wide = false,
            title = "QR kódok kigenerálása",
            description = "Az összes tokenből generál egy QR kódot, amit egy zipben le lehet tölteni.",
            content = listOf(
                FormElement(
                    fieldName = ADD_BASE_URL, label = "Oldal URL-jének csatolása", type = FormElementType.CHECKBOX,
                    formatRegex = ".*", invalidFormatMessage = "", values = "",
                    note = "Az oldal URL-je: ${tokenComponent.qrFrontendBaseUrl} Ha nem ez, állítsd át a komponens testreszabásánal.",
                    required = true, permanent = false, defaultValue = "true"
                ),
                FormElement(
                    fieldName = CORRECTION_LEVEL, label = "Hibajavító kódolás", type = FormElementType.SELECT,
                    formatRegex = ".*", invalidFormatMessage = "", values = "L,M,Q,H",
                    note = "L = ~7%, M = ~15%, Q = ~25%, H = ~30% hibajavítás.",
                    required = true, permanent = false, defaultValue = "M"
                ),
                FormElement(
                    fieldName = EXTENSION, label = "Hibajavító kódolás", type = FormElementType.SELECT,
                    formatRegex = ".*", invalidFormatMessage = "", values = "png,jpg",
                    note = "",
                    required = true, permanent = false, defaultValue = "png"
                ),
                FormElement(
                    fieldName = SIZE, label = "Képek mérete", type = FormElementType.NUMBER,
                    formatRegex = ".*", invalidFormatMessage = "", values = "",
                    note = "Pixelben meghatározva, ettől picit eltérhet.",
                    required = true, permanent = false, defaultValue = "1000"
                ),
                FormElement(
                    fieldName = EXTRA_TEXT, label = "Extra szöveg", type = FormElementType.NUMBER,
                    formatRegex = ".*", invalidFormatMessage = "", values = "",
                    note = "A QR alján fog megjelenni",
                    required = true, permanent = false, defaultValue = applicationComponent.siteName
                ),
            ),
            buttonCaption = "Generálás",
            buttonIcon = "qr_code",
            action = "download",
            method = "post",
        )
    }

    @PostMapping("/download", produces = ["application/zip"])
    fun downloadAndGenerateQrs(auth: Authentication, @RequestParam allRequestParams: Map<String, String>): ResponseEntity<ByteArrayResource> {
        val user = auth.getUser()
        check (showPermission.validate(user)) { "Insufficient permissions" }

        val addBaseUrl = allRequestParams.getOrDefault(ADD_BASE_URL, "off").equals("on", ignoreCase = true)
        val correctionLevel = ErrorCorrectionLevel.valueOf(allRequestParams.getOrDefault(CORRECTION_LEVEL, "").trim())
        val size = allRequestParams.getOrDefault(SIZE, "").trim().toIntOrNull() ?: 1000
        val extension = allRequestParams.getOrDefault(EXTENSION, "").trim()
        val extraText = allRequestParams.getOrDefault(EXTRA_TEXT, "").trim()

        val tokens = tokenRepository.findAll().sortedBy { it.title }
        val zipBytes = ByteArrayOutputStream().use { baos ->
            ZipOutputStream(baos).use { zos ->
                for (token in tokens) {
                    val imgBytes = renderQrWithMessages(
                        token = token,
                        correctionLevel = correctionLevel,
                        size = size,
                        extraText = extraText,
                        extension = extension,
                        addBaseUrl = addBaseUrl,
                    )
                    val entryName = "${token.title.formatFileName()}_qr_${token.id}.${extension}"
                    zos.putNextEntry(ZipEntry(entryName))
                    zos.write(imgBytes)
                    zos.closeEntry()
                }
            }
            baos.toByteArray()
        }

        val headers = HttpHeaders().apply {
            contentType = MediaType("application", "zip")
            add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"qrcodes.zip\"")
            add(HttpHeaders.LAST_MODIFIED, ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli().toString())
        }
        val body = ByteArrayResource(zipBytes)
        return ResponseEntity.ok()
            .headers(headers)
            .contentLength(zipBytes.size.toLong())
            .body(body)
    }

    private fun renderQrWithMessages(
        token: TokenEntity,
        correctionLevel: ErrorCorrectionLevel,
        size: Int,
        extraText: String,
        extension: String,
        addBaseUrl: Boolean
    ): ByteArray {
        val hints = mapOf(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.ERROR_CORRECTION to correctionLevel,
            EncodeHintType.MARGIN to 1
        )

        val bitMatrix = QRCodeWriter().encode(if (addBaseUrl) "${tokenComponent.qrFrontendBaseUrl}${token.token}" else token.token, BarcodeFormat.QR_CODE, size, size, hints)

        val qrImage = BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until size) {
            for (y in 0 until size) {
                qrImage.setRGB(x, y, if (bitMatrix.get(x, y)) Color.BLACK.rgb else Color.WHITE.rgb)
            }
        }

        val leftMargin = 40
        val rightMargin = 40
        val topMargin = 64
        val bottomMargin = 64

        val canvasW = leftMargin + size + rightMargin
        val canvasH = topMargin + size + bottomMargin

        val canvas = BufferedImage(canvasW, canvasH, BufferedImage.TYPE_INT_RGB)
        val g = canvas.createGraphics()
        try {
            g.color = Color.WHITE
            g.fillRect(0, 0, canvasW, canvasH)

            g.drawImage(qrImage, leftMargin, topMargin, null)

            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            val baseFont = Font("SansSerif", Font.PLAIN, 26)

            g.font = baseFont
            val fmTop = g.fontMetrics
            val topX = (canvasW / 2) - (fmTop.stringWidth(token.title) / 2)
            val topY = max(fmTop.ascent + 10, fmTop.height)
            g.color = Color.BLACK
            g.drawString(token.title, topX, topY)

            val bottomText = extraText
            val fmBottom = g.fontMetrics
            g.font = baseFont
            val bottomX = (canvasW / 2) - (fmBottom.stringWidth(bottomText) / 2)
            val bottomY = topMargin + size + bottomMargin - 20
            g.color = Color.BLACK
            g.drawString(bottomText, bottomX, bottomY)

        } finally {
            g.dispose()
        }

        return ByteArrayOutputStream().use { out ->
            ImageIO.write(canvas, extension, out)
            out.toByteArray()
        }
    }

}

private fun String.formatFileName(): String {
    return this.replace(" ", "_")
        .replace("á", "a")
        .replace("Á", "A")
        .replace("é", "e")
        .replace("É", "E")
        .replace("í", "i")
        .replace("Í", "I")
        .replace("ó", "o")
        .replace("Ó", "O")
        .replace("ö", "o")
        .replace("Ö", "O")
        .replace("ő", "o")
        .replace("Ő", "O")
        .replace("ú", "u")
        .replace("Ú", "U")
        .replace("ü", "u")
        .replace("Ü", "U")
        .replace("ű", "u")
        .replace("Ű", "U")
        .replace("#", "_")
        .replace("/", "-")
        .replace("|", "-")
        .replace(".", "-")
        .replace(":", "")
        .replace(";", "")
        .replace("[^A-Za-z0-9_-]".toRegex(), "")
}
