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

    fun getInputs(): List<Pair<KProperty1<out Any, *>, GenerateInput>> {
        return type.memberProperties.asSequence()
                .filter { it.findAnnotation<GenerateInput>() != null }
                .map { Pair(it, it.findAnnotation<GenerateInput>()!!) }
                .filter { it.second.visible }
                .sortedBy { it.second.order }
                .toList()
    }

}