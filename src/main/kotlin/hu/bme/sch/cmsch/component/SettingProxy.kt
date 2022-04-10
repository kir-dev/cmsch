package hu.bme.sch.cmsch.component

import hu.bme.sch.cmsch.model.RoleType

const val cachePeriod: Long = 60 * 60 * 1000

enum class SettingType {
    TEXT,
    LONG_TEXT,
    NUMBER,
    BOOLEAN,
    MIN_ROLE,
    COMPONENT_GROUP
}

open class SettingProxy(
    private val componentPropertyService: ComponentSettingService,
    val component: String,
    val property: String,
    defaultValue: String = "",
    private val cache: Boolean = true,
    val persist: Boolean = true,
    val constant: Boolean = true,
    val serverSideOnly: Boolean = false,
    private val type: SettingType = SettingType.TEXT,
    val fieldName: String = property,
    val description: String = ""
) {

    private var lastTimeUpdated = 0L

    var rawValue: String = defaultValue

    fun setValue(value: String) {
        lastTimeUpdated = System.currentTimeMillis()
        rawValue = value
    }

    fun getValue(): String {
        if (cache && System.currentTimeMillis() > lastTimeUpdated + cachePeriod)
            return rawValue
        if (persist)
            componentPropertyService.refreshCachedSetting(this)
        return rawValue
    }

    fun setAndPersistValue(value: String) {
        setValue(value)
        if (persist)
            componentPropertyService.persistSetting(this)
    }

}

class MinRoleSettingProxy(
    componentPropertyService: ComponentSettingService,
    component: String,
    property: String,
    defaultValue: String,
    fieldName: String = "",
    description: String = ""
) : SettingProxy(
    componentPropertyService, component, property,
    defaultValue = defaultValue, type = SettingType.MIN_ROLE,
    fieldName = fieldName, description = description
) {

    fun isAvailableForRole(role: RoleType): Boolean {
        return rawValue.split(",").contains(role.name)
    }

}

