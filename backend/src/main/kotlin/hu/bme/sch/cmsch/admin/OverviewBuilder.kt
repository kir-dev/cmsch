package hu.bme.sch.cmsch.admin

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.ControlAction
import java.io.ByteArrayOutputStream
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

class OverviewBuilder<T : Any>(val type: KClass<T>) {

    private val csv = CsvParserUtil(type)

    private fun getColumnDefinitions(): List<Pair<KProperty1<T, *>, GenerateOverview>> {
        return type.memberProperties.asSequence()
                .filter { it.findAnnotation<GenerateOverview>() != null }
                .map { Pair(it, it.findAnnotation<GenerateOverview>()!!) }
                .filter { it.second.visible }
                .sortedBy { it.second.order }
                .toList()
    }

    fun getColumnsAsJson(): String {
        return "[" + (getColumnDefinitions().joinToString(",") {
            "{\"title\":\"${it.second.columnName}\", " +
                    "\"field\":\"${it.first.name}\", " +
                    "\"hozAlign\":\"${it.second.alignment()}\", " +
                    "\"sorter\":\"${it.second.sorter()}\"" +
                    it.second.extra() +
                    "}"
        }) + "]"
    }

    private fun formatValue(type: GenerateOverview, value: Any?): String {
        return if (type.renderer == OVERVIEW_TYPE_TEXT || type.renderer == OVERVIEW_TYPE_ICON) {
            "\"${(value ?: "").toString().replace("\"", "")}\""
        } else {
            (value ?: "0").toString().replace("\"", "")
        }
    }

    fun getTableDataAsJson(overview: Iterable<T>): String {
        val fields = getColumnDefinitions()
        return "[" + (overview.toList()
            .map { row -> fields.map { "\"${it.first.name}\":${formatValue(it.second, it.first.get(row))}" } }
            .joinToString(",") { row -> "{${row.joinToString(",")}}" }) + "]"
    }

    fun getInputs(): List<Pair<KProperty1<out Any, *>, GenerateInput>> {
        return type.memberProperties.asSequence()
                .filter { it.findAnnotation<GenerateInput>() != null }
                .map { Pair(it, it.findAnnotation<GenerateInput>()!!) }
                .filter { it.second.visible }
                .sortedBy { it.second.order }
                .toList()
    }

    fun exportToCsv(entities: List<T>): String {
        val outputStream = ByteArrayOutputStream()
        csv.exportToCsv(entities, outputStream)
        return outputStream.toString().trim()
    }

    fun toJson(list: List<ControlAction>, objectMapper: ObjectMapper): String {
        return objectMapper
            .writerFor(object : TypeReference<List<ControlAction>>() {})
            .writeValueAsString(list)
    }

}
