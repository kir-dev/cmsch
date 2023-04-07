package hu.bme.sch.cmsch.admin

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.ControlAction
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

class OverviewBuilder<T : Any>(val type: KClass<T>) {

    fun getColumns(): List<String> {
        return type.memberProperties.asSequence()
                .map { it.findAnnotation<GenerateOverview>() }
                .filterNotNull()
                .filter { it.visible }
                .sortedBy { it.order }
                .map { it.columnName }
                .toList()
    }

    fun getColumnDefinitions(): List<Pair<KProperty1<T, *>, GenerateOverview>> {
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
        return if (type.renderer == OVERVIEW_TYPE_TEXT) {
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

    fun getImportModifiers(): List<Pair<KProperty1<out Any, *>, ImportFormat>> {
        return type.memberProperties.asSequence()
                .filter { it.findAnnotation<ImportFormat>() != null }
                .map { Pair(it, it.findAnnotation<ImportFormat>()!!) }
                .filter { !it.second.ignore }
                .sortedBy { it.second.columnId }
                .toList()
    }

    fun getInputs(): List<Pair<KProperty1<out Any, *>, GenerateInput>> {
        return type.memberProperties.asSequence()
                .filter { it.findAnnotation<GenerateInput>() != null }
                .map { Pair(it, it.findAnnotation<GenerateInput>()!!) }
                .filter { it.second.visible }
                .sortedBy { it.second.order }
                .toList()
    }

    fun <T> exportToCsv(entities: List<T>): String {
        val details = getImportModifiers()
        val header = details.joinToString(";") { it.first.name }
        val result = entities
                .asSequence()
                .map {
                    val result = mutableListOf<String>()
                    for (detail in details) {
                        if (detail.second.type == IMPORT_LOB) {
                            result.add(
                                Base64.getEncoder().encodeToString(
                                    (detail.first.getter.call(it)?.toString() ?: detail.second.defaultValue)
                                        .toByteArray()
                                )
                            )
                        } else {
                            result.add(
                                (detail.first.getter.call(it)?.toString() ?: detail.second.defaultValue)
                                    .replace(";", "")
                                    .replace("\n", " ")
                                    .replace("\r", "")
                            )
                        }
                    }
                    result.joinToString(";")
                }
                .joinToString("\n")
        return "${header}\n${result}"
    }

    fun toJson(list: List<ControlAction>, objectMapper: ObjectMapper): String {
        return objectMapper
            .writerFor(object : TypeReference<List<ControlAction>>() {})
            .writeValueAsString(list)
    }


}
