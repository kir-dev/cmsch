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
@Table(name="qrTowers")
@ConditionalOnBean(QrFightComponent::class)
data class QrTowerEntity(

    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 1, label = "Torony neve")
    @property:GenerateOverview(columnName = "Név", order = 1)
    @property:ImportFormat(ignore = false, columnId = 0)
    var displayName: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 2, label = "Selector név",
        note = "Ez alapján lesz kiválasztható token olvasáskor")
    @property:GenerateOverview(columnName = "Selector", order = 2)
    @property:ImportFormat(ignore = false, columnId = 1)
    var selector: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 3, label = "Kategória",
        note = "Ez alapján lesz szinthez hozzárendelve")
    @property:GenerateOverview(columnName = "Kategória", order = 3)
    @property:ImportFormat(ignore = false, columnId = 2)
    var category: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 4, label = "Lezárva")
    @property:GenerateOverview(columnName = "Lezárva", order = 4, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 3, type = IMPORT_BOOLEAN)
    var locked: Boolean = false,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 5, label = "Foglalható ekkortól")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_LONG)
    var availableFrom: Long = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 6, label = "Foglalható eddig")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 5, type = IMPORT_LONG)
    var availableTo: Long = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 7, label = "Tulajdonos felhasználó ID-je", defaultValue = "0",
        note = "Ezt a rendszer majd magának tartja karban, nem kell ide semmit se írni.")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 6, type = IMPORT_INT)
    var ownerUserId: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 8, label = "Tulajdonos felhasználó neve",
        note = "Ez csak megjegyzés, nem ez alapján számolódik. " +
                "Ezt a rendszer majd magának tartja karban, nem kell ide semmit se írni.")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 7)
    var ownerUserName: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 9, label = "Tulajdonos csoport ID-je", defaultValue = "0",
        note = "Ezt a rendszer majd magának tartja karban, nem kell ide semmit se írni.")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 8, type = IMPORT_INT)
    var ownerGroupId: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 10, label = "Tulajdonos csoport neve",
        note = "Ez csak megjegyzés, nem ez alapján számolódik. " +
                "Ezt a rendszer majd magának tartja karban, nem kell ide semmit se írni.")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 9)
    var ownerGroupName: String = "",

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN, order = 11, label = "Publikus leírás")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 10, type = IMPORT_LOB)
    var publicMessage: String = "",

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN, order = 12, label = "Leírás a tulajdonosoknak")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 11, type = IMPORT_LOB)
    var ownerMessage: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 13, label = "Idő logolása", note = "10 percenként eggyel megnöveli a tulaj sorát")
    @property:GenerateOverview(columnName = "Számlál", order = 5, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 12, type = IMPORT_BOOLEAN)
    var recordTime: Boolean = false,

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 14, label = "Beolvasás log")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 13, type = IMPORT_LOB)
    var history: String = "",

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 15, label = "Birtoklás állása")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 14, type = IMPORT_LOB)
    var state: String = "",

    @Column(nullable = false, length = 64)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_TEXT, order = 16, label = "Helytartó",
        note = "Ha a birtokos felhasználó az ID-je, ha csoport akkor a neve. " +
                "Ezt a rendszer majd magának tartja karban, nem kell ide semmit se írni.")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 15)
    var holder: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 17, label = "Helytartás ennyi időegysége (perc)",
        note = "Ezt a rendszer majd magának tartja karban, nem kell ide semmit se írni.", defaultValue = "0")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 16, type = IMPORT_INT)
    var holderFor: Int = 0,

    @Column(nullable = false, columnDefinition = "BOOLEAN default false")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 18, label = "Totem", note = "A totemek véglegesen kerülnek foglalásra, nem váltanak gazdát")
    @property:GenerateOverview(columnName = "Totem", centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 17, type = IMPORT_BOOLEAN)
    var totem: Boolean = false,

) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "QRFightTower",
        view = "control/qr-towers",
        showPermission = StaffPermissions.PERMISSION_SHOW_QR_FIGHT
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as QrTowerEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, name = '$displayName', selector = $selector)"
    }

}
