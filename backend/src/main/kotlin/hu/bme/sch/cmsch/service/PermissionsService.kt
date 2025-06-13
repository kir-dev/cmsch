package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.admission.AdmissionComponent
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.app.UserHandlingComponent
import hu.bme.sch.cmsch.component.bmejegy.BmejegyComponent
import hu.bme.sch.cmsch.component.challenge.ChallengeComponent
import hu.bme.sch.cmsch.component.communities.CommunitiesComponent
import hu.bme.sch.cmsch.component.conference.ConferenceComponent
import hu.bme.sch.cmsch.component.countdown.CountdownComponent
import hu.bme.sch.cmsch.component.debt.DebtComponent
import hu.bme.sch.cmsch.component.email.EmailComponent
import hu.bme.sch.cmsch.component.errorlog.ErrorLogComponent
import hu.bme.sch.cmsch.component.event.EventComponent
import hu.bme.sch.cmsch.component.form.FormComponent
import hu.bme.sch.cmsch.component.gallery.GalleryComponent
import hu.bme.sch.cmsch.component.home.HomeComponent
import hu.bme.sch.cmsch.component.impressum.ImpressumComponent
import hu.bme.sch.cmsch.component.key.AccessKeyComponent
import hu.bme.sch.cmsch.component.leaderboard.LeaderBoardComponent
import hu.bme.sch.cmsch.component.location.LocationComponent
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.component.messaging.MessagingComponent
import hu.bme.sch.cmsch.component.news.NewsComponent
import hu.bme.sch.cmsch.component.profile.ProfileComponent
import hu.bme.sch.cmsch.component.proto.ProtoComponent
import hu.bme.sch.cmsch.component.pushnotification.PushNotificationComponent
import hu.bme.sch.cmsch.component.qrfight.QrFightComponent
import hu.bme.sch.cmsch.component.race.RaceComponent
import hu.bme.sch.cmsch.component.riddle.RiddleComponent
import hu.bme.sch.cmsch.component.sheets.SheetsComponent
import hu.bme.sch.cmsch.component.staticpage.StaticPageComponent
import hu.bme.sch.cmsch.component.task.TaskComponent
import hu.bme.sch.cmsch.component.team.TeamComponent
import hu.bme.sch.cmsch.component.token.TokenComponent
import hu.bme.sch.cmsch.extending.CmschPermissionSource
import hu.bme.sch.cmsch.util.DI
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import kotlin.reflect.KClass

@Service
class PermissionsService(
    permissionSources: List<CmschPermissionSource>
) {
    final val allControlPermissions = permissionSources.flatMap { it.getControlPermissions() }
    final val allStaffPermissions = permissionSources.flatMap { it.getStaffPermissions() }
    final val allPermissions = listOf(allControlPermissions, allStaffPermissions).flatten()
}

class PermissionValidator constructor(
    val permissionString: String = "",
    val description: String = "",
    val component: KClass<out ComponentBase>? = null,
    val readOnly: Boolean = false, // Note: this is just a label but used for giving read-only permissions
    val validate: Function1<CmschUser, Boolean> = {
            user -> user.isAdmin() || (permissionString.isNotEmpty() && user.hasPermission(permissionString))
    }
)

@Component
class CorePermissionSource : CmschPermissionSource {

    override fun getControlPermissions(): List<PermissionValidator> {
        return ControlPermissions.allPermissions()
    }

    override fun getStaffPermissions(): List<PermissionValidator> {
        return StaffPermissions.allPermissions()
    }

}

fun interface PermissionGroup {
    fun allPermissions(): List<PermissionValidator>
}

object CompositePermission {
    fun of(vararg permissions: PermissionValidator) = PermissionValidator(
        permissionString = "<<composite-permission>>",
        description = permissions.joinToString(", ") { it.description },
        component = null,
        validate = { user -> permissions.any { it.validate(user) } }
    )
}

object ImplicitPermissions : PermissionGroup {

    val PERMISSION_IMPLICIT_HAS_GROUP = PermissionValidator(
        description = "The user has a group",
        readOnly = false,
        permissionString = "HAS_GROUP")
            { user -> DI.instance.userService.getById(user.internalId).group != null }

    val PERMISSION_IMPLICIT_ANYONE = PermissionValidator(
        description = "Everyone has this permission",
        readOnly = false,
        permissionString = "ANYONE")
            { _ -> true }

    val PERMISSION_NOBODY = PermissionValidator(
        description = "Nobody has this permission",
        readOnly = false,
        permissionString = "NOBODY")
            { _ -> false }

    val PERMISSION_SUPERUSER_ONLY = PermissionValidator { user -> user.isSuperuser() }

    override fun allPermissions() = listOf(
        PERMISSION_IMPLICIT_HAS_GROUP,
        PERMISSION_IMPLICIT_ANYONE,
        PERMISSION_NOBODY,
        PERMISSION_SUPERUSER_ONLY
    )
}

object ControlPermissions : PermissionGroup {

    val PERMISSION_CONTROL_NEWS = PermissionValidator(
        "NEWS_CONTROL",
        "Hírek komponens testreszabása",
        readOnly = false,
        component = NewsComponent::class
    )

    val PERMISSION_CONTROL_TASKS = PermissionValidator(
        "TASK_CONTROL",
        "Feladatok komponens testreszabása",
        readOnly = false,
        component = TaskComponent::class
    )

    val PERMISSION_CONTROL_EVENTS = PermissionValidator(
        "EVENT_CONTROL",
        "Események komponens testreszabása",
        readOnly = false,
        component = EventComponent::class
    )

    val PERMISSION_CONTROL_ERROR_LOG = PermissionValidator(
        "ERROR_LOG_CONTROL",
        "Kliens hibaüzenetek komponens testreszabása",
        readOnly = false,
        component = ErrorLogComponent::class
    )

    val PERMISSION_CONTROL_GALLERY = PermissionValidator(
        "GALLERY_CONTROL",
        "Galéria komponens testreszabása",
        readOnly = false,
        component = GalleryComponent::class
    )

    val PERMISSION_CONTROL_DEBTS = PermissionValidator(
        "DEBT_CONTROL",
        "Debt komponens testreszabása",
        readOnly = false,
        component = DebtComponent::class
    )

    val PERMISSION_CONTROL_RIDDLE = PermissionValidator(
        "RIDDLE_CONTROL",
        "Riddle komponens testreszabása",
        readOnly = false,
        component = RiddleComponent::class
    )

    val PERMISSION_CONTROL_TOKEN = PermissionValidator(
        "TOKEN_CONTROL",
        "Token komponens testreszabása",
        readOnly = false,
        component = TokenComponent::class
    )

    val PERMISSION_CONTROL_STATIC_PAGES = PermissionValidator(
        "STATICPAGES_CONTROL",
        "StaticPage komponens testreszabása",
        readOnly = false,
        component = StaticPageComponent::class
    )

    val PERMISSION_CONTROL_LEADERBOARD = PermissionValidator(
        "LEADERBOARD_CONTROL",
        "LeaderBoard komponens testreszabása",
        readOnly = false,
        component = LeaderBoardComponent::class
    )

    val PERMISSION_CONTROL_PROFILE = PermissionValidator(
        "PROFILE_CONTROL",
        "Profil komponens testreszabása",
        readOnly = false,
        component = ProfileComponent::class
    )

    val PERMISSION_CONTROL_APP = PermissionValidator(
        "APP_CONTROL",
        "Az alkalmazás testreszabása",
        readOnly = false,
        component = ApplicationComponent::class,
    )

    val PERMISSION_CONTROL_APP_EXPORT = PermissionValidator(
        "APP_EXPORT_CONTROL",
        "Teljes alkalmazás állapotának kiexportálása",
        readOnly = false,
        component = ApplicationComponent::class
    )

    val PERMISSION_CONTROL_APP_IMPORT = PermissionValidator(
        "APP_IMPORT_CONTROL",
        "Teljes alkalmazás állapotának beimportálása",
        readOnly = false,
        component = ApplicationComponent::class
    )

    val PERMISSION_CONTROL_FOOTER = PermissionValidator(
        "FOOTER_CONTROL",
        "Lábléc beállításai",
        readOnly = false,
        component = ApplicationComponent::class
    )

    val PERMISSION_INCREASED_SESSION_DURATION = PermissionValidator(
        "INCREASED_SESSION_DURATION",
        "Megnövelt session idő",
        readOnly = false,
        component = ApplicationComponent::class
    )

    val PERMISSION_SHOW_FILES = PermissionValidator(
        "FILE_SHOW",
        "Feltöltött fájlok megtekintése",
        readOnly = false,
        component = ApplicationComponent::class
    )

    val PERMISSION_DELETE_FILES = PermissionValidator(
        "FILE_DELETE",
        "Feltöltött fájlok törlése",
        readOnly = false,
        component = ApplicationComponent::class
    )

    val PERMISSION_UPLOAD_FILES = PermissionValidator(
        "FILE_UPLOAD",
        "Új fájl feltöltése",
        readOnly = false,
        component = ApplicationComponent::class
    )

    val PERMISSION_CONTROL_IMPRESSUM = PermissionValidator(
        "IMPRESSUM_CONTROL",
        "Impresszum komponens testreszabása",
        readOnly = false,
        component = ImpressumComponent::class
    )

    val PERMISSION_CONTROL_COUNTDOWN = PermissionValidator(
        "COUNTDOWN_CONTROL",
        "Visszaszámlálás komponens testreszabása",
        readOnly = false,
        component = CountdownComponent::class
    )

    val PERMISSION_CONTROL_FORM = PermissionValidator(
        "FORM_CONTROL",
        "Űrlapok komponens testreszabása",
        readOnly = false,
        component = FormComponent::class
    )

    val PERMISSION_CONTROL_CHALLENGE = PermissionValidator(
        "CHALLENGE_CONTROL",
        "Beadások komponens testreszabása",
        readOnly = false,
        component = ChallengeComponent::class
    )

    val PERMISSION_CONTROL_HOME = PermissionValidator(
        "HOME_CONTROL",
        "Kezdőlap komponens testreszabása",
        readOnly = false,
        component = HomeComponent::class
    )

    val PERMISSION_CONTROL_NOTIFICATIONS = PermissionValidator(
        "NOTIFICATION_CONTROL",
        "Értesítés komponens testreszabása",
        readOnly = false,
        component = PushNotificationComponent::class
    )

    val PERMISSION_SEND_NOTIFICATIONS = PermissionValidator(
        "NOTIFICATION_SEND",
        "Push Értesítés küldése",
        readOnly = false,
        component = PushNotificationComponent::class
    )
    val PERMISSION_CONTROL_COMMUNITIES = PermissionValidator(
        "COMMUNITIES_CONTROL",
        "Kezdőlap komponens testreszabása",
        readOnly = false,
        component = CommunitiesComponent::class
    )

    val PERMISSION_CONTROL_ADMISSION = PermissionValidator(
        "ADMISSION_CONTROL",
        "Beléptetés komponens testreszabása",
        readOnly = false,
        component = AdmissionComponent::class
    )

    val PERMISSION_CONTROL_RACE = PermissionValidator(
        "RACE_CONTROL",
        "Verseny (sörmérés) komponens testreszabása",
        readOnly = false,
        component = RaceComponent::class
    )

    val PERMISSION_CONTROL_TEAM = PermissionValidator(
        "TEAM_CONTROL",
        "Csapat komponens testreszabása",
        readOnly = false,
        component = TeamComponent::class
    )

    val PERMISSION_CONTROL_QR_FIGHT = PermissionValidator(
        "QR_FIGHT_CONTROL",
        "QR Fight komponens testreszabása",
        readOnly = false,
        component = QrFightComponent::class
    )

    val PERMISSION_CONTROL_BMEJEGY = PermissionValidator(
        "BMEJEGY_CONTROL",
        "Bmejegy komponens testreszabása",
        readOnly = false,
        component = BmejegyComponent::class
    )

    val PERMISSION_CONTROL_LOCATION = PermissionValidator(
        "LOCATION_CONTROL",
        "Helymeghatározás komponens testreszabása",
        readOnly = false,
        component = LocationComponent::class
    )

    val PERMISSION_SHOW_AUDIT_LOG = PermissionValidator(
        "SHOW_AUDIT_LOG",
        "Audit log olvasása",
        readOnly = false,
        component = ApplicationComponent::class
    )

    val PERMISSION_DEV_DEBUG = PermissionValidator(
        "DEVELOPER_DEBUG",
        "Fejlesztői beállítások",
        readOnly = false,
        component = ApplicationComponent::class
    )

    val PERMISSION_SHOW_INSTANCE = PermissionValidator(
        "SHOW_INSTANCE_INFO",
        "Szerver adatok megtekintése",
        readOnly = false,
        component = ApplicationComponent::class
    )

    val PERMISSION_SHOW_LIVE_STATS = PermissionValidator(
        "SHOW_LIVE_STATS",
        "Élő statisztikák megjelenítése",
        readOnly = false,
        component = ApplicationComponent::class
    )

    val PERMISSION_CONTROL_MESSAGING = PermissionValidator(
        "MESSAGING_CONTROL",
        "Értesítések komponens kezelése",
        readOnly = false,
        component = MessagingComponent::class
    )

    val PERMISSION_SEND_MESSAGE = PermissionValidator(
        "MESSAGING_SEND",
        "Debug üzenet küldése",
        readOnly = false,
        component = MessagingComponent::class
    )

    val PERMISSION_CONTROL_EMAILS = PermissionValidator(
        "EMAILS_CONTROL",
        "Email komponens testreszabása",
        readOnly = false,
        component = EmailComponent::class
    )

    val PERMISSION_SEND_EMAIL = PermissionValidator(
        "MESSAGING_SEND",
        "Teszt email küldése",
        readOnly = false,
        component = EmailComponent::class
    )

    val PERMISSION_CONTROL_ACCESS_KEYS = PermissionValidator(
        "ACCESS_KEY_CONTROL",
        "Hozzáférési kulcs komponens testreszabása",
        readOnly = false,
        component = AccessKeyComponent::class
    )

    val PERMISSION_CONTROL_PROTO = PermissionValidator(
        "PROTO_CONTROL",
        "Prototípusok testreszabása",
        readOnly = false,
        component = ApplicationComponent::class
    )

    val PERMISSION_CONTROL_CONFERENCE = PermissionValidator(
        "CONFERENCE_CONTROL",
        "Konferencia testreszabása",
        readOnly = false,
        component = ApplicationComponent::class
    )

    val PERMISSION_CONTROL_SHEETS = PermissionValidator(
        "SHEETS_CONTROL",
        "Sheets integráció testreszabása",
        readOnly = false,
        component = SheetsComponent::class
    )

    override fun allPermissions() = listOf(
        PERMISSION_CONTROL_NEWS,
        PERMISSION_CONTROL_TASKS,
        PERMISSION_CONTROL_EVENTS,
        PERMISSION_CONTROL_ERROR_LOG,
        PERMISSION_CONTROL_GALLERY,
        PERMISSION_CONTROL_DEBTS,
        PERMISSION_CONTROL_RIDDLE,
        PERMISSION_CONTROL_TOKEN,
        PERMISSION_CONTROL_STATIC_PAGES,
        PERMISSION_CONTROL_LEADERBOARD,
        PERMISSION_CONTROL_PROFILE,
        PERMISSION_CONTROL_APP,
        PERMISSION_CONTROL_APP_EXPORT,
        PERMISSION_CONTROL_FOOTER,
        PERMISSION_INCREASED_SESSION_DURATION,
        PERMISSION_SHOW_FILES,
        PERMISSION_DELETE_FILES,
        PERMISSION_UPLOAD_FILES,
        PERMISSION_CONTROL_IMPRESSUM,
        PERMISSION_CONTROL_COUNTDOWN,
        PERMISSION_CONTROL_FORM,
        PERMISSION_CONTROL_CHALLENGE,
        PERMISSION_CONTROL_HOME,
        PERMISSION_CONTROL_NOTIFICATIONS,
        PERMISSION_SEND_NOTIFICATIONS,
        PERMISSION_CONTROL_COMMUNITIES,
        PERMISSION_CONTROL_ADMISSION,
        PERMISSION_CONTROL_RACE,
        PERMISSION_CONTROL_TEAM,
        PERMISSION_CONTROL_QR_FIGHT,
        PERMISSION_CONTROL_BMEJEGY,
        PERMISSION_SHOW_AUDIT_LOG,
        PERMISSION_DEV_DEBUG,
        PERMISSION_SHOW_INSTANCE,
        PERMISSION_SHOW_LIVE_STATS,
        PERMISSION_CONTROL_MESSAGING,
        PERMISSION_SEND_MESSAGE,
        PERMISSION_CONTROL_EMAILS,
        PERMISSION_SEND_EMAIL,
        PERMISSION_CONTROL_ACCESS_KEYS,
        PERMISSION_CONTROL_PROTO,
        PERMISSION_CONTROL_CONFERENCE,
        PERMISSION_CONTROL_SHEETS,
    )

}

object StaffPermissions : PermissionGroup {

    /// TaskComponent

    val PERMISSION_RATE_TASKS = PermissionValidator(
        "TASK_RATE",
        "A feladat beadások értékelése és megtekintése",
        readOnly = false,
        component = TaskComponent::class
    )

    val PERMISSION_SHOW_TASKS = PermissionValidator(
        "TASK_SHOW",
        "Feladatok megtekintése",
        readOnly = true,
        component = TaskComponent::class
    )

    val PERMISSION_EDIT_TASKS = PermissionValidator(
        "TASK_EDIT",
        "Feladatok szerkesztése",
        readOnly = false,
        component = TaskComponent::class
    )

    val PERMISSION_CREATE_TASKS = PermissionValidator(
        "TASK_CREATE",
        "Feladatok létrehozása",
        readOnly = false,
        component = TaskComponent::class
    )

    val PERMISSION_DELETE_TASKS = PermissionValidator(
        "TASK_DELETE",
        "Feladatok törlése",
        readOnly = false,
        component = TaskComponent::class
    )

    val PERMISSION_SHOW_TASK_CATEGORIES = PermissionValidator(
        "TASK_CATEGORY_SHOW",
        "Feladat kategóriák megtekintése",
        readOnly = true,
        component = TaskComponent::class
    )

    val PERMISSION_EDIT_TASK_CATEGORIES = PermissionValidator(
        "TASK_CATEGORY_EDIT",
        "Feladat kategóriák szerkesztése",
        readOnly = false,
        component = TaskComponent::class
    )

    val PERMISSION_CREATE_TASK_CATEGORIES = PermissionValidator(
        "TASK_CATEGORY_CREATE",
        "Feladat kategóriák létrehozása",
        readOnly = false,
        component = TaskComponent::class
    )

    val PERMISSION_DELETE_TASK_CATEGORIES = PermissionValidator(
        "TASK_CATEGORY_DELETE",
        "Feladat kategóriák törlése",
        readOnly = false,
        component = TaskComponent::class
    )

    val PERMISSION_TASK_MANUAL_SUBMIT = PermissionValidator(
        "TASK_MANUAL_SUBMIT",
        "Feladatok komponens testreszabása",
        readOnly = false,
        component = TaskComponent::class
    )

    /// DebtComponent

    val PERMISSION_SHOW_DEBTS = PermissionValidator(
        "DEBT_SHOW",
        "Összes tartozás megtekintése",
        readOnly = true,
        component = DebtComponent::class
    )

    val PERMISSION_EDIT_DEBTS = PermissionValidator(
        "DEBT_EDIT",
        "Tartozások szerkesztése",
        readOnly = false,
        component = DebtComponent::class
    )

    val PERMISSION_DELETE_DEBTS = PermissionValidator(
        "DEBT_DELETE",
        "Tartozások törlése",
        readOnly = false,
        component = DebtComponent::class
    )

    val PERMISSION_SHOW_PRODUCTS = PermissionValidator(
        "DEBT_PRODUCT_SHOW",
        "Termékek megtekintése",
        readOnly = true,
        component = DebtComponent::class
    )

    val PERMISSION_EDIT_PRODUCTS = PermissionValidator(
        "DEBT_PRODUCT_EDIT",
        "Termékek szerkesztése",
        readOnly = false,
        component = DebtComponent::class
    )

    val PERMISSION_CREATE_PRODUCTS = PermissionValidator(
        "DEBT_PRODUCT_CREATE",
        "Termékek létrehozása",
        readOnly = false,
        component = DebtComponent::class
    )

    val PERMISSION_DELETE_PRODUCTS = PermissionValidator(
        "DEBT_PRODUCT_DELETE",
        "Termékek törlése",
        readOnly = false,
        component = DebtComponent::class
    )

    val PERMISSION_SELL_FOOD = PermissionValidator(
        "DEBT_SELL_FOOD",
        "Étel típusú termék eladása",
        readOnly = false,
        component = DebtComponent::class
    )

    val PERMISSION_SELL_MERCH = PermissionValidator(
        "DEBT_SELL_MERCH",
        "Merch típusú termék eladása",
        readOnly = false,
        component = DebtComponent::class
    )

    val PERMISSION_SELL_ANY_PRODUCT = PermissionValidator(
        "DEBT_SELL_ANY_PRODUCT",
        "Bármilyen típusú termék eladása",
        readOnly = false,
        component = DebtComponent::class
    )

    val PERMISSION_SHOW_SOLD_STATS = PermissionValidator(
        "DEBT_SHOW_SOLD_STATS",
        "Eladott termékek statisztikájának megtekintése",
        readOnly = true,
        component = DebtComponent::class
    )

    /// LocationComponent

    val PERMISSION_TRACK_ONE_GROUP = PermissionValidator(
        "LOCATION_TRACK_ONE_GROUP",
        "Egy csoport követése",
        readOnly = false,
        component = LocationComponent::class
    )

    val PERMISSION_TRACK_EVERYBODY = PermissionValidator(
        "LOCATION_TRACK_EVERYBODY",
        "Az összes csoport követése",
        readOnly = false,
        component = LocationComponent::class
    )

    val PERMISSION_SHOW_LOCATIONS = PermissionValidator(
        "LOCATION_SHOW",
        "Az összes nyers pozíció és jelző megtekintése",
        readOnly = true,
        component = LocationComponent::class
    )

    val PERMISSION_CREATE_LOCATIONS = PermissionValidator(
        "LOCATION_CREATE",
        "Jelző készítése",
        readOnly = false,
        component = LocationComponent::class
    )

    val PERMISSION_EDIT_LOCATIONS = PermissionValidator(
        "LOCATION_EDIT",
        "Nyers pozíció és jelző szerkesztése",
        readOnly = false,
        component = LocationComponent::class
    )

    val PERMISSION_DELETE_LOCATIONS = PermissionValidator(
        "LOCATION_DELETE",
        "Nyers pozíció és jelző törlése",
        readOnly = false,
        component = LocationComponent::class
    )

    val PERMISSION_BROADCAST_LOCATION = PermissionValidator(
        "LOCATION_BROADCAST",
        "Közvetítés funkció (mindenki láthassa)",
        readOnly = false,
        component = LocationComponent::class
    )

    // RiddleComponent

    val PERMISSION_SHOW_RIDDLES = PermissionValidator(
        "RIDDLE_SHOW",
        "Riddle megtekintése",
        readOnly = true,
        component = RiddleComponent::class
    )

    val PERMISSION_EDIT_RIDDLES = PermissionValidator(
        "RIDDLE_EDIT",
        "Riddle szerkesztése",
        readOnly = false,
        component = RiddleComponent::class
    )

    val PERMISSION_CREATE_RIDDLES = PermissionValidator(
        "RIDDLE_CREATE",
        "Riddle létrehozása",
        readOnly = false,
        component = RiddleComponent::class
    )

    val PERMISSION_DELETE_RIDDLES = PermissionValidator(
        "RIDDLE_DELETE",
        "Riddle törlése",
        readOnly = false,
        component = RiddleComponent::class
    )

    val PERMISSION_SHOW_RIDDLE_CATEGORIES = PermissionValidator(
        "RIDDLE_CATEGORY_SHOW",
        "Riddle kategória megtekintése",
        readOnly = true,
        component = RiddleComponent::class
    )

    val PERMISSION_EDIT_RIDDLE_CATEGORIES = PermissionValidator(
        "RIDDLE_CATEGORY_EDIT",
        "Riddle kategória szerkesztése",
        readOnly = false,
        component = RiddleComponent::class
    )

    val PERMISSION_CREATE_RIDDLE_CATEGORIES = PermissionValidator(
        "RIDDLE_CATEGORY_CREATE",
        "Riddle kategória létrehozása",
        readOnly = false,
        component = RiddleComponent::class
    )

    val PERMISSION_DELETE_RIDDLE_CATEGORIES = PermissionValidator(
        "RIDDLE_CATEGORY_DELETE",
        "Riddle kategória törlése",
        readOnly = false,
        component = RiddleComponent::class
    )

    val PERMISSION_SHOW_RIDDLE_SUBMISSIONS = PermissionValidator(
        "RIDDLE_SUBMISSIONS_SHOW",
        "Riddle beadások listázása",
        readOnly = true,
        component = RiddleComponent::class
    )

    val PERMISSION_DELETE_RIDDLE_SUBMISSIONS = PermissionValidator(
        "RIDDLE_SUBMISSIONS_DELETE",
        "Riddle beadások törlése",
        readOnly = false,
        component = RiddleComponent::class
    )

    /// NewsComponent

    val PERMISSION_SHOW_NEWS = PermissionValidator(
        "NEWS_SHOW",
        "Hírek megtekintése",
        readOnly = true,
        component = NewsComponent::class
    )

    val PERMISSION_EDIT_NEWS = PermissionValidator(
        "NEWS_EDIT",
        "Hírek szerkesztése",
        readOnly = false,
        component = NewsComponent::class
    )

    val PERMISSION_CREATE_NEWS = PermissionValidator(
        "NEWS_CREATE",
        "Hírek létrehozása",
        readOnly = false,
        component = NewsComponent::class
    )

    val PERMISSION_DELETE_NEWS = PermissionValidator(
        "NEWS_DELETE",
        "Hírek törlése",
        readOnly = false,
        component = NewsComponent::class
    )

    /// EventComponent

    val PERMISSION_SHOW_EVENTS = PermissionValidator(
        "EVENT_SHOW",
        "Események megtekintése",
        readOnly = true,
        component = EventComponent::class
    )

    val PERMISSION_EDIT_EVENTS = PermissionValidator(
        "EVENT_EDIT",
        "Események szerkesztése",
        readOnly = false,
        component = EventComponent::class
    )

    val PERMISSION_CREATE_EVENTS = PermissionValidator(
        "EVENT_CREATE",
        "Események létrehozása",
        readOnly = false,
        component = EventComponent::class
    )

    val PERMISSION_DELETE_EVENTS = PermissionValidator(
        "EVENT_DELETE",
        "Események törlése",
        readOnly = false,
        component = EventComponent::class
    )

    /// ErrorLogComponent

    val PERMISSION_SHOW_ERROR_LOG = PermissionValidator(
        "ERROR_LOG_SHOW",
        "Hibaüzenetek megtekintése",
        readOnly = true,
        component = ErrorLogComponent::class
    )

    val PERMISSION_DELETE_ERROR_LOG = PermissionValidator(
        "ERROR_LOG_DELETE",
        "Hibaüzenetek törlése",
        readOnly = false,
        component = ErrorLogComponent::class
    )


    /// GalleryComponent

    val PERMISSION_SHOW_GALLERY = PermissionValidator(
        "GALLERY_SHOW",
        "Galéria képek megtekintése",
        readOnly = true,
        component = GalleryComponent::class
    )

    val PERMISSION_EDIT_GALLERY = PermissionValidator(
        "GALLERY_EDIT",
        "Galéria képek szerkesztése",
        readOnly = false,
        component = GalleryComponent::class
    )

    val PERMISSION_CREATE_GALLERY = PermissionValidator(
        "GALLERY_CREATE",
        "Galéria képek létrehozása",
        readOnly = false,
        component = GalleryComponent::class
    )

    val PERMISSION_DELETE_GALLERY = PermissionValidator(
        "GALLERY_DELETE",
        "Galéria képek törlése",
        readOnly = false,
        component = GalleryComponent::class
    )

    /// TokenComponent

    val PERMISSION_SHOW_TOKENS = PermissionValidator(
        "TOKEN_SHOW",
        "Tokenek megtekintése",
        readOnly = true,
        component = TokenComponent::class
    )

    val PERMISSION_EDIT_TOKENS = PermissionValidator(
        "TOKEN_EDIT",
        "Tokenek szerkesztése",
        readOnly = false,
        component = TokenComponent::class
    )

    val PERMISSION_CREATE_TOKENS = PermissionValidator(
        "TOKEN_CREATE",
        "Tokenek létrehozása",
        readOnly = false,
        component = TokenComponent::class
    )

    val PERMISSION_DELETE_TOKENS = PermissionValidator(
        "TOKEN_DELETE",
        "Tokenek törlése",
        readOnly = false,
        component = TokenComponent::class
    )

    val PERMISSION_SHOW_TOKEN_SUBMISSIONS = PermissionValidator(
        "TOKEN_SUBMISSION_SHOW",
        "Tokenek beolvasások megtekintése",
        readOnly = true,
        component = TokenComponent::class
    )

    val PERMISSION_EDIT_TOKEN_SUBMISSIONS = PermissionValidator(
        "TOKEN_SUBMISSION_DELETE",
        "Tokenek beolvasások törlése",
        readOnly = false,
        component = TokenComponent::class
    )

    /// UserHandlingComponent

    val PERMISSION_SHOW_GROUPS = PermissionValidator(
        "GROUP_SHOW",
        "Csoportok megtekintése",
        readOnly = true,
        component = UserHandlingComponent::class
    )

    val PERMISSION_EDIT_GROUPS = PermissionValidator(
        "GROUP_EDIT",
        "Csoportok szerkesztése",
        readOnly = false,
        component = UserHandlingComponent::class
    )

    val PERMISSION_CREATE_GROUPS = PermissionValidator(
        "GROUP_CREATE",
        "Csoportok létrehozása",
        readOnly = false,
        component = UserHandlingComponent::class
    )

    val PERMISSION_DELETE_GROUPS = PermissionValidator(
        "GROUP_DELETE",
        "Csoportok törlése", // NOTE: Ezt a jogot most csak a SUPERUSER birtokolja
        readOnly = false,
        component = UserHandlingComponent::class
    )

    val PERMISSION_SHOW_USERS = PermissionValidator(
        "USER_SHOW",
        "Felhasználók megtekintése",
        readOnly = true,
        component = UserHandlingComponent::class
    )

    val PERMISSION_EDIT_USERS = PermissionValidator(
        "USER_EDIT",
        "Felhasználók szerkesztése",
        readOnly = false,
        component = UserHandlingComponent::class
    )

    val PERMISSION_DELETE_USERS = PermissionValidator(
        "USER_DELETE",
        "Felhasználók  törlése",
        readOnly = false,
        component = UserHandlingComponent::class
    )

    val PERMISSION_SHOW_GUILD_MAPPINGS = PermissionValidator(
        "GUILD_MAPPING_SHOW",
        "Gárda hozzárendelések megtekintése",
        readOnly = true,
        component = UserHandlingComponent::class
    )

    val PERMISSION_EDIT_GUILD_MAPPINGS = PermissionValidator(
        "GUILD_MAPPING_EDIT",
        "Gárda hozzárendelések szerkesztése",
        readOnly = false,
        component = UserHandlingComponent::class
    )

    val PERMISSION_CREATE_GUILD_MAPPINGS = PermissionValidator(
        "GUILD_MAPPING_CREATE",
        "Gárda hozzárendelések létrehozása",
        readOnly = false,
        component = UserHandlingComponent::class
    )

    val PERMISSION_DELETE_GUILD_MAPPINGS = PermissionValidator(
        "GUILD_MAPPING_DELETE",
        "Gárda hozzárendelések törlése",
        readOnly = false,
        component = UserHandlingComponent::class
    )

    val PERMISSION_SHOW_GROUP_MAPPINGS = PermissionValidator(
        "GROUP_MAPPING_SHOW",
        "Csoport hozzárendelések megtekintése",
        readOnly = true,
        component = UserHandlingComponent::class
    )

    val PERMISSION_EDIT_GROUP_MAPPINGS = PermissionValidator(
        "GROUP_MAPPING_EDIT",
        "Csoport hozzárendelések szerkesztése",
        readOnly = false,
        component = UserHandlingComponent::class
    )

    val PERMISSION_CREATE_GROUP_MAPPINGS = PermissionValidator(
        "GROUP_MAPPING_CREATE",
        "Csoport hozzárendelések létrehozása",
        readOnly = false,
        component = UserHandlingComponent::class
    )

    val PERMISSION_DELETE_GROUP_MAPPINGS = PermissionValidator(
        "GROUP_MAPPING_DELETE",
        "Csoport hozzárendelések törlése",
        readOnly = false,
        component = UserHandlingComponent::class
    )

    val PERMISSION_SHOW_PERMISSION_GROUPS = PermissionValidator(
        "PERMISSION_GROUP_SHOW",
        "Jogkörök megtekintése",
        readOnly = true,
        component = UserHandlingComponent::class
    )

    val PERMISSION_EDIT_PERMISSION_GROUPS = PermissionValidator(
        "PERMISSION_GROUP_EDIT",
        "Jogkörök szerkesztése",
        readOnly = false,
        component = UserHandlingComponent::class
    )

    val PERMISSION_CREATE_PERMISSION_GROUPS = PermissionValidator(
        "PERMISSION_GROUP_CREATE",
        "Jogkörök létrehozása",
        readOnly = false,
        component = UserHandlingComponent::class
    )

    val PERMISSION_DELETE_PERMISSION_GROUPS = PermissionValidator(
        "PERMISSION_GROUP_DELETE",
        "Jogkörök törlése",
        readOnly = false,
        component = UserHandlingComponent::class
    )

    /// StaticPageComponent

    val PERMISSION_SHOW_STATIC_PAGES = PermissionValidator(
        "STATIC_PAGE_SHOW",
        "Statikus oldalak megtekintése",
        readOnly = true,
        component = StaticPageComponent::class
    )

    val PERMISSION_EDIT_STATIC_PAGES = PermissionValidator(
        "STATIC_PAGE_EDIT",
        "Statikus oldalak szerkesztése",
        readOnly = false,
        component = StaticPageComponent::class
    )

    val PERMISSION_CREATE_STATIC_PAGES = PermissionValidator(
        "STATIC_PAGE_CREATE",
        "Statikus oldalak létrehozása",
        readOnly = false,
        component = StaticPageComponent::class
    )

    val PERMISSION_DELETE_STATIC_PAGES = PermissionValidator(
        "STATIC_PAGE_DELETE",
        "Statikus oldalak törlése",
        readOnly = false,
        component = StaticPageComponent::class
    )

    val PERMISSION_MODIFY_ANY_STATIC_PAGES = PermissionValidator(
        "STATICPAGE_MODIFY_ANY",
        "Az összes statikus oldal módosítása",
        readOnly = false,
        component = StaticPageComponent::class
    )

    /// LeaderBoardComponent

    val PERMISSION_SHOW_LEADERBOARD = PermissionValidator(
        "LEADERBOARD_SHOW",
        "Toplista megtekintése",
        readOnly = true,
        component = LeaderBoardComponent::class
    )

    /// FormComponent

    val PERMISSION_SHOW_FORM = PermissionValidator(
        "FORM_SHOW",
        "Űrlapok megtekintése",
        readOnly = true,
        component = FormComponent::class
    )

    val PERMISSION_EDIT_FORM = PermissionValidator(
        "FORM_EDIT",
        "Űrlapok szerkesztése",
        readOnly = false,
        component = FormComponent::class
    )

    val PERMISSION_CREATE_FORM = PermissionValidator(
        "FORM_CREATE",
        "Űrlapok létrehozása",
        readOnly = false,
        component = FormComponent::class
    )

    val PERMISSION_DELETE_FORM = PermissionValidator(
        "FORM_DELETE",
        "Űrlapok törlése",
        readOnly = false,
        component = FormComponent::class
    )

    val PERMISSION_SHOW_FORM_RESULTS = PermissionValidator(
        "FORM_RESULT_SHOW",
        "Űrlapok beadások megtekintése",
        readOnly = true,
        component = FormComponent::class
    )

    val PERMISSION_EDIT_FORM_RESULTS = PermissionValidator(
        "FORM_RESULT_EDIT",
        "Űrlapok beadások szerkesztése",
        readOnly = false,
        component = FormComponent::class
    )

    val PERMISSION_DELETE_FORM_RESULTS = PermissionValidator(
        "FORM_RESULT_DELETE",
        "Űrlapok beadások törlése",
        readOnly = false,
        component = FormComponent::class
    )

    /// ChallengeComponent

    val PERMISSION_SHOW_CHALLENGES = PermissionValidator(
        "CHALLENGE_SHOW",
        "Beadások megtekintése",
        readOnly = true,
        component = ChallengeComponent::class
    )

    val PERMISSION_EDIT_CHALLENGES = PermissionValidator(
        "CHALLENGE_EDIT",
        "Beadások szerkesztése",
        readOnly = false,
        component = ChallengeComponent::class
    )

    val PERMISSION_CREATE_CHALLENGES = PermissionValidator(
        "CHALLENGE_CREATE",
        "Beadások létrehozása",
        readOnly = false,
        component = ChallengeComponent::class
    )

    val PERMISSION_DELETE_CHALLENGES = PermissionValidator(
        "CHALLENGE_DELETE",
        "Beadások törlése",
        readOnly = false,
        component = ChallengeComponent::class
    )

    /// AdmissionComponent

    val PERMISSION_VALIDATE_ADMISSION = PermissionValidator(
        "ADMISSION_VALIDATE",
        "Beléptetés kezelése",
        readOnly = false,
        component = AdmissionComponent::class
    )

    val PERMISSION_EXPORT_ADMISSION = PermissionValidator(
        "ADMISSION_EXPORT",
        "Beléptetés kimentése",
        readOnly = false,
        component = AdmissionComponent::class
    )

    val PERMISSION_SHOW_ADMISSIONS = PermissionValidator(
        "ADMISSION_SHOW",
        "Beléptetés logok megtekintése",
        readOnly = true,
        component = AdmissionComponent::class
    )

    val PERMISSION_EDIT_ADMISSIONS = PermissionValidator(
        "ADMISSION_EDIT",
        "Beléptetés logok szerkesztése",
        readOnly = false,
        component = AdmissionComponent::class
    )

    val PERMISSION_CREATE_ADMISSIONS = PermissionValidator(
        "ADMISSION_CREATE",
        "Beléptetés logok létrehozása",
        readOnly = false,
        component = AdmissionComponent::class
    )

    val PERMISSION_DELETE_ADMISSIONS = PermissionValidator(
        "ADMISSION_DELETE",
        "Beléptetés logok törlése",
        readOnly = false,
        component = AdmissionComponent::class
    )

    val PERMISSION_SHOW_TICKETS = PermissionValidator(
        "TICKET_SHOW",
        "Jegyek megtekintése",
        readOnly = true,
        component = AdmissionComponent::class
    )

    val PERMISSION_EDIT_TICKETS = PermissionValidator(
        "TICKET_EDIT",
        "Jegyek szerkesztése",
        readOnly = false,
        component = AdmissionComponent::class
    )

    val PERMISSION_CREATE_TICKETS = PermissionValidator(
        "TICKET_CREATE",
        "Jegyek létrehozása",
        readOnly = false,
        component = AdmissionComponent::class
    )

    val PERMISSION_DELETE_TICKETS = PermissionValidator(
        "TICKET_DELETE",
        "Jegyek törlése",
        readOnly = false,
        component = AdmissionComponent::class
    )

    /// RaceComponent

    val PERMISSION_SHOW_RACE = PermissionValidator(
        "RACE_SHOW",
        "Verseny eredmények megtekintése",
        readOnly = true,
        component = RaceComponent::class
    )

    val PERMISSION_EDIT_RACE = PermissionValidator(
        "RACE_EDIT",
        "Verseny eredmények szerkesztése",
        readOnly = false,
        component = RaceComponent::class
    )

    val PERMISSION_CREATE_RACE = PermissionValidator(
        "RACE_CREATE",
        "Verseny eredmények létrehozása",
        readOnly = false,
        component = RaceComponent::class
    )

    val PERMISSION_DELETE_RACE = PermissionValidator(
        "RACE_DELETE",
        "Verseny eredmények törlése",
        readOnly = false,
        component = RaceComponent::class
    )

    val PERMISSION_SHOW_RACE_CATEGORY = PermissionValidator(
        "RACE_CATEGORY_SHOW",
        "Verseny kategóriák megtekintése",
        readOnly = true,
        component = RaceComponent::class
    )

    val PERMISSION_EDIT_RACE_CATEGORY = PermissionValidator(
        "RACE_CATEGORY_EDIT",
        "Verseny kategóriák szerkesztése",
        readOnly = false,
        component = RaceComponent::class
    )

    val PERMISSION_CREATE_RACE_CATEGORY = PermissionValidator(
        "RACE_CATEGORY_CREATE",
        "Verseny kategóriák létrehozása",
        readOnly = false,
        component = RaceComponent::class
    )

    val PERMISSION_DELETE_RACE_CATEGORY = PermissionValidator(
        "RACE_CATEGORY_DELETE",
        "Verseny kategóriák törlése",
        readOnly = false,
        component = RaceComponent::class
    )

    /// QrFightComponent

    val PERMISSION_SHOW_QR_FIGHT = PermissionValidator(
        "QR_FIGHT_SHOW",
        "QR Fight megtekintése",
        readOnly = true,
        component = QrFightComponent::class
    )

    val PERMISSION_EDIT_QR_FIGHT = PermissionValidator(
        "QR_FIGHT_EDIT",
        "QR Fight szerkesztése",
        readOnly = false,
        component = QrFightComponent::class
    )

    val PERMISSION_CREATE_QR_FIGHT = PermissionValidator(
        "QR_FIGHT_CREATE",
        "QR Fight létrehozása",
        readOnly = false,
        component = QrFightComponent::class
    )

    val PERMISSION_DELETE_QR_FIGHT = PermissionValidator(
        "QR_FIGHT_DELETE",
        "QR Fight törlése",
        readOnly = false,
        component = QrFightComponent::class
    )

    /// CommunitiesComponent

    val PERMISSION_SHOW_COMMUNITIES = PermissionValidator(
        "COMMUNITIES_SHOW",
        "Körök és reszortok megtekintése",
        readOnly = true,
        component = CommunitiesComponent::class
    )

    val PERMISSION_EDIT_COMMUNITIES = PermissionValidator(
        "COMMUNITIES_EDIT",
        "Körök és reszortok szerkesztése",
        readOnly = false,
        component = CommunitiesComponent::class
    )

    val PERMISSION_CREATE_COMMUNITIES = PermissionValidator(
        "COMMUNITIES_CREATE",
        "Körök és reszortok létrehozása",
        readOnly = false,
        component = CommunitiesComponent::class
    )

    val PERMISSION_DELETE_COMMUNITIES = PermissionValidator(
        "COMMUNITIES_DELETE",
        "Körök és reszortok törlése",
        readOnly = false,
        component = CommunitiesComponent::class
    )

    /// BmejegyComponent

    val PERMISSION_SHOW_BME_TICKET = PermissionValidator(
        "BME_TICKET_SHOW",
        "BME Jegy bejegyzések megtekintése",
        readOnly = true,
        component = BmejegyComponent::class
    )

    val PERMISSION_EDIT_BME_TICKET = PermissionValidator(
        "BME_TICKET_EDIT",
        "BME Jegy bejegyzések szerkesztése",
        readOnly = false,
        component = BmejegyComponent::class
    )

    val PERMISSION_CREATE_BME_TICKET = PermissionValidator(
        "BME_TICKET_CREATE",
        "BME Jegy bejegyzések létrehozása",
        readOnly = false,
        component = BmejegyComponent::class
    )

    val PERMISSION_DELETE_BME_TICKET = PermissionValidator(
        "BME_TICKET_DELETE",
        "BME Jegy bejegyzések törlése",
        readOnly = false,
        component = BmejegyComponent::class
    )

    /// TeamComponent

    val PERMISSION_SHOW_TEAM_JOINS = PermissionValidator(
        "TEAM_JOIN_SHOW",
        "Csapat jelentkezési kérelmek megtekintése",
        readOnly = true,
        component = TeamComponent::class
    )

    val PERMISSION_EDIT_TEAM_JOINS = PermissionValidator(
        "TEAM_JOIN_EDIT",
        "Csapat jelentkezési kérelmek szerkesztése",
        readOnly = false,
        component = TeamComponent::class
    )

    val PERMISSION_CREATE_TEAM_JOINS = PermissionValidator(
        "TEAM_JOIN_CREATE",
        "Csapat jelentkezési kérelmek készítése",
        readOnly = false,
        component = TeamComponent::class
    )

    val PERMISSION_DELETE_TEAM_JOINS = PermissionValidator(
        "TEAM_JOIN_DELETE",
        "Csapat jelentkezési kérelmek törlése",
        readOnly = false,
        component = TeamComponent::class
    )

    val PERMISSION_SHOW_TEAM_INTRODUCTIONS = PermissionValidator(
        "TEAM_INTRODUCTION_SHOW",
        "Csapat bemutatkozások megtekintése",
        readOnly = true,
        component = TeamComponent::class
    )

    val PERMISSION_EDIT_TEAM_INTRODUCTIONS = PermissionValidator(
        "TEAM_INTRODUCTION_EDIT",
        "Csapat bemutatkozások elfogadása vagy elutasítása",
        readOnly = false,
        component = TeamComponent::class
    )

    /// AccessKeyComponent

    val PERMISSION_SHOW_ACCESS_KEYS = PermissionValidator(
        "ACCESS_KEYS_SHOW",
        "Hozzáférési kulcsok megtekintése",
        readOnly = true,
        component = TeamComponent::class
    )

    val PERMISSION_EDIT_ACCESS_KEYS = PermissionValidator(
        "ACCESS_KEYS_EDIT",
        "Hozzáférési kulcsok szerkesztése",
        readOnly = false,
        component = TeamComponent::class
    )

    val PERMISSION_CREATE_ACCESS_KEYS = PermissionValidator(
        "ACCESS_KEYS_CREATE",
        "Hozzáférési kulcsok létrehozása",
        readOnly = false,
        component = TeamComponent::class
    )

    val PERMISSION_DELETE_ACCESS_KEYS = PermissionValidator(
        "ACCESS_KEYS_DELETE",
        "Hozzáférési kulcsok törlése",
        readOnly = false,
        component = TeamComponent::class
    )

    /// EventComponent

    val PERMISSION_SHOW_EMAILS = PermissionValidator(
        "EMAIL_SHOW",
        "Email sablonok megtekintése",
        readOnly = true,
        component = EmailComponent::class
    )

    val PERMISSION_EDIT_EMAILS = PermissionValidator(
        "EMAIL_EDIT",
        "Email sablonok szerkesztése",
        readOnly = false,
        component = EmailComponent::class
    )

    val PERMISSION_CREATE_EMAILS = PermissionValidator(
        "EMAIL_CREATE",
        "Email sablonok létrehozása",
        readOnly = false,
        component = EmailComponent::class
    )

    val PERMISSION_DELETE_EMAILS = PermissionValidator(
        "EMAIL_DELETE",
        "Email sablonok törlése",
        readOnly = false,
        component = EmailComponent::class
    )

    /// ProtoComponent

    val PERMISSION_SHOW_PROTO = PermissionValidator(
        "PROTO_SHOW",
        "Prototípusok megtekintése",
        readOnly = true,
        component = ProtoComponent::class
    )

    val PERMISSION_EDIT_PROTO = PermissionValidator(
        "PROTO_EDIT",
        "Prototípusok szerkesztése",
        readOnly = false,
        component = ProtoComponent::class
    )

    val PERMISSION_CREATE_PROTO = PermissionValidator(
        "PROTO_CREATE",
        "Prototípusok létrehozása",
        readOnly = false,
        component = ProtoComponent::class
    )

    val PERMISSION_DELETE_PROTO = PermissionValidator(
        "PROTO_DELETE",
        "Prototípusok törlése",
        readOnly = false,
        component = ProtoComponent::class
    )

    /// ConferenceComponent

    val PERMISSION_SHOW_CONFERENCE = PermissionValidator(
        "CONFERENCE_SHOW",
        "Konferencia tartalmának megtekintése",
        readOnly = true,
        component = ConferenceComponent::class
    )

    val PERMISSION_EDIT_CONFERENCE = PermissionValidator(
        "CONFERENCE_EDIT",
        "Konferencia tartalmának szerkesztése",
        readOnly = false,
        component = ConferenceComponent::class
    )

    val PERMISSION_CREATE_CONFERENCE = PermissionValidator(
        "CONFERENCE_CREATE",
        "Konferencia tartalmának létrehozása",
        readOnly = false,
        component = ConferenceComponent::class
    )

    val PERMISSION_DELETE_CONFERENCE = PermissionValidator(
        "CONFERENCE_DELETE",
        "Konferencia tartalmának törlése",
        readOnly = false,
        component = ConferenceComponent::class
    )

    /// SheeetsComponent

    val PERMISSION_SHOW_SHEETS = PermissionValidator(
        "SHEETS_SHOW",
        "Sheets integrációk megtekintése",
        readOnly = true,
        component = SheetsComponent::class
    )

    val PERMISSION_EDIT_SHEETS = PermissionValidator(
        "SHEETS_EDIT",
        "Sheets integrációk szerkesztése",
        readOnly = false,
        component = SheetsComponent::class
    )

    val PERMISSION_CREATE_SHEETS = PermissionValidator(
        "SHEETS_CREATE",
        "Sheets integrációk létrehozása",
        readOnly = false,
        component = SheetsComponent::class
    )

    val PERMISSION_DELETE_SHEETS = PermissionValidator(
        "SHEETS_DELETE",
        "Sheets integrációk törlése",
        readOnly = false,
        component = SheetsComponent::class
    )

    override fun allPermissions() = listOf(
        PERMISSION_RATE_TASKS,
        PERMISSION_SHOW_TASKS,
        PERMISSION_EDIT_TASKS,
        PERMISSION_CREATE_TASKS,
        PERMISSION_DELETE_TASKS,
        PERMISSION_SHOW_TASK_CATEGORIES,
        PERMISSION_EDIT_TASK_CATEGORIES,
        PERMISSION_CREATE_TASK_CATEGORIES,
        PERMISSION_DELETE_TASK_CATEGORIES,
        PERMISSION_TASK_MANUAL_SUBMIT,

        PERMISSION_SHOW_DEBTS,
        PERMISSION_EDIT_DEBTS,
        PERMISSION_SHOW_PRODUCTS,
        PERMISSION_EDIT_PRODUCTS,
        PERMISSION_CREATE_PRODUCTS,
        PERMISSION_DELETE_PRODUCTS,
        PERMISSION_SELL_FOOD,
        PERMISSION_SELL_MERCH,
        PERMISSION_SELL_ANY_PRODUCT,
        PERMISSION_SHOW_SOLD_STATS,

        PERMISSION_TRACK_ONE_GROUP,
        PERMISSION_TRACK_EVERYBODY,
        PERMISSION_SHOW_LOCATIONS,
        PERMISSION_CREATE_LOCATIONS,
        PERMISSION_EDIT_LOCATIONS,
        PERMISSION_DELETE_LOCATIONS,
        PERMISSION_BROADCAST_LOCATION,

        PERMISSION_SHOW_RIDDLES,
        PERMISSION_EDIT_RIDDLES,
        PERMISSION_CREATE_RIDDLES,
        PERMISSION_DELETE_RIDDLES,
        PERMISSION_SHOW_RIDDLE_CATEGORIES,
        PERMISSION_EDIT_RIDDLE_CATEGORIES,
        PERMISSION_CREATE_RIDDLE_CATEGORIES,
        PERMISSION_DELETE_RIDDLE_CATEGORIES,
        PERMISSION_SHOW_RIDDLE_SUBMISSIONS,
        PERMISSION_DELETE_RIDDLE_SUBMISSIONS,

        PERMISSION_SHOW_NEWS,
        PERMISSION_EDIT_NEWS,
        PERMISSION_CREATE_NEWS,
        PERMISSION_DELETE_NEWS,

        PERMISSION_SHOW_EVENTS,
        PERMISSION_EDIT_EVENTS,
        PERMISSION_CREATE_EVENTS,
        PERMISSION_DELETE_EVENTS,

        PERMISSION_SHOW_ERROR_LOG,
        PERMISSION_DELETE_ERROR_LOG,

        PERMISSION_SHOW_GALLERY,
        PERMISSION_EDIT_GALLERY,
        PERMISSION_CREATE_GALLERY,
        PERMISSION_DELETE_GALLERY,

        PERMISSION_SHOW_TOKENS,
        PERMISSION_EDIT_TOKENS,
        PERMISSION_CREATE_TOKENS,
        PERMISSION_DELETE_TOKENS,
        PERMISSION_SHOW_TOKEN_SUBMISSIONS,
        PERMISSION_EDIT_TOKEN_SUBMISSIONS,

        PERMISSION_SHOW_GROUPS,
        PERMISSION_EDIT_GROUPS,
        PERMISSION_CREATE_GROUPS,
        PERMISSION_DELETE_GROUPS,
        PERMISSION_SHOW_USERS,
        PERMISSION_EDIT_USERS,
        PERMISSION_DELETE_USERS,
        PERMISSION_SHOW_GUILD_MAPPINGS,
        PERMISSION_EDIT_GUILD_MAPPINGS,
        PERMISSION_CREATE_GUILD_MAPPINGS,
        PERMISSION_DELETE_GUILD_MAPPINGS,
        PERMISSION_SHOW_GROUP_MAPPINGS,
        PERMISSION_EDIT_GROUP_MAPPINGS,
        PERMISSION_CREATE_GROUP_MAPPINGS,
        PERMISSION_DELETE_GROUP_MAPPINGS,
        PERMISSION_SHOW_PERMISSION_GROUPS,
        PERMISSION_EDIT_PERMISSION_GROUPS,
        PERMISSION_CREATE_PERMISSION_GROUPS,
        PERMISSION_DELETE_PERMISSION_GROUPS,

        PERMISSION_SHOW_STATIC_PAGES,
        PERMISSION_EDIT_STATIC_PAGES,
        PERMISSION_CREATE_STATIC_PAGES,
        PERMISSION_DELETE_STATIC_PAGES,
        PERMISSION_MODIFY_ANY_STATIC_PAGES,

        PERMISSION_SHOW_LEADERBOARD,

        PERMISSION_SHOW_FORM,
        PERMISSION_EDIT_FORM,
        PERMISSION_CREATE_FORM,
        PERMISSION_DELETE_FORM,
        PERMISSION_SHOW_FORM_RESULTS,
        PERMISSION_EDIT_FORM_RESULTS,
        PERMISSION_DELETE_FORM_RESULTS,

        PERMISSION_SHOW_CHALLENGES,
        PERMISSION_EDIT_CHALLENGES,
        PERMISSION_CREATE_CHALLENGES,
        PERMISSION_DELETE_CHALLENGES,

        PERMISSION_VALIDATE_ADMISSION,
        PERMISSION_SHOW_ADMISSIONS,
        PERMISSION_EDIT_ADMISSIONS,
        PERMISSION_CREATE_ADMISSIONS,
        PERMISSION_DELETE_ADMISSIONS,
        PERMISSION_SHOW_TICKETS,
        PERMISSION_EDIT_TICKETS,
        PERMISSION_CREATE_TICKETS,
        PERMISSION_DELETE_TICKETS,

        PERMISSION_SHOW_RACE,
        PERMISSION_EDIT_RACE,
        PERMISSION_CREATE_RACE,
        PERMISSION_DELETE_RACE,
        PERMISSION_SHOW_RACE_CATEGORY,
        PERMISSION_EDIT_RACE_CATEGORY,
        PERMISSION_CREATE_RACE_CATEGORY,
        PERMISSION_DELETE_RACE_CATEGORY,

        PERMISSION_SHOW_QR_FIGHT,
        PERMISSION_EDIT_QR_FIGHT,
        PERMISSION_CREATE_QR_FIGHT,
        PERMISSION_DELETE_QR_FIGHT,

        PERMISSION_SHOW_COMMUNITIES,
        PERMISSION_EDIT_COMMUNITIES,
        PERMISSION_CREATE_COMMUNITIES,
        PERMISSION_DELETE_COMMUNITIES,

        PERMISSION_SHOW_BME_TICKET,
        PERMISSION_EDIT_BME_TICKET,
        PERMISSION_CREATE_BME_TICKET,
        PERMISSION_DELETE_BME_TICKET,

        PERMISSION_SHOW_TEAM_JOINS,
        PERMISSION_EDIT_TEAM_JOINS,
        PERMISSION_CREATE_TEAM_JOINS,
        PERMISSION_DELETE_TEAM_JOINS,

        PERMISSION_SHOW_TEAM_INTRODUCTIONS,
        PERMISSION_EDIT_TEAM_INTRODUCTIONS,

        PERMISSION_SHOW_ACCESS_KEYS,
        PERMISSION_EDIT_ACCESS_KEYS,
        PERMISSION_CREATE_ACCESS_KEYS,
        PERMISSION_DELETE_ACCESS_KEYS,

        PERMISSION_SHOW_EMAILS,
        PERMISSION_EDIT_EMAILS,
        PERMISSION_CREATE_EMAILS,
        PERMISSION_DELETE_EMAILS,

        PERMISSION_SHOW_PROTO,
        PERMISSION_EDIT_PROTO,
        PERMISSION_CREATE_PROTO,
        PERMISSION_DELETE_PROTO,

        PERMISSION_SHOW_CONFERENCE,
        PERMISSION_EDIT_CONFERENCE,
        PERMISSION_CREATE_CONFERENCE,
        PERMISSION_DELETE_CONFERENCE,

        PERMISSION_SHOW_SHEETS,
        PERMISSION_EDIT_SHEETS,
        PERMISSION_CREATE_SHEETS,
        PERMISSION_DELETE_SHEETS,
    )

}
