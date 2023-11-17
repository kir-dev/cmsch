package hu.bme.sch.cmsch.component.admission

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import hu.bme.sch.cmsch.component.form.ResponseRepository
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.TimeService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.full.memberProperties

@Service
@ConditionalOnBean(AdmissionComponent::class)
open class AdmissionService(
    private val admissionEntryRepository: AdmissionEntryRepository,
    private val clock: TimeService,
    private val auditLogService: AuditLogService,
    private val admissionComponent: AdmissionComponent,
    private val responseRepository: Optional<ResponseRepository>,
    private val ticketRepository: TicketRepository,
    private val userRepository: UserRepository
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
                    userName = response.userEntity?.fullName ?: response.userName,
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

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    open fun generateAdmissionExportForForm(id: Int): ByteArrayOutputStream {
        val outputStream = ByteArrayOutputStream()
        val responses = responseRepository.orElseThrow().findAllByFormId(id)
        val admissions = admissionEntryRepository.findAllByFormIdAndAllowedTrue(id)
            .associateBy { it.responseId }

        val content = responses.map {
            val admission = admissions[it.id]
            AdmissionByFormController.AdmissionExportDto(
                formId = it.formId,
                responseId = it.id,
                userId = it.submitterUserId ?: 0,
                userName = it.submitterUserName,
                joined = admission != null,
                response = it.submission,
                timestamp = admission?.timestamp ?: 0,
                grant = admission?.grantType?.name ?: "NOT_ENTERED"
            )
        }

        val csvMapper = CsvMapper()
        val csvSchemaBuilder = CsvSchema.builder()
            .setUseHeader(true)
            .setReorderColumns(true)

        AdmissionByFormController.AdmissionExportDto::class.memberProperties.forEach { property ->
            csvSchemaBuilder.addColumn(property.name)
        }

        val csvSchema = csvSchemaBuilder.build()
            .withHeader()
            .withQuoteChar('"')
            .withEscapeChar('\\')
            .withColumnSeparator(',')

        csvMapper.writerFor(List::class.java)
            .with(csvSchema)
            .writeValue(outputStream, content)
        return outputStream
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    open fun getTicketByQr(cmschId: String): TicketEntity? {
        val user = userRepository.findByCmschId(cmschId).getOrNull()
        if (user == null) {
            return ticketRepository.findTop1ByQrCodeAndUseCmschIdTrue(cmschId).firstOrNull()

        } else {
            val ticketByEmail = ticketRepository.findTop1ByEmailAndUseCmschIdFalse(user.email).firstOrNull()
            if (user.email.isNotEmpty() && ticketByEmail != null) {
                return ticketByEmail
            }

            return ticketRepository.findTop1ByQrCodeAndUseCmschIdTrue(cmschId).firstOrNull()
        }
    }


    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    open fun countEntries(cmschId: String): Long {
        return admissionEntryRepository.countAllByToken(cmschId)
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    open fun hasTicket(cmschId: String): Boolean {
        return getTicketByQr(cmschId) != null
    }

}