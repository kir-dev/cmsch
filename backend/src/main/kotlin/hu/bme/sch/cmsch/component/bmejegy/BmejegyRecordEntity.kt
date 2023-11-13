package hu.bme.sch.cmsch.component.bmejegy

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import jakarta.persistence.*

@Entity
@Table(name="bmejegyRecords")
@ConditionalOnBean(BmejegyComponent::class)
data class BmejegyRecordEntity(

    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id : Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 1, label = "Termék")
    @property:GenerateOverview(columnName = "Termék", order = 1)
    @property:ImportFormat(ignore = false, columnId = 0)
    var item: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 2, label = "Vásárló")
    @property:GenerateOverview(columnName = "Vásárló", order = 2)
    @property:ImportFormat(ignore = false, columnId = 1)
    var fullName: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 3, label = "Státusz")
    @property:GenerateOverview(columnName = "Státusz", order = 4)
    @property:ImportFormat(ignore = false, columnId = 2)
    var status: String = "N/A",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 4, label = "Rendelés kulcs", note = "order_key mező")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 3)
    var orderKey: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 256, order = 5, label = "Email")
    @property:GenerateOverview(columnName = "Email", order = 3)
    @property:ImportFormat(ignore = false, columnId = 4)
    var email: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 6, label = "QR")
    @property:GenerateOverview(columnName = "QR", order = 5)
    @property:ImportFormat(ignore = false, columnId = 5)
    var qrCode: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 7, label = "Szig. szám")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 6)
    var photoId: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 8, label = "Teljesítve ekkorra")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 7)
    var date: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 9, label = "Beérkezett ekkor")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 8, type = IMPORT_LONG)
    var registered: Long = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 10, label = "Rendelés azonosító", note = "ID mező")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 9)
    var idId: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 11, label = "Rendelés termék azonosító", note = "order_item_id mező")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 10)
    var itemId: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 12, label = "Rendelés összege")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 11)
    var total: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 13, label = "Szak", note = "Nem mindig van kitöltve")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 12)
    var faculty: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 14, label = "Beazonosított user ID-ja",
        note = "Csak akkor írd át ha tudod mit csinálsz")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 13, type = IMPORT_INT)
    var matchedUserId: Int = 0,

    @Lob
    @Column(nullable = false, columnDefinition = "CLOB default ''")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 15, label = "Összes adat")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 14, type = IMPORT_LOB)
    var rawData: String = "",
) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "BmejegyRecord",
        view = "control/bmejegy-tickets",
        showPermission = StaffPermissions.PERMISSION_SHOW_BME_TICKET
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BmejegyRecordEntity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return "BmejegyRecordEntity(id=$id, item='$item', fullName='$fullName')"
    }


}
