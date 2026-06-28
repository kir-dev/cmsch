package hu.bme.sch.cmsch.component.support

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnBooleanProperty(value = ["hu.bme.sch.cmsch.component.load.support"])
class SupportComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "support",
    "/support",
    "Ügyfélszolgálat",
    ControlPermissions.PERMISSION_CONTROL_SUPPORT,
    listOf(SupportThreadEntity::class, SupportMessageEntity::class),
    env
) {

    val supportGroup by SettingGroup(fieldName = "Ügyfélszolgálat")

    final override var menuDisplayName by StringSettingRef("Ügyfélszolgálat", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek")

    final override var minRole by MinRoleSettingRef(setOf(RoleType.BASIC),
        fieldName = "Jogosultságok", description = "Mely szerepkörökkel nyitható meg az oldal")

    var siteTitle by StringSettingRef("Ügyfélszolgálat",
        fieldName = "Oldal neve",
        description = "A frontend oldalon megjelenő cím")

    var topMessage by StringSettingRef("", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Felső üzenet (lista oldal)",
        description = "Markdown szöveg a threadek listája felett")

    var newThreadButtonLabel by StringSettingRef("Új üzenet",
        fieldName = "Gomb felirata",
        description = "Az 'Új üzenet' gomb szövege")

    var adminLabel by StringSettingRef("rendező",
        fieldName = "Admin megnevezése",
        description = "Hogyan jelenik meg az admin az ügyfél számára (pl. 'rendező', 'support')")

    var newThreadTopMessage by StringSettingRef("", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Felső üzenet (új thread)",
        description = "Markdown szöveg az új szál form felett")

    var newThreadBottomMessage by StringSettingRef("", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Alsó üzenet (új thread)",
        description = "Markdown szöveg az új szál form alatt")

    var roleMappings by StringSettingRef("BASIC=Résztvevő,ATTENDEE=Résztvevő,PRIVILEGED=Kiemelt,STAFF=Rendező,ADMIN=Adminisztrátor", serverSideOnly = true,
        fieldName = "Szerepkör megnevezések",
        description = "Vesszővel elválasztott SZEREPKÖR=Megnevezés párok (pl. BASIC=Résztvevő,STAFF=Rendező)")

    var sendEmailOnAdminReply by BooleanSettingRef(true, serverSideOnly = true,
        fieldName = "Email küldése admin válaszkor",
        description = "Ha be van kapcsolva, az admin válasza emailben értesíti az ügyfelet")

    var answerEmailTemplateSelector by StringSettingRef("support_answer",
        fieldName = "Válasz email sablon",
        description = "A válaszoláshoz használt email sablon neve (selector). Változók: {{title}}, {{message}}, {{solver}}, {{threadUrl}}")

    var emailWebhookEnabled by BooleanSettingRef(false, serverSideOnly = true,
        fieldName = "Bejövő email webhook engedélyezve",
        description = "Ha ki van kapcsolva, a bejövő email webhook endpoint 404-et ad vissza")

    var incomingEmailSecret by StringSettingRef("", serverSideOnly = true,
        fieldName = "Bejövő email titok",
        description = "Ha üres, nincs ellenőrzés. Ha meg van adva, X-Support-Secret fejlécként kell küldeni.")

    var allowedSenderHostRegex by StringSettingRef("", serverSideOnly = true,
        fieldName = "Engedélyezett küldő host regex",
        description = "Ha üres, nincs ellenőrzés. Ha meg van adva, a bejövő email küldőjének host-ja ennek kell megfeleljen.")

    val maxOpenThreads by NumberSettingRef(5,
        fieldName = "Max nyitott szálak",
        description = "Egy felhasználó legfeljebb ennyi nyitott szálat nyithat egyszerre")

    val maxResponseLength by NumberSettingRef(3000, serverSideOnly = true,
        fieldName = "Max válasz hossz (karakter)",
        description = "Az üzenetek maximális hossza karakterekben")

    val maxCustomerResponsesWithoutAnswer by NumberSettingRef(10, serverSideOnly = true,
        fieldName = "Max egymás utáni ügyfél válasz",
        description = "Ennyi egymás utáni ügyfél üzenet után nem lehet újabb üzenetet küldeni admin válasz nélkül")

    var blockedEmails by StringSettingRef("", serverSideOnly = true,
        fieldName = "Tiltott emailek",
        description = "Vesszővel elválasztott email címek, amelyek nem küldhetnek üzenetet")

    var blockedUserIds by StringSettingRef("", serverSideOnly = true,
        fieldName = "Tiltott felhasználó azonosítók",
        description = "Vesszővel elválasztott belső azonosítók, amelyek nem küldhetnek üzenetet")
}
