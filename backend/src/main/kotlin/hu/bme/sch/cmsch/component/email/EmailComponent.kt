package hu.bme.sch.cmsch.component.email

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.component.app.ComponentSettingService
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
            minRole,
            emailGroup,
            emailProvider,

            mailgunGroup,
            enableMailgun,
            mailgunEmailAccount,
            mailgunAccountName,
            mailgunDomain,

            kirmailGroup,
            enableKirMail,
            kirmailToken,
            kirmailEmailAddress,
            kirmailAccountName,
            kirmailReplyTo,
            kirmailQueue,
        )
    }

    val emailGroup = SettingProxy(componentSettingService, component,
        "emailGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Email küldés",
        description = ""
    )

    val emailProvider = SettingProxy(componentSettingService, component,
        "emailProvider", "kirmail",
        fieldName = "Email szolgáltató", serverSideOnly = true,
        description = "Ezek lehetnek: kirmail, mailgun (ettől még be kell kapcsolni őket lentebb)"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "", minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    val mailgunGroup = SettingProxy(componentSettingService, component,
        "mailgunGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Mailgun beállítások",
        description = ""
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

    /// -------------------------------------------------------------------------------------------------------------------

    val kirmailGroup = SettingProxy(componentSettingService, component,
        "kirmailGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Kir Mail beállítások",
        description = ""
    )

    val enableKirMail = SettingProxy(componentSettingService, component,
        "enableKirMail", "false", type = SettingType.BOOLEAN,
        fieldName = "Küldés Kir Maillel", serverSideOnly = true,
        description = "Csak akkor működik ha token be van állítva"
    )

    val kirmailToken = SettingProxy(componentSettingService, component,
        "kirmailToken", "",
        fieldName = "Kir Mail Token", serverSideOnly = true, minRoleToEdit = RoleType.SUPERUSER,
        description = "Ez az access token. Generáld az admin.mail.kir-dev.hu-n!"
    )

    val kirmailEmailAddress = SettingProxy(componentSettingService, component,
        "kirmailEmailAddress", "noreply-golyatabor@sch.bme.hu",
        fieldName = "Kir Mail Email cím", serverSideOnly = true,
        description = "Erről a címről fogja küldeni"
    )

    val kirmailAccountName = SettingProxy(componentSettingService, component,
        "kirmailAccountName", "Rendezők",
        fieldName = "Kir Mail Email teljes név", serverSideOnly = true,
        description = "Ez a név lesz elküldve a felhasználóhoz"
    )

    val kirmailReplyTo = SettingProxy(componentSettingService, component,
        "kirmailReplyTo", "golyatabor@sch.bme.hu",
        fieldName = "Kir Mail Válasz emailcím", serverSideOnly = true,
        description = "Erre küldjék a választ a felhasználók (reply-to)"
    )

    val kirmailQueue = SettingProxy(componentSettingService, component,
        "kirmailQueue", "ms-golya",
        fieldName = "Kir Mail Queue", serverSideOnly = true,
        description = "Erre küldjék a választ a felhasználók (reply-to)"
    )

}
