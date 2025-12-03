package hu.bme.sch.cmsch.util

import org.springframework.ui.Model

fun Model.addFields(obj: Any) {
    val props = obj::class.members
        .filterIsInstance<kotlin.reflect.KProperty1<Any, *>>()
        .associate { it.name to it.get(obj) }

    this.addAllAttributes(props)
}
