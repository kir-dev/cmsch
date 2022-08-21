package hu.bme.sch.cmsch.component.profile

import hu.bme.sch.cmsch.component.*
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
) : ComponentBase("profile", "/profile", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
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
            showGroup,
            showNeptun,
            showEmail,
            showProfilePicture,
            showQr,

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
        )
    }

    final val title = SettingProxy(componentSettingService, component,
        "title", "Profil",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Profil", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val countersGroup = SettingProxy(componentSettingService, component,
        "countersGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Számlálók", description = "Kör alakú progressbarok amik bizonyos haladást mutatnak," +
                "a komponenseknek is be kell kapcsolva legyenek, hogy az itteni beállítások működjenek"
    )

    val showTasks = SettingProxy(componentSettingService, component,
        "showTasks", "false", type = SettingType.BOOLEAN,
        fieldName = "Feladat számláló aktív", description = "Legyen-e látató a feladat számláló?"
    )

    val taskCounterName = SettingProxy(componentSettingService, component,
        "taskCounterName", "BucketList",
        fieldName = "Feladat számláló neve", description = "Ez a felirata a feladat számlálónak"
    )

    val showRiddles = SettingProxy(componentSettingService, component,
        "showRiddles", "false", type = SettingType.BOOLEAN,
        fieldName = "Riddle számláló aktív", description = "Legyen-e látató a riddle szmláló?"
    )

    val riddleCounterName = SettingProxy(componentSettingService, component,
        "riddleCounterName", "Riddleök",
        fieldName = "Riddle számláló neve", description = "Ez a felirata a riddle számlálónak"
    )

    val showTokens = SettingProxy(componentSettingService, component,
        "showTokens", "false", type = SettingType.BOOLEAN,
        fieldName = "Token számláló aktív", description = "Legyen-e látató a token szmláló?"
    )

    val tokenCounterName = SettingProxy(componentSettingService, component,
        "tokenCounterName", "QR kódok",
        fieldName = "Token számláló neve", description = "Ez a felirata a riddle számlálónak"
    )

    val tokenCounterCategory = SettingProxy(componentSettingService, component,
        "tokenCounterCategory", "default", serverSideOnly = true,
        fieldName = "Token számláló kategóriái", description = "Ebből a kategóriából látszódnak a tokenek, * = minden"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val profileFieldsGroup = SettingProxy(componentSettingService, component,
        "profileFieldsGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Profil adatok", description = "Milyen adatok jelenlenek meg a felhasználóról a profilban"
    )

    val showFullName = SettingProxy(componentSettingService, component,
        "showFullName", "true", type = SettingType.BOOLEAN,
        fieldName = "Teljes név látható", description = "Ha ez hamis, akkor a neve helyett a profil menü címe jelenik meg"
    )

    val showAlias = SettingProxy(componentSettingService, component,
        "showAlias", "false", type = SettingType.BOOLEAN,
        fieldName = "Becenév látható", description = "Ha van nickneve, akkor ki legyen-e írva"
    )

    val showGroup = SettingProxy(componentSettingService, component,
        "showGroup", "false", type = SettingType.BOOLEAN,
        fieldName = "Csoport tagság látható", description = "Ki van írva, hogy melyik csoportnak a tagja"
    )

    val showGuild = SettingProxy(componentSettingService, component,
        "showGuild", "false", type = SettingType.BOOLEAN,
        fieldName = "Gárda látható", description = "Ki van írva, hogy melyik gárda"
    )

    val showMajor = SettingProxy(componentSettingService, component,
        "showMajor", "false", type = SettingType.BOOLEAN,
        fieldName = "Szak látható", description = "Ki van írva, hogy melyik szakra jár"
    )

    val showNeptun = SettingProxy(componentSettingService, component,
        "showNeptun", "false", type = SettingType.BOOLEAN,
        fieldName = "Neptun kód látható", description = "Ki van írva a neptunkódja"
    )

    val showEmail = SettingProxy(componentSettingService, component,
        "showEmail", "false", type = SettingType.BOOLEAN,
        fieldName = "Emailcím látható", description = "Legyen kiírva az emailcíme"
    )

    val showProfilePicture = SettingProxy(componentSettingService, component,
        "showProfilePicture", "false", type = SettingType.BOOLEAN,
        fieldName = "Profil kép látható", description = ""
    )

    val showQr = SettingProxy(componentSettingService, component,
        "showQr", "false", type = SettingType.BOOLEAN,
        fieldName = "Egyedi QR kód látható", description = "Ezt lehet használni a fizetéshez, meg belépés szabályozáshoz"
    )

    val groupTitle = SettingProxy(componentSettingService, component,
        "groupTitle", "Tankör",
        fieldName = "Csoport név", description = "Csoport, csapat vagy tankör a csoport. Így fog megjelenni."
    )

    val messageBoxContent = SettingProxy(componentSettingService, component,
        "messageBoxContent", "", type = SettingType.LONG_TEXT,
        fieldName = "Üzenet doboz", description = "Ha üres, nem látszik"
    )

    val messageBoxLevel = SettingProxy(componentSettingService, component,
        "messageBoxLevel", "", type = SettingType.TEXT,
        fieldName = "Üzenet doboz típusa", description = "success, info, warning, error"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val groupLeadersGroup = SettingProxy(componentSettingService, component,
        "groupLeadersGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Csport vezetők adatai", description = ""
    )

    val showGroupLeaders = SettingProxy(componentSettingService, component,
        "showGroupName", "false", type = SettingType.BOOLEAN,
        fieldName = "Csoport mutatása", description = "Csoport vezetők elérhetősége"
    )

    val groupLeadersHeader = SettingProxy(componentSettingService, component,
        "groupLeadersHeader", "Tankörseniorok",
        fieldName = "Csoport modul fejléce", description = "Ez a felirata a csoport vezetőinek elérhetőségeinek"
    )

    val showGroupLeadersLocations = SettingProxy(componentSettingService, component,
        "showGroupLeadersLocations", "false", type = SettingType.BOOLEAN,
        fieldName = "Csoport helyzetének mutatása", description = "Csoport vezetők pozíciójának mutatása"
    )

    val locationTimeout = SettingProxy(componentSettingService, component,
        "locationTimeout", "600", type = SettingType.NUMBER, serverSideOnly = true,
        fieldName = "Helyzet lejárata", description = "Ennyi ideig (másodpercben) mutassa a " +
                "helyzeteket ha nem érkezik újabb frissítés"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val tokenGoalGroup = SettingProxy(componentSettingService, component,
        "tokenGoalGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Token célok kiírja üzenetként", description = "Tipikusan tanköri jelenétre használt funkció; " +
                "a szám meghatározása a token komponens része (A token komponensnek is be kel " +
                "kapcsolva legyen, hogy ez működjön)"
    )

    val showMinimumTokenMessage = SettingProxy(componentSettingService, component,
        "showMinimumToken", "false", type = SettingType.BOOLEAN,
        fieldName = "Minimum token üzenet akítv", description = "A szükséges összegűjthető tokenekról legyen-e üzenet?"
    )

    val minTokenNotEnoughMessage = SettingProxy(componentSettingService, component,
        "minTokenMsg", "Még {} darab kell a tanköri jelenléthez", type = SettingType.LONG_TEXT,
        fieldName = "'Nincs elég' üzenet", description = "Az üzenet ha még nincs elérve a cél, {} = a szám amennyi kell még"
    )

    val minTokenDoneMessage = SettingProxy(componentSettingService, component,
        "minTokenAchievedMsg", "Megvan a tanköri jelenlét", type = SettingType.LONG_TEXT,
        fieldName = "'Már van elég' üzenet", description = "Ha üres, nem látszik"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val fillProfileGroup = SettingProxy(componentSettingService, component,
        "fillProfileGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Profil kitöltöttsége", description = "Ha egy Feladat Kategória PROFILE_REQUIRED-re van állítva, " +
                "akkor a bennelévő feladatok szükségesek ahhoz, hogy a profil teljes legyen."
    )

    val showIncompleteProfile = SettingProxy(componentSettingService, component,
        "showIncompleteProfile", "false", type = SettingType.BOOLEAN,
        fieldName = "Profil kitöltöttsége üzenet akítv", description = "Megjelenlen-e a profil kitöltöttségére vonatkozó üzenet a profilban?"
    )

    val profileIncomplete = SettingProxy(componentSettingService, component,
        "profileIncomplete", "A profil nem teljes! A következő dolgok hiányoznak: {}",
        type = SettingType.LONG_TEXT,
        fieldName = "'Profil hiányos' üzenet", description = "A profil még nincs teljesen kitöltve üzenet, " +
                "{} = az összes kitöltendő feladat neve felsorolva. (Ha üres, nem látszik.)"
    )

    val profileComplete = SettingProxy(componentSettingService, component,
        "profileComplete", "A profil sikeresen ki lett töltve!", type = SettingType.LONG_TEXT,
        fieldName = "'Profil kitöltve' üzenet", description = "Ha üres, nem látszik, ha ki van töltve a profil akkor sem."
    )

}
