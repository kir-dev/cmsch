package hu.bme.sch.cmsch.component.profile

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["profile"],
    havingValue = "true",
    matchIfMissing = false
)
class ProfileComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "profile",
    "/profile",
    "Profil",
    ControlPermissions.PERMISSION_CONTROL_PROFILE,
    listOf(),
    env
) {

    final override val allSettings by lazy {
        listOf(
            profileGroup,
            title, menuDisplayName, minRole,

            countersGroup,
            showTasks,
            taskCounterName,
            showRiddles,
            riddleCounterName,
            showTokens,
            tokenCounterName,
            tokenCounterCategory,

            profileFieldsGroup,
            showFullName,
            showGuild,
            showMajor,
            showAlias,
            aliasChangeEnabled,
            aliasRegex,
            showGroup,
            showNeptun,
            showEmail,
            showProfilePicture,
            showQr,
            showQrMinRole,
            showQrOnlyIfTicketPresent,
            qrTitle,
            bmejegyQrIfPresent,
            showGroupMessage,
            showUserMessage,
            groupTitle,
            messageBoxContent,
            messageBoxLevel,

            groupLeadersGroup,
            groupLeadersHeader,
            showGroupLeaders,
            showGroupLeadersLocations,
            locationTimeout,

            tokenGoalGroup,
            showMinimumTokenMessage,
            minTokenNotEnoughMessage,
            minTokenDoneMessage,

            fillProfileGroup,
            showIncompleteProfile,
            profileIncomplete,
            profileComplete,

            groupSelectionGroup,
            selectionEnabled,
        )
    }

    val profileGroup = SettingGroup(component, "profileGroup", fieldName = "Profil")

    final val title = StringSettingRef(componentSettingService, component,
        "title", "Profil", fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = StringSettingRef(componentSettingService, component,
        "menuDisplayName", "Profil", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", "", fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val countersGroup = SettingGroup(component, "countersGroup", fieldName = "Számlálók",
        description = "Kör alakú progressbarok amik bizonyos haladást mutatnak, a komponenseknek is be kell kapcsolva legyenek, hogy az itteni beállítások működjenek"
    )

    val showTasks = BooleanSettingRef(componentSettingService, component,
        "showTasks", false, fieldName = "Feladat számláló aktív", description = "Legyen-e látható a feladat számláló?"
    )

    val taskCounterName = StringSettingRef(componentSettingService,
        component, "taskCounterName", "BucketList",
        fieldName = "Feladat számláló neve", description = "Ez a felirata a feladat számlálónak"
    )

    val showRiddles = BooleanSettingRef(componentSettingService, component,
        "showRiddles", false, fieldName = "Riddle számláló aktív", description = "Legyen-e látható a riddle számláló?"
    )

    val riddleCounterName = StringSettingRef(componentSettingService,
        component, "riddleCounterName", "Riddleök",
        fieldName = "Riddle számláló neve", description = "Ez a felirata a riddle számlálónak"
    )

    val showTokens = BooleanSettingRef(componentSettingService, component,
        "showTokens", false,
        fieldName = "Token számláló aktív", description = "Legyen-e látható a token számláló?"
    )

    val tokenCounterName = StringSettingRef(componentSettingService, component,
        "tokenCounterName", "QR kódok",
        fieldName = "Token számláló neve", description = "Ez a felirata a riddle számlálónak"
    )

    val tokenCounterCategory = StringSettingRef(componentSettingService, component,
        "tokenCounterCategory", "default", serverSideOnly = true,
        fieldName = "Token számláló kategóriái", description = "Ebből a kategóriából látszódnak a tokenek, * = minden"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val profileFieldsGroup = SettingGroup(component, "profileFieldsGroup",
        fieldName = "Profil adatok", description = "Milyen adatok jelenjenek meg a felhasználóról a profilban"
    )

    val showFullName = BooleanSettingRef(componentSettingService,
        component, "showFullName", true, fieldName = "Teljes név látható",
        description = "Ha ez hamis, akkor a neve helyett a profil menü címe jelenik meg"
    )

    val showAlias = BooleanSettingRef(componentSettingService, component, "showAlias",
        false, fieldName = "Becenév látható", description = "Ha van nickneve, akkor ki legyen-e írva"
    )

    val aliasChangeEnabled = BooleanSettingRef(componentSettingService, component, "aliasChangeEnabled",
        false, fieldName = "Becenév szerkeszthető", description = "Lehet-e megváltoztatni a becenevet"
    )

    val aliasRegex = StringSettingRef(componentSettingService, component,
        "aliasRegex", "^[A-Za-z0-9 \\-_ÁáÉéÍíÓóÖöŐőÚúÜüŰű\\/]*$", serverSideOnly = true,
        fieldName = "Becenév minta", description = "Ennek a regex mintának kell megfeleljen a beceneveknek"
    )

    val showGroup = BooleanSettingRef(componentSettingService, component,
        "showGroup", false, fieldName = "Csoport tagság látható",
        description = "Ki van írva, hogy melyik csoportnak a tagja"
    )

    val showGuild = BooleanSettingRef(componentSettingService, component,
        "showGuild", false, fieldName = "Gárda látható", description = "Ki van írva, hogy melyik gárda"
    )

    val showMajor = BooleanSettingRef(componentSettingService, component,
        "showMajor", false, fieldName = "Szak látható", description = "Ki van írva, hogy melyik szakra jár"
    )

    val showNeptun = BooleanSettingRef(componentSettingService, component,
        "showNeptun", false, fieldName = "Neptun kód látható", description = "Ki van írva a neptunkódja"
    )

    val showEmail = BooleanSettingRef(componentSettingService, component,
        "showEmail", false, fieldName = "Emailcím látható", description = "Legyen kiírva az emailcíme"
    )

    val showProfilePicture = BooleanSettingRef(componentSettingService, component,
        "showProfilePicture", false, fieldName = "Profil kép látható"
    )

    val showQr = BooleanSettingRef(componentSettingService, component,
        "showQr", false, fieldName = "Egyedi QR kód látható",
        description = "Jelenlen meg a jegy. Ezt lehet használni a fizetéshez, meg belépés szabályozáshoz"
    )

    val showQrMinRole = MinRoleSettingRef(componentSettingService, component,
        "showQrMinRole", defaultValue = MinRoleSettingRef.ALL_ROLES,
        fieldName = "Egyedi QR kód láthatóság jogosultságai", description = "Egyedi QR kód látható ezeknek a roleoknak"
    )

    val showQrOnlyIfTicketPresent = BooleanSettingRef(componentSettingService, component,
        "showQrOnlyIfTicketPresent", false, serverSideOnly = true, fieldName = "QR csak ha van jegye",
        description = "A profil QR csak akkor látszik ha van jegye az illetőnek (Nem BMEJEGY)"
    )

    val qrTitle = StringSettingRef(componentSettingService, component,
        "qrTitle", "CMSCH ID", fieldName = "QR kód fejléc", description = "Ez a fejléc jelenik meg a QR kód felett"
    )

    val bmejegyQrIfPresent = BooleanSettingRef(componentSettingService, component,
        "bmejegyQrIfPresent", false, serverSideOnly = true,
        fieldName = "BMEJEGY kód küldése", description = "A bmejegyes voucher kód leküldése, ha van valid jegye"
    )

    val groupTitle = StringSettingRef(componentSettingService, component,
        "groupTitle", "Tankör", fieldName = "Csoport név",
        description = "Csoport, csapat vagy tankör a csoport. Így fog megjelenni."
    )

    val messageBoxContent = StringSettingRef(componentSettingService, component,
        "messageBoxContent", "", type = SettingType.LONG_TEXT,
        fieldName = "Üzenet doboz", description = "Ha üres, nem látszik"
    )

    val messageBoxLevel = StringSettingRef(componentSettingService, component,
        "messageBoxLevel", "", fieldName = "Üzenet doboz típusa", description = "success, info, warning, error"
    )

    val showGroupMessage = BooleanSettingRef(componentSettingService, component,
        "showGroupMessage", false, serverSideOnly = true,
        fieldName = "Csoporthoz üzenet mutatása", description = "A csoporthoz tartozó üzenet kijelzése a profilban"
    )

    val showUserMessage = BooleanSettingRef(componentSettingService, component,
        "showUserMessage", false, serverSideOnly = true, fieldName = "Felhasználói üzenet mutatása",
        description = "A felhasználóhoz tartozó üzenet kijelzése a profilban"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val groupLeadersGroup = SettingGroup(component, "groupLeadersGroup", fieldName = "Csoport vezetők adatai")

    val showGroupLeaders = BooleanSettingRef(componentSettingService, component,
        "showGroupName", false,
        fieldName = "Csoport mutatása", description = "Csoport vezetők elérhetősége"
    )

    val groupLeadersHeader = StringSettingRef(componentSettingService, component,
        "groupLeadersHeader", "Tankörseniorok",
        fieldName = "Csoport modul fejléce", description = "Ez a felirata a csoport vezetőinek elérhetőségeinek"
    )

    val showGroupLeadersLocations = BooleanSettingRef(componentSettingService, component,
        "showGroupLeadersLocations", false,
        fieldName = "Csoport helyzetének mutatása", description = "Csoport vezetők pozíciójának mutatása"
    )

    val locationTimeout = NumberSettingRef(componentSettingService, component,
        "locationTimeout", 600, serverSideOnly = true, fieldName = "Helyzet lejárata", strictConversion = false,
        description = "Ennyi ideig (másodpercben) mutassa a helyzeteket ha nem érkezik újabb frissítés"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val tokenGoalGroup = SettingGroup(component, "tokenGoalGroup", fieldName = "A token célt kiírja üzenetként",
        description = "Tipikusan tanköri jelenétre használt funkció; a szám meghatározása a token komponens része (A token komponensnek is be kell kapcsolva legyen, hogy ez működjön)"
    )

    val showMinimumTokenMessage = BooleanSettingRef(componentSettingService, component,
        "showMinimumToken", false,
        fieldName = "Minimum token üzenet aktív", description = "A szükséges összegyűjthető tokenekről legyen-e üzenet?"
    )

    val minTokenNotEnoughMessage = StringSettingRef(componentSettingService,
        component,
        "minTokenMsg",
        "Még {} darab kell a tanköri jelenléthez",
        type = SettingType.LONG_TEXT,
        fieldName = "'Nincs elég' üzenet",
        description = "Az üzenet ha még nincs elérve a cél, {} = a szám amennyi kell még"
    )

    val minTokenDoneMessage = StringSettingRef(componentSettingService, component,
        "minTokenAchievedMsg", "Megvan a tanköri jelenlét", type = SettingType.LONG_TEXT,
        fieldName = "'Már van elég' üzenet", description = "Ha üres, nem látszik"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val fillProfileGroup = SettingGroup(component, "fillProfileGroup", fieldName = "Profil kitöltöttsége",
        description = "Ha egy Feladat Kategória PROFILE_REQUIRED-re van állítva, akkor a benne lévő feladatok szükségesek ahhoz, hogy a profil teljes legyen."
    )

    val showIncompleteProfile = BooleanSettingRef(componentSettingService, component,
        "showIncompleteProfile", false, fieldName = "Profil kitöltöttsége üzenet aktív",
        description = "Megjelenjen-e a profil kitöltöttségére vonatkozó üzenet a profilban?"
    )

    val profileIncomplete = StringSettingRef(componentSettingService, component,
        "profileIncomplete", "A profil nem teljes! A következő dolgok hiányoznak: {}",
        type = SettingType.LONG_TEXT,
        fieldName = "'Profil hiányos' üzenet", description = "A profil még nincs teljesen kitöltve üzenet, " +
                "{} = az összes kitöltendő feladat neve felsorolva. (Ha üres, nem látszik.)"
    )

    val profileComplete = StringSettingRef(componentSettingService,
        component,
        "profileComplete",
        "A profil sikeresen ki lett töltve!",
        type = SettingType.LONG_TEXT,
        fieldName = "'Profil kitöltve' üzenet",
        description = "Ha üres, nem látszik, ha ki van töltve a profil akkor sem."
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val groupSelectionGroup = SettingGroup(component, "groupSelectionGroup", fieldName = "Csoport választása",
        description = "Ez a kategória csak akkor lesz használható, hogyha a 'groupselection' komponens be van kapcsolva."
    )

    val selectionEnabled = BooleanSettingRef(componentSettingService, component,
        "selectionEnabled", true, fieldName = "Választás engedélyezve",
        description = "Csak akkor jelenik meg a lehetőség ha ez be van kapcsolva"
    )

}
