package hu.bme.sch.cmsch.controller.recovery

import hu.bme.sch.cmsch.admin.dashboard.*
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.form.FormElement
import hu.bme.sch.cmsch.component.form.FormElementType
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ImplicitPermissions
import hu.bme.sch.cmsch.util.getUser
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

private const val ARE_YOU_SURE = "__confirm"
private const val CLASS_NAME = "__class"
private const val INDEX = "__index"
const val EMERGENCY_MEASURE_VIEW = "emergency"

@Controller
@RequestMapping("/admin/control/$EMERGENCY_MEASURE_VIEW")
@ConditionalOnBean(ApplicationComponent::class)
class EmergencyDashboard(
    adminMenuService: AdminMenuService,
    applicationComponent: ApplicationComponent,
    auditLogService: AuditLogService,
    private val measures: List<EmergencyMeasure>,
) : DashboardPage(
    view = EMERGENCY_MEASURE_VIEW,
    title = "Műveletek",
    description = "Műveletek arra az esetre ha valami nagyon elbaszódna.",
    wide = false,
    adminMenuService = adminMenuService,
    component = applicationComponent,
    auditLog = auditLogService,

    showPermission = ImplicitPermissions.PERMISSION_SUPERUSER_ONLY,
    adminMenuCategory = ApplicationComponent.DEVELOPER_CATEGORY,
    adminMenuIcon = "emergency",
    adminMenuPriority = 32
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val measuresByClassName = measures.associateBy { it.javaClass.name }

    override fun getComponents(user: CmschUser, requestParams: Map<String, String>): List<DashboardComponent> {
        return listOf(
            *generateMeasureForms(user),
            permissionCard,
        )
    }

    fun generateMeasureForms(user: CmschUser): Array<DashboardFormCard> {
        return measures
            .sortedBy { it.order }
            .mapIndexed { index, measure ->
                return@mapIndexed DashboardFormCard(
                    id = index,
                    wide = false,
                    title = measure.displayName,
                    description = measure.description,
                    content = listOf(
                        *(measure.getFields(user).toTypedArray()),
                        FormElement(
                            fieldName = ARE_YOU_SURE, label = "Biztos vagy benne?",
                            type = FormElementType.CHECKBOX,
                            formatRegex = ".*", invalidFormatMessage = "", values = "",
                            note = "Megértetted, hogy mivel jár ha lefuttatod ezt!",
                            required = true, permanent = false, defaultValue = "false"
                        ),
                        FormElement(
                            fieldName = CLASS_NAME, label = "", type = FormElementType.CUSTOM_BACKEND_ONLY,
                            formatRegex = ".*", invalidFormatMessage = "", values = "", note = "",
                            customType = FormElementBackendCustomType.HIDDEN,
                            required = true, permanent = false,
                            defaultValue = measure.javaClass.name
                        ),
                        FormElement(
                            fieldName = INDEX, label = "", type = FormElementType.CUSTOM_BACKEND_ONLY,
                            formatRegex = ".*", invalidFormatMessage = "", values = "", note = "",
                            customType = FormElementBackendCustomType.HIDDEN,
                            required = true, permanent = false,
                            defaultValue = index.toString()
                        ),
                    ),
                    buttonCaption = "Mehet",
                    buttonIcon = "emergency",
                    action = "activate",
                    method = "post",
                )
            }
            .toTypedArray()
    }

    private val permissionCard = DashboardPermissionCard(
        id = 5000,
        permission = showPermission.permissionString,
        description = "Ez a jog szükséges ennek az oldalnak az olvasásához.",
        wide = false
    )

    @PostMapping("/activate")
    fun activate(auth: Authentication, @RequestParam allRequestParams: Map<String, String>): String {
        val user = auth.getUser()
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        val index = allRequestParams.getOrDefault(INDEX, "").trim().toIntOrNull()
            ?: return dashboardPage(view = EMERGENCY_MEASURE_VIEW, message = "Index nem található")
        val sure = allRequestParams.getOrDefault(ARE_YOU_SURE, "off").equals("on", ignoreCase = true)
        if (!sure) {
            return dashboardPage(view = EMERGENCY_MEASURE_VIEW, card = index, message = "Meg kell erősítened a műveletet!")
        }

        val className = allRequestParams.getOrDefault(CLASS_NAME, "").trim()
        val measure = measuresByClassName[className]
            ?: return dashboardPage(view = EMERGENCY_MEASURE_VIEW, card = index, message = "Művelet nem található")

        try {
            return measure.executeMeasure(user, index, allRequestParams)
        } catch (e: Exception) {
            log.error("Error while executing emergency measure", e)
            return dashboardPage(view = EMERGENCY_MEASURE_VIEW, card = index, message = "Hiba történt! ${e.message}")
        }
    }

}