package hu.bme.sch.cmsch.component.sheets

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hu.bme.sch.cmsch.admin.dashboard.*
import hu.bme.sch.cmsch.component.form.FormElement
import hu.bme.sch.cmsch.component.form.FormElementType
import hu.bme.sch.cmsch.component.form.FormRepository
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.StaffPermissions
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.transaction
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.jvm.optionals.getOrNull

const val SHEETS_WIZARD = "sheets-setup-wizard"

@Controller
@RequestMapping("/admin/control/$SHEETS_WIZARD")
@ConditionalOnBean(SheetsComponent::class)
class SheetsSetupWizard(
    private val adminMenuService: AdminMenuService,
    auditLogService: AuditLogService,
    sheetsComponent: SheetsComponent,
    private val formRepository: Optional<FormRepository>,
    private val platformTransactionManager: PlatformTransactionManager,
    private val sheetsUpdaterService: SheetsUpdaterService,
    private val sheetsRepository: SheetsRepository,
) : DashboardPage(
    view = SHEETS_WIZARD,
    title = "Integráció készítése",
    description = "",
    wide = false,
    adminMenuService = adminMenuService,
    component = sheetsComponent,
    auditLog = auditLogService,
    showPermission = StaffPermissions.PERMISSION_CREATE_SHEETS,
    adminMenuCategory = null,
    adminMenuIcon = "add_circle",
    adminMenuPriority = 2
) {

    private final val objectMapper = jacksonObjectMapper()
    private final val formStructReader: ObjectReader = objectMapper.readerFor(object : TypeReference<List<FormElement>>() {})

    private val permissionCard = DashboardPermissionCard(
        1,
        permission = showPermission.permissionString,
        description = "Ez a jog szükséges ennek az oldalnak az olvasásához.",
        wide = wide
    )

    override fun getComponents(user: CmschUser, requestParams: Map<String, String>): List<DashboardComponent> {
        return listOf(
            permissionCard,
            DashboardDocsCard(
                id = 2,
                title = "Hogyan lehet?",
                description = "",
                wide = wide,
                markdownContent = "Ahhoz, hogy integrációt hozz létre: menj bele pl. az úrlapok menübe és kattints az integráció hozzáadása ikonra!"
            )
        )
    }

    @GetMapping("/form/{formId}")
    fun viewForm(model: Model, auth: Authentication, @RequestParam requestParams: Map<String, String>, @PathVariable formId: Int): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /${view}", showPermission.permissionString)
            return "admin403"
        }

        model.addAttribute("title", title)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("wide", wide)
        model.addAttribute("components", getComponentsForForms(user, requestParams, formId))
        model.addAttribute("user", user)
        model.addAttribute("card", requestParams.getOrDefault("card", "-1").toIntOrNull())
        model.addAttribute("message", requestParams.getOrDefault("message", ""))

        return "dashboard"
    }

    fun getComponentsForForms(user: CmschUser, requestParams: Map<String, String>, formId: Int): List<DashboardComponent> {
        val token = requestParams.getOrDefault("token", UUID.randomUUID().toString())
        val name = requestParams.getOrDefault("name", "névtelen integráció")
        return listOf(
            permissionCard,
            DashboardDocsCard(
                id = 2,
                title = "Űrlap és Google sheets összekötése",
                description = "",
                wide = wide,
                markdownContent = """
                    |Ezzel a funkcióval élőben követheted az űrlapod állapotát Google Sheetsben.
                    |
                    |Készíts egy új google sheets-et, ehhez menj például a [https://sheets.new/](https://sheets.new/) oldalra. Ha már korábban létrehoztad használhatod a meglévő táblázatodat is.
                    |
                    |Kattints a fölső menüben a `Extensions` menüben az `Apps Script` lehetőségre.
                    |
                    |Másold be az alábbi kódot:
                    |
                    |```
                    |${sheetsScript.replace("%TOKEN%", token)}
                    |```
                    |
                    |Mentsd el a scriptet. Névnek add meg például, hogy "$name".
                    |
                    |Kattins a `Deployment` / `New deployment` gombra. Add meg a deployment nevét (pl. "$name"). Válaszd ki, hogy mindenki által hozzáférhető legyen a script. Ne aggódj az adatokhoz nem fog senki hozzáférni, csak az akinek megvan a token.
                    |
                    |Nyomd meg a `Deploy` gombot.
                    |
                    |Másold be a a Deployment URL-jét az alábbi űrlapba. Ha sikeres volt a beállítás, akkor erre utaló üzenetet fogsz kapni. Ha nem, akkor nézd meg, nem hagytál-e ki valamit.
                    |
                    |Ha a Google átvariálná a lépéseket, akkor kérlek értesítsd a ˙kir-dev˙-et. 
                    |    
                """.trimMargin()
            ),
            DashboardFormCard(
                id = 3,
                wide = false,
                title = "Véglegesítés",
                description = "Kövesd a fenti leírást, és írd be a Deployment URL-jét!",
                content = listOf(
                    FormElement(
                        fieldName = "url", label = "Url", type = FormElementType.TEXT,
                        formatRegex = ".*", invalidFormatMessage = "", values = "",
                        note = "Deployment URL. Így kell kinézzen: https://script.google.com/macros/s/.../exec",
                        required = true, permanent = false, defaultValue = ""
                    ),
                    FormElement(
                        fieldName = "name", label = "Integráció neve", type = FormElementType.TEXT,
                        formatRegex = ".*", invalidFormatMessage = "", values = "",
                        note = "Ezzel a névvel fogod a menüben megtalálni. Másra nincs használva.",
                        required = true, permanent = false, defaultValue = name
                    ),
                    FormElement(
                        fieldName = "token", label = "Token", type = FormElementType.TEXT,
                        formatRegex = ".*", invalidFormatMessage = "", values = "",
                        note = "Ez lesz a token. Ne írd át, ha nem tudod mit csinálsz.",
                        required = true, permanent = false, defaultValue = token
                    ),
                ),
                buttonCaption = "Mehet",
                buttonIcon = "verified",
                action = "form/${formId}",
                method = "post",
                multipartForm = false,
            )
        )
    }

    @PostMapping("/form/{formId}")
    fun refreshTask(auth: Authentication, @RequestParam requestParams: Map<String, String>, @PathVariable formId: Int): String {
        val user = auth.getUser()
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        val card = 3
        val token = requestParams.getOrDefault("token", "n/a")
        val url = requestParams.getOrDefault("url", "")
        val name = requestParams.getOrDefault("name", "névtelen integráció")

        if (formRepository.isEmpty) {
            return dashboardPage(SHEETS_WIZARD, card, "Form komponens nincs betöltve!")
        }

        val status = formValidateRequest(token, url, name, formId)

        return when (status) {
            SheetsUpdateStatus.INVALID_TOKEN -> "redirect:/admin/control/$view/form/$formId?card=$card&message=${URLEncoder.encode("Hibás token! Ellenőrizd, hogy jó szerepel a kódban!", StandardCharsets.UTF_8)}&token=$token&name=${URLEncoder.encode(name, StandardCharsets.UTF_8)}#${card}"
            SheetsUpdateStatus.UNSUPPORTED_MODE -> "redirect:/admin/control/$view/form/$formId?card=$card&message=${URLEncoder.encode("Nem támogatott funkció!", StandardCharsets.UTF_8)}&token=$token&name=${URLEncoder.encode(name, StandardCharsets.UTF_8)}#${card}"
            SheetsUpdateStatus.VERIFIED, SheetsUpdateStatus.OK -> "redirect:/admin/control/$view/form/$formId?card=$card&message=${URLEncoder.encode("Beállítás sikeres!", StandardCharsets.UTF_8)}#${card}"
            SheetsUpdateStatus.CONNECTION_ERROR -> "redirect:/admin/control/$view/form/$formId?card=$card&message=${URLEncoder.encode("Hiba a kommunikáció közben!", StandardCharsets.UTF_8)}&token=$token&name=${URLEncoder.encode(name, StandardCharsets.UTF_8)}#${card}"
            SheetsUpdateStatus.FORM_NOT_FOUND -> "redirect:/admin/control/$view/form/$formId?card=$card&message=${URLEncoder.encode("Ez az űrlap nem található!", StandardCharsets.UTF_8)}&token=$token&name=${URLEncoder.encode(name, StandardCharsets.UTF_8)}#${card}"
        }
    }

    private fun formValidateRequest(token: String, url: String, name: String, formId: Int): SheetsUpdateStatus {
        val forms = formRepository.orElseThrow()
        val formEntity = platformTransactionManager.transaction(readOnly = true) {
            forms.findById(formId).getOrNull() ?: return SheetsUpdateStatus.FORM_NOT_FOUND
        }

        val formStruct = formStructReader.readValue<List<FormElement>>(formEntity.formJson)
        val columns = formStruct.map { it.label }
        val status = sheetsUpdaterService.validateForm(token, url, columns, formId, name)
        if (status == SheetsUpdateStatus.OK) {
            platformTransactionManager.transaction(readOnly = false, isolation = TransactionDefinition.ISOLATION_READ_COMMITTED) {
                sheetsRepository.save(
                    SheetsEntity(
                        name = name,
                        url = url,
                        token = token,
                        formTrigger = formId,
                        enabled = true
                    )
                )
            }
        }
        return status
    }

    private val sheetsScript = """
        var TOKEN = '%TOKEN%';
        
        // Ez egy generált kód, neked semmit sem kell változtass rajta!
        // verzió: 1.0.0
        
        function doPost(e) {
          const request = JSON.parse(Utilities.newBlob(
            Utilities.base64Decode(e.parameter['postData'])
          ).getDataAsString());
        
          if (request.token != TOKEN) {
            return response('INVALID_TOKEN');
          }
        
          if (request.mode == 'VERIFY') {
            return response('VERIFIED');
          } else if (request.mode == 'UPDATE') {
            return updateSheets(request);
          } else if (request.mode == 'REFRESH') {
            deleteAll();
            return updateSheets(request);
          } else if (request.mode == 'DELETE') {
            return deleteLine(request);
          }
        
          return response('UNSUPPORTED_MODE');
        }
        
        function updateSheets(request) {
          const sheet = SpreadsheetApp.getActiveSpreadsheet().getActiveSheet();
          const startingLine = request.startingLine + 1;
          const data = request.data;
          data.forEach((row, index) => {
            const targetRow = startingLine + index;
            if (row.length !== 0) {
              sheet.getRange(targetRow, 1, 1, 
                  row.length).setValues([row]);
            }
          });
          return response('OK');
        }
        
        function deleteLine(request) {
          const sheet = SpreadsheetApp.getActiveSpreadsheet().getActiveSheet();
          const startingLine = request.startingLine + 1;
          sheet.getRange(startingLine, 1, 1, 
              Math.max(1, sheet.getLastColumn())).clearContent();
          return response('OK');
        }
        
        function deleteAll() {
          const sheet = SpreadsheetApp.getActiveSpreadsheet().getActiveSheet();
          sheet.getRange(1, 1, Math.max(1, sheet.getLastRow()), 
              Math.max(1, sheet.getLastColumn())).clearContent();
        }

        function response(reason) {
          const output = ContentService.createTextOutput();
          output.setContent(`{"status":"${"$"}{reason}"}`);
          output.setMimeType(ContentService.MimeType.JSON);
          return output;
        }
    """.trimIndent()

}