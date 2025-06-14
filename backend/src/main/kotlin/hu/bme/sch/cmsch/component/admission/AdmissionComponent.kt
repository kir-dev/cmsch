package hu.bme.sch.cmsch.component.admission

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["admission"],
    havingValue = "true",
    matchIfMissing = false
)
class AdmissionComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "admission",
    "/",
    "Beléptetés",
    ControlPermissions.PERMISSION_CONTROL_ADMISSION,
    listOf(AdmissionEntryEntity::class),
    env
) {

    final override val menuDisplayName = null

    final override var minRole by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )


    /// -------------------------------------------------------------------------------------------------------------------

    val controlGroup by SettingGroup(fieldName = "Beléptetés működése")

    var onlyAcceptApprovedForms by BooleanSettingRef(defaultValue = false,
        fieldName = "Csak az elfogadott formok számítanak",
        description = "Ha be van kapcsolva, akkor csak az elfogadott és nem elutasított formok számítanak. Csak akkor működik, ha a forms komponens be van kapcsolva."
    )

    var saveEntryLog by BooleanSettingRef(defaultValue = true, fieldName = "Beléptetések mentése",
        description = "Ha be van kapcsolva, akkor minden beengedés logolva van"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val groupAccessGroup by SettingGroup(fieldName = "Csoportok hozzáférése",
        description = "Ha nincs tiltólistán, akkor a legmagasabb beállított hozzáférést fogja megkapni"
    )

    var userGroups by StringSettingRef(defaultValue = "", fieldName = "USER hozzáférésű csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva"
    )

    var vipGroups by StringSettingRef(defaultValue = "", fieldName = "VIP hozzáférésű csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva"
    )

    var performerGroups by StringSettingRef(defaultValue = "", fieldName = "PERFORMER hozzáférésű csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva"
    )

    var organizerGroups by StringSettingRef(defaultValue = "", fieldName = "ORGANIZER hozzáférésű csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva"
    )

    var leadOrganizerGroups by StringSettingRef(defaultValue = "", fieldName = "LEAD_ORGANIZER hozzáférésű csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val userAccessGroup by SettingGroup(fieldName = "Felhasználók hozzáférése",
        description = "Ha nincs tiltólistán, akkor a legmagasabb beállított hozzáférést fogja megkapni"
    )

    var vipUsers by StringSettingRef(defaultValue = "", fieldName = "USER hozzáférésű felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva"
    )

    var performerUsers by StringSettingRef(defaultValue = "", fieldName = "USER hozzáférésű felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva"
    )

    var organizerUsers by StringSettingRef(defaultValue = "", fieldName = "USER hozzáférésű felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva"
    )

    var leadOrganizerUsers by StringSettingRef(defaultValue = "", fieldName = "USER hozzáférésű felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva"
    )

    var userUsers by StringSettingRef(defaultValue = "", fieldName = "USER hozzáférésű felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val roleGroup by SettingGroup(fieldName = "Szerepek hozzáférése",
        description = "Ha nincs tiltólistán, akkor a legmagasabb beállított hozzáférést fogja megkapni"
    )

    var grantUserByDefault by BooleanSettingRef(defaultValue = false, fieldName = "USER hozzáférés alapértelmezetten",
        description = "Ha be van kapcsolva, akkor minden (REGULAR+) felhasználó jogosult a belépésre alapból"
    )

    var grantUserByAttendee by BooleanSettingRef(defaultValue = false, fieldName = "USER hozzáférés résztvevőknek",
        description = "Ha be van kapcsolva, akkor minden résztvevő (ATTENDEE+) felhasználó jogosult a belépésre"
    )

    var grantUserByPrivileged by BooleanSettingRef(defaultValue = false,
        fieldName = "USER hozzáférés privileged résztvevőknek",
        description = "Ha be van kapcsolva, akkor minden résztvevő (PRIVILEGED+) felhasználó jogosult a belépésre"
    )

    var grantUserByStaff by BooleanSettingRef(defaultValue = false, fieldName = "USER hozzáférés szervezőknek",
        description = "Ha be van kapcsolva, akkor minden szervező (STAFF+) felhasználó jogosult a belépésre"
    )

    var grantOrganizerByStaff by BooleanSettingRef(defaultValue = false,
        fieldName = "ORGANIZER hozzáférés szervezőknek",
        description = "Ha be van kapcsolva, akkor minden szervező (STAFF+) felhasználó jogosult a szervező rangra"
    )

    var grantOrganizerByAdmin by BooleanSettingRef(defaultValue = false, fieldName = "ORGANIZER hozzáférés adminoknak",
        description = "Ha be van kapcsolva, akkor minden szervező (ADMIN+) felhasználó jogosult a szervező rangra"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val banListGroup by SettingGroup(
        fieldName = "Tiltó lista",
        description = "Ha nincs tiltólistán, akkor a legmagasabb beállított hozzáférést fogja megkapni"
    )

    var bannedGroups by StringSettingRef(defaultValue = "", fieldName = "Kitiltott csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva"
    )

    var bannedUsers by StringSettingRef(defaultValue = "", fieldName = "Kitiltott felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val ticketGroup by SettingGroup(fieldName = "Jegyek", description = "A jegyellenőrzés menü beállításai")

    var ticketShowEntryCount by BooleanSettingRef(defaultValue = true, fieldName = "Belépések számának mutatása",
        description = "Beolvasáskor mutatja a beolvasónak", serverSideOnly = true
    )

    var ticketAllowBmejegy by BooleanSettingRef(defaultValue = true, fieldName = "BME Jegyesek beengedése",
        description = "Csak akkor működik, ha a bmejegy komponens be van kapcsolva", serverSideOnly = true
    )

}
