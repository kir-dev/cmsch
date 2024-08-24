package hu.bme.sch.cmsch.controller.admin

import com.fasterxml.jackson.databind.MappingIterator
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import hu.bme.sch.cmsch.admin.IconStatus
import hu.bme.sch.cmsch.admin.dashboard.*
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.form.FormElement
import hu.bme.sch.cmsch.component.form.FormElementType
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.transaction
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition.ISOLATION_READ_COMMITTED
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

private const val NAME_COLUMN = "nameColumn"
private const val NICKNAME_COLUMN = "nicknameColumn"
private const val PHONE_COLUMN = "phoneColumn"
private const val FACEBOOK_COLUMN = "facebookColumn"
private const val DRY_RUN = "dryRun"

private const val SELECTOR_COLUMN = "selectorColumn"

private const val VIEW = "tsz-import"

@Controller
@RequestMapping("/admin/control/$VIEW")
@ConditionalOnBean(ApplicationComponent::class)
class TszImportDashboard(
    adminMenuService: AdminMenuService,
    applicationComponent: ApplicationComponent,
    auditLogService: AuditLogService,
    private val platformTransactionManager: PlatformTransactionManager,
    private val groupRepository: GroupRepository
) : DashboardPage(
    view = VIEW,
    title = "TSZ import",
    description = "TSZ-ek importálása csoportokba",
    wide = false,
    adminMenuService = adminMenuService,
    component = applicationComponent,
    auditLog = auditLogService,

    adminMenuCategory = ApplicationComponent.DATA_SOURCE_CATEGORY,
    showPermission = ControlPermissions.PERMISSION_CONTROL_APP,
    adminMenuIcon = "upgrade",
    adminMenuPriority = 4
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val csvCache: ConcurrentMap<String, List<StatusTableRow>> = ConcurrentHashMap()
    private val formStateCache: ConcurrentMap<String, TszImportFormState> = ConcurrentHashMap()

    private data class TszImportFormState(
        val selectorColumn: String,
        val nameColumn: String,
        val nicknameColumn: String,
        val phoneColumn: String,
        val facebookColumn: String,
        val dryRun: Boolean,
    )

    override fun getComponents(user: CmschUser, requestParams: Map<String, String>): List<DashboardComponent> {
        return listOf(
            getLastImport(user),
            getCsvForm(user),
            permissionCard,
        )
    }

    private val permissionCard = DashboardPermissionCard(
        id = 1,
        permission = showPermission.permissionString,
        description = "Ez a jog szükséges ennek az oldalnak az olvasásához.",
        wide = false
    )

    fun getLastImport(user: CmschUser): DashboardComponent {
        return DashboardStatusTableCard(
            id = 2,
            title = "Előző importálás eredménye",
            description = "Ha üres, akkor még nem volt importálás",
            header = listOf("Tankör", "Importált szöveg"),
            content = csvCache.getOrDefault(user.internalId, listOf()),
            wide = wide,
            exportable = true
        )
    }

    fun getCsvForm(user: CmschUser): DashboardFormCard {
        val lastValues = formStateCache[user.internalId]
        return DashboardFormCard(
            id = 3,
            wide = false,
            title = "TSZ-ek importálása",
            description = "",
            content = listOf(
                FormElement(
                    fieldName = "csvFile", label = "CSV fájl", type = FormElementType.FILE,
                    formatRegex = ".*", invalidFormatMessage = "", values = "",
                    note = "Ebből a fileból lesz importálva. A felső oszlop a fejléc.",
                    required = true, permanent = false, defaultValue = ""
                ),
                FormElement(
                    fieldName = SELECTOR_COLUMN, label = "Tankör oszlop neve", type = FormElementType.TEXT,
                    formatRegex = ".*", invalidFormatMessage = "", values = "",
                    note = "A CSV-ben ezzel a névvel szerepel az oszlop",
                    required = true, permanent = false, defaultValue = lastValues?.selectorColumn ?: "group"
                ),
                FormElement(
                    fieldName = NAME_COLUMN, label = "Csoport név oszlop", type = FormElementType.TEXT,
                    formatRegex = ".*", invalidFormatMessage = "", values = "",
                    note = "A CSV-ben ezzel a névvel szerepel a TSZ neve oszlop",
                    required = true, permanent = false, defaultValue = lastValues?.nameColumn ?: "name"
                ),
                FormElement(
                    fieldName = NICKNAME_COLUMN, label = "Csoport név oszlop", type = FormElementType.TEXT,
                    formatRegex = ".*", invalidFormatMessage = "", values = "",
                    note = "A CSV-ben ezzel a névvel szerepel a TSZ beceneve oszlop",
                    required = true, permanent = false, defaultValue = lastValues?.nicknameColumn ?: "nickname"
                ),
                FormElement(
                    fieldName = PHONE_COLUMN, label = "Csoport név oszlop", type = FormElementType.TEXT,
                    formatRegex = ".*", invalidFormatMessage = "", values = "",
                    note = "A CSV-ben ezzel a névvel szerepel a TSZ telefonszáma oszlop",
                    required = true, permanent = false, defaultValue = lastValues?.phoneColumn ?: "phone"
                ),
                FormElement(
                    fieldName = FACEBOOK_COLUMN, label = "Csoport név oszlop", type = FormElementType.TEXT,
                    formatRegex = ".*", invalidFormatMessage = "", values = "",
                    note = "A CSV-ben ezzel a névvel szerepel a TSZ facebookja oszlop",
                    required = true, permanent = false, defaultValue = lastValues?.facebookColumn ?: "facebook"
                ),
                FormElement(
                    fieldName = DRY_RUN,
                    label = "Dry run",
                    type = FormElementType.CHECKBOX,
                    formatRegex = ".*",
                    invalidFormatMessage = "",
                    values = "",
                    note = "Ha ezt bekattintod, akkor nem lesz valódi import. Csak ki fogja írni mi jönne létre.",
                    required = true,
                    permanent = false,
                    defaultValue = if (lastValues?.dryRun != false) "true" else "false"
                ),
            ),
            buttonCaption = "Importálás",
            buttonIcon = "upgrade",
            action = "import",
            method = "post",
            multipartForm = true,
        )
    }

    @PostMapping("/import")
    fun upgradeCsv(
        auth: Authentication,
        @RequestParam("csvFile") csvFile: MultipartFile,
        @RequestParam allRequestParams: Map<String, String>
    ): String {
        val user = auth.getUser()
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        val selectorColumn = allRequestParams.getOrDefault(SELECTOR_COLUMN, "").trim()
        val nameColumn = allRequestParams.getOrDefault(NAME_COLUMN, "").trim()
        val nicknameColumn = allRequestParams.getOrDefault(NICKNAME_COLUMN, "").trim()
        val phoneColumn = allRequestParams.getOrDefault(PHONE_COLUMN, "").trim()
        val facebookColumn = allRequestParams.getOrDefault(FACEBOOK_COLUMN, "").trim()
        val dryRun = allRequestParams.getOrDefault(DRY_RUN, "off").equals("on", ignoreCase = true)

        formStateCache[user.internalId] = TszImportFormState(
            selectorColumn = selectorColumn,
            nameColumn = nameColumn,
            nicknameColumn = nicknameColumn,
            phoneColumn = phoneColumn,
            facebookColumn = facebookColumn,
            dryRun = dryRun,
        )

        val missingUsers = mutableListOf<String>()
        val importStatus = mutableListOf<StatusTableRow>()

        val csv = parseCsv(csvFile, ',')

        val tsz = platformTransactionManager.transaction(readOnly = true) {
            return@transaction csv.mapNotNull { csvLine ->
                val selector = csvLine[selectorColumn]
                    ?: run {
                        importStatus.add(StatusTableRow(row = listOf("no selector", "-"), status = IconStatus.CROSS))
                        return@mapNotNull null
                    }

                val name = csvLine[nameColumn]
                    ?: run {
                        importStatus.add(StatusTableRow(row = listOf(selector, "no name"), status = IconStatus.CROSS))
                        return@mapNotNull null
                    }

                val nickname = csvLine[nicknameColumn]
                    ?: run {
                        importStatus.add(StatusTableRow(row = listOf(selector, "no nickname"), status = IconStatus.CROSS))
                        return@mapNotNull null
                    }

                val phone = csvLine[phoneColumn]
                    ?: run {
                        importStatus.add(StatusTableRow(row = listOf(selector, "no phone"), status = IconStatus.CROSS))
                        return@mapNotNull null
                    }

                val facebook = csvLine[facebookColumn]
                    ?: run {
                        importStatus.add(StatusTableRow(row = listOf(selector, "no facebook"), status = IconStatus.CROSS))
                        return@mapNotNull null
                    }

                val mappedString = "$name${if (nickname.isNotBlank()) (" ($nickname)") else ""} | $phone | $facebook"

                if (groupRepository.findByName(selector.trim()).isEmpty) {
                    missingUsers.add(selector)
                    importStatus.add(StatusTableRow(row = listOf(selector, "no group matched"), status = IconStatus.CROSS))
                    return@mapNotNull null
                }

                importStatus.add(StatusTableRow(row = listOf(selector, mappedString), status = IconStatus.TICK))
                return@mapNotNull Pair(selector, mappedString)
            }.groupBy({ it.first }, { it.second })
        }

        csvCache[user.internalId] = importStatus

        if (!dryRun && missingUsers.isEmpty()) {
            var affectedSize = 0
            platformTransactionManager.transaction(readOnly = false, isolation = ISOLATION_READ_COMMITTED) {
                val affectedGroups = tsz.map { mappings ->
                    val group = groupRepository.findByName(mappings.key).get()
                    group.staff1 = if (mappings.value.isNotEmpty()) mappings.value[0] else ""
                    group.staff2 = if (mappings.value.size > 1) mappings.value[1] else ""
                    group.staff3 = if (mappings.value.size > 2) mappings.value[2] else ""
                    group.staff4 = if (mappings.value.size > 3) mappings.value[3] else ""
                    return@map group
                }
                groupRepository.saveAll(affectedGroups)
                affectedSize = affectedGroups.size
            }
            return dashboardPage(view = VIEW, card = 2, message = "$affectedSize tankör módosítva")
        }

        if (missingUsers.isNotEmpty()) {
            log.info("Missing groups: $missingUsers")
        }

        return dashboardPage(view = VIEW, card = 2, message = "Nem történt módosítás")
    }

    fun parseCsv(file: MultipartFile, separator: Char): List<Map<String, String>> {
        val mapper = CsvMapper()
        val schema = mapper.schemaFor(Map::class.java).withColumnSeparator(separator).withHeader()
        val results = mutableListOf<Map<String, String>>()

        file.inputStream.use { stream ->
            val it: MappingIterator<Map<String, String>> = mapper.readerFor(Map::class.java)
                .with(schema)
                .readValues(stream)

            while (it.hasNext()) {
                val rowAsMap: Map<String, String> = it.next()
                results.add(rowAsMap)
            }
        }

        return results
    }

}