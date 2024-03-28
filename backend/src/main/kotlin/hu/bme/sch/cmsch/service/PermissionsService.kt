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
import hu.bme.sch.cmsch.component.proto.ProtoComponent
import hu.bme.sch.cmsch.component.qrfight.QrFightComponent
import hu.bme.sch.cmsch.component.race.RaceComponent
import hu.bme.sch.cmsch.component.riddle.RiddleComponent
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
    private val permissionSources: List<CmschPermissionSource>
) {
    val allControlPermissions = permissionSources.flatMap { it.getControlPermissions() }
    val allStaffPermissions = permissionSources.flatMap { it.getStaffPermissions() }
}

class PermissionValidator internal constructor(
    val permissionString: String = "",
    val description: String = "",
    val component: KClass<out ComponentBase>? = null,
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
        permissionString = "HAS_GROUP")
            { user -> DI.instance.userService.getById(user.internalId).group != null }

    val PERMISSION_IMPLICIT_ANYONE = PermissionValidator(
        description = "Everyone has this permission",
        permissionString = "ANYONE")
            { _ -> true }

    val PERMISSION_NOBODY = PermissionValidator(
        description = "Nobody has this permission",
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
        component = NewsComponent::class
    )

    val PERMISSION_CONTROL_TASKS = PermissionValidator(
        "TASK_CONTROL",
        "Feladatok komponens testreszabása",
        component = TaskComponent::class
    )

    val PERMISSION_CONTROL_EVENTS = PermissionValidator(
        "EVENT_CONTROL",
        "Események komponens testreszabása",
        component = EventComponent::class
    )

    val PERMISSION_CONTROL_GALLERY = PermissionValidator(
        "GALLERY_CONTROL",
        "Galéria komponens testreszabása",
        component = GalleryComponent::class
    )

    val PERMISSION_CONTROL_DEBTS = PermissionValidator(
        "DEBT_CONTROL",
        "Debt komponens testreszabása",
        component = DebtComponent::class
    )

    val PERMISSION_CONTROL_RIDDLE = PermissionValidator(
        "RIDDLE_CONTROL",
        "Riddle komponens testreszabása",
        component = RiddleComponent::class
    )

    val PERMISSION_CONTROL_TOKEN = PermissionValidator(
        "TOKEN_CONTROL",
        "Token komponens testreszabása",
        component = TokenComponent::class
    )

    val PERMISSION_CONTROL_STATIC_PAGES = PermissionValidator(
        "STATICPAGES_CONTROL",
        "StaticPage komponens testreszabása",
        component = StaticPageComponent::class
    )

    val PERMISSION_CONTROL_LEADERBOARD = PermissionValidator(
        "LEADERBOARD_CONTROL",
        "LeaderBoard komponens testreszabása",
        component = LeaderBoardComponent::class
    )

    val PERMISSION_CONTROL_PROFILE = PermissionValidator(
        "PROFILE_CONTROL",
        "Profil komponens testreszabása"
    )

    val PERMISSION_CONTROL_APP = PermissionValidator(
        "APP_CONTROL",
        "Az alkalmazás testreszabása",
        component = ApplicationComponent::class
    )

    val PERMISSION_CONTROL_APP_EXPORT = PermissionValidator(
        "APP_EXPORT_CONTROL",
        "Teljes alkalmazás állapotának kiexportálása",
        component = ApplicationComponent::class
    )

    val PERMISSION_CONTROL_APP_IMPORT = PermissionValidator(
        "APP_IMPORT_CONTROL",
        "Teljes alkalmazás állapotának beimportálása",
        component = ApplicationComponent::class
    )

    val PERMISSION_CONTROL_FOOTER = PermissionValidator(
        "FOOTER_CONTROL",
        "Lábléc beállításai",
        component = ApplicationComponent::class
    )

    val PERMISSION_INCREASED_SESSION_DURATION = PermissionValidator(
        "INCREASED_SESSION_DURATION",
        "Megnövelt session idő",
        component = ApplicationComponent::class
    )

    val PERMISSION_SHOW_FILES = PermissionValidator(
        "FILE_SHOW",
        "Feltöltött fájlok megtekintése",
        component = ApplicationComponent::class
    )

    val PERMISSION_DELETE_FILES = PermissionValidator(
        "FILE_DELETE",
        "Feltöltött fájlok törlése",
        component = ApplicationComponent::class
    )

    val PERMISSION_UPLOAD_FILES = PermissionValidator(
        "FILE_UPLOAD",
        "Új fájl feltöltése",
        component = ApplicationComponent::class
    )

    val PERMISSION_CONTROL_IMPRESSUM = PermissionValidator(
        "IMPRESSUM_CONTROL",
        "Impresszum komponens testreszabása",
        component = ImpressumComponent::class
    )

    val PERMISSION_CONTROL_COUNTDOWN = PermissionValidator(
        "COUNTDOWN_CONTROL",
        "Visszaszámlálás komponens testreszabása",
        component = CountdownComponent::class
    )

    val PERMISSION_CONTROL_FORM = PermissionValidator(
        "FORM_CONTROL",
        "Űrlapok komponens testreszabása",
        component = FormComponent::class
    )

    val PERMISSION_CONTROL_CHALLENGE = PermissionValidator(
        "CHALLENGE_CONTROL",
        "Beadások komponens testreszabása",
        component = ChallengeComponent::class
    )

    val PERMISSION_CONTROL_HOME = PermissionValidator(
        "HOME_CONTROL",
        "Kezdőlap komponens testreszabása",
        component = HomeComponent::class
    )

    val PERMISSION_CONTROL_COMMUNITIES = PermissionValidator(
        "COMMUNITIES_CONTROL",
        "Kezdőlap komponens testreszabása",
        component = CommunitiesComponent::class
    )

    val PERMISSION_CONTROL_ADMISSION = PermissionValidator(
        "ADMISSION_CONTROL",
        "Beléptetés komponens testreszabása",
        component = AdmissionComponent::class
    )

    val PERMISSION_CONTROL_RACE = PermissionValidator(
        "RACE_CONTROL",
        "Verseny (sörmérés) komponens testreszabása",
        component = RaceComponent::class
    )

    val PERMISSION_CONTROL_TEAM = PermissionValidator(
        "TEAM_CONTROL",
        "Csapat komponens testreszabása",
        component = TeamComponent::class
    )

    val PERMISSION_CONTROL_QR_FIGHT = PermissionValidator(
        "QR_FIGHT_CONTROL",
        "QR Fight komponens testreszabása",
        component = QrFightComponent::class
    )

    val PERMISSION_CONTROL_BMEJEGY = PermissionValidator(
        "BMEJEGY_CONTROL",
        "Bmejegy komponens testreszabása",
        component = BmejegyComponent::class
    )

    val PERMISSION_CONTROL_LOCATION = PermissionValidator(
        "LOCATION_CONTROL",
        "Helymeghatározás komponens testreszabása",
        component = LocationComponent::class
    )

    val PERMISSION_SHOW_AUDIT_LOG = PermissionValidator(
        "SHOW_AUDIT_LOG",
        "Audit log olvasása",
        component = ApplicationComponent::class
    )

    val PERMISSION_DEV_DEBUG = PermissionValidator(
        "DEVELOPER_DEBUG",
        "Fejlesztői beállítások",
        component = ApplicationComponent::class
    )

    val PERMISSION_SHOW_INSTANCE = PermissionValidator(
        "SHOW_INSTANCE_INFO",
        "Szerver adatok megtekintése",
        component = ApplicationComponent::class
    )

    val PERMISSION_SHOW_LIVE_STATS = PermissionValidator(
        "SHOW_LIVE_STATS",
        "Élő statisztikák megjelenítése",
        component = ApplicationComponent::class
    )

    val PERMISSION_CONTROL_MESSAGING = PermissionValidator(
        "MESSAGING_CONTROL",
        "Értesítések komponens kezelése",
        component = MessagingComponent::class
    )

    val PERMISSION_SEND_MESSAGE = PermissionValidator(
        "MESSAGING_SEND",
        "Debug üzenet küldése",
        component = MessagingComponent::class
    )

    val PERMISSION_CONTROL_EMAILS = PermissionValidator(
        "EMAILS_CONTROL",
        "Email komponens testreszabása",
        component = EmailComponent::class
    )

    val PERMISSION_SEND_EMAIL = PermissionValidator(
        "MESSAGING_SEND",
        "Teszt email küldése",
        component = EmailComponent::class
    )

    val PERMISSION_CONTROL_ACCESS_KEYS = PermissionValidator(
        "ACCESS_KEY_CONTROL",
        "Hozzáférési kulcs komponens testreszabása",
        component = AccessKeyComponent::class
    )

    val PERMISSION_CONTROL_PROTO = PermissionValidator(
        "PROTO_CONTROL",
        "Prototípusok testreszabása",
        component = ApplicationComponent::class
    )

    val PERMISSION_CONTROL_CONFERENCE = PermissionValidator(
        "CONFERENCE_CONTROL",
        "Konferencia testreszabása",
        component = ApplicationComponent::class
    )

    override fun allPermissions() = listOf(
        PERMISSION_CONTROL_NEWS,
        PERMISSION_CONTROL_TASKS,
        PERMISSION_CONTROL_EVENTS,
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
    )

}

object StaffPermissions : PermissionGroup {

    /// TaskComponent

    val PERMISSION_RATE_TASKS = PermissionValidator(
        "TASK_RATE",
        "A feladat beadások értékelése és megtekintése",
        component = TaskComponent::class
    )

    val PERMISSION_SHOW_TASKS = PermissionValidator(
        "TASK_SHOW",
        "Feladatok megtekintése",
        component = TaskComponent::class
    )

    val PERMISSION_EDIT_TASKS = PermissionValidator(
        "TASK_EDIT",
        "Feladatok szerkesztése",
        component = TaskComponent::class
    )

    val PERMISSION_CREATE_TASKS = PermissionValidator(
        "TASK_CREATE",
        "Feladatok létrehozása",
        component = TaskComponent::class
    )

    val PERMISSION_DELETE_TASKS = PermissionValidator(
        "TASK_DELETE",
        "Feladatok törlése",
        component = TaskComponent::class
    )

    val PERMISSION_SHOW_TASK_CATEGORIES = PermissionValidator(
        "TASK_CATEGORY_SHOW",
        "Feladat kategóriák megtekintése",
        component = TaskComponent::class
    )

    val PERMISSION_EDIT_TASK_CATEGORIES = PermissionValidator(
        "TASK_CATEGORY_EDIT",
        "Feladat kategóriák szerkesztése",
        component = TaskComponent::class
    )

    val PERMISSION_CREATE_TASK_CATEGORIES = PermissionValidator(
        "TASK_CATEGORY_CREATE",
        "Feladat kategóriák létrehozása",
        component = TaskComponent::class
    )

    val PERMISSION_DELETE_TASK_CATEGORIES = PermissionValidator(
        "TASK_CATEGORY_DELETE",
        "Feladat kategóriák törlése",
        component = TaskComponent::class
    )

    val PERMISSION_TASK_MANUAL_SUBMIT = PermissionValidator(
        "TASK_MANUAL_SUBMIT",
        "Feladatok komponens testreszabása",
        component = TaskComponent::class
    )

    /// DebtComponent

    val PERMISSION_SHOW_DEBTS = PermissionValidator(
        "DEBT_SHOW",
        "Összes tartozás megtekintése",
        component = DebtComponent::class
    )

    val PERMISSION_EDIT_DEBTS = PermissionValidator(
        "DEBT_EDIT",
        "Tartozások szerkesztése",
        component = DebtComponent::class
    )

    val PERMISSION_DELETE_DEBTS = PermissionValidator(
        "DEBT_DELETE",
        "Tartozások törlése",
        component = DebtComponent::class
    )

    val PERMISSION_SHOW_PRODUCTS = PermissionValidator(
        "DEBT_PRODUCT_SHOW",
        "Termékek megtekintése",
        component = DebtComponent::class
    )

    val PERMISSION_EDIT_PRODUCTS = PermissionValidator(
        "DEBT_PRODUCT_EDIT",
        "Termékek szerkesztése",
        component = DebtComponent::class
    )

    val PERMISSION_CREATE_PRODUCTS = PermissionValidator(
        "DEBT_PRODUCT_CREATE",
        "Termékek létrehozása",
        component = DebtComponent::class
    )

    val PERMISSION_DELETE_PRODUCTS = PermissionValidator(
        "DEBT_PRODUCT_DELETE",
        "Termékek törlése",
        component = DebtComponent::class
    )

    val PERMISSION_SELL_FOOD = PermissionValidator(
        "DEBT_SELL_FOOD",
        "Étel típusú termék eladása",
        component = DebtComponent::class
    )

    val PERMISSION_SELL_MERCH = PermissionValidator(
        "DEBT_SELL_MERCH",
        "Merch típusú termék eladása",
        component = DebtComponent::class
    )

    val PERMISSION_SELL_ANY_PRODUCT = PermissionValidator(
        "DEBT_SELL_ANY_PRODUCT",
        "Bármilyen típusú termék eladása",
        component = DebtComponent::class
    )

    val PERMISSION_SHOW_SOLD_STATS = PermissionValidator(
        "DEBT_SHOW_SOLD_STATS",
        "Eladott termékek statisztikájának megtekintése",
        component = DebtComponent::class
    )

    /// LocationComponent

    val PERMISSION_TRACK_ONE_GROUP = PermissionValidator(
        "LOCATION_TRACK_ONE_GROUP",
        "Egy csoport követése",
        component = LocationComponent::class
    )

    val PERMISSION_TRACK_EVERYBODY = PermissionValidator(
        "LOCATION_TRACK_EVERYBODY",
        "Az összes csoport követése",
        component = LocationComponent::class
    )

    val PERMISSION_SHOW_LOCATIONS = PermissionValidator(
        "LOCATION_SHOW",
        "Az összes nyers pozíció és jelző megtekintése",
        component = LocationComponent::class
    )

    val PERMISSION_CREATE_LOCATIONS = PermissionValidator(
        "LOCATION_CREATE",
        "Jelző készítése",
        component = LocationComponent::class
    )

    val PERMISSION_EDIT_LOCATIONS = PermissionValidator(
        "LOCATION_EDIT",
        "Nyers pozíció és jelző szerkesztése",
        component = LocationComponent::class
    )

    val PERMISSION_DELETE_LOCATIONS = PermissionValidator(
        "LOCATION_DELETE",
        "Nyers pozíció és jelző törlése",
        component = LocationComponent::class
    )

    val PERMISSION_BROADCAST_LOCATION = PermissionValidator(
        "LOCATION_BROADCAST",
        "Közvetítés funkció (mindenki láthassa)",
        component = LocationComponent::class
    )

    // RiddleComponent

    val PERMISSION_SHOW_RIDDLES = PermissionValidator(
        "RIDDLE_SHOW",
        "Riddle megtekintése",
        component = RiddleComponent::class
    )

    val PERMISSION_EDIT_RIDDLES = PermissionValidator(
        "RIDDLE_EDIT",
        "Riddle szerkesztése",
        component = RiddleComponent::class
    )

    val PERMISSION_CREATE_RIDDLES = PermissionValidator(
        "RIDDLE_CREATE",
        "Riddle létrehozása",
        component = RiddleComponent::class
    )

    val PERMISSION_DELETE_RIDDLES = PermissionValidator(
        "RIDDLE_DELETE",
        "Riddle törlése",
        component = RiddleComponent::class
    )

    val PERMISSION_SHOW_RIDDLE_CATEGORIES = PermissionValidator(
        "RIDDLE_CATEGORY_SHOW",
        "Riddle kategória megtekintése",
        component = RiddleComponent::class
    )

    val PERMISSION_EDIT_RIDDLE_CATEGORIES = PermissionValidator(
        "RIDDLE_CATEGORY_EDIT",
        "Riddle kategória szerkesztése",
        component = RiddleComponent::class
    )

    val PERMISSION_CREATE_RIDDLE_CATEGORIES = PermissionValidator(
        "RIDDLE_CATEGORY_CREATE",
        "Riddle kategória létrehozása",
        component = RiddleComponent::class
    )

    val PERMISSION_DELETE_RIDDLE_CATEGORIES = PermissionValidator(
        "RIDDLE_CATEGORY_DELETE",
        "Riddle kategória törlése",
        component = RiddleComponent::class
    )

    val PERMISSION_SHOW_RIDDLE_SUBMISSIONS = PermissionValidator(
        "RIDDLE_SUBMISSIONS_SHOW",
        "Riddle beadások listázása",
        component = RiddleComponent::class
    )

    val PERMISSION_DELETE_RIDDLE_SUBMISSIONS = PermissionValidator(
        "RIDDLE_SUBMISSIONS_DELETE",
        "Riddle beadások törlése",
        component = RiddleComponent::class
    )

    /// NewsComponent

    val PERMISSION_SHOW_NEWS = PermissionValidator(
        "NEWS_SHOW",
        "Hírek megtekintése",
        component = NewsComponent::class
    )

    val PERMISSION_EDIT_NEWS = PermissionValidator(
        "NEWS_EDIT",
        "Hírek szerkesztése",
        component = NewsComponent::class
    )

    val PERMISSION_CREATE_NEWS = PermissionValidator(
        "NEWS_CREATE",
        "Hírek létrehozása",
        component = NewsComponent::class
    )

    val PERMISSION_DELETE_NEWS = PermissionValidator(
        "NEWS_DELETE",
        "Hírek törlése",
        component = NewsComponent::class
    )

    /// EventComponent

    val PERMISSION_SHOW_EVENTS = PermissionValidator(
        "EVENT_SHOW",
        "Események megtekintése",
        component = EventComponent::class
    )

    val PERMISSION_EDIT_EVENTS = PermissionValidator(
        "EVENT_EDIT",
        "Események szerkesztése",
        component = EventComponent::class
    )

    val PERMISSION_CREATE_EVENTS = PermissionValidator(
        "EVENT_CREATE",
        "Események létrehozása",
        component = EventComponent::class
    )

    val PERMISSION_DELETE_EVENTS = PermissionValidator(
        "EVENT_DELETE",
        "Események törlése",
        component = EventComponent::class
    )

    /// GalleryComponent

    val PERMISSION_SHOW_GALLERY = PermissionValidator(
        "GALLERY_SHOW",
        "Galéria megtekintése",
        component = GalleryComponent::class
    )

    val PERMISSION_EDIT_GALLERY = PermissionValidator(
        "GALLERY_EDIT",
        "Galéria szerkesztése",
        component = GalleryComponent::class
    )

    val PERMISSION_CREATE_GALLERY = PermissionValidator(
        "GALLERY_CREATE",
        "Kép létrehozása a Galériában",
        component = GalleryComponent::class
    )

    val PERMISSION_DELETE_GALLERY = PermissionValidator(
        "GALLERY_DELETE",
        "Kép törlése a Galériában",
        component = GalleryComponent::class
    )

    /// TokenComponent

    val PERMISSION_SHOW_TOKENS = PermissionValidator(
        "TOKEN_SHOW",
        "Tokenek megtekintése",
        component = TokenComponent::class
    )

    val PERMISSION_EDIT_TOKENS = PermissionValidator(
        "TOKEN_EDIT",
        "Tokenek szerkesztése",
        component = TokenComponent::class
    )

    val PERMISSION_CREATE_TOKENS = PermissionValidator(
        "TOKEN_CREATE",
        "Tokenek létrehozása",
        component = TokenComponent::class
    )

    val PERMISSION_DELETE_TOKENS = PermissionValidator(
        "TOKEN_DELETE",
        "Tokenek törlése",
        component = TokenComponent::class
    )

    val PERMISSION_SHOW_TOKEN_SUBMISSIONS = PermissionValidator(
        "TOKEN_SUBMISSION_SHOW",
        "Tokenek beolvasások megtekintése",
        component = TokenComponent::class
    )

    val PERMISSION_EDIT_TOKEN_SUBMISSIONS = PermissionValidator(
        "TOKEN_SUBMISSION_DELETE",
        "Tokenek beolvasások törlése",
        component = TokenComponent::class
    )

    /// UserHandlingComponent

    val PERMISSION_SHOW_GROUPS = PermissionValidator(
        "GROUP_SHOW",
        "Csoportok megtekintése",
        component = UserHandlingComponent::class
    )

    val PERMISSION_EDIT_GROUPS = PermissionValidator(
        "GROUP_EDIT",
        "Csoportok szerkesztése",
        component = UserHandlingComponent::class
    )

    val PERMISSION_CREATE_GROUPS = PermissionValidator(
        "GROUP_CREATE",
        "Csoportok létrehozása",
        component = UserHandlingComponent::class
    )

    val PERMISSION_DELETE_GROUPS = PermissionValidator(
        "GROUP_DELETE",
        "Csoportok törlése",
        component = UserHandlingComponent::class
    )

    val PERMISSION_SHOW_USERS = PermissionValidator(
        "USER_SHOW",
        "Felhasználók megtekintése",
        component = UserHandlingComponent::class
    )

    val PERMISSION_EDIT_USERS = PermissionValidator(
        "USER_EDIT",
        "Felhasználók szerkesztése",
        component = UserHandlingComponent::class
    )

    val PERMISSION_DELETE_USERS = PermissionValidator(
        "USER_DELETE",
        "Felhasználók  törlése",
        component = UserHandlingComponent::class
    )

    val PERMISSION_SHOW_GUILD_MAPPINGS = PermissionValidator(
        "GUILD_MAPPING_SHOW",
        "Gárda hozzárendelések megtekintése",
        component = UserHandlingComponent::class
    )

    val PERMISSION_EDIT_GUILD_MAPPINGS = PermissionValidator(
        "GUILD_MAPPING_EDIT",
        "Gárda hozzárendelések szerkesztése",
        component = UserHandlingComponent::class
    )

    val PERMISSION_CREATE_GUILD_MAPPINGS = PermissionValidator(
        "GUILD_MAPPING_CREATE",
        "Gárda hozzárendelések létrehozása",
        component = UserHandlingComponent::class
    )

    val PERMISSION_DELETE_GUILD_MAPPINGS = PermissionValidator(
        "GUILD_MAPPING_DELETE",
        "Gárda hozzárendelések törlése",
        component = UserHandlingComponent::class
    )

    val PERMISSION_SHOW_GROUP_MAPPINGS = PermissionValidator(
        "GROUP_MAPPING_SHOW",
        "Csoport hozzárendelések megtekintése",
        component = UserHandlingComponent::class
    )

    val PERMISSION_EDIT_GROUP_MAPPINGS = PermissionValidator(
        "GROUP_MAPPING_EDIT",
        "Csoport hozzárendelések szerkesztése",
        component = UserHandlingComponent::class
    )

    val PERMISSION_CREATE_GROUP_MAPPINGS = PermissionValidator(
        "GROUP_MAPPING_CREATE",
        "Csoport hozzárendelések létrehozása",
        component = UserHandlingComponent::class
    )

    val PERMISSION_DELETE_GROUP_MAPPINGS = PermissionValidator(
        "GROUP_MAPPING_DELETE",
        "Csoport hozzárendelések törlése",
        component = UserHandlingComponent::class
    )

    /// StaticPageComponent

    val PERMISSION_SHOW_STATIC_PAGES = PermissionValidator(
        "STATIC_PAGE_SHOW",
        "Statikus oldalak megtekintése",
        component = StaticPageComponent::class
    )

    val PERMISSION_EDIT_STATIC_PAGES = PermissionValidator(
        "STATIC_PAGE_EDIT",
        "Statikus oldalak szerkesztése",
        component = StaticPageComponent::class
    )

    val PERMISSION_CREATE_STATIC_PAGES = PermissionValidator(
        "STATIC_PAGE_CREATE",
        "Statikus oldalak létrehozása",
        component = StaticPageComponent::class
    )

    val PERMISSION_DELETE_STATIC_PAGES = PermissionValidator(
        "STATIC_PAGE_DELETE",
        "Statikus oldalak törlése",
        component = StaticPageComponent::class
    )

    val PERMISSION_MODIFY_ANY_STATIC_PAGES = PermissionValidator(
        "STATICPAGE_MODIFY_ANY",
        "Az összes statikus oldal módosítása",
        component = StaticPageComponent::class
    )

    /// LeaderBoardComponent

    val PERMISSION_SHOW_LEADERBOARD = PermissionValidator(
        "LEADERBOARD_SHOW",
        "Toplista megtekintése",
        component = LeaderBoardComponent::class
    )

    /// FormComponent

    val PERMISSION_SHOW_FORM = PermissionValidator(
        "FORM_SHOW",
        "Űrlapok megtekintése",
        component = FormComponent::class
    )

    val PERMISSION_EDIT_FORM = PermissionValidator(
        "FORM_EDIT",
        "Űrlapok szerkesztése",
        component = FormComponent::class
    )

    val PERMISSION_CREATE_FORM = PermissionValidator(
        "FORM_CREATE",
        "Űrlapok létrehozása",
        component = FormComponent::class
    )

    val PERMISSION_DELETE_FORM = PermissionValidator(
        "FORM_DELETE",
        "Űrlapok törlése",
        component = FormComponent::class
    )

    val PERMISSION_SHOW_FORM_RESULTS = PermissionValidator(
        "FORM_RESULT_SHOW",
        "Űrlapok beadások megtekintése",
        component = FormComponent::class
    )

    val PERMISSION_EDIT_FORM_RESULTS = PermissionValidator(
        "FORM_RESULT_EDIT",
        "Űrlapok beadások szerkesztése",
        component = FormComponent::class
    )

    val PERMISSION_DELETE_FORM_RESULTS = PermissionValidator(
        "FORM_RESULT_DELETE",
        "Űrlapok beadások törlése",
        component = FormComponent::class
    )

    /// ChallengeComponent

    val PERMISSION_SHOW_CHALLENGES = PermissionValidator(
        "CHALLENGE_SHOW",
        "Beadások megtekintése",
        component = ChallengeComponent::class
    )

    val PERMISSION_EDIT_CHALLENGES = PermissionValidator(
        "CHALLENGE_EDIT",
        "Beadások szerkesztése",
        component = ChallengeComponent::class
    )

    val PERMISSION_CREATE_CHALLENGES = PermissionValidator(
        "CHALLENGE_CREATE",
        "Beadások létrehozása",
        component = ChallengeComponent::class
    )

    val PERMISSION_DELETE_CHALLENGES = PermissionValidator(
        "CHALLENGE_DELETE",
        "Beadások törlése",
        component = ChallengeComponent::class
    )

    /// AdmissionComponent

    val PERMISSION_VALIDATE_ADMISSION = PermissionValidator(
        "ADMISSION_VALIDATE",
        "Beléptetés kezelése",
        component = AdmissionComponent::class
    )

    val PERMISSION_EXPORT_ADMISSION = PermissionValidator(
        "ADMISSION_EXPORT",
        "Beléptetés kimentése",
        component = AdmissionComponent::class
    )

    val PERMISSION_SHOW_ADMISSIONS = PermissionValidator(
        "ADMISSION_SHOW",
        "Beléptetés logok megtekintése",
        component = AdmissionComponent::class
    )

    val PERMISSION_EDIT_ADMISSIONS = PermissionValidator(
        "ADMISSION_EDIT",
        "Beléptetés logok szerkesztése",
        component = AdmissionComponent::class
    )

    val PERMISSION_CREATE_ADMISSIONS = PermissionValidator(
        "ADMISSION_CREATE",
        "Beléptetés logok létrehozása",
        component = AdmissionComponent::class
    )

    val PERMISSION_DELETE_ADMISSIONS = PermissionValidator(
        "ADMISSION_DELETE",
        "Beléptetés logok törlése",
        component = AdmissionComponent::class
    )

    val PERMISSION_SHOW_TICKETS = PermissionValidator(
        "TICKET_SHOW",
        "Jegyek megtekintése",
        component = AdmissionComponent::class
    )

    val PERMISSION_EDIT_TICKETS = PermissionValidator(
        "TICKET_EDIT",
        "Jegyek szerkesztése",
        component = AdmissionComponent::class
    )

    val PERMISSION_CREATE_TICKETS = PermissionValidator(
        "TICKET_CREATE",
        "Jegyek létrehozása",
        component = AdmissionComponent::class
    )

    val PERMISSION_DELETE_TICKETS = PermissionValidator(
        "TICKET_DELETE",
        "Jegyek törlése",
        component = AdmissionComponent::class
    )

    /// RaceComponent

    val PERMISSION_SHOW_RACE = PermissionValidator(
        "RACE_SHOW",
        "Verseny eredmények megtekintése",
        component = RaceComponent::class
    )

    val PERMISSION_EDIT_RACE = PermissionValidator(
        "RACE_EDIT",
        "Verseny eredmények szerkesztése",
        component = RaceComponent::class
    )

    val PERMISSION_CREATE_RACE = PermissionValidator(
        "RACE_CREATE",
        "Verseny eredmények létrehozása",
        component = RaceComponent::class
    )

    val PERMISSION_DELETE_RACE = PermissionValidator(
        "RACE_DELETE",
        "Verseny eredmények törlése",
        component = RaceComponent::class
    )

    val PERMISSION_SHOW_RACE_CATEGORY = PermissionValidator(
        "RACE_CATEGORY_SHOW",
        "Verseny kategóriák megtekintése",
        component = RaceComponent::class
    )

    val PERMISSION_EDIT_RACE_CATEGORY = PermissionValidator(
        "RACE_CATEGORY_EDIT",
        "Verseny kategóriák szerkesztése",
        component = RaceComponent::class
    )

    val PERMISSION_CREATE_RACE_CATEGORY = PermissionValidator(
        "RACE_CATEGORY_CREATE",
        "Verseny kategóriák létrehozása",
        component = RaceComponent::class
    )

    val PERMISSION_DELETE_RACE_CATEGORY = PermissionValidator(
        "RACE_CATEGORY_DELETE",
        "Verseny kategóriák törlése",
        component = RaceComponent::class
    )

    /// QrFightComponent

    val PERMISSION_SHOW_QR_FIGHT = PermissionValidator(
        "QR_FIGHT_SHOW",
        "QR Fight megtekintése",
        component = QrFightComponent::class
    )

    val PERMISSION_EDIT_QR_FIGHT = PermissionValidator(
        "QR_FIGHT_EDIT",
        "QR Fight szerkesztése",
        component = QrFightComponent::class
    )

    val PERMISSION_CREATE_QR_FIGHT = PermissionValidator(
        "QR_FIGHT_CREATE",
        "QR Fight létrehozása",
        component = QrFightComponent::class
    )

    val PERMISSION_DELETE_QR_FIGHT = PermissionValidator(
        "QR_FIGHT_DELETE",
        "QR Fight törlése",
        component = QrFightComponent::class
    )

    /// CommunitiesComponent

    val PERMISSION_SHOW_COMMUNITIES = PermissionValidator(
        "COMMUNITIES_SHOW",
        "Körök és reszortok megtekintése",
        component = CommunitiesComponent::class
    )

    val PERMISSION_EDIT_COMMUNITIES = PermissionValidator(
        "COMMUNITIES_EDIT",
        "Körök és reszortok szerkesztése",
        component = CommunitiesComponent::class
    )

    val PERMISSION_CREATE_COMMUNITIES = PermissionValidator(
        "COMMUNITIES_CREATE",
        "Körök és reszortok létrehozása",
        component = CommunitiesComponent::class
    )

    val PERMISSION_DELETE_COMMUNITIES = PermissionValidator(
        "COMMUNITIES_DELETE",
        "Körök és reszortok törlése",
        component = CommunitiesComponent::class
    )

    /// BmejegyComponent

    val PERMISSION_SHOW_BME_TICKET = PermissionValidator(
        "BME_TICKET_SHOW",
        "BME Jegy bejegyzések megtekintése",
        component = BmejegyComponent::class
    )

    val PERMISSION_EDIT_BME_TICKET = PermissionValidator(
        "BME_TICKET_EDIT",
        "BME Jegy bejegyzések szerkesztése",
        component = BmejegyComponent::class
    )

    val PERMISSION_CREATE_BME_TICKET = PermissionValidator(
        "BME_TICKET_CREATE",
        "BME Jegy bejegyzések létrehozása",
        component = BmejegyComponent::class
    )

    val PERMISSION_DELETE_BME_TICKET = PermissionValidator(
        "BME_TICKET_DELETE",
        "BME Jegy bejegyzések törlése",
        component = BmejegyComponent::class
    )


    /// TeamComponent

    val PERMISSION_SHOW_TEAM_JOINS = PermissionValidator(
        "TEAM_JOIN_SHOW",
        "Csapat jelentkezési kérelmek megtekintése",
        component = TeamComponent::class
    )

    val PERMISSION_EDIT_TEAM_JOINS = PermissionValidator(
        "TEAM_JOIN_EDIT",
        "Csapat jelentkezési kérelmek szerkesztése",
        component = TeamComponent::class
    )

    val PERMISSION_CREATE_TEAM_JOINS = PermissionValidator(
        "TEAM_JOIN_CREATE",
        "Csapat jelentkezési kérelmek készítése",
        component = TeamComponent::class
    )

    val PERMISSION_DELETE_TEAM_JOINS = PermissionValidator(
        "TEAM_JOIN_DELETE",
        "Csapat jelentkezési kérelmek törlése",
        component = TeamComponent::class
    )

    /// AccessKeyComponent

    val PERMISSION_SHOW_ACCESS_KEYS = PermissionValidator(
        "ACCESS_KEYS_SHOW",
        "Hozzáférési kulcsok megtekintése",
        component = TeamComponent::class
    )

    val PERMISSION_EDIT_ACCESS_KEYS = PermissionValidator(
        "ACCESS_KEYS_EDIT",
        "Hozzáférési kulcsok szerkesztése",
        component = TeamComponent::class
    )

    val PERMISSION_CREATE_ACCESS_KEYS = PermissionValidator(
        "ACCESS_KEYS_CREATE",
        "Hozzáférési kulcsok létrehozása",
        component = TeamComponent::class
    )

    val PERMISSION_DELETE_ACCESS_KEYS = PermissionValidator(
        "ACCESS_KEYS_DELETE",
        "Hozzáférési kulcsok törlése",
        component = TeamComponent::class
    )

    /// EventComponent

    val PERMISSION_SHOW_EMAILS = PermissionValidator(
        "EMAIL_SHOW",
        "Email sablonok megtekintése",
        component = EmailComponent::class
    )

    val PERMISSION_EDIT_EMAILS = PermissionValidator(
        "EMAIL_EDIT",
        "Email sablonok szerkesztése",
        component = EmailComponent::class
    )

    val PERMISSION_CREATE_EMAILS = PermissionValidator(
        "EMAIL_CREATE",
        "Email sablonok létrehozása",
        component = EmailComponent::class
    )

    val PERMISSION_DELETE_EMAILS = PermissionValidator(
        "EMAIL_DELETE",
        "Email sablonok törlése",
        component = EmailComponent::class
    )

    /// ProtoComponent

    val PERMISSION_SHOW_PROTO = PermissionValidator(
        "PROTO_SHOW",
        "Prototípusok megtekintése",
        component = ProtoComponent::class
    )

    val PERMISSION_EDIT_PROTO = PermissionValidator(
        "PROTO_EDIT",
        "Prototípusok szerkesztése",
        component = ProtoComponent::class
    )

    val PERMISSION_CREATE_PROTO = PermissionValidator(
        "PROTO_CREATE",
        "Prototípusok létrehozása",
        component = ProtoComponent::class
    )

    val PERMISSION_DELETE_PROTO = PermissionValidator(
        "PROTO_DELETE",
        "Prototípusok törlése",
        component = ProtoComponent::class
    )

    /// ConferenceComponent

    val PERMISSION_SHOW_CONFERENCE = PermissionValidator(
        "CONFERENCE_SHOW",
        "Konferencia tartalmának megtekintése",
        component = ConferenceComponent::class
    )

    val PERMISSION_EDIT_CONFERENCE = PermissionValidator(
        "CONFERENCE_EDIT",
        "Konferencia tartalmának szerkesztése",
        component = ConferenceComponent::class
    )

    val PERMISSION_CREATE_CONFERENCE = PermissionValidator(
        "CONFERENCE_CREATE",
        "Konferencia tartalmának létrehozása",
        component = ConferenceComponent::class
    )

    val PERMISSION_DELETE_CONFERENCE = PermissionValidator(
        "CONFERENCE_DELETE",
        "Konferencia tartalmának törlése",
        component = ConferenceComponent::class
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
    )

}
