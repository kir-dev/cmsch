package hu.bme.sch.cmsch.setting

import hu.bme.sch.cmsch.model.RoleType

enum class SettingType {
    TEXT,
    URL,
    COLOR,
    LONG_TEXT,
    LONG_TEXT_MARKDOWN,
    IMAGE,
    NUMBER,
    BOOLEAN,
    MIN_ROLE,
    COMPONENT_GROUP,
    JSON,
    DATE_TIME,
    COMPONENT_NAME,
    BOOLEAN_JSON_LIST
}

open class SettingRef<T : Any>(
    private val componentPropertyService: ComponentSettingService,
    val serializer: SettingSerializer<T>,
    override val component: String,
    override val property: String,
    defaultValue: T,
    val strictConversion: Boolean,
    val cache: Boolean,
    val persist: Boolean,
    private val serverSideOnly: Boolean,
    override val type: SettingType,
    override val fieldName: String,
    override val description: String,
    override val minRoleToEdit: RoleType,
) : MutableSetting<T> {

    val defaultValue: T = componentPropertyService.getBaseValue(this, defaultValue)

    var rawValue: String?
        get() = componentPropertyService.getValue(this)
        set(value) = componentPropertyService.setValue(this, value)

    override val isServerSideOnly: Boolean
        get() = (type == SettingType.COMPONENT_GROUP) || serverSideOnly

    override fun setValue(value: T) {
        rawValue = serializer.serialize(value, strictConversion)
    }

    override fun getValue(): T = rawValue?.let {
        serializer.deserialize(it, defaultValue, strictConversion)
    } ?: defaultValue

    override fun parseAndSet(value: String) = setValue(serializer.deserialize(value, defaultValue, strictConversion))

    override fun getStringValue(): String = rawValue ?: serializer.serialize(defaultValue, strictConversion)

}
