package hu.bme.sch.cmsch.setting

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import kotlin.reflect.KProperty

class DummySetting(
    override val component: String,
    override val property: String,
    override val type: SettingType,
    override val fieldName: String,
    override val description: String,
    override val minRoleToEdit: RoleType,
    override val isServerSideOnly: Boolean,
) : Setting<Any> {

    override fun getValue() = ""

}

data class SettingGroup(
    private val fieldName: String,
    private val description: String = "",
    private val minRoleToEdit: RoleType = RoleType.ADMIN,
) : SettingRegisteringLoader<DummySetting>() {

    override fun provideSetting(thisRef: ComponentBase, prop: KProperty<*>): DummySetting =
        DummySetting(
            type = SettingType.COMPONENT_GROUP,
            isServerSideOnly = true,
            component = thisRef.component,
            property = prop.name,
            fieldName = fieldName,
            description = description,
            minRoleToEdit = minRoleToEdit,
        )

}
