package hu.bme.sch.cmsch.admin

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

    fun getColumns(): List<Map<String, Any>> = getColumnDefinitions().map {
        mapOf(
            "title" to it.second.columnName,
            "field" to it.first.name,
            "hozAlign" to it.second.alignment(),
            "sorter" to it.second.renderer.sorter,
            *it.second.renderer.formatSettings
        )
    }

    fun getTableData(overview: Iterable<T>): List<Map<String, Any>> {
        val fields = getColumnDefinitions()
        return overview.map { row ->
            fields.associate { it.first.name to it.second.formatValue(it.first.get(row)) }
        }
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
}
