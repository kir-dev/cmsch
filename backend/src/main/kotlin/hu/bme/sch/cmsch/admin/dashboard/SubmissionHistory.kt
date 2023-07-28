package hu.bme.sch.cmsch.admin.dashboard

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

val historyMapper = ObjectMapper()
val historyReader = historyMapper.readerFor(object : TypeReference<MutableList<SubmissionHistory>>() {})
val historyWriter = historyMapper.writerFor(object : TypeReference<MutableList<SubmissionHistory>>() {})

data class SubmissionHistory(
    var date: Long = 0,
    var submitterName: String = "",
    var adminResponse: Boolean = false,
    var content: String = "",
    var contentUrl: String = "",
    var status: String = "",
    var type: String = "TEXT"
) {
    companion object {
        @JvmStatic
        fun convertStringToHistory(data: String): List<SubmissionHistory> {
            if (data.isBlank())
                return listOf()

            return historyReader.readValue(data)
        }
    }
}
