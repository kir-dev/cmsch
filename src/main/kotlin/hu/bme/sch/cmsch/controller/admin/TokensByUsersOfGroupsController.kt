package hu.bme.sch.cmsch.controller.admin

import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.*
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.itextpdf.layout.properties.VerticalAlignment
import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.TokenPropertyRepository
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.dto.virtual.TokenCollectorGroupVirtualEntity
import hu.bme.sch.cmsch.dto.virtual.TokenVirtualEntity
import hu.bme.sch.cmsch.service.AchievementsService
import hu.bme.sch.cmsch.service.RealtimeConfigService
import hu.bme.sch.cmsch.component.riddle.RiddleService
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

const val PREFERRED_TOKEN_TYPE = "default"

@Controller
@RequestMapping("/admin/control/token-properties-of-groups")
class TokensByUsersOfGroupsController(
    private val tokenPropertyRepository: TokenPropertyRepository,
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val config: RealtimeConfigService,
    private val riddleService: RiddleService,
    private val achievementsService: AchievementsService
) {

    private val view = "token-properties-of-groups"
    private val titleSingular = "Tokenek tankörönként"
    private val titlePlural = "Tokenek tankörönként"
    private val description = "Beolvasott tokenek tankörönként csoportosítva"

    private val overviewDescriptor = OverviewBuilder(TokenCollectorGroupVirtualEntity::class)
    private val propertyDescriptor = OverviewBuilder(TokenVirtualEntity::class)

    @GetMapping("")
    fun view(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantMedia }?.not() != false) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("columns", overviewDescriptor.getColumns())
        model.addAttribute("fields", overviewDescriptor.getColumnDefinitions())
        model.addAttribute("rows", fetchOverview())
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_PDF)

        return "overview"
    }

    private fun fetchOverview(): List<TokenCollectorGroupVirtualEntity> {
        return tokenPropertyRepository.findAll().groupBy { it.ownerUser?.groupName ?: "n/a" }
                .filter { it.value.isNotEmpty() }
                .map {
                    TokenCollectorGroupVirtualEntity(
                            it.value[0].ownerUser?.group?.id ?: 0,
                            it.key
                    )
                }
    }

    @ResponseBody
    @GetMapping(value = ["/pdf/{id}"], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generatePdf(@PathVariable id: Int, request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<ByteArray> {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantMedia }?.not() != false) {
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

        val kirdevLogo: Image = Image(ImageDataFactory.create(
            ClassPathResource("/static/images/kirdev-logo.png").inputStream.readAllBytes()
        )).scaleToFit(70f, 70f)

        val ssslLogo: Image = Image(ImageDataFactory.create(
            ClassPathResource("/static/images/sssl-logo.png").inputStream.readAllBytes()
        )).scaleToFit(70f, 70f)

        val font = PdfFontFactory.createFont("OpenSans-Regular.ttf")
        val header = Paragraph()
            .add(ssslLogo)
            .add(Paragraph("GÓLYAKÖRTE 2022\nJELENLÉTI - ${group.name}")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFont(font)
                .setFontSize(28f)
                .setMarginLeft(40f)
                .setMarginRight(40f))
            .add(kirdevLogo)
            .setTextAlignment(TextAlignment.CENTER)
            .setVerticalAlignment(VerticalAlignment.MIDDLE)
            .setMarginBottom(20f)
        document.add(header)

        document.add(Paragraph("Rövid összefoglaló".uppercase())
            .setTextAlignment(TextAlignment.CENTER)
            .setFont(font)
            .setFontSize(20f))

        val minTokenToComplete = config.getMinTokenToComplete()
        val signed = tokensByUsers
            .filter { it.value.count { t -> t.token?.type == PREFERRED_TOKEN_TYPE } >= minTokenToComplete }
            .count()
        document.add(Paragraph("A hallgatók a rendezvény alatt ellátogathattak a Schönherz öntevékeny köreinek " +
                "standjaihoz, ahol miután megismerkedtek az adott körrel, beolvashattak egy-egy QR kódot. Ezáltal digitális " +
                "pecséteket szerezhettek. A tanköri jelenléthez szükséges pecsétek száma: ")
            .add(Text("$minTokenToComplete db").setUnderline())
            .add(". A tankörből ")
            .add(Text("${tokensByUsers.count{ it.value.isNotEmpty() }} fő").setUnderline())
            .add(" legalább egy pecséttel rendelkezik, és ")
            .add(Text("$signed fő").setUnderline())
            .add(" szerezte meg a jelenlétet. A rendezvényen a hallgatók megoldhattak logikai feladatokat (riddleöket) és beadhattak megoldást kreatív feladatokra (buckelist) is.")
            .setTextAlignment(TextAlignment.JUSTIFIED)
            .setMarginBottom(20f)
            .setFont(font)
            .setFontSize(12f))

        val table: Table = Table(UnitValue.createPercentArray(floatArrayOf(40f, 15f, 15f, 15f, 15f)))
            .useAllAvailableWidth()
        table.setMarginBottom(40f)

        table.addHeaderCell(Cell().add(Paragraph("NÉV")
            .setTextAlignment(TextAlignment.CENTER)
            .setFont(font).setFontSize(12f)))
        table.addHeaderCell(Cell().add(Paragraph("PECSÉT")
            .setTextAlignment(TextAlignment.CENTER)
            .setFont(font).setFontSize(12f)))
        table.addHeaderCell(Cell().add(Paragraph("JELENLÉT")
            .setTextAlignment(TextAlignment.CENTER)
            .setFont(font).setFontSize(12f)))
        table.addHeaderCell(Cell().add(Paragraph("RIDDLE")
            .setTextAlignment(TextAlignment.CENTER)
            .setFont(font).setFontSize(12f)))
        table.addHeaderCell(Cell().add(Paragraph("BUCKETLIST")
            .setTextAlignment(TextAlignment.CENTER)
            .setFont(font).setFontSize(12f)))

        tokensByUsers.forEach { user ->
            table.addCell(Cell().add(Paragraph(user.key.fullName)
                .setPaddingLeft(4f)
                .setFont(font).setFontSize(12f)))
            val tokens = user.value.count { it.token?.type == PREFERRED_TOKEN_TYPE }
            table.addCell(Cell().add(Paragraph("$tokens db")
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(font).setFontSize(12f)))
            table.addCell(Cell().add(Paragraph(
                if (tokens >= minTokenToComplete) "igen" else "nem")
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(font).setFontSize(12f)))

            val riddles = riddleService.getCompletedRiddleCount(user.key)
            table.addCell(Cell().add(Paragraph("$riddles db")
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(font).setFontSize(12f)))
            val achievemnts = achievementsService.getSubmittedAchievementsForUser(user.key)
            table.addCell(Cell().add(Paragraph("$achievemnts db")
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(font).setFontSize(12f)))
        }
        document.add(table)

        document.add(Paragraph("Pecsétek hallgatónként".uppercase())
            .setTextAlignment(TextAlignment.CENTER)
            .setFont(font)
            .setFontSize(20f))

        val formatter = SimpleDateFormat("yyyy.MM.dd. HH:mm:ss")
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
                .filter { it.token?.type == PREFERRED_TOKEN_TYPE }
                .forEach {
                    userTable.addCell(Cell().add(Paragraph(it.token?.title ?: "n/a")
                        .setPaddingLeft(4f)
                        .setFont(font).setFontSize(12f)))
                    userTable.addCell(Cell().add(Paragraph(formatter.format(it.recieved * 1000))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFont(font).setFontSize(12f)))
                }

            userTable.setMarginBottom(20f)
            document.add(userTable)
        }

        document.add(Paragraph("Az exportot az SSSL megbízásából a Kir-Dev generálta a résztvevők hozzájárulásával!\nkir-dev@sch.bme.hu | https://kir-dev.sch.bme.hu")
            .setTextAlignment(TextAlignment.CENTER)
            .setFont(font)
            .setFontSize(12f)
            .setMarginTop(20f))

        document.close()
        response.addHeader("Content-Disposition", "attachment; filename=jelenleti-export-2022-${group.name}.pdf")
        return ResponseEntity.ok(output.toByteArray())
    }


}

