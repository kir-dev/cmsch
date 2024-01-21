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

enum class SponsorCategory {
    MAIN_SPONSOR,
    FEATURED_SPONSOR,
    SPONSOR,
    NO_ASSOCIATION,
}

@Entity
@Table(name="conferenceCompanies")
@ConditionalOnBean(ConferenceComponent::class)
data class ConferenceCompanyEntity(
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
    var name: String = "",

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 255, order = 2, label = "Logó URL")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var logoUrl: String = "",

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 255, order = 3, label = "URL")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var url: String = "",

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 4, label = "Kategória",
        source = [ "MAIN_SPONSOR", "FEATURED_SPONSOR", "SPONSOR", "NO_ASSOCIATION" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, type = IMPORT_ENUM)
    var category: SponsorCategory = SponsorCategory.NO_ASSOCIATION,

    @field:JsonIgnore
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 5, label = "Selector",
        note = "Ez a mező van arra használva, hogy meg tudd hivatkozni ezt az entitást. " +
                "Egyedinek kell lennie, hogy működjön!")
    @property:GenerateOverview(columnName = "Selector", order = 2)
    @property:ImportFormat(ignore = false)
    var selector: String = "",

    @field:JsonIgnore
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 5, label = "Látható")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, type = IMPORT_BOOLEAN)
    var visible: Boolean = false

) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "ConferenceCompany",
        view = "control/conference-company",
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
        return this::class.simpleName + "(id = $id, name = $name)"
    }

}