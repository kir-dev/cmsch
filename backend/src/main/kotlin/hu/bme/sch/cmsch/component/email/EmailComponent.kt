package hu.bme.sch.cmsch.component.email

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.event.EventEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
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
    componentSettingService,
    "email",
    "/",
    "Email",
    ControlPermissions.PERMISSION_CONTROL_EVENTS,
    listOf(EventEntity::class),
    env
) {

    val emailGroup by SettingGroup(fieldName = "Email küldés")

    final override var minRole by MinRoleSettingRef(setOf(), minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal")

    var emailProvider by StringSettingRef("kirmail", fieldName = "Email szolgáltató", serverSideOnly = true,
        description = "Ezek lehetnek: kirmail, mailgun (ettől még be kell kapcsolni őket lentebb)")

    /// -------------------------------------------------------------------------------------------------------------------

    val mailgunGroup by SettingGroup(fieldName = "Mailgun beállítások")

    var enableMailgun by BooleanSettingRef(fieldName = "Küldés Mailgunnal", serverSideOnly = true,
        description = "Csak akkor működik ha API key meg van adva környezeti változónak")

    var mailgunEmailAccount by StringSettingRef("noreply", fieldName = "Email felhasználó", serverSideOnly = true,
        description = "Ezzel az email felhasználónévvel lesznek kiküldve.")

    var mailgunAccountName by StringSettingRef("Rendezők", fieldName = "Email teljes név", serverSideOnly = true,
        description = "Ez a név lesz elküldve a felhasználóhoz")

    var mailgunDomain by StringSettingRef("golya.sch-bme.hu", fieldName = "Email domainje", serverSideOnly = true,
        description = "Ez a @ utáni rész. Fel kell konfigolva legyen, nem lehet akármit ideírni.")

    /// -------------------------------------------------------------------------------------------------------------------

    val kirmailGroup by SettingGroup(fieldName = "Kir Mail beállítások")

    var enableKirMail by BooleanSettingRef(fieldName = "Küldés Kir Maillel", serverSideOnly = true,
        description = "Csak akkor működik ha token be van állítva")

    var kirmailToken by StringSettingRef(fieldName = "Kir Mail Token", serverSideOnly = true,
        minRoleToEdit = RoleType.SUPERUSER, description = "Ez az access token. Generáld az admin.mail.kir-dev.hu-n!")

    var kirmailEmailAddress by StringSettingRef("noreply-golyatabor@sch.bme.hu",
        fieldName = "Kir Mail Email cím", serverSideOnly = true, description = "Erről a címről fogja küldeni")

    var kirmailAccountName by StringSettingRef("Rendezők",
        fieldName = "Kir Mail Email teljes név", serverSideOnly = true,
        description = "Ez a név lesz elküldve a felhasználóhoz")

    var kirmailReplyTo by StringSettingRef("golyatabor@sch.bme.hu",
        fieldName = "Kir Mail Válasz emailcím", serverSideOnly = true,
        description = "Erre küldjék a választ a felhasználók (reply-to)")

    var kirmailQueue by StringSettingRef("ms-golya", fieldName = "Kir Mail Queue", serverSideOnly = true,
        description = "Erre küldjék a választ a felhasználók (reply-to)")

}
