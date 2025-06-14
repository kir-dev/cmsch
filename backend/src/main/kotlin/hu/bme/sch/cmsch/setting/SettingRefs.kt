package hu.bme.sch.cmsch.setting

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import kotlin.reflect.KProperty


data class StringSettingRef(
    private val componentPropertyService: ComponentSettingService,
    private val component: String,
    private val property: String,
    private val defaultValue: String = "",
    private val strictConversion: Boolean = true,
    private val cache: Boolean = true,
    private val persist: Boolean = true,
    private val serverSideOnly: Boolean = false,
    private val type: SettingType = SettingType.TEXT,
    private val fieldName: String = property,
    private val description: String = "",
    private val minRoleToEdit: RoleType = RoleType.ADMIN
) {

    operator fun provideDelegate(thisRef: ComponentBase, prop: KProperty<*>): SettingRef<String> =
        SettingRef(
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

}

data class JsonSettingRef(
    private val componentPropertyService: ComponentSettingService,
    private val component: String,
    private val property: String,
    private val defaultValue: List<Map<String, Any>> = emptyList(),
    private val strictConversion: Boolean = true,
    private val cache: Boolean = true,
    private val persist: Boolean = true,
    private val serverSideOnly: Boolean = false,
    private val type: SettingType = SettingType.JSON,
    private val fieldName: String = property,
    private val description: String = "",
    private val minRoleToEdit: RoleType = RoleType.ADMIN
) {

    operator fun provideDelegate(thisRef: ComponentBase, prop: KProperty<*>): SettingRef<List<Map<String, Any>>> =
        SettingRef(
            componentPropertyService = componentPropertyService,
            serializer = JsonSettingSerializer,
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

}

data class NumberSettingRef(
    private val componentPropertyService: ComponentSettingService,
    private val component: String,
    private val property: String,
    private val defaultValue: Long = 0,
    private val strictConversion: Boolean = true,
    private val cache: Boolean = true,
    private val persist: Boolean = true,
    private val serverSideOnly: Boolean = false,
    private val type: SettingType = SettingType.NUMBER,
    private val fieldName: String = property,
    private val description: String = "",
    private val minRoleToEdit: RoleType = RoleType.ADMIN
) {

    operator fun provideDelegate(thisRef: ComponentBase, prop: KProperty<*>): SettingRef<Long> =
        SettingRef(
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

}

data class BooleanSettingRef(
    private val componentPropertyService: ComponentSettingService,
    private val component: String,
    private val property: String,
    private val defaultValue: Boolean = false,
    private val strictConversion: Boolean = true,
    private val cache: Boolean = true,
    private val persist: Boolean = true,
    private val serverSideOnly: Boolean = false,
    private val type: SettingType = SettingType.BOOLEAN,
    private val fieldName: String = property,
    private val description: String = "",
    private val minRoleToEdit: RoleType = RoleType.ADMIN
) {

    operator fun provideDelegate(thisRef: ComponentBase, prop: KProperty<*>): SettingRef<Boolean> =
        SettingRef(
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
        )


}

class MinRoleSettingRef(
    private val componentPropertyService: ComponentSettingService,
    private val component: String,
    private val property: String,
    private val defaultValue: Set<RoleType>,
    private val cache: Boolean = true,
    private val fieldName: String = "",
    private val description: String = "",
    private val minRoleToEdit: RoleType = RoleType.STAFF,
    private val grantedForRoles: Set<RoleType> = setOf(RoleType.ADMIN, RoleType.SUPERUSER)
) {

    companion object {
        val ALL_ROLES by lazy { RoleType.entries.toSet() }
        val ALL_ROLES_FROM_ATTENDEE by lazy { RoleType.entries.filter { it.value >= RoleType.ATTENDEE.value }.toSet() }
        val ALL_ROLES_FROM_PRIVILEGED by lazy {
            RoleType.entries.filter { it.value >= RoleType.PRIVILEGED.value }.toSet()
        }
        val ALL_POSSIBLE_ROLES by lazy { RoleType.entries.filter { it.value != RoleType.NOBODY.value }.toSet() }
    }

    operator fun provideDelegate(thisRef: ComponentBase, prop: KProperty<*>): SettingRef<Set<RoleType>> =
        SettingRef(
            componentPropertyService,
            RoleTypeSetSettingSerializer(grantedForRoles),
            component,
            property,
            defaultValue = defaultValue,
            type = SettingType.MIN_ROLE,
            fieldName = fieldName,
            description = description,
            cache = cache,
            minRoleToEdit = minRoleToEdit,
            serverSideOnly = true,
            persist = true,
            strictConversion = true,
        )

}
