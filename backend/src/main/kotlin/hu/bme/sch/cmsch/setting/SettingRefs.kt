package hu.bme.sch.cmsch.setting

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import kotlin.reflect.KProperty


data class StringSettingRef(
    private val defaultValue: String = "",
    private val strictConversion: Boolean = true,
    private val cache: Boolean = true,
    private val persist: Boolean = true,
    private val serverSideOnly: Boolean = false,
    private val type: SettingType = SettingType.TEXT,
    private val fieldName: String? = null,
    private val description: String = "",
    private val minRoleToEdit: RoleType = RoleType.ADMIN
) : SettingRegisteringLoader<SettingRef<String>>() {

    override fun provideSetting(thisRef: ComponentBase, prop: KProperty<*>): SettingRef<String> =
        SettingRef(
            componentPropertyService = thisRef.componentSettingService,
            serializer = StringSettingSerializer,
            component = thisRef.component,
            property = prop.name,
            defaultValue = defaultValue,
            strictConversion = strictConversion,
            cache = cache,
            persist = persist,
            serverSideOnly = serverSideOnly,
            type = type,
            fieldName = fieldName ?: prop.name,
            description = description,
            minRoleToEdit = minRoleToEdit,
        )

}

data class SelectSettingRef(
    private val defaultValue: String,
    private val options: Set<String>,
    private val strictConversion: Boolean = true,
    private val cache: Boolean = true,
    private val persist: Boolean = true,
    private val serverSideOnly: Boolean = false,
    private val type: SettingType = SettingType.SELECT,
    private val fieldName: String? = null,
    private val description: String = "",
    private val minRoleToEdit: RoleType = RoleType.ADMIN,
) : SettingRegisteringLoader<SettingRef<String>>() {

    override fun provideSetting(thisRef: ComponentBase, prop: KProperty<*>): SettingRef<String> =
        object : SettingRef<String>(
            componentPropertyService = thisRef.componentSettingService,
            serializer = SelectSettingSerializer(options),
            component = thisRef.component,
            property = prop.name,
            defaultValue = defaultValue,
            strictConversion = strictConversion,
            cache = cache,
            persist = persist,
            serverSideOnly = serverSideOnly,
            type = type,
            fieldName = fieldName ?: prop.name,
            description = description,
            minRoleToEdit = minRoleToEdit,
        ) {
            val options = SelectSettingRef::options
        }

}

data class EnumSettingRef<T>(
    private val defaultValue: T,
    private val strictConversion: Boolean = true,
    private val cache: Boolean = true,
    private val persist: Boolean = true,
    private val serverSideOnly: Boolean = false,
    private val type: SettingType = SettingType.SELECT,
    private val fieldName: String? = null,
    private val description: String = "",
    private val minRoleToEdit: RoleType = RoleType.ADMIN
) : SettingRegisteringLoader<SettingRef<T>>() where T : Enum<T> {

    override fun provideSetting(thisRef: ComponentBase, prop: KProperty<*>): SettingRef<T> =
        object : SettingRef<T>(
            componentPropertyService = thisRef.componentSettingService,
            serializer = EnumSettingSerializer(defaultValue.javaClass),
            component = thisRef.component,
            property = prop.name,
            defaultValue = defaultValue,
            strictConversion = strictConversion,
            cache = cache,
            persist = persist,
            serverSideOnly = serverSideOnly,
            type = type,
            fieldName = fieldName ?: prop.name,
            description = description,
            minRoleToEdit = minRoleToEdit,
        ) {
            val options = defaultValue.javaClass.enumConstants
        }

}

data class JsonSettingRef<T : Any>(
    private val defaultValue: T,
    private val strictConversion: Boolean = true,
    private val cache: Boolean = true,
    private val persist: Boolean = true,
    private val serverSideOnly: Boolean = false,
    private val type: SettingType = SettingType.JSON,
    private val fieldName: String? = null,
    private val description: String = "",
    private val minRoleToEdit: RoleType = RoleType.ADMIN
) : SettingRegisteringLoader<SettingRef<T>>() {

    override fun provideSetting(thisRef: ComponentBase, prop: KProperty<*>): SettingRef<T> =
        SettingRef(
            componentPropertyService = thisRef.componentSettingService,
            serializer = JsonSettingSerializer(),
            component = thisRef.component,
            property = prop.name,
            defaultValue = defaultValue,
            strictConversion = strictConversion,
            cache = cache,
            persist = persist,
            serverSideOnly = serverSideOnly,
            type = type,
            fieldName = fieldName ?: prop.name,
            description = description,
            minRoleToEdit = minRoleToEdit,
        )

}

data class NumberSettingRef(
    private val defaultValue: Long = 0,
    private val strictConversion: Boolean = true,
    private val cache: Boolean = true,
    private val persist: Boolean = true,
    private val serverSideOnly: Boolean = false,
    private val type: SettingType = SettingType.NUMBER,
    private val fieldName: String? = null,
    private val description: String = "",
    private val minRoleToEdit: RoleType = RoleType.ADMIN
) : SettingRegisteringLoader<SettingRef<Long>>() {

    override fun provideSetting(thisRef: ComponentBase, prop: KProperty<*>): SettingRef<Long> =
        SettingRef(
            componentPropertyService = thisRef.componentSettingService,
            serializer = LongSettingSerializer,
            component = thisRef.component,
            property = prop.name,
            defaultValue = defaultValue,
            strictConversion = strictConversion,
            cache = cache,
            persist = persist,
            serverSideOnly = serverSideOnly,
            type = type,
            fieldName = fieldName ?: prop.name,
            description = description,
            minRoleToEdit = minRoleToEdit,
        )

}

data class BooleanSettingRef(
    private val defaultValue: Boolean = false,
    private val strictConversion: Boolean = true,
    private val cache: Boolean = true,
    private val persist: Boolean = true,
    private val serverSideOnly: Boolean = false,
    private val type: SettingType = SettingType.BOOLEAN,
    private val fieldName: String? = null,
    private val description: String = "",
    private val minRoleToEdit: RoleType = RoleType.ADMIN
) : SettingRegisteringLoader<SettingRef<Boolean>>() {

    override fun provideSetting(thisRef: ComponentBase, prop: KProperty<*>): SettingRef<Boolean> =
        SettingRef(
            componentPropertyService = thisRef.componentSettingService,
            serializer = BooleanSettingSerializer,
            component = thisRef.component,
            property = prop.name,
            defaultValue = defaultValue,
            strictConversion = strictConversion,
            cache = cache,
            persist = persist,
            serverSideOnly = serverSideOnly,
            type = type,
            fieldName = fieldName ?: prop.name,
            description = description,
            minRoleToEdit = minRoleToEdit,
        )


}

class MinRoleSettingRef(
    private val defaultValue: Set<RoleType>,
    private val cache: Boolean = true,
    private val fieldName: String? = null,
    private val description: String = "",
    private val minRoleToEdit: RoleType = RoleType.STAFF,
    private val grantedForRoles: Set<RoleType> = setOf(RoleType.ADMIN, RoleType.SUPERUSER)
) : SettingRegisteringLoader<SettingRef<Set<RoleType>>>() {


    companion object {
        val ALL_ROLES by lazy { RoleType.entries.toSet() }
        val ALL_ROLES_FROM_ATTENDEE by lazy { RoleType.entries.filter { it.value >= RoleType.ATTENDEE.value }.toSet() }
        val ALL_ROLES_FROM_PRIVILEGED by lazy {
            RoleType.entries.filter { it.value >= RoleType.PRIVILEGED.value }.toSet()
        }
        val ALL_POSSIBLE_ROLES by lazy { RoleType.entries.filter { it.value != RoleType.NOBODY.value }.toSet() }
    }

    override fun provideSetting(thisRef: ComponentBase, prop: KProperty<*>): SettingRef<Set<RoleType>> =
        SettingRef(
            componentPropertyService = thisRef.componentSettingService,
            serializer = RoleTypeSetSettingSerializer(grantedForRoles),
            component = thisRef.component,
            property = prop.name,
            defaultValue = defaultValue,
            type = SettingType.MIN_ROLE,
            fieldName = fieldName ?: prop.name,
            description = description,
            cache = cache,
            minRoleToEdit = minRoleToEdit,
            serverSideOnly = true,
            persist = true,
            strictConversion = true,
        )

}
