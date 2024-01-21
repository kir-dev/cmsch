package hu.bme.sch.cmsch.component.conference

import com.fasterxml.jackson.annotation.JsonIgnore
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name="conferences")
@ConditionalOnBean(ConferenceComponent::class)
data class ConferenceEntity(
    @Id
    @field:JsonIgnore
    @GeneratedValue
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 255, order = 1, label = "Név")
    @property:GenerateOverview(visible = true, columnName = "Név", order = 1)
    @property:ImportFormat(ignore = false)
    var title: String = "",

    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 2, label = "Prioritás", defaultValue = "0")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, type = IMPORT_INT)
    var priority: Int = 0,

    @Lob
    @field:JsonIgnore
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 65535, order = 3, label = "Képek URL-jei",
        note = "A képek URL-jeit vesszővel elválasztva kell megadni.")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var imageUrlsArray: String = "",
) : ManagedEntity {

    @get:Transient
    val imageUrls: List<String>
        get() = imageUrlsArray.split(", *".toRegex())

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "Conference",
        view = "control/conference-entity",
        showPermission = StaffPermissions.PERMISSION_SHOW_CONFERENCE
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ConferenceCompanyEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, title = $title)"
    }

}