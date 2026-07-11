package hu.bme.sch.cmsch.component.support

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.util.UUID

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
    listOf(SupportThreadEntity::class, SupportMessageEntity::class, SupportScheduleEntity::class),
    env
) {

    // -----------------------------------------------------------------------------------------------------------------

    val megjelenes by SettingGroup(fieldName = "Megjelenés")

    var siteTitle by StringSettingRef("Ügyfélszolgálat",
        fieldName = "Oldal neve",
        description = "A frontend oldalon megjelenő cím")

    var topMessage by StringSettingRef("", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Felső üzenet (lista oldal)",
        description = "Markdown szöveg a megkeresések listája felett")

    var newThreadButtonLabel by StringSettingRef("Új üzenet",
        fieldName = "Gomb felirata",
        description = "Az 'Új megkeresés' gomb szövege")

    var adminLabel by StringSettingRef("rendező",
        fieldName = "Admin megnevezése",
        description = "Hogyan jelenik meg az admin az ügyfél számára (pl. 'rendező', 'support')")

    var newThreadTopMessage by StringSettingRef("", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Felső üzenet (új megkeresés)",
        description = "Markdown szöveg az új megkeresés form felett")

    var newThreadBottomMessage by StringSettingRef("", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Alsó üzenet (új megkeresés)",
        description = "Markdown szöveg az új megkeresés form alatt")

    // -----------------------------------------------------------------------------------------------------------------

    val mukodes by SettingGroup(fieldName = "Működés")

    final override var menuDisplayName by StringSettingRef("Ügyfélszolgálat", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek")

    final override var minRole by MinRoleSettingRef(setOf(RoleType.BASIC),
        fieldName = "Jogosultságok", description = "Mely szerepkörökkel nyitható meg az oldal")

    val maxOpenThreads by NumberSettingRef(5,
        fieldName = "Max nyitott megkeresések",
        description = "Egy felhasználó egyszerre legfeljebb ennyi nyitott megkeresést tarthat")

    val maxResponseLength by NumberSettingRef(3000, serverSideOnly = true,
        fieldName = "Max válasz hossz (karakter)",
        description = "Az üzenetek maximális hossza karakterekben")

    val maxCustomerResponsesWithoutAnswer by NumberSettingRef(10, serverSideOnly = true,
        fieldName = "Max egymás utáni ügyfél válasz",
        description = "Ennyi egymás utáni ügyfél üzenet után admin válasz szükséges")

    var roleMappings by StringSettingRef("BASIC=Résztvevő,ATTENDEE=Résztvevő,PRIVILEGED=Kiemelt,STAFF=Rendező,ADMIN=Adminisztrátor", serverSideOnly = true,
        fieldName = "Szerepkör megnevezések",
        description = "Vesszővel elválasztott SZEREPKÖR=Megnevezés párok (pl. BASIC=Résztvevő,STAFF=Rendező)")

    var sendEmailOnAdminReply by BooleanSettingRef(true, serverSideOnly = true,
        fieldName = "Email küldése felelős válaszkor",
        description = "Ha be van kapcsolva, a felelős válasza emailben értesíti az ügyfelet")

    var answerEmailTemplateSelector by StringSettingRef("support_answer",
        fieldName = "Válasz email sablon",
        serverSideOnly = true,
        description = "A válaszoláshoz használt email sablon neve (selector). Változók: {{title}}, {{message}}, {{{messageHtml}}}, {{solver}}, {{threadUrl}}, {{creationDate}}, {{lastAnswerDate}}. A {{{messageHtml}}} háromszoros kapcsos zárójelekkel illesztendő be.")

    var newThreadEmailTemplateSelector by StringSettingRef("support_new_thread", serverSideOnly = true,
        fieldName = "Megkeresés visszaigazoló sablon",
        description = "Ha üres, nem küld visszaigazolást. Sablon neve (selector). Változók: {{title}}, {{message}}, {{{messageHtml}}}, {{threadUrl}}, {{creationDate}}, {{lastAnswerDate}}. A {{{messageHtml}}} háromszoros kapcsos zárójelekkel illesztendő be.")

    // -----------------------------------------------------------------------------------------------------------------

    val biztonsag by SettingGroup(fieldName = "Biztonság")

    var blockedEmails by StringSettingRef("", serverSideOnly = true,
        fieldName = "Tiltott emailek",
        description = "Vesszővel elválasztott email címek, amelyek nem küldhetnek üzenetet")

    var blockedUserIds by StringSettingRef("", serverSideOnly = true,
        fieldName = "Tiltott felhasználó azonosítók",
        description = "Vesszővel elválasztott belső azonosítók, amelyek nem küldhetnek üzenetet")

    var emailWebhookEnabled by BooleanSettingRef(false, serverSideOnly = true,
        fieldName = "Bejövő email webhook engedélyezve",
        description = "Ha ki van kapcsolva, a bejövő email webhook endpoint 404-et ad vissza")

    var incomingEmailSecret by StringSettingRef(UUID.randomUUID().toString(), serverSideOnly = true,
        fieldName = "Bejövő email webhook titkos azonosító (UUID)",
        description = "A webhook URL részét képező titkos azonosító. Az endpoint: /api/support/incoming-email/{titkos-azonosito}")

    var allowedToAddress by StringSettingRef("", serverSideOnly = true,
        fieldName = "Engedélyezett 'To' cím",
        description = "Ha meg van adva, csak erre a 'To' email címre érkező emailek kerülnek feldolgozásra")

    var allowedResentFromAddress by StringSettingRef("", serverSideOnly = true,
        fieldName = "Engedélyezett 'Resent-From' cím",
        description = "Ha meg van adva, csak az erről a 'Resent-From' email címről érkező emailek kerülnek feldolgozásra")

    // -----------------------------------------------------------------------------------------------------------------

    val beosztas by SettingGroup(fieldName = "Beosztás")

    var scheduleEnabled by BooleanSettingRef(false, serverSideOnly = true,
        fieldName = "Automatikus hozzárendelés beosztás alapján",
        description = "Ha be van kapcsolva, új megkeresés vagy ügyfél üzenet esetén automatikusan hozzárendel egy ügyfélszolgálatost a beosztás alapján. A beosztást az 'Ügyfélszolgálat beosztás' oldalon lehet kezelni.")

    var defaultSupportUserIds by StringSettingRef("", serverSideOnly = true,
        fieldName = "Alapértelmezett ügyfélszolgálatosok (azonosítók)",
        description = "Vesszővel elválasztott belső azonosítók (internalId). Ha a beosztásban nincs elérhető felhasználó, ezek közül választ egyet véletlenszerűen.")

    var assignEmailTemplateSelector by StringSettingRef("support_assign", serverSideOnly = true,
        fieldName = "Hozzárendelés email sablon",
        description = "A hozzárendelési értesítőhöz használt email sablon neve (selector). Változók: {{title}}, {{message}}, {{{messageHtml}}}, {{userName}}, {{creationDate}}, {{lastAnswerDate}}, {{adminUrl}}. A {{{messageHtml}}} háromszoros kapcsos zárójelekkel illesztendő be.")
}
