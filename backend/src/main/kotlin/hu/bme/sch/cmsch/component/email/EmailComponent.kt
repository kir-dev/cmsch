package hu.bme.sch.cmsch.component.email

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.component.event.EventEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["email"],
    havingValue = "true",
    matchIfMissing = false
)
class EmailComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "email",
    "/",
    "Email",
    ControlPermissions.PERMISSION_CONTROL_EVENTS,
    listOf(EventEntity::class),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            mailgunGroup,
            minRole,
            enableMailgun,
            mailgunEmailAccount,
            mailgunAccountName,
            mailgunDomain
        )
    }

    val mailgunGroup = SettingProxy(componentSettingService, component,
        "eventsGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Mailgun beállítások",
        description = ""
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "", minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    val enableMailgun = SettingProxy(componentSettingService, component,
        "enableMailgun", "false", type = SettingType.BOOLEAN,
        fieldName = "Küldés Mailgunnal", serverSideOnly = true,
        description = "Csak akkor működik ha API key meg van adva környezeti változónak"
    )

    val mailgunEmailAccount = SettingProxy(componentSettingService, component,
        "mailgunEmailAccount", "noreply",
        fieldName = "Email felhasználó", serverSideOnly = true,
        description = "Ezzel az email felhasználónévvel lesznek kiküldve."
    )

    val mailgunAccountName = SettingProxy(componentSettingService, component,
        "mailgunAccountName", "Rendezők",
        fieldName = "Email teljes név", serverSideOnly = true,
        description = "Ez a név lesz elküldve a felhasználóhoz"
    )

    val mailgunDomain = SettingProxy(componentSettingService, component,
        "mailgunDomain", "golya.sch-bme.hu",
        fieldName = "Email domainje", serverSideOnly = true,
        description = "Ez a @ utáni rész. Fel kell konfigolva legyen, nem lehet akármit ideírni."
    )

}
