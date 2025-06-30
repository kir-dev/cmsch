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
    componentSettingService,
    "profile",
    "/profile",
    "Profil",
    ControlPermissions.PERMISSION_CONTROL_PROFILE,
    listOf(),
    env
) {

    val profileGroup by SettingGroup(fieldName = "Profil")

    final var title by StringSettingRef("Profil", fieldName = "Lap címe",
        description = "Ez jelenik meg a böngésző címsorában")

    final override var menuDisplayName by StringSettingRef("Profil", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek")

    final override var minRole by MinRoleSettingRef(setOf(), fieldName = "Jogosultságok",
        description = "Melyik roleokkal nyitható meg az oldal")

    /// -------------------------------------------------------------------------------------------------------------------

    val countersGroup by SettingGroup(fieldName = "Számlálók",
        description = "Kör alakú progressbarok amik bizonyos haladást mutatnak, a komponenseknek is be kell kapcsolva legyenek, hogy az itteni beállítások működjenek")

    var showTasks by BooleanSettingRef(
        fieldName = "Feladat számláló aktív", description = "Legyen-e látható a feladat számláló?")

    var taskCounterName by StringSettingRef("BucketList",
        fieldName = "Feladat számláló neve", description = "Ez a felirata a feladat számlálónak")

    var showRiddles by BooleanSettingRef(fieldName = "Riddle számláló aktív",
        description = "Legyen-e látható a riddle számláló?")

    var riddleCounterName by StringSettingRef("Riddleök",
        fieldName = "Riddle számláló neve", description = "Ez a felirata a riddle számlálónak")

    var showTokens by BooleanSettingRef(fieldName = "Token számláló aktív",
        description = "Legyen-e látható a token számláló?")

    var tokenCounterName by StringSettingRef("QR kódok",
        fieldName = "Token számláló neve", description = "Ez a felirata a riddle számlálónak")

    var tokenCounterCategory by StringSettingRef("default", serverSideOnly = true,
        fieldName = "Token számláló kategóriái", description = "Ebből a kategóriából látszódnak a tokenek, * = minden")

    /// -------------------------------------------------------------------------------------------------------------------

    val profileFieldsGroup by SettingGroup(fieldName = "Profil adatok",
        description = "Milyen adatok jelenjenek meg a felhasználóról a profilban")

    var showFullName by BooleanSettingRef(true, fieldName = "Teljes név látható",
        description = "Ha ez hamis, akkor a neve helyett a profil menü címe jelenik meg")

    var showGuild by BooleanSettingRef(fieldName = "Gárda látható", description = "Ki van írva, hogy melyik gárda")

    var showMajor by BooleanSettingRef(fieldName = "Szak látható", description = "Ki van írva, hogy melyik szakra jár")

    var showAlias by BooleanSettingRef(fieldName = "Becenév látható",
        description = "Ha van nickneve, akkor ki legyen-e írva")

    var aliasChangeEnabled by BooleanSettingRef(fieldName = "Becenév szerkeszthető",
        description = "Lehet-e megváltoztatni a becenevet")

    var aliasRegex by StringSettingRef("^[A-Za-z0-9 \\-_ÁáÉéÍíÓóÖöŐőÚúÜüŰű\\/]*$", serverSideOnly = true,
        fieldName = "Becenév minta", description = "Ennek a regex mintának kell megfeleljen a beceneveknek")

    var showGroup by BooleanSettingRef(fieldName = "Csoport tagság látható",
        description = "Ki van írva, hogy melyik csoportnak a tagja")

    var showNeptun by BooleanSettingRef(fieldName = "Neptun kód látható", description = "Ki van írva a neptunkódja")

    var showEmail by BooleanSettingRef(fieldName = "Emailcím látható", description = "Legyen kiírva az emailcíme")

    var showProfilePicture by BooleanSettingRef(fieldName = "Profil kép látható")

    var showQr by BooleanSettingRef(fieldName = "Egyedi QR kód látható",
        description = "Jelenlen meg a jegy. Ezt lehet használni a fizetéshez, meg belépés szabályozáshoz")

    var showQrMinRole by MinRoleSettingRef(defaultValue = MinRoleSettingRef.ALL_ROLES,
        fieldName = "Egyedi QR kód láthatóság jogosultságai", description = "Egyedi QR kód látható ezeknek a roleoknak")

    var showQrOnlyIfTicketPresent by BooleanSettingRef(serverSideOnly = true, fieldName = "QR csak ha van jegye",
        description = "A profil QR csak akkor látszik ha van jegye az illetőnek (Nem BMEJEGY)")

    var qrTitle by StringSettingRef("CMSCH ID",
        fieldName = "QR kód fejléc", description = "Ez a fejléc jelenik meg a QR kód felett")

    var bmejegyQrIfPresent by BooleanSettingRef(serverSideOnly = true,
        fieldName = "BMEJEGY kód küldése", description = "A bmejegyes voucher kód leküldése, ha van valid jegye")

    var showGroupMessage by BooleanSettingRef(serverSideOnly = true,
        fieldName = "Csoporthoz üzenet mutatása", description = "A csoporthoz tartozó üzenet kijelzése a profilban")

    var showUserMessage by BooleanSettingRef(serverSideOnly = true, fieldName = "Felhasználói üzenet mutatása",
        description = "A felhasználóhoz tartozó üzenet kijelzése a profilban")

    var groupTitle by StringSettingRef("Tankör", fieldName = "Csoport név",
        description = "Csoport, csapat vagy tankör a csoport. Így fog megjelenni.")

    var messageBoxContent by StringSettingRef(type = SettingType.LONG_TEXT,
        fieldName = "Üzenet doboz", description = "Ha üres, nem látszik")

    var messageBoxLevel by StringSettingRef(fieldName = "Üzenet doboz típusa",
        description = "success, info, warning, error")

    /// -------------------------------------------------------------------------------------------------------------------

    val groupLeadersGroup by SettingGroup(fieldName = "Csoport vezetők adatai")

    var showGroupLeaders by BooleanSettingRef(fieldName = "Csoport mutatása",
        description = "Csoport vezetők elérhetősége")

    var groupLeadersHeader by StringSettingRef("Tankörseniorok",
        fieldName = "Csoport modul fejléce", description = "Ez a felirata a csoport vezetőinek elérhetőségeinek")

    var showGroupLeadersLocations by BooleanSettingRef(fieldName = "Csoport helyzetének mutatása",
        description = "Csoport vezetők pozíciójának mutatása")

    var locationTimeout by NumberSettingRef(600, serverSideOnly = true,
        fieldName = "Helyzet lejárata", strictConversion = false,
        description = "Ennyi ideig (másodpercben) mutassa a helyzeteket ha nem érkezik újabb frissítés")

    /// -------------------------------------------------------------------------------------------------------------------

    val tokenGoalGroup by SettingGroup(fieldName = "A token célt kiírja üzenetként",
        description = "Tipikusan tanköri jelenétre használt funkció; a szám meghatározása a token komponens része (A token komponensnek is be kell kapcsolva legyen, hogy ez működjön)")

    var showMinimumTokenMessage by BooleanSettingRef(fieldName = "Minimum token üzenet aktív",
        description = "A szükséges összegyűjthető tokenekről legyen-e üzenet?")

    var minTokenNotEnoughMessage by StringSettingRef("Még {} darab kell a tanköri jelenléthez",
        type = SettingType.LONG_TEXT, fieldName = "'Nincs elég' üzenet",
        description = "Az üzenet ha még nincs elérve a cél, {} = a szám amennyi kell még")

    var minTokenDoneMessage by StringSettingRef("Megvan a tanköri jelenlét", type = SettingType.LONG_TEXT,
        fieldName = "'Már van elég' üzenet", description = "Ha üres, nem látszik")

    /// -------------------------------------------------------------------------------------------------------------------

    val fillProfileGroup by SettingGroup(fieldName = "Profil kitöltöttsége",
        description = "Ha egy Feladat Kategória PROFILE_REQUIRED-re van állítva, akkor a benne lévő feladatok szükségesek ahhoz, hogy a profil teljes legyen.")

    var showIncompleteProfile by BooleanSettingRef(fieldName = "Profil kitöltöttsége üzenet aktív",
        description = "Megjelenjen-e a profil kitöltöttségére vonatkozó üzenet a profilban?")

    var profileIncomplete by StringSettingRef("A profil nem teljes! A következő dolgok hiányoznak: {}",
        type = SettingType.LONG_TEXT, fieldName = "'Profil hiányos' üzenet",
        description = "A profil még nincs teljesen kitöltve üzenet, {} = az összes kitöltendő feladat neve felsorolva. (Ha üres, nem látszik.)")

    var profileComplete by StringSettingRef("A profil sikeresen ki lett töltve!",
        type = SettingType.LONG_TEXT, fieldName = "'Profil kitöltve' üzenet",
        description = "Ha üres, nem látszik, ha ki van töltve a profil akkor sem.")

    /// -------------------------------------------------------------------------------------------------------------------

    val groupSelectionGroup by SettingGroup(fieldName = "Csoport választása",
        description = "Ez a kategória csak akkor lesz használható, hogyha a 'groupselection' komponens be van kapcsolva.")

    var selectionEnabled by BooleanSettingRef(true, fieldName = "Választás engedélyezve",
        description = "Csak akkor jelenik meg a lehetőség ha ez be van kapcsolva")

}
