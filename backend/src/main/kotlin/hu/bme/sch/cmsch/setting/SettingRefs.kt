package hu.bme.sch.cmsch.setting

import hu.bme.sch.cmsch.model.RoleType


class StringSettingRef(
    componentPropertyService: ComponentSettingService,
    component: String,
    property: String,
    defaultValue: String = "",
    strictConversion: Boolean = true,
    cache: Boolean = true,
    persist: Boolean = true,
    serverSideOnly: Boolean = false,
    type: SettingType = SettingType.TEXT,
    fieldName: String = property,
    description: String = "",
    minRoleToEdit: RoleType = RoleType.ADMIN
) : SettingRef<String>(
    componentPropertyService = componentPropertyService,
    serializer = StringSettingSerializer,
    component = component,
    property = property,
    defaultValue = defaultValue,
    strictConversion = strictConversion,
    cache = cache,
    persist = persist,
    serverSideOnly = serverSideOnly,
    type = type,
    fieldName = fieldName,
    description = description,
    minRoleToEdit = minRoleToEdit,
)

class NumberSettingRef(
    componentPropertyService: ComponentSettingService,
    component: String,
    property: String,
    defaultValue: Long = 0,
    strictConversion: Boolean = true,
    cache: Boolean = true,
    persist: Boolean = true,
    serverSideOnly: Boolean = false,
    type: SettingType = SettingType.NUMBER,
    fieldName: String = property,
    description: String = "",
    minRoleToEdit: RoleType = RoleType.ADMIN
) : SettingRef<Long>(
    componentPropertyService = componentPropertyService,
    serializer = LongSettingSerializer,
    component = component,
    property = property,
    defaultValue = defaultValue,
    strictConversion = strictConversion,
    cache = cache,
    persist = persist,
    serverSideOnly = serverSideOnly,
    type = type,
    fieldName = fieldName,
    description = description,
    minRoleToEdit = minRoleToEdit,
)

class BooleanSettingRef(
    componentPropertyService: ComponentSettingService,
    component: String,
    property: String,
    defaultValue: Boolean = false,
    strictConversion: Boolean = true,
    cache: Boolean = true,
    persist: Boolean = true,
    serverSideOnly: Boolean = false,
    type: SettingType = SettingType.BOOLEAN,
    fieldName: String = property,
    description: String = "",
    minRoleToEdit: RoleType = RoleType.ADMIN
) : SettingRef<Boolean>(
    componentPropertyService = componentPropertyService,
    serializer = BooleanSettingSerializer,
    component = component,
    property = property,
    defaultValue = defaultValue,
    strictConversion = strictConversion,
    cache = cache,
    persist = persist,
    serverSideOnly = serverSideOnly,
    type = type,
    fieldName = fieldName,
    description = description,
    minRoleToEdit = minRoleToEdit,
) {

    fun <T> mapIfTrue(mapper: () -> T?): T? {
        return if (getValue()) mapper.invoke() else null
    }

}

class MinRoleSettingRef(
    componentPropertyService: ComponentSettingService,
    component: String,
    property: String,
    defaultValue: String,
    cache: Boolean = true,
    fieldName: String = "",
    description: String = "",
    minRoleToEdit: RoleType = RoleType.STAFF,
    val grantedForRoles: List<String> = listOf(RoleType.ADMIN.name, RoleType.SUPERUSER.name)
) : SettingRef<String>(
    componentPropertyService, StringSettingSerializer, component, property,
    defaultValue = defaultValue, type = SettingType.MIN_ROLE,
    fieldName = fieldName, description = description, cache = cache,
    minRoleToEdit = minRoleToEdit, serverSideOnly = true, persist = true, strictConversion = true,
) {

    companion object {
        val ALL_ROLES by lazy { RoleType.entries.joinToString(",") { it.name } }
        val ALL_ROLES_FROM_ATTENDEE by lazy {
            RoleType.entries.filter { it.value >= RoleType.ATTENDEE.value }.joinToString(",") { it.name }
        }
        val ALL_ROLES_FROM_PRIVILEGED by lazy {
            RoleType.entries.filter { it.value >= RoleType.PRIVILEGED.value }.joinToString(",") { it.name }
        }
        val ALL_POSSIBLE_ROLES by lazy { RoleType.entries.filter { it.value != RoleType.NOBODY.value } }
    }

    fun isAvailableForRole(role: RoleType): Boolean {
        return getValue().split(",").contains(role.name) || grantedForRoles.contains(role.name)
    }

}
