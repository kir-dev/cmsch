package hu.bme.sch.cmsch.component.token

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
@Table(name="tokens")
@ConditionalOnBean(TokenComponent::class)
data class TokenEntity(

    @Id
    @GeneratedValue
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Token neve")
    @property:GenerateOverview(columnName = "Név", order = 1)
    @property:ImportFormat(ignore = false)
    var title: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 2, label = "Token")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var token: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 3, label = "Beolvasható-e a token")
    @property:GenerateOverview(columnName = "Olvasható", order = 2, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false)
    var visible: Boolean = false,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 32, order = 4, label = "Típus")
    @property:GenerateOverview(columnName = "Típus", order = 3)
    @property:ImportFormat(ignore = false)
    var type: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 32, order = 5, label = "Ikon")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var icon: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 6, label = "Pont", type = INPUT_TYPE_NUMBER, defaultValue = "0",
        note = "Egész szám, hány pontot ér a megszerzése")
    @property:GenerateOverview(columnName = "Pont", order = 4, centered = true)
    @property:ImportFormat(ignore = false)
    var score: Int? = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 32, order = 7, label = "Kiváltott esemény",
        note = "QR fighthoz az akció amit kivált. capture:<tower>, history:<tower> vagy enslave:<tower>")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var action: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false, columnDefinition = "BOOLEAN default FALSE")
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 8, label = "Aktív cél",
        note = "Csak akkor ha a QR Fight komponens is be van töltve")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var activeTarget: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false, columnDefinition = "VARCHAR(255) default ''")
    @property:GenerateInput(maxLength = 255, order = 9, label = "Kijelzett kép URL-je",
        note = "Ha nem üres, megjelenik beolvasás után")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var displayIconUrl: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT default ''")
    @property:GenerateInput(order = 10, label = "Kijelzett szöveg",
        note = "Ha nem üres, megjelenik beolvasás után", type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var displayDescription: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = true, columnDefinition = "BIGINT DEFAULT NULL")
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 11, label = "Scannelhető innentől")
    @property:GenerateOverview(columnName = "Ettől", order = 5, renderer = OVERVIEW_TYPE_DATE)
    @property:ImportFormat(ignore = false, type = IMPORT_LONG)
    var availableFrom: Long? = null,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = true, columnDefinition = "BIGINT DEFAULT NULL")
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 12, label = "Scannelhető eddig")
    @property:GenerateOverview(columnName = "Eddig", order = 6, renderer = OVERVIEW_TYPE_DATE)
    @property:ImportFormat(ignore = false, type = IMPORT_LONG)
    var availableUntil: Long? = null,

): ManagedEntity {

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

}
