package hu.bme.sch.cmsch.component.token

import com.fasterxml.jackson.databind.ObjectMapper
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.*
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.itextpdf.layout.properties.VerticalAlignment
import hu.bme.sch.cmsch.component.riddle.RiddleBusinessLogicService
import hu.bme.sch.cmsch.component.task.TasksService
import hu.bme.sch.cmsch.controller.admin.ControlAction
import hu.bme.sch.cmsch.controller.admin.SimpleEntityPage
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_TOKENS
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_SHOW_TOKEN_SUBMISSIONS
import hu.bme.sch.cmsch.service.StorageService
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.readBundledAsset
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@Controller
@RequestMapping("/admin/control/token-properties-of-groups")
@ConditionalOnBean(TokenComponent::class)
class TokenAdminTokensByUsersOfGroupsController(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val tokenPropertyRepository: TokenPropertyRepository,
    private val tokenComponent: TokenComponent,
    private val riddleService: Optional<RiddleBusinessLogicService>,
    private val tasksService: Optional<TasksService>,
    private val clock: TimeService,
    storageService: StorageService,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: TokenComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    env: Environment
) : SimpleEntityPage<TokenCollectorGroupVirtualEntity>(
    "token-properties-of-groups",
    TokenCollectorGroupVirtualEntity::class, ::TokenCollectorGroupVirtualEntity,
    "Jelenléti export", "Jelenléti export",
    "Beolvasott tokenek csoportonként csoportosítva.",

    transactionManager,
    { tokenPropertyRepository.findAll().groupBy { it.ownerUser?.groupName ?: "n/a" }
        .filter { it.value.isNotEmpty() }
        .map {
            TokenCollectorGroupVirtualEntity(
                it.value[0].ownerUser?.group?.id ?: 0,
                it.key
            )
        } },

    permission = PERMISSION_EDIT_TOKENS,

    importService,
    adminMenuService,
    storageService,
    component,
    auditLog,
    objectMapper,
    env,

    adminMenuIcon = "file_download",
    adminMenuPriority = 5,

    controlActions = mutableListOf(
        ControlAction(
            "Mentés",
            "pdf/{id}",
            "save",
            PERMISSION_SHOW_TOKEN_SUBMISSIONS,
            100,
            false,
            "Mentés PDF fájlba"
        )
    )
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val supportedColumns = listOf("stamp", "time-between-scans", "attendance", "riddle", "achievement")

    @ResponseBody
    @GetMapping(value = ["/pdf/{id}"], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generatePdf(@PathVariable id: Int, auth: Authentication, response: HttpServletResponse): ResponseEntity<ByteArray> {
        if (showPermission.validate(auth.getUser()).not()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val group = groupRepository.findById(id).orElseThrow()
        val users = userRepository.findAllByGroupName(group.name)
            .sortedBy { it.fullName }

        val tokensByUsers = users.associateWith { tokenPropertyRepository.findAllByOwnerUser_Id(it.id) }

        val output = ByteArrayOutputStream()
        val pdfDoc = PdfDocument(PdfWriter(output))
        val document = Document(pdfDoc, PageSize.A4)
        document.setMargins(20f, 40f, 20f, 40f)

        val font = PdfFontFactory.createFont("OpenSans-Regular.ttf")
        val header = Paragraph()
        storageService.readObject(tokenComponent.reportLogo.getValue().replace("/cdn/", "/")).map { // todo investigate this
            Image(ImageDataFactory.create(it)).scaleToFit(70f, 70f)
        }.ifPresent(header::add)

        val eventName = tokenComponent.reportTitle.getValue()
        header.add(Paragraph("${eventName}\nJELENLÉTI - ${group.name}")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFont(font)
                .setFontSize(28f)
                .setMarginLeft(40f)
                .setMarginRight(40f))

        readBundledAsset("/static/images/kirdev-logo.png").map {
            Image(ImageDataFactory.create(it)).scaleToFit(70f, 70f)
        }.ifPresent(header::add)

        header.setTextAlignment(TextAlignment.CENTER)
            .setVerticalAlignment(VerticalAlignment.MIDDLE)
            .setMarginBottom(20f)
        document.add(header)

        document.add(Paragraph("Rövid összefoglaló".uppercase())
            .setTextAlignment(TextAlignment.CENTER)
            .setFont(font)
            .setFontSize(20f))

        val description = tokenComponent.reportDescription.getValue()
        val minTokenToComplete = tokenComponent.collectRequiredTokens.getValue().toIntOrNull() ?: Int.MAX_VALUE
        val preferredTokenType = tokenComponent.collectRequiredType.getValue()
        val signed = tokensByUsers
            .filter { it.value.count { t -> preferredTokenType == ALL_TOKEN_TYPE || t.token?.type == preferredTokenType } >= minTokenToComplete }
            .count()
        document.add(Paragraph("$description A tanköri jelenléthez szükséges pecsétek száma: ")
            .add(Text("$minTokenToComplete db").setUnderline())
            .add(". A tankörből ")
            .add(Text("${tokensByUsers.count{ it.value.isNotEmpty() }} fő").setUnderline())
            .add(" legalább egy pecséttel rendelkezik, és ")
            .add(Text("$signed fő").setUnderline())
            .add(" szerezte meg a jelenlétet.")
            .setTextAlignment(TextAlignment.JUSTIFIED)
            .setMarginBottom(20f)
            .setFont(font)
            .setFontSize(12f))

        val columns = tokenComponent.reportSummaryTableColumns.getValue().split(",")
            .map { it.trim().lowercase() }
            .filter { supportedColumns.contains(it) }

        val nameHeaderPercent = 40f
        val headerPercents = ArrayList<Float>().also {
            it.add(nameHeaderPercent)
            it.addAll(columns.map { (100f - nameHeaderPercent) / columns.size })
        }

        val table: Table = Table(UnitValue.createPercentArray(headerPercents.toFloatArray()))
            .useAllAvailableWidth()
        table.setMarginBottom(40f)

        addSummaryTableHeaders(table, font, columns)
        tokensByUsers.forEach { addSummaryTableRow(it, table, font, columns, minTokenToComplete, preferredTokenType) }
        document.add(table)

        document.add(Paragraph("Pecsétek hallgatónként".uppercase())
            .setTextAlignment(TextAlignment.CENTER)
            .setFont(font)
            .setFontSize(20f))

        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm:ss")
        tokensByUsers.forEach { user ->
            document.add(Paragraph(user.key.fullName)
                .setTextAlignment(TextAlignment.LEFT)
                .setFont(font)
                .setFontSize(16f))

            val userTable: Table = Table(UnitValue.createPercentArray(floatArrayOf(60f, 40f)))
                .useAllAvailableWidth()

            userTable.addHeaderCell(Cell().add(Paragraph("KÖR")
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(font).setFontSize(12f)))
            userTable.addHeaderCell(Cell().add(Paragraph("MIKOR")
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(font).setFontSize(12f)))

            user.value
                .filter { preferredTokenType == ALL_TOKEN_TYPE || it.token?.type == preferredTokenType }
                .forEach {
                    userTable.addCell(Cell().add(Paragraph(it.token?.title ?: "n/a")
                        .setPaddingLeft(4f)
                        .setFont(font).setFontSize(12f)))
                    val date = Instant.ofEpochSecond(it.recieved).atZone(clock.timeZone).format(formatter)
                    userTable.addCell(Cell().add(Paragraph(date)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFont(font).setFontSize(12f)))
                }

            userTable.setMarginBottom(20f)
            document.add(userTable)
        }

        val footerText = tokenComponent.reportFooterText.getValue()
        document.add(Paragraph("${footerText}\nhello@kir-dev.hu | https://kir-dev.hu")
            .setTextAlignment(TextAlignment.CENTER)
            .setFont(font)
            .setFontSize(12f)
            .setMarginTop(20f))

        document.close()
        response.addHeader("Content-Disposition", "attachment; filename=jelenleti-export-${group.name}.pdf")
        return ResponseEntity.ok(output.toByteArray())
    }

    private fun addSummaryTableRow(
        user: Map.Entry<UserEntity, List<TokenPropertyEntity>>,
        table: Table,
        font: PdfFont?,
        columns: List<String>,
        minTokenToComplete: Int,
        preferredTokenType: String
    ) {
        table.addCell(Cell().add(Paragraph(user.key.fullName)
            .setPaddingLeft(4f)
            .setFont(font).setFontSize(12f)))
        val tokens = user.value.filter { preferredTokenType == ALL_TOKEN_TYPE || it.token?.type == preferredTokenType }
        val tokenCount = tokens.size
        columns.forEach {
           val cellValue = when (it) {
               "time-between-scans" -> getDurationMinutesBetweenFirstAndLastScan(tokens)
               "stamp" -> "$tokenCount db"
               "attendance" -> if (tokenCount >= minTokenToComplete) "igen" else "nem"
               "riddle" -> "${riddleService.map { it.getCompletedRiddleCountUser(user.key) }.orElse(0)} db"
               "achievement" -> "${tasksService.map { it.getSubmittedTasksForUser(user.key) }.orElse(0)} db"

               else -> {
                   log.warn("Unknown column when generating attendance report '{}'", it)
                   ""
               }
           }
            table.addCell(Cell().add(Paragraph(cellValue)
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(font).setFontSize(12f)))
        }
    }

    private fun getDurationMinutesBetweenFirstAndLastScan(tokens: List<TokenPropertyEntity>): String {
        if (tokens.size < 2) return "n/a"
        val stats = tokens.stream().mapToLong { it.recieved }.summaryStatistics()
        val minutes = ChronoUnit.MINUTES.between(Instant.ofEpochSecond(stats.min), Instant.ofEpochSecond(stats.max))
        return "$minutes perc"
    }

    private fun addSummaryTableHeaders(table: Table, font: PdfFont, columns: List<String>) {
        table.addHeaderCell(Cell().add(Paragraph("NÉV")
            .setTextAlignment(TextAlignment.CENTER)
            .setFont(font).setFontSize(12f)))
        columns.forEach {
            val headerName = when (it) {
                "time-between-scans" -> "ELTÖLTÖTT IDŐ"
                "stamp" -> "PECSÉT"
                "attendance" -> "JELENLÉT"
                "riddle" -> "RIDDLE"
                "achievement" -> "REJTVÉNYEK"
                else -> {
                    log.warn("Unknown column when generating attendance report '{}'", it)
                    ""
                }
            }
            table.addHeaderCell(Cell().add(Paragraph(headerName)
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(font).setFontSize(12f)))
        }
    }

}
