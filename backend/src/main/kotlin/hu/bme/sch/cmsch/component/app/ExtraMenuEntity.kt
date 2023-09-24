package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.ControlPermissions
import org.hibernate.Hibernate
import org.springframework.core.env.Environment
import jakarta.persistence.*

@Entity
@Table(name="extraMenus")
data class ExtraMenuEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 1, label = "Név", note = "A menü neve")
    @property:GenerateOverview(columnName = "Név", order = 1)
    var name: String = "",

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 255, order = 2, label = "Url", note = "Az url ahova irányítson. " +
            "Ha külső akkor kell protokoll megjelölés is, ha csak kategória, akkor hagyd üresen.")
    @property:GenerateOverview(columnName = "Url", order = 2)
    var url: String = "",

    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 3, label = "Külső hivatkozás", note = "A menü külső címre továbbítson-e át")
    @property:GenerateOverview(visible = false)
    var external: Boolean = false,

) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "ExtraMenu",
        view = "control/extra-menus",
        showPermission = ControlPermissions.PERMISSION_CONTROL_APP
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ExtraMenuEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
