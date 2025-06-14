package hu.bme.sch.cmsch.setting

import hu.bme.sch.cmsch.model.RoleType

abstract class DummySetting<T : Any>(
    override val component: String,
    override val property: String,
    override val type: SettingType,
    override val fieldName: String,
    override val description: String,
    override val minRoleToEdit: RoleType,
    override val isServerSideOnly: Boolean,
) : Setting<T> {

    override fun getValue() = throw NotImplementedError()

}

data class SettingGroup(
    override val component: String,
    override val property: String,
    override val fieldName: String,
    override val description: String = "",
    override val minRoleToEdit: RoleType = RoleType.ADMIN,
) : DummySetting<Any>(
    type = SettingType.COMPONENT_GROUP,
    isServerSideOnly = true,
    component = component,
    property = property,
    fieldName = fieldName,
    description = description,
    minRoleToEdit = minRoleToEdit,
)
