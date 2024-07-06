package hu.bme.sch.cmsch.component.sheets

import com.fasterxml.jackson.core.JacksonException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hu.bme.sch.cmsch.component.form.FormElement
import hu.bme.sch.cmsch.component.form.FormEntity
import hu.bme.sch.cmsch.component.form.ResponseEntity
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.extending.FormSubmissionListener
import hu.bme.sch.cmsch.util.transaction
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager

@Service
@ConditionalOnBean(SheetsComponent::class)
class SheetsFormsListener(
    private val sheetsUpdaterService: SheetsUpdaterService,
    private val sheetsRepository: SheetsRepository,
    private val platformTransactionManager: PlatformTransactionManager,
) : FormSubmissionListener {

    private val log = LoggerFactory.getLogger(javaClass)

    private final val objectMapper = jacksonObjectMapper()
    private final val objReader = objectMapper.readerFor(object : TypeReference<Map<String, Any>>() {})
    private final val formStructReader: ObjectReader = objectMapper.readerFor(object : TypeReference<List<FormElement>>() {})

    @Async
    override fun onFormSubmitted(user: CmschUser, form: FormEntity, response: ResponseEntity) {
        updateLine(form, response)
    }

    @Async
    override fun onFormUpdated(user: CmschUser, form: FormEntity, response: ResponseEntity) {
        updateLine(form, response)
    }

    @Async
    override fun onResponseDeleted(entity: ResponseEntity) {
        removeLine(entity)
    }

    override fun onResponsesPurged() {
        // Purging will not remove the lines at the moment
    }

    private fun updateLine(form: FormEntity, response: ResponseEntity) {
        val sheets = platformTransactionManager.transaction(readOnly = true) { sheetsRepository.findAllByFormTriggerAndEnabledTrue(form.id) }
        if (sheets.isEmpty())
            return

        val formStruct = try {
            formStructReader.readValue<List<FormElement>>(form.formJson)
        } catch (e: JacksonException) {
            listOf()
        }

        val columnLabels = formStruct.map { it.fieldName }
        val values = objReader.readValue<Map<String, Any>>(response.submission)
        val row = columnLabels.map { values[it]?.toString() ?: "" }

        sheets.forEach { sheet ->
            log.info("Updating sheet '{}' by form response id: {}", sheet.name, response.id)
            sheetsUpdaterService.insertLine(response.line, row, sheet)
        }
    }

    private fun removeLine(response: ResponseEntity) {
        val sheets = platformTransactionManager.transaction(readOnly = true) {
            sheetsRepository.findAllByFormTriggerAndEnabledTrue(response.formId)
        }
        if (sheets.isEmpty())
            return

        sheets.forEach { sheet ->
            log.info("Deleting from sheet '{}' by form response id: {}", sheet.name, response.id)
            sheetsUpdaterService.deleteLine(response.line, sheet)
        }
    }

}