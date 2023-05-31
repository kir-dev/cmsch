package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.model.RoleType
import org.hibernate.Hibernate
import java.io.Serializable
import java.util.*
import jakarta.persistence.*

@Entity
@Table(name="menus")
@IdClass(MenuId::class)
data class MenuEntity(
    @Id
    @Column(nullable = false)
    var menuId: String = "",

    @Id
    @Enumerated(EnumType.STRING)
    var role: RoleType = RoleType.NOBODY,

    @Column(nullable = false, name = "`order`")
    var order: Int = 0,

    @Column(nullable = false)
    var visible: Boolean = false,

    @Column(nullable = false)
    var subMenu: Boolean = false
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as MenuEntity

        return menuId == other.menuId
                && role == other.role
    }

    override fun hashCode(): Int = Objects.hash(menuId, role)

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(menuId = $menuId , role = $role )"
    }

}

data class MenuId(
    var menuId: String = "",
    var role: RoleType = RoleType.NOBODY
) : Serializable
