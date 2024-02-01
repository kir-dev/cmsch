package hu.bme.sch.cmsch.component.conference

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.component.communities.CommunityEntity
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import hu.bme.sch.cmsch.util.StringToArraySerializer
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

enum class RoomType {
    IB028,
    IB025,
    OTHERS,
}

enum class LanguageType {
    EN,
    HU;

    @JsonValue
    fun getCustomString(): String {
        return name.lowercase()
    }
}

@Entity
@Table(name="conferencePresentations")
@ConditionalOnBean(ConferenceComponent::class)
data class ConferencePresentationEntity(
    @Id
    @field:JsonIgnore
    @GeneratedValue
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Slug")
    @property:GenerateOverview(columnName = "Slug", order = 2)
    @property:ImportFormat(ignore = false)
    var slug: String = "",

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 2, label = "Cím")
    @property:GenerateOverview(columnName = "Cím", order = 1)
    @property:ImportFormat(ignore = false)
    var title: String = "",

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 3, label = "Terem", source = [ "IB028", "IB025", "OTHERS" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, type = IMPORT_ENUM)
    var room: RoomType = RoomType.OTHERS,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 4, label = "Nyelv", source = [ "HU", "EN" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, type = IMPORT_ENUM)
    var language: LanguageType = LanguageType.HU,

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 5, label = "Kezdet ideje")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var startTime: String = "",

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 6, label = "Eddig tart")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var endTime: String = "",

    @Lob
    @Column(nullable = false)
    @property:GenerateInput(order = 7, type = INPUT_TYPE_BLOCK_TEXT, label = "Leírás", enabled = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, type = IMPORT_LOB)
    var description: String = "",

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 255, order = 8, label = "Kérdések URL")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var questionsUrl: String = "",

    @field:JsonIgnore
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 4, label = "Előadó a seletora",
        note = "Annak az előadónak a selectora ami ehhez az előadáshoz van rendelve. " +
                "Ha üres vagy nem létező cég van megadva, akkor null lesz.")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var presenterSelector: String = "",

    @field:JsonIgnore
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 9, label = "Selector",
        note = "Ez a mező van arra használva, hogy meg tudd hivatkozni ezt az entitást. " +
                "Egyedinek kell lennie, hogy működjön!")
    @property:GenerateOverview(columnName = "Selector", order = 1)
    @property:ImportFormat(ignore = false)
    var selector: String = "",

    @field:JsonIgnore
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 10, label = "Látható")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, type = IMPORT_BOOLEAN)
    var visible: Boolean = false,

) : ManagedEntity {

    @Transient
    var presenter: ConferencePresenterEntity? = null

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "ConferencePresentation",
        view = "control/conference-presentation",
        showPermission = StaffPermissions.PERMISSION_SHOW_CONFERENCE
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ConferencePresentationEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, title = $title)"
    }

}
