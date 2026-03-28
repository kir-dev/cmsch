package hu.bme.sch.cmsch.component.profile

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnBooleanProperty(value = ["hu.bme.sch.cmsch.component.load.profile"])
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
        fieldName = "Token számláló neve", description = "Ez a felirata a token számlálónak")

    var tokenCounterCategory by StringSettingRef("default", serverSideOnly = true,
        fieldName = "Token számláló kategóriái", description = "Ebből a kategóriából számolja a tokeneket. A '*' minden kategóriát jelent.")

    var showRaceStats by BooleanSettingRef(fieldName = "Mérés eredmény látható",
        description = "Legyen-e látható a mérés eredmény?")

    /// -------------------------------------------------------------------------------------------------------------------

    val profileFieldsGroup by SettingGroup(fieldName = "Profil adatok",
        description = "Milyen adatok jelenjenek meg a felhasználóról a profilban")

    var showFullName by BooleanSettingRef(true, fieldName = "Teljes név látható",
        description = "Ha ez hamis, akkor a név helyett az oldal címe jelenik meg")

    var showGuild by BooleanSettingRef(fieldName = "Gárda látható", description = "Megjelenjen-e a felhasználó gárdája")

    var showMajor by BooleanSettingRef(fieldName = "Szak látható", description = "Megjelenjen-e a felhasználó szakja")

    var showAlias by BooleanSettingRef(fieldName = "Becenév látható",
        description = "Megjelenjen-e a felhasználó beceneve")

    var aliasChangeEnabled by BooleanSettingRef(fieldName = "Becenév szerkeszthető",
        description = "Módosíthatja-e a felhasználó a becenevét")

    var aliasRegex by StringSettingRef("^[A-Za-z0-9 \\-_ÁáÉéÍíÓóÖöŐőÚúÜüŰű\\/]*$", serverSideOnly = true,
        fieldName = "Becenév minta", description = "A becenévnek erre a reguláris kifejezésre kell illeszkednie")

    var showGroup by BooleanSettingRef(fieldName = "Csoport tagság látható",
        description = "Megjelenjen-e a felhasználó csoporttagsága")

    var showNeptun by BooleanSettingRef(fieldName = "Neptun kód látható", description = "Megjelenjen-e a felhasználó Neptun kódja")

    var showEmail by BooleanSettingRef(fieldName = "Email-cím látható", description = "Megjelenjen-e a felhasználó email-címe")

    var showProfilePicture by BooleanSettingRef(fieldName = "Profilkép látható")

    var showQr by BooleanSettingRef(fieldName = "Egyedi QR kód látható",
        description = "Jelenjen meg az egyedi azonosító QR kód. Használható fizetéshez és beléptetéshez.")

    var showQrMinRole by MinRoleSettingRef(defaultValue = MinRoleSettingRef.ALL_ROLES,
        fieldName = "Egyedi QR kód láthatóság jogosultságai", description = "Mely szerepkörök számára legyen látható az egyedi QR kód")

    var showQrOnlyIfTicketPresent by BooleanSettingRef(serverSideOnly = true, fieldName = "QR csak ha van jegye",
        description = "A profil QR kód csak akkor látszik, ha a felhasználónak van érvényes jegye (nem BMEJEGY)")

    var qrTitle by StringSettingRef("CMSCH ID",
        fieldName = "QR kód fejléc", description = "A QR kód felett megjelenő felirat")

    var bmejegyQrIfPresent by BooleanSettingRef(serverSideOnly = true,
        fieldName = "BMEJEGY kód küldése", description = "A BMEJEGY voucher kódjának küldése, ha van érvényes jegy")

    var noQrIfNoBmejegy by BooleanSettingRef(serverSideOnly = true,
        fieldName = "Csak BMEJEGY kód küldése", description = "Ha nincs BMEJEGY voucher kód, akkor nem jelenik meg QR kód")

    var showGroupMessage by BooleanSettingRef(serverSideOnly = true,
        fieldName = "Csoporthoz üzenet mutatása", description = "A csoporthoz tartozó üzenet megjelenítése a profilban")

    var showUserMessage by BooleanSettingRef(serverSideOnly = true, fieldName = "Felhasználói üzenet mutatása",
        description = "A felhasználóhoz tartozó üzenet megjelenítése a profilban")

    var groupTitle by StringSettingRef("Tankör", fieldName = "Csoport neve",
        description = "A csoport megnevezése (pl. tankör, csapat, szakma).")

    var messageBoxContent by StringSettingRef(type = SettingType.LONG_TEXT,
        fieldName = "Üzenet doboz", description = "Ha üres, nem jelenik meg")

    var messageBoxLevel by StringSettingRef(fieldName = "Üzenet doboz típusa",
        description = "success, info, warning, error")

    /// -------------------------------------------------------------------------------------------------------------------

    val groupLeadersGroup by SettingGroup(fieldName = "Csoportvezetők adatai")

    var showGroupLeaders by BooleanSettingRef(fieldName = "Vezetők mutatása",
        description = "A csoportvezetők elérhetőségének megjelenítése")

    var groupLeadersHeader by StringSettingRef("Tankörseniorok",
        fieldName = "Vezetők modul fejléce", description = "A csoportvezetők elérhetőségeit tartalmazó modul címe")

    var showGroupLeadersLocations by BooleanSettingRef(fieldName = "Vezetők helyzetének mutatása",
        description = "A csoportvezetők aktuális pozíciójának megjelenítése")

    var locationTimeout by NumberSettingRef(600, serverSideOnly = true,
        fieldName = "Helyzet lejárata", strictConversion = false,
        description = "Ennyi ideig (másodpercben) jelenjen meg a helyzet az utolsó frissítés után")

    /// -------------------------------------------------------------------------------------------------------------------

    val tokenGoalGroup by SettingGroup(fieldName = "Token cél megjelenítése",
        description = "A token komponensben meghatározott cél elérésére vonatkozó üzenet (a token komponensnek aktívnak kell lennie)")

    var showMinimumTokenMessage by BooleanSettingRef(fieldName = "Minimum token üzenet aktív",
        description = "Megjelenjen-e üzenet a szükséges tokenek gyűjtéséről?")

    var minTokenNotEnoughMessage by StringSettingRef("Még {} darab kell a tanköri jelenléthez",
        type = SettingType.LONG_TEXT, fieldName = "'Nincs elég' üzenet",
        description = "Üzenet, ha a cél még nincs elérve. A {} helyére a hátralévő darabszám kerül.")

    var minTokenDoneMessage by StringSettingRef("Megvan a tanköri jelenlét", type = SettingType.LONG_TEXT,
        fieldName = "'Már van elég' üzenet", description = "Ha üres, nem jelenik meg")

    /// -------------------------------------------------------------------------------------------------------------------

    val fillProfileGroup by SettingGroup(fieldName = "Profil kitöltöttsége",
        description = "Ha egy feladatkategória PROFILE_REQUIRED-re van állítva, akkor az abban lévő feladatok elvégzése szükséges a teljes profilhoz.")

    var showIncompleteProfile by BooleanSettingRef(fieldName = "Profil kitöltöttsége üzenet aktív",
        description = "Megjelenjen-e a profil kitöltöttségére vonatkozó üzenet?")

    var profileIncomplete by StringSettingRef("A profil nem teljes! A következő dolgok hiányoznak: {}",
        type = SettingType.LONG_TEXT, fieldName = "'Profil hiányos' üzenet",
        description = "Üzenet hiányos profil esetén. A {} helyére a hiányzó feladatok kerülnek. Ha üres, nem jelenik meg.")

    var profileComplete by StringSettingRef("A profil sikeresen ki lett töltve!",
        type = SettingType.LONG_TEXT, fieldName = "'Profil kitöltve' üzenet",
        description = "Üzenet teljes profil esetén. Ha üres, nem jelenik meg.")

    /// -------------------------------------------------------------------------------------------------------------------

    val groupSelectionGroup by SettingGroup(fieldName = "Csoport választása",
        description = "Ez a kategória csak akkor lesz használható, hogyha a 'groupselection' komponens be van kapcsolva.")

    var selectionEnabled by BooleanSettingRef(true, fieldName = "Választás engedélyezve",
        description = "Csak akkor jelenik meg a lehetőség ha ez be van kapcsolva")

}
