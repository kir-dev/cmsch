package hu.bme.sch.g7.admin

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties

class OverviewBuilder(val type: KClass<*>) {

    fun getColumns(): List<String> {
        return type.memberProperties.asSequence()
                .map { it.findAnnotation<GenerateOverview>() }
                .filterNotNull()
                .filter { it.visible }
                .sortedBy { it.order }
                .map { it.columnName }
                .toList()
    }

    fun getColumnDefinitions(): List<Pair<KProperty1<out Any, *>, GenerateOverview>> {
        return type.memberProperties.asSequence()
                .filter { it.findAnnotation<GenerateOverview>() != null }
                .map { Pair(it, it.findAnnotation<GenerateOverview>()!!) }
                .filter { it.second.visible }
                .sortedBy { it.second.order }
                .toList()
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
        val header = details.map { it.first.name }.joinToString(";")
        val result = entities
                .asSequence()
                .map {
                    val result = mutableListOf<String>()
                    for (detail in details)
                        result.add((detail.first.getter.call(it)?.toString() ?: detail.second.defaultValue)
                                .replace(";", "")
                                .replace("\n", " ")
                                .replace("\r", ""))
                    result.joinToString(";")
                }
                .joinToString("\n")
        return "${header}\n${result}"
    }

}