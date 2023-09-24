package hu.bme.sch.cmsch.component.communities

import com.fasterxml.jackson.annotation.JsonView
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import hu.bme.sch.cmsch.util.StringToArraySerializer
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import jakarta.persistence.*

@Entity
@Table(name="organizationEntities")
@ConditionalOnBean(CommunitiesComponent::class)
data class OrganizationEntity(

    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class, Preview::class ])
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class, Preview::class ])
    @property:GenerateInput(order = 1, label = "Reszort neve", enabled = true)
    @property:GenerateOverview(columnName = "Név", order = 2)
    @property:ImportFormat(ignore = false, columnId = 0)
    var name: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 2, label = "Név rejtett",
        note = "El legyen rejtve a név az oldalon vagy ne (csak a logó elég)")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 1, type = IMPORT_BOOLEAN)
    var hideName: Boolean = false,

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class, Preview::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 3, label = "Rövid leírás")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 2)
    var shortDescription: String = "",

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN, order = 4, label = "Teljes leírás")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 3)
    var descriptionParagraphs: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(order = 5, label = "Website url", enabled = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 4)
    var website: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class, Preview::class ])
    @property:GenerateInput(order = 6, label = "Logó url", enabled = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 5)
    var logo: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(order = 7, label = "Sötét logó url", enabled = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 6)
    var darkLogo: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(order = 8, label = "Alapítva", enabled = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 7)
    var established: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(order = 9, label = "E-mail cím", enabled = true,
        note = "Amin elérhető a kör")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 8)
    var email: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, min = 0, order = 10, label = "Tagok száma")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 9, type = IMPORT_INT)
    var members: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(order = 11, label = "Szín", enabled = true,
        note = "Ezzel a színnel jelenik meg az oldalon")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 10)
    var color: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class, Preview::class ])
    @property:GenerateInput(order = 12, label = "Érdeklődési körök", enabled = true,
        note = "Az értékeket vesszővel elválasztva írd be (pl: alma, körte, barack)")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 11)
    @get:JsonSerialize(using = StringToArraySerializer::class)
    var interests: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(order = 13, label = "Facebook URL", enabled = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 12)
    var facebook: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(order = 14, label = "Instagram URL", enabled = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 13)
    var instagram: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(order = 15, label = "Jelentkezés URL-je", enabled = true,
        note = "Ha nincs, akkor az lesz kiírva, hogy személyesen lehet jelentkezni")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 14)
    var application: String = "",

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(order = 16, type = INPUT_TYPE_BLOCK_TEXT, label = "Képek URL-jei", enabled = true,
        note = "Az értékeket vesszővel elválasztva írd be (pl: alma, körte, barack)")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 15, type = IMPORT_LOB)
    @get:JsonSerialize(using = StringToArraySerializer::class)
    var imageIds: String = "",

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(order = 17, type = INPUT_TYPE_BLOCK_TEXT, label = "Videók URL-jei", enabled = true,
        note = "Az értékeket vesszővel elválasztva írd be (pl: alma, körte, barack)")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 16, type = IMPORT_LOB)
    @get:JsonSerialize(using = StringToArraySerializer::class)
    var videoIds: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 18, label = "Látható")
    @property:GenerateOverview(columnName = "Látható", order = 3, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 17, type = IMPORT_BOOLEAN)
    var visible: Boolean = false,

) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "Organization",
        view = "control/organization",
        showPermission = StaffPermissions.PERMISSION_SHOW_COMMUNITIES
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as OrganizationEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, name = $name)"
    }

}
