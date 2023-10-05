package hu.bme.sch.cmsch.component.form

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.controller.admin.calculateSearchSettings
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.StaffPermissions
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/forms")
@ConditionalOnBean(FormComponent::class)
class FormController(
    repo: FormRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: FormComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    env: Environment
) : OneDeepEntityPage<FormEntity>(
    "forms",
    FormEntity::class, ::FormEntity,
    "Űrlap", "Űrlapok",
    "Űrlapok az eseményre vagy annak részprogramjára való jelentkezéshez.",

    transactionManager,
    repo,
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission =   StaffPermissions.PERMISSION_SHOW_FORM,
    createPermission = StaffPermissions.PERMISSION_CREATE_FORM,
    editPermission =   StaffPermissions.PERMISSION_EDIT_FORM,
    deletePermission = StaffPermissions.PERMISSION_DELETE_FORM,

    createEnabled = true,
    editEnabled = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "view_list",
    adminMenuPriority = 1,

    searchSettings = calculateSearchSettings<FormEntity>(false)
)
