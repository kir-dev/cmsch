package hu.bme.sch.cmsch.component.qrfight

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import jakarta.persistence.*

@Entity
@Table(name="qrLevels")
@ConditionalOnBean(QrFightComponent::class)
data class QrLevelEntity(

    @Id
    @GeneratedValue
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Szint neve")
    @property:GenerateOverview(columnName = "Név", order = 1)
    @property:ImportFormat(ignore = false, columnId = 0)
    var displayName: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 2, label = "Kategória",
        note = "Azonosnak kell legyen a token kategóriájával. Az egymásra épülésnél is ezt kell használni. Szintek között legyen egyedi.")
    @property:GenerateOverview(columnName = "Kategória", order = 4, centered = true, )
    @property:ImportFormat(ignore = false, columnId = 1)
    var category: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 3, label = "Szint elérhető", note = "Az endpointok működnek-e hozzá")
    @property:GenerateOverview(columnName = "Elérhető", order = 2, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 2, type = IMPORT_BOOLEAN)
    var enabled: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 4, label = "Szint látható", note = "A felületen látható-e")
    @property:GenerateOverview(columnName = "Látható", order = 3, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 3, type = IMPORT_BOOLEAN)
    var visible: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false, name = "`order`")
    @property:GenerateInput(order = 5, label = "Sorrend", type = INPUT_TYPE_NUMBER, defaultValue = "0",
        note = "Egész szám, ami alapján rendezve lesz")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_INT)
    var order: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 6, label = "Elérhető ekkortól")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 5, type = IMPORT_LONG)
    var availableFrom: Long = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 7, label = "Elérhető eddig")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 6, type = IMPORT_LONG)
    var availableTo: Long = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 8, label = "Min. token a teljesítéshez")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 7, type = IMPORT_INT)
    var minAmountToComplete: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(maxLength = 64, order = 9, label = "Előfeltétel", note = "Üres string vagy a kategória neve")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 8)
    var dependsOn: String = "",

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN, order = 10, label = "Leírás amég nem elérhető")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 9, type = IMPORT_LOB)
    var hintBeforeEnabled: String = "",

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN, order = 11, label = "Leírás ha elérhető")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 10, type = IMPORT_LOB)
    var hintWhileOpen: String = "",

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN, order = 12, label = "Leírás miután teljesítve lett")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 11, type = IMPORT_LOB)
    var hintAfterCompleted: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 13, label = "Extra szint",
        note = "Külön látszanak a sima szintektől")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 12, type = IMPORT_BOOLEAN)
    var extraLevel: Boolean = false,

): ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "QRFightLevel",
        view = "control/qr-levels",
        showPermission = StaffPermissions.PERMISSION_SHOW_QR_FIGHT
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as QrLevelEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, name = '$displayName', category = $category)"
    }

}
