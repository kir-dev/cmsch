package hu.bme.sch.cmsch.component.token

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
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.transaction
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.security.SecureRandom

private const val VIEW = "token-bulk-generate"

private const val NAMES = "names"
private const val LENGTH = "length"
private const val TYPE = "type"
private const val ICON = "icon"
private const val SCORE = "score"
private const val UNTIL_DATE = "until_date"

@Controller
@RequestMapping("/admin/control/$VIEW")
@ConditionalOnBean(TokenComponent::class)
class BulkTokenGeneratorDashboard(
    adminMenuService: AdminMenuService,
    auditLogService: AuditLogService,
    tokenComponent: TokenComponent,
    private val platformTransactionManager: PlatformTransactionManager,
    private val timeService: TimeService,
    private val tokenRepository: TokenRepository,
) : DashboardPage(
    view = VIEW,
    title = "Token generálás",
    description = "Egyben sok token generálása",
    wide = false,
    adminMenuService = adminMenuService,
    component = tokenComponent,
    auditLog = auditLogService,

    showPermission = StaffPermissions.PERMISSION_CREATE_TOKENS,
    adminMenuIcon = "qr_code_2_add",
    adminMenuPriority = 2
) {

    private val random = SecureRandom()
    private val supportedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray()

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
            id = 2,
            wide = false,
            title = "QR kódok kigenerálása",
            description = "Az összes tokenből generál egy QR kódot, amit egy zipben le lehet tölteni.",
            content = listOf(
                FormElement(
                    fieldName = NAMES, label = "Nevek", type = FormElementType.LONG_TEXT,
                    formatRegex = ".*", invalidFormatMessage = "", values = "",
                    note = "A különböző nevek soronként",
                    required = true, permanent = false, defaultValue = "Első\nMásodik"
                ),
                FormElement(
                    fieldName = LENGTH, label = "Kódok hossza", type = FormElementType.SELECT,
                    formatRegex = ".*", invalidFormatMessage = "", values = "12,20,30,40",
                    note = "Ilyen hosszú az egyedi része a kódnak",
                    required = true, permanent = false, defaultValue = "12"
                ),
                FormElement(
                    fieldName = TYPE, label = "Típus", type = FormElementType.TEXT,
                    formatRegex = ".*", invalidFormatMessage = "", values = "",
                    note = "Ez az típus lesz beállítva",
                    required = true, permanent = false, defaultValue = "default"
                ),
                FormElement(
                    fieldName = ICON, label = "Ikon", type = FormElementType.TEXT,
                    formatRegex = ".*", invalidFormatMessage = "", values = "",
                    note = "Ez az ikon lesz beállítva",
                    required = true, permanent = false, defaultValue = "stamp"
                ),
                FormElement(
                    fieldName = SCORE, label = "Pont érték", type = FormElementType.NUMBER,
                    formatRegex = ".*", invalidFormatMessage = "", values = "",
                    note = "Ennyi pontot fog érni a QR kód",
                    required = true, permanent = false, defaultValue = "1"
                ),
                FormElement(
                    fieldName = UNTIL_DATE, label = "Hány napig érhető el", type = FormElementType.NUMBER,
                    formatRegex = ".*", invalidFormatMessage = "", values = "",
                    note = "Mostantól fogva hány egész napig legyen beolvasható",
                    required = true, permanent = false, defaultValue = "7"
                ),
            ),
            buttonCaption = "Generálás",
            buttonIcon = "qr_code_2_add",
            action = "insert",
            method = "post",
        )
    }

    @PostMapping("/insert")
    fun downloadAndGenerateQrs(
        auth: Authentication,
        @RequestParam allRequestParams: Map<String, String>
    ): String {
        val user = auth.getUser()
        check(showPermission.validate(user)) { "Insufficient permissions" }

        val names = allRequestParams.getOrDefault(NAMES, "").split("(\r\n|\r|\n)".toRegex())
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        val length = allRequestParams.getOrDefault(LENGTH, "").trim().toIntOrNull() ?: 12
        val type =  allRequestParams.getOrDefault(TYPE, "").trim()
        val icon = allRequestParams.getOrDefault(ICON, "").trim()
        val score = allRequestParams.getOrDefault(SCORE, "").trim().toIntOrNull() ?: 1
        val dateUntil = allRequestParams.getOrDefault(UNTIL_DATE, "").trim().toIntOrNull() ?: 0

        val n = platformTransactionManager.transaction(readOnly = false) {
            val result = tokenRepository.saveAll(names.map {
                TokenEntity(
                    title = it,
                    icon = icon,
                    type = type,
                    score = score,
                    visible = true,
                    token = (0 ..< length).joinToString("") {
                        supportedChars[random.nextInt(supportedChars.size)].toString()
                    },
                    availableFrom = timeService.getTimeInSeconds(),
                    availableUntil = timeService.getTimeInSeconds() + (dateUntil * 24 * 60 * 60),
                )
            })
            return@transaction result.count()
        }

        return dashboardPage(VIEW, 2, "${n}db token beszúrva")
    }

}