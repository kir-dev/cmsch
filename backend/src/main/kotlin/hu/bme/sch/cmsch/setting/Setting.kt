package hu.bme.sch.cmsch.setting

import hu.bme.sch.cmsch.component.ComponentBase
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

abstract class SettingRegisteringLoader<T : Setting<*>> {
    operator fun provideDelegate(thisRef: ComponentBase, prop: KProperty<*>): T {
        val setting = provideSetting(thisRef, prop)
        thisRef.registerSetting(setting)
        return setting
    }


    protected abstract fun provideSetting(thisRef: ComponentBase, prop: KProperty<*>): T

}
