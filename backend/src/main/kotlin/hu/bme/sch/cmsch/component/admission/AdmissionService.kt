package hu.bme.sch.cmsch.component.admission

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.TimeService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(AdmissionComponent::class)
open class AdmissionService(
    private val admissionEntryRepository: AdmissionEntryRepository,
    private val clock: TimeService,
    private val auditLogService: AuditLogService,
    private val admissionComponent: AdmissionComponent
) {

    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    open fun logEntryAttempt(
        response: AdmissionResponse,
        gate: CmschUser,
        token: String,
    ): AdmissionResponse {
        if (admissionComponent.saveEntryLog.isValueTrue()) {
            admissionEntryRepository.save(
                AdmissionEntryEntity(
                    userName = response.userEntity?.fullName ?: "n/a",
                    userId = response.userEntity?.id ?: 0,
                    timestamp = clock.getTimeInSeconds(),
                    formId = response.formId ?: 0,
                    responseId = response.responseId ?: 0,
                    grantType = response.entryRole,
                    allowed = response.accessGranted,
                    token = token,
                    response = "${response.groupName} ${response.userName}",
                    gateUserId = gate.id
                )
            )
        }
        if (response.accessGranted) {
            auditLogService.fine(gate, "admission", "granting access to ${response.userName} access: ${response.entryRole.name}")
        } else {
            auditLogService.fine(gate, "admission", "rejecting access to ${response.userName} access: ${response.entryRole.name}")
        }
        return response
    }

}