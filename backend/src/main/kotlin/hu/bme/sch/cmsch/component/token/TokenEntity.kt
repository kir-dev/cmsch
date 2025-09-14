package hu.bme.sch.cmsch.component.token

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
import org.hibernate.annotations.ColumnDefault
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name="tokens")
@ConditionalOnBean(TokenComponent::class)
data class TokenEntity(

    @Id
    @GeneratedValue
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Token neve")
    @property:GenerateOverview(columnName = "Név", order = 1)
    @property:ImportFormat
    var title: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 2, label = "Token", type = InputType.TOKEN_QR_TEXT_FIELD,
        note = "Mind a két alább generált QR jó, de csak a sűrűbb az ami a sima olvasóval is működik, mert abban benne van az oldal URL-je. A másik QR-kód csak az oldalon megnyitott olvasóval működik.")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var token: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.SWITCH, order = 3, label = "Beolvasható-e a token")
    @property:GenerateOverview(columnName = "Olvasható", order = 2, centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var visible: Boolean = false,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 32, order = 4, label = "Típus")
    @property:GenerateOverview(columnName = "Típus", order = 3)
    @property:ImportFormat
    var type: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 32, order = 5, label = "Ikon")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var icon: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 5, label = "Rarity",
        source = ["COMMON", "UNCOMMON", "RARE", "EPIC", "LEGENDARY", "RAINBOW"])
    @property:GenerateOverview(columnName = "Rarity", order = 4)
    @property:ImportFormat
    var rarity: String? = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 6, label = "Pont", type = InputType.NUMBER, defaultValue = "0",
        note = "Egész szám, hány pontot ér a megszerzése")
    @property:GenerateOverview(columnName = "Pont", order = 5, centered = true)
    @property:ImportFormat
    var score: Int? = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 32, order = 7, label = "Kiváltott esemény",
        note = "QR fighthoz az akció amit kivált. capture:<tower>, history:<tower> vagy enslave:<tower>")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var action: String = "",

    @field:JsonView(value = [ Edit::class ])
    @ColumnDefault("false")
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.SWITCH, order = 8, label = "Aktív cél",
        note = "Csak akkor ha a QR Fight komponens is be van töltve")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var activeTarget: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @ColumnDefault("''")
    @Column(nullable = false, columnDefinition = "TEXT")
    @property:GenerateInput(order = 9, label = "Kijelzett kép URL-je", type = InputType.IMAGE_URL,
        note = "Ha nem üres, megjelenik beolvasás után")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var displayIconUrl: String = "",

    @field:JsonView(value = [ Edit::class ])
    @ColumnDefault("''")
    @Column(nullable = false, columnDefinition = "TEXT")
    @property:GenerateInput(order = 10, label = "Kijelzett szöveg",
        note = "Ha nem üres, megjelenik beolvasás után", type = InputType.BLOCK_TEXT_MARKDOWN)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var displayDescription: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @ColumnDefault("null")
    @Column(nullable = true, columnDefinition = "BIGINT")
    @property:GenerateInput(type = InputType.DATE, defaultValue = "0", order = 11, label = "Scannelhető innentől")
    @property:GenerateOverview(columnName = "Ettől", order = 6, renderer = OverviewType.DATE)
    @property:ImportFormat
    var availableFrom: Long? = null,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @ColumnDefault("null")
    @Column(nullable = true, columnDefinition = "BIGINT")
    @property:GenerateInput(type = InputType.DATE, defaultValue = "0", order = 12, label = "Scannelhető eddig")
    @property:GenerateOverview(columnName = "Eddig", order = 7, renderer = OverviewType.DATE)
    @property:ImportFormat
    var availableUntil: Long? = null,

): ManagedEntity, Duplicatable {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "Token",
        view = "control/tokens",
        showPermission = StaffPermissions.PERMISSION_SHOW_TOKENS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TokenEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }

    override fun duplicate(): TokenEntity {
        return this.copy()
    }

}
