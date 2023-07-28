package hu.bme.sch.cmsch.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import hu.bme.sch.cmsch.component.impressum.OrganizerDto
import hu.bme.sch.cmsch.model.RoleType

const val cachePeriod: Long = 60 * 60 * 1000

val multiplePeopleMapper: ObjectReader = ObjectMapper().readerForListOf(OrganizerDto::class.java)

enum class SettingType {
    TEXT,
    URL,
    COLOR,
    LONG_TEXT,
    LONG_TEXT_MARKDOWN,
    IMAGE,
    NUMBER {
        override fun process(value: String) = value.toLongOrNull() ?: 0
    },
    BOOLEAN {
        override fun process(value: String) = value.equals("true", ignoreCase = true)
    },
    MIN_ROLE,
    COMPONENT_GROUP,
    MULTIPLE_PEOPLE {
        override fun process(value: String): List<OrganizerDto> = try {
            multiplePeopleMapper.readValue(value)
        } catch (e: Throwable) {
            listOf()
        }
    },
    DATE_TIME {
        override fun process(value: String) = value.toLongOrNull() ?: 0
    },
    COMPONENT_NAME,
    BOOLEAN_JSON_LIST;

    open fun process(value: String): Any = value

}

open class SettingProxy(
    private val componentPropertyService: ComponentSettingService,
    val component: String,
    val property: String,
    defaultValue: String = "",
    private val cache: Boolean = true,
    val persist: Boolean = true,
    val constant: Boolean = true,
    private val serverSideOnly: Boolean = false,
    val type: SettingType = SettingType.TEXT,
    val fieldName: String = property,
    val description: String = "",
    val minRoleToEdit: RoleType = RoleType.ADMIN
) {

    private var lastTimeUpdated = 0L

    var rawValue: String = defaultValue

    val isServerSideOnly: Boolean
        get() = (type == SettingType.COMPONENT_GROUP) || serverSideOnly

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

    fun isValueTrue(): Boolean {
        return getValue().equals("true", ignoreCase = true)
    }

    fun getIntValue(default: Int = 0): Int {
        return getValue().toIntOrNull() ?: default
    }

    fun <T> mapIfTrue(mapper: () -> T?): T? {
        return if (isValueTrue()) mapper.invoke() else null
    }

    fun getMappedValue(): Any {
        return type.process(getValue())
    }

}

class MinRoleSettingProxy(
    componentPropertyService: ComponentSettingService,
    component: String,
    property: String,
    defaultValue: String,
    fieldName: String = "",
    description: String = "",
    minRoleToEdit: RoleType = RoleType.STAFF
) : SettingProxy(
    componentPropertyService, component, property,
    defaultValue = defaultValue, type = SettingType.MIN_ROLE,
    fieldName = fieldName, description = description,
    minRoleToEdit = minRoleToEdit, serverSideOnly = true
) {

    companion object {
        val ALL_ROLES by lazy { RoleType.values().joinToString(",") { it.name } }
        val ALL_ROLES_FROM_ATTENDEE by lazy { RoleType.values().filter { it.value >= RoleType.ATTENDEE.value }.joinToString(",") { it.name } }
        val ALL_ROLES_FROM_PRIVILEGED by lazy { RoleType.values().filter { it.value >= RoleType.PRIVILEGED.value }.joinToString(",") { it.name } }
    }

    fun isAvailableForRole(role: RoleType): Boolean {
        return rawValue.split(",").contains(role.name) || role == RoleType.ADMIN || role == RoleType.SUPERUSER
    }

}

