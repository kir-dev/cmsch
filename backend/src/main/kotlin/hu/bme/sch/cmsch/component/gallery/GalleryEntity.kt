package hu.bme.sch.cmsch.component.gallery

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.Duplicatable
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name = "gallery")
@ConditionalOnBean(GalleryComponent::class)
data class GalleryEntity(
    @Id
    @GeneratedValue
    @field:JsonView(value = [Edit::class])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Cím")
    @property:GenerateOverview(columnName = "Cím", order = 1, useForSearch = true)
    @property:ImportFormat
    var title: String = "",

    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.SWITCH, order = 2, label = "Ezek a képek szerepelnek először")
    @property:GenerateOverview(columnName = "Kiemelt", order = 2, centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var highlighted: Boolean = false,

    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.SWITCH, order = 3, label = "Megjelenhet a kezdőlapon")
    @property:GenerateOverview(columnName = "Kezdőlapra", order = 3, centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var showOnHomePage: Boolean = false,

    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @Column(nullable = false)
    @property:GenerateInput(
        maxLength = 64, order = 4, label = "Url",
        note = "A galériában tárolt kép linkje"
    )
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var url: String = "",
) : ManagedEntity, Duplicatable {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "GalleryEntity",
        view = "control/gallery",
        showPermission = StaffPermissions.PERMISSION_SHOW_GALLERY
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as GalleryEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }

    override fun duplicate(): GalleryEntity {
        return this.copy()
    }

}
