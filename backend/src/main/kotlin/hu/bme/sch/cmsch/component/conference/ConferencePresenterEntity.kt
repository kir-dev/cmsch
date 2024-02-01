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
@Table(name="conferencePresenters")
@ConditionalOnBean(ConferenceComponent::class)
data class ConferencePresenterEntity(
    @Id
    @field:JsonIgnore
    @GeneratedValue
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Név")
    @property:GenerateOverview(columnName = "Név", order = 1)
    @property:ImportFormat(ignore = false)
    var name: String = "",

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 2, label = "Beosztás")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var rank: String = "",

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 255, order = 3, label = "Fotó URL")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var pictureUrl: String = "",

    @field:JsonIgnore
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 4, label = "Cégének a seletora",
        note = "Annak a cégnek a selectora ami ehhez az előadóhoz van rendelve. " +
                "Ha üres vagy nem létező cég van megadva, akkor null lesz.")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var companySelector: String = "",

    @field:JsonIgnore
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 5, label = "Selector",
        note = "Ezzel lehet hivatkozni erre az előadóra.")
    @property:GenerateOverview(visible = true, columnName = "Selector", order = 2)
    @property:ImportFormat(ignore = false)
    var selector: String = "",

    @field:JsonIgnore
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 6, label = "Látható")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, type = IMPORT_BOOLEAN)
    var visible: Boolean = false

) : ManagedEntity {

    @Transient
    var company: ConferenceCompanyEntity? = null

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "ConferencePresenter",
        view = "control/conference-presenter",
        showPermission = StaffPermissions.PERMISSION_SHOW_CONFERENCE
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ConferencePresenterEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, name = $name)"
    }

}
