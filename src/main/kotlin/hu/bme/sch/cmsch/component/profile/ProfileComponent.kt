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
            showAchievements,
            achievementCounterName,
            showRiddles,
            riddleCounterName,
            showTokens,
            tokenCounterName,
            tokenCounterCategory,

            profileFieldsGroup,
            showFullName,
            showGuild,
            showNeptun,
            showProfilePicture,
            showQr,

            groupGroup,
            showGroupName,
            groupTitle,
            allowGroupSelectWhenNotAssigned,
            messageBoxContent,
            messageBoxLevel,

            groupLeadersGroup,
            showGroupLeaders,
            showGroupLeadersLocations,

            tokenGoalGroup,
            showMinimumTokenMessage,
            minTokenNotEnoughMessage,
            minTokenDoneMessage,
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

    val showAchievements = SettingProxy(componentSettingService, component,
        "showAchievements", "false", type = SettingType.BOOLEAN,
        fieldName = "Achievement számláló aktív", description = "Legyen-e látató az achievement szmláló?"
    )

    val achievementCounterName = SettingProxy(componentSettingService, component,
        "achievementCounterName", "BucketList",
        fieldName = "Achievement számláló neve", description = "Ez a felirata az achievement számlálónak"
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
        "tokenCounterCategory", "default", serverSideOnly = true, // TODO: * = minden
        fieldName = "Token számláló kategóriái", description = "Ebből a kategóriából látszódnak a tokenek, * = minden"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val profileFieldsGroup = SettingProxy(componentSettingService, component,
        "profileFieldsGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Profil adatok", description = "Milyen adatok jelenlenek meg a felhasználóról a profilban"
    )

    val showFullName = SettingProxy(componentSettingService, component,
        "showFullName", "false", type = SettingType.BOOLEAN,
        fieldName = "Teljes név látható", description = "Ha ez hamis, akkor a neve helyett a profil menü címe jelenik meg"
    )

    val showGuild = SettingProxy(componentSettingService, component,
        "showGuild", "false", type = SettingType.BOOLEAN,
        fieldName = "Gárda látható", description = "Ki van írva, hogy melyik gárda"
    )

    val showNeptun = SettingProxy(componentSettingService, component,
        "showNeptun", "false", type = SettingType.BOOLEAN,
        fieldName = "Neptun kód látható", description = "Ki van írva a neptunkódja"
    )

    val showProfilePicture = SettingProxy(componentSettingService, component,
        "showProfilePicture", "false", type = SettingType.BOOLEAN,
        fieldName = "Profil kép látható", description = ""
    )

    val showQr = SettingProxy(componentSettingService, component,
        "showQr", "false", type = SettingType.BOOLEAN,
        fieldName = "Egyedi QR kód látható", description = "Ezt lehet használni a fizetéshez, meg belépés szabályozáshoz"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val groupGroup = SettingProxy(componentSettingService, component,
        "groupGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Felhasználó csoportja", description = "A csoport/tankör kiírása és választás lehetősége"
    )

    val showGroupName = SettingProxy(componentSettingService, component,
        "showGroupName", "false", type = SettingType.BOOLEAN,
        fieldName = "Csoport név látható", description = "Csoport, csapat vagy tankör neve ki van írva"
    )

    val groupTitle = SettingProxy(componentSettingService, component,
        "groupTitle", "Tankör",
        fieldName = "Csoport név", description = "Csoport, csapat vagy tankör a csoport"
    )

    val allowGroupSelectWhenNotAssigned = SettingProxy(componentSettingService, component,
        "allowGroupSelect", "false", type = SettingType.BOOLEAN,
        fieldName = "Csoport választás engedéyzett", description = "Ha nincsen csoportja engedje választani"
    )

    val messageBoxContent = SettingProxy(componentSettingService, component,
        "messageBoxContent", "", type = SettingType.LONG_TEXT,
        fieldName = "Üzenet doboz", description = "ha üres, nem látszik"
    )

    val messageBoxLevel = SettingProxy(componentSettingService, component,
        "messageBoxLevel", "", type = SettingType.TEXT,
        fieldName = "Üzenet doboz típusa", description = "success, info, warning, error"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val groupLeadersGroup = SettingProxy(componentSettingService, component,
        "groupLeadersGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Tankörvezek adatai", description = ""
    )

    val showGroupLeaders = SettingProxy(componentSettingService, component,
        "showGroupName", "false", type = SettingType.BOOLEAN,
        fieldName = "Csoport mutatása", description = "Tankörseniorok elérhetősége"
    )

    val showGroupLeadersLocations = SettingProxy(componentSettingService, component,
        "showGroupLeadersLocations", "false", type = SettingType.BOOLEAN,
        fieldName = "Csoport helyzetének mutatása", description = "Tankörseniorok pozíciójának mutatása"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val tokenGoalGroup = SettingProxy(componentSettingService, component,
        "tokenGoalGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Token célok kiírja üzenetként", description = "Tipikusan tanköri jelenétre használt funkció; " +
                "a szám meghatározása a token komponens része (A token komponensnek is be kel kapcsolva legyen, hogy ez működjön)"
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
        fieldName = "'Már van elég' üzenet", description = "ha üres, nem látszik"
    )

}
