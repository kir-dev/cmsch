package hu.bme.sch.cmsch.component.sheets

import com.fasterxml.jackson.core.JacksonException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hu.bme.sch.cmsch.component.form.FormElement
import hu.bme.sch.cmsch.component.form.FormRepository
import hu.bme.sch.cmsch.component.form.ResponseRepository
import hu.bme.sch.cmsch.util.transaction
import hu.bme.sch.cmsch.util.urlEncode
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import java.io.IOException
import java.util.*
import kotlin.jvm.optionals.getOrNull


@Service
@ConditionalOnBean(SheetsComponent::class)
class SheetsUpdaterService(
    private val transactionManager: PlatformTransactionManager,
    private val sheetsRepository: SheetsRepository,
    private val formRepository: FormRepository,
    private val responseRepository: ResponseRepository
) {

    private final val log = LoggerFactory.getLogger(javaClass)
    private final val objectMapper = jacksonObjectMapper()
    private final val sheetsUpdateRequestWriter = objectMapper.writerFor(SheetsUpdateRequest::class.java)
    private final val sheetsUpdateResponseReader = objectMapper.readerFor(SheetsUpdateResponse::class.java)
    private final val objectReader = objectMapper.readerFor(object : TypeReference<Map<String, Any>>() {})
    private final val formStructReader: ObjectReader = objectMapper.readerFor(object : TypeReference<List<FormElement>>() {})

    fun validateForm(token: String, url: String, columns: List<String>, formId: Int, integrationName: String): SheetsUpdateStatus {
        if (!url.startsWith("https://script.google.com/")) {
            log.warn("Invalid URL for sheets integration: $url")
            return SheetsUpdateStatus.CONNECTION_ERROR
        }
        try {
            val sheetsUpdateRequest = SheetsUpdateRequest(
                token = token,
                mode = SheetsUpdateMode.UPDATE,
                startingLine = 0,
                data = listOf(columns),
                formId = formId,
                integrationName = integrationName,
            )

            val client = OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .build()

            val request = Request.Builder()
                .url("$url?postData=${getPostData(sheetsUpdateRequest)?.urlEncode()}")
                .post(ByteArray(0).toRequestBody())
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }

                return sheetsUpdateResponseReader.readValue<SheetsUpdateResponse>(response.body?.string())?.status
                    ?: SheetsUpdateStatus.CONNECTION_ERROR
            }
        } catch (e: Exception) {
            log.error("Failed to validate integration: ${e.message}", e)
            return SheetsUpdateStatus.CONNECTION_ERROR
        }
    }

    fun insertLine(line: Int, row: List<String>, sheet: SheetsEntity): SheetsUpdateStatus {
        try {
            val sheetsUpdateRequest = SheetsUpdateRequest(
                token = sheet.token,
                mode = SheetsUpdateMode.UPDATE,
                startingLine = line,
                data = listOf(row),
                formId = sheet.formTrigger,
                integrationName = sheet.name,
            )

            val (client, request) = createRequest(sheet, getPostData(sheetsUpdateRequest))

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                return sheetsUpdateResponseReader.readValue<SheetsUpdateResponse>(response.body?.string())?.status
                    ?: SheetsUpdateStatus.CONNECTION_ERROR
            }
        } catch (e: Exception) {
            log.error("Failed to insert line based on form: ${sheet.formTrigger} sheet: ${sheet.id} row: $row ${e.message}", e)
            return SheetsUpdateStatus.CONNECTION_ERROR
        }
    }

    fun deleteLine(line: Int, sheet: SheetsEntity): SheetsUpdateStatus {
        try {
            val sheetsUpdateRequest = SheetsUpdateRequest(
                token = sheet.token,
                mode = SheetsUpdateMode.DELETE,
                startingLine = line,
                data = listOf(),
                formId = sheet.formTrigger,
                integrationName = sheet.name,
            )

            val (client, request) = createRequest(sheet, getPostData(sheetsUpdateRequest))

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                return sheetsUpdateResponseReader.readValue<SheetsUpdateResponse>(response.body?.string())?.status
                    ?: SheetsUpdateStatus.CONNECTION_ERROR
            }
        } catch (e: Exception) {
            log.error("Failed to delete line based on form: ${sheet.formTrigger} sheet: ${sheet.id} ${e.message}", e)
            return SheetsUpdateStatus.CONNECTION_ERROR
        }
    }

    fun refreshWholeDocument(rows: List<List<String>>, sheet: SheetsEntity): SheetsUpdateStatus {
        try {
            val sheetsUpdateRequest = SheetsUpdateRequest(
                token = sheet.token,
                mode = SheetsUpdateMode.REFRESH,
                startingLine = 0,
                data = rows,
                formId = sheet.formTrigger,
                integrationName = sheet.name,
            )

            val (client, request) = createRequest(sheet, getPostData(sheetsUpdateRequest))

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                return sheetsUpdateResponseReader.readValue<SheetsUpdateResponse>(response.body?.string())?.status
                    ?: SheetsUpdateStatus.CONNECTION_ERROR
            }
        } catch (e: Exception) {
            log.error("Failed to delete line based on form: ${sheet.formTrigger} sheet: ${sheet.id} ${e.message}", e)
            return SheetsUpdateStatus.CONNECTION_ERROR
        }
    }

    fun updateRange(startLine: Int, rows: List<List<String>>, sheet: SheetsEntity): SheetsUpdateStatus {
        try {
            val sheetsUpdateRequest = SheetsUpdateRequest(
                token = sheet.token,
                mode = SheetsUpdateMode.UPDATE,
                startingLine = startLine,
                data = rows,
                formId = sheet.formTrigger,
                integrationName = sheet.name,
            )

            val (client, request) = createRequest(sheet, getPostData(sheetsUpdateRequest))

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                return sheetsUpdateResponseReader.readValue<SheetsUpdateResponse>(response.body?.string())?.status
                    ?: SheetsUpdateStatus.CONNECTION_ERROR
            }
        } catch (e: Exception) {
            log.error("Failed to insert line based on form: ${sheet.formTrigger} sheet: ${sheet.id} row: $rows ${e.message}", e)
            return SheetsUpdateStatus.CONNECTION_ERROR
        }
    }

    private fun createRequest(
        sheet: SheetsEntity,
        postData: String?
    ): Pair<OkHttpClient, Request> {
        val client = OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .build()

        val request = Request.Builder()
            .url("${sheet.url}?postData=${postData?.urlEncode()}")
            .post(ByteArray(0).toRequestBody())
            .build()
        return Pair(client, request)
    }

    private fun getPostData(sheetsUpdateRequest: SheetsUpdateRequest): String? =
        Base64.getEncoder().encodeToString(sheetsUpdateRequestWriter.writeValueAsBytes(sheetsUpdateRequest))

    fun updateAllSheetsByForm(formId: Int): String {
        val sheets = transactionManager.transaction(readOnly = true) { sheetsRepository.findAllByFormTriggerAndEnabledTrue(formId) }
        if (sheets.isEmpty())
            return "redirect:/admin/control/forms?message=no-synced-found"

        val form = formRepository.findById(formId).getOrNull()
            ?: return "redirect:/admin/control/forms?message=form-does-not-exists"

        val formStruct = try {
            formStructReader.readValue<List<FormElement>>(form.formJson)
        } catch (e: JacksonException) {
            listOf()
        }

        val content = mutableMapOf<Int, List<String>>()
        val header = formStruct.map { it.label }
        content[0] = header

        val columnLabels = formStruct.map { it.fieldName }
        transactionManager.transaction(readOnly = true) {
            responseRepository.findAllByFormId(formId).map { response ->
                val values = objectReader.readValue<Map<String, Any>>(response.submission)
                val row = columnLabels.map { values[it]?.toString() ?: "" }
                content[response.line] = row
            }
        }

        val lines = mutableListOf<List<String>>()
        val maxId = content.maxOf { it.key }
        for (i in 0 .. maxId) {
            lines.add(content[i] ?: listOf())
        }

        sheets.forEach { sheet ->
            log.info("Refreshing sheet '{}' with content: {}", sheet.name, lines)
            refreshWholeDocument(lines, sheet)
        }

        return "redirect:/admin/control/forms"
    }

}

enum class SheetsUpdateStatus {
    INVALID_TOKEN,
    UNSUPPORTED_MODE,
    VERIFIED,
    OK,
    CONNECTION_ERROR,
    FORM_NOT_FOUND,
}

enum class SheetsUpdateMode {
    VERIFY,
    UPDATE,
    DELETE,
    REFRESH,
}

data class SheetsUpdateRequest(
    val token: String,
    val mode: SheetsUpdateMode,
    val startingLine: Int,
    val data: List<List<String>>,
    val formId: Any,
    val integrationName: Any,
)

data class SheetsUpdateResponse(
    val status: SheetsUpdateStatus
)
