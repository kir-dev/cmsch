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
    "admission",
    "/",
    "Beléptetés",
    ControlPermissions.PERMISSION_CONTROL_ADMISSION,
    listOf(AdmissionEntryEntity::class),
    env
) {

    final override val allSettings by lazy {
        listOf(
            minRole,

            controlGroup,
            onlyAcceptApprovedForms,
            saveEntryLog,

            groupAccessGroup,
            userGroups,
            vipGroups,
            organizerGroups,
            performerGroups,
            leadOrganizerGroups,

            userAccessGroup,
            userUsers,
            vipUsers,
            organizerUsers,
            performerUsers,
            leadOrganizerUsers,

            roleGroup,
            grantUserByDefault,
            grantUserByAttendee,
            grantUserByPrivileged,
            grantUserByStaff,
            grantOrganizerByStaff,
            grantOrganizerByAdmin,

            banListGroup,
            bannedGroups,
            bannedUsers,

            ticketGroup,
            ticketShowEntryCount,
            ticketAllowBmejegy,
        )
    }


    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )


    /// -------------------------------------------------------------------------------------------------------------------

    val controlGroup = ControlGroup(component, "controlGroup", fieldName = "Beléptetés működése", description = "")

    val onlyAcceptApprovedForms = BooleanSettingRef(componentSettingService, component,
        "onlyAcceptApprovedForms", defaultValue = false, fieldName = "Csak az elfogadott formok számítanak",
        description = "Ha be van kapcsolva, akkor csak az elfogadott és nem elutasított formok számítanak. Csak akkor működik, ha a forms komponens be van kapcsolva."
    )

    val saveEntryLog = BooleanSettingRef(componentSettingService, component,
        "saveEntryLog", true, fieldName = "Beléptetések mentése",
        description = "Ha be van kapcsolva, akkor minden beengedés logolva van"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val groupAccessGroup = ControlGroup(component, "groupAccessGroup", fieldName = "Csoportok hozzáférése",
        description = "Ha nincs tiltólistán, akkor a legmagasabb beállított hozzáférést fogja megkapni"
    )

    val userGroups = StringSettingRef(componentSettingService, component,
        "userGroups", "", type = SettingType.TEXT, fieldName = "USER hozzáférésű csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva"
    )

    val vipGroups = StringSettingRef(componentSettingService, component,
        "vipGroups", "", type = SettingType.TEXT, fieldName = "VIP hozzáférésű csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva"
    )

    val performerGroups = StringSettingRef(componentSettingService, component,
        "performerGroups", "", type = SettingType.TEXT, fieldName = "PERFORMER hozzáférésű csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva"
    )

    val organizerGroups = StringSettingRef(componentSettingService, component,
        "organizerGroups", "", type = SettingType.TEXT, fieldName = "ORGANIZER hozzáférésű csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva"
    )

    val leadOrganizerGroups = StringSettingRef(componentSettingService, component,
        "leadOrganizerGroups", "", type = SettingType.TEXT, fieldName = "LEAD_ORGANIZER hozzáférésű csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val userAccessGroup = ControlGroup(component, "userAccessGroup", fieldName = "Felhasználók hozzáférése",
        description = "Ha nincs tiltólistán, akkor a legmagasabb beállított hozzáférést fogja megkapni"
    )

    val vipUsers = StringSettingRef(componentSettingService, component,
        "vipUsers", "", type = SettingType.TEXT, fieldName = "USER hozzáférésű felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva"
    )

    val performerUsers = StringSettingRef(componentSettingService, component,
        "performerUsers", "", type = SettingType.TEXT, fieldName = "USER hozzáférésű felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva"
    )

    val organizerUsers = StringSettingRef(componentSettingService, component,
        "organizerUsers", "", type = SettingType.TEXT, fieldName = "USER hozzáférésű felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva"
    )

    val leadOrganizerUsers = StringSettingRef(componentSettingService, component,
        "leadOrganizerUsers", "", type = SettingType.TEXT, fieldName = "USER hozzáférésű felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva"
    )

    val userUsers = StringSettingRef(componentSettingService, component,
        "userUsers", "", type = SettingType.TEXT, fieldName = "USER hozzáférésű felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val roleGroup = ControlGroup(component, "roleGroup", fieldName = "Szerepek hozzáférése",
        description = "Ha nincs tiltólistán, akkor a legmagasabb beállított hozzáférést fogja megkapni"
    )

    val grantUserByDefault = BooleanSettingRef(componentSettingService, component,
        "grantUserByDefault", false, fieldName = "USER hozzáférés alapértelmezetten",
        description = "Ha be van kapcsolva, akkor minden (REGULAR+) felhasználó jogosult a belépésre alapból"
    )

    val grantUserByAttendee = BooleanSettingRef(componentSettingService, component,
        "grantUserByAttendee", false, fieldName = "USER hozzáférés résztvevőknek",
        description = "Ha be van kapcsolva, akkor minden résztvevő (ATTENDEE+) felhasználó jogosult a belépésre"
    )

    val grantUserByPrivileged = BooleanSettingRef(componentSettingService, component,
        "grantUserByPrivileged", false, fieldName = "USER hozzáférés privileged résztvevőknek",
        description = "Ha be van kapcsolva, akkor minden résztvevő (PRIVILEGED+) felhasználó jogosult a belépésre"
    )

    val grantUserByStaff = BooleanSettingRef(componentSettingService, component,
        "grantUserByStaff", false, fieldName = "USER hozzáférés szervezőknek",
        description = "Ha be van kapcsolva, akkor minden szervező (STAFF+) felhasználó jogosult a belépésre"
    )

    val grantOrganizerByStaff = BooleanSettingRef(componentSettingService, component,
        "grantOrganizerByStaff", false, fieldName = "ORGANIZER hozzáférés szervezőknek",
        description = "Ha be van kapcsolva, akkor minden szervező (STAFF+) felhasználó jogosult a szervező rangra"
    )

    val grantOrganizerByAdmin = BooleanSettingRef(componentSettingService, component,
        "grantOrganizerByAdmin", false, fieldName = "ORGANIZER hozzáférés adminoknak",
        description = "Ha be van kapcsolva, akkor minden szervező (ADMIN+) felhasználó jogosult a szervező rangra"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val banListGroup = ControlGroup(component, "banListGroup", fieldName = "Tiltó lista",
        description = "Ha nincs tiltólistán, akkor a legmagasabb beállított hozzáférést fogja megkapni"
    )

    val bannedGroups = StringSettingRef(componentSettingService, component,
        "bannedGroups", "", type = SettingType.TEXT, fieldName = "Kitiltott csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva"
    )

    val bannedUsers = StringSettingRef(componentSettingService, component,
        "bannedUsers", "", type = SettingType.TEXT,
        fieldName = "Kitiltott felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val ticketGroup = ControlGroup(component, "ticketGroup", fieldName = "Jegyek",
        description = "A jegyellenőrzés menü beállításai")

    val ticketShowEntryCount = BooleanSettingRef(componentSettingService, component,
        "ticketShowEntryCount", true, fieldName = "Belépések számának mutatása",
        description = "Beolvasáskor mutatja a beolvasónak", serverSideOnly = true
    )

    val ticketAllowBmejegy = BooleanSettingRef(componentSettingService, component,
        "ticketAllowBmejegy", true, fieldName = "BME Jegyesek beengedése",
        description = "Csak akkor működik, ha a bmejegy komponens be van kapcsolva", serverSideOnly = true
    )

}
