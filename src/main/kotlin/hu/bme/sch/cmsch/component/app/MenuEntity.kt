package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.model.RoleType
import org.hibernate.Hibernate
import javax.persistence.*

@Entity
@Table(name="users")
data class MenuEntity(

    @Id
    @GeneratedValue
    @Column(nullable = false)
    var id: Int = 0,

    @Column(nullable = false)
    var role: RoleType = RoleType.NOBODY,

    @Lob
    @Column(nullable = false)
    var menuJson: String = "{}",

) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as MenuEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }

}
