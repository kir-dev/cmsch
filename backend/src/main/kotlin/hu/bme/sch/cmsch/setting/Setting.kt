package hu.bme.sch.cmsch.setting

import hu.bme.sch.cmsch.model.RoleType
import kotlin.reflect.KProperty

interface Setting<T : Any> {
    val component: String
    val property: String
    val type: SettingType
    val fieldName: String
    val description: String
    val minRoleToEdit: RoleType
    val isServerSideOnly: Boolean

    fun getValue(): T

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = getValue()

    fun getStringValue(): String = getValue().toString()
}

interface MutableSetting<T : Any> : Setting<T> {
    fun setValue(value: T)

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = setValue(value)

    fun parseAndSet(value: String)
}

fun <T> Setting<Boolean>.mapIfTrue(mapper: () -> T?): T? {
    return if (getValue()) mapper.invoke() else null
}
