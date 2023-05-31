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
    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Token neve")
    @property:GenerateOverview(columnName = "Név", order = 1)
    @property:ImportFormat(ignore = false, columnId = 0)
    var title: String = "",

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 2, label = "Token")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 1)
    var token: String = "",

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 3, label = "Beolvasható-e a token")
    @property:GenerateOverview(columnName = "Olvasható", order = 2, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 2, type = IMPORT_BOOLEAN)
    var visible: Boolean = false,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 32, order = 4, label = "Típus")
    @property:GenerateOverview(columnName = "Típus", order = 3)
    @property:ImportFormat(ignore = false, columnId = 3)
    var type: String = "",

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 32, order = 5, label = "Ikon")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 4)
    var icon: String = "",

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 6, label = "Pont", type = INPUT_TYPE_NUMBER, defaultValue = "0",
        note = "Egész szám, hány pontot ér a megszerzése")
    @property:GenerateOverview(columnName = "Pont", order = 4, centered = true)
    @property:ImportFormat(ignore = false, columnId = 5, type = IMPORT_INT)
    var score: Int? = 0,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false, columnDefinition = "VARCHAR(255) default ''")
    @property:GenerateInput(maxLength = 32, order = 7, label = "Kiváltott esemény",
        note = "QR fighthoz az akció amit kivált. capture:<tower> vagy history:<tower>")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 6)
    var action: String? = "",

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false, columnDefinition = "BOOLEAN default FALSE")
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 8, label = "Aktív cél",
        note = "Csak akkor ha a QR Fight komponens is be van töltve")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 7, type = IMPORT_BOOLEAN)
    var activeTarget: Boolean? = false,

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
