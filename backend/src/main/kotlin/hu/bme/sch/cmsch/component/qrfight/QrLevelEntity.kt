package hu.bme.sch.cmsch.component.qrfight

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
@Table(name="qrLevels")
@ConditionalOnBean(QrFightComponent::class)
data class QrLevelEntity(

    @Id
    @GeneratedValue
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Szint neve")
    @property:GenerateOverview(columnName = "Név", order = 1)
    @property:ImportFormat
    var displayName: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 2, label = "Kategória",
        note = "Azonosnak kell legyen a token kategóriájával. Az egymásra épülésnél is ezt kell használni. Szintek között legyen egyedi.")
    @property:GenerateOverview(columnName = "Kategória", order = 4, centered = true, )
    @property:ImportFormat
    var category: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.SWITCH, order = 3, label = "Szint elérhető", note = "Az endpointok működnek-e hozzá")
    @property:GenerateOverview(columnName = "Elérhető", order = 2, centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var enabled: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.SWITCH, order = 4, label = "Szint látható", note = "A felületen látható-e")
    @property:GenerateOverview(columnName = "Látható", order = 3, centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var visible: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false, name = "`order`")
    @property:GenerateInput(order = 5, label = "Sorrend", type = InputType.NUMBER, defaultValue = "0",
        note = "Egész szám, ami alapján rendezve lesz")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var order: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.DATE, defaultValue = "0", order = 6, label = "Elérhető ekkortól")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var availableFrom: Long = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.DATE, defaultValue = "0", order = 7, label = "Elérhető eddig")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var availableTo: Long = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.NUMBER, order = 8, label = "Min. token a teljesítéshez")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var minAmountToComplete: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(maxLength = 64, order = 9, label = "Előfeltétel", note = "Üres string vagy a kategória neve")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var dependsOn: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.BLOCK_TEXT_MARKDOWN, order = 10, label = "Leírás amég nem elérhető")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var hintBeforeEnabled: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.BLOCK_TEXT_MARKDOWN, order = 11, label = "Leírás ha elérhető")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var hintWhileOpen: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.BLOCK_TEXT_MARKDOWN, order = 12, label = "Leírás miután teljesítve lett")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var hintAfterCompleted: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.SWITCH, order = 13, label = "Extra szint",
        note = "Külön látszanak a sima szintektől\nha be van kapcsolva, akkor felülírja a treasureHuntLevelt")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var extraLevel: Boolean = false,

    @Column(nullable = false, columnDefinition="tinyint(1) default 0")
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.SWITCH, order = 14, label = "Treasure hunt szint",
        note = "Olyan tokeneket tartalmazó szint, ahol az addig megszerzett tokenek adják a hintet a további tokenekhez")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var treasureHuntLevel: Boolean = false,

): ManagedEntity, Duplicatable {

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

    override fun duplicate(): QrLevelEntity {
        return this.copy()
    }

}
