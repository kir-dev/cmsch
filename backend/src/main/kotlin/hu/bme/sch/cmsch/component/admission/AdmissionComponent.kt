package hu.bme.sch.cmsch.component.admission

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
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
    componentSettingService, env
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

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )


    /// -------------------------------------------------------------------------------------------------------------------

    val controlGroup = SettingProxy(componentSettingService, component,
        "controlGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Beléptetés működése",
        description = ""
    )

    val onlyAcceptApprovedForms = SettingProxy(componentSettingService, component,
        "onlyAcceptApprovedForms", "false", type = SettingType.BOOLEAN,
        fieldName = "Csak az elfogadott formok számítanak",
        description = "Ha be van kapcsolva, akkor csak az elfogadott és nem elutasított formok számítanak. Csak akkor működik, ha a forms komponens be van kapcsolva."
    )

    val saveEntryLog = SettingProxy(componentSettingService, component,
        "saveEntryLog", "true", type = SettingType.BOOLEAN,
        fieldName = "Beléptetések mentése",
        description = "Ha be van kapcsolva, akkor minden beengedés logolva van"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val groupAccessGroup = SettingProxy(componentSettingService, component,
        "groupAccessGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Csoportok hozzáférése",
        description = "Ha nincs tiltólistán, akkor a legmagasabb beállított hozzáférést fogja megkapni"
    )

    val userGroups = SettingProxy(componentSettingService, component,
        "userGroups", "", type = SettingType.TEXT,
        fieldName = "USER hozzáférésű csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva"
    )

    val vipGroups = SettingProxy(componentSettingService, component,
        "vipGroups", "", type = SettingType.TEXT,
        fieldName = "VIP hozzáférésű csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva"
    )

    val performerGroups = SettingProxy(componentSettingService, component,
        "performerGroups", "", type = SettingType.TEXT,
        fieldName = "PERFORMER hozzáférésű csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva"
    )

    val organizerGroups = SettingProxy(componentSettingService, component,
        "organizerGroups", "", type = SettingType.TEXT,
        fieldName = "ORGANIZER hozzáférésű csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva"
    )

    val leadOrganizerGroups = SettingProxy(componentSettingService, component,
        "leadOrganizerGroups", "", type = SettingType.TEXT,
        fieldName = "LEAD_ORGANIZER hozzáférésű csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val userAccessGroup = SettingProxy(componentSettingService, component,
        "userAccessGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Felhasználók hozzáférése",
        description = "Ha nincs tiltólistán, akkor a legmagasabb beállított hozzáférést fogja megkapni"
    )

    val vipUsers = SettingProxy(componentSettingService, component,
        "vipUsers", "", type = SettingType.TEXT,
        fieldName = "USER hozzáférésű felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva"
    )

    val performerUsers = SettingProxy(componentSettingService, component,
        "performerUsers", "", type = SettingType.TEXT,
        fieldName = "USER hozzáférésű felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva"
    )

    val organizerUsers = SettingProxy(componentSettingService, component,
        "organizerUsers", "", type = SettingType.TEXT,
        fieldName = "USER hozzáférésű felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva"
    )

    val leadOrganizerUsers = SettingProxy(componentSettingService, component,
        "leadOrganizerUsers", "", type = SettingType.TEXT,
        fieldName = "USER hozzáférésű felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva"
    )

    val userUsers = SettingProxy(componentSettingService, component,
        "userUsers", "", type = SettingType.TEXT,
        fieldName = "USER hozzáférésű felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val roleGroup = SettingProxy(componentSettingService, component,
        "roleGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Szerepek hozzáférése",
        description = "Ha nincs tiltólistán, akkor a legmagasabb beállított hozzáférést fogja megkapni"
    )

    val grantUserByDefault = SettingProxy(componentSettingService, component,
        "grantUserByDefault", "false", type = SettingType.BOOLEAN,
        fieldName = "USER hozzáférés alapértelmezetten",
        description = "Ha be van kapcsolva, akkor minden (REGULAR+) felhasználó jogosult a belépésre alapból"
    )

    val grantUserByAttendee = SettingProxy(componentSettingService, component,
        "grantUserByAttendee", "false", type = SettingType.BOOLEAN,
        fieldName = "USER hozzáférés résztvevőknek",
        description = "Ha be van kapcsolva, akkor minden résztvevő (ATTENDEE+) felhasználó jogosult a belépésre"
    )

    val grantUserByStaff = SettingProxy(componentSettingService, component,
        "grantUserByStaff", "false", type = SettingType.BOOLEAN,
        fieldName = "USER hozzáférés szervezőknek",
        description = "Ha be van kapcsolva, akkor minden szervező (STAFF+) felhasználó jogosult a belépésre"
    )

    val grantOrganizerByStaff = SettingProxy(componentSettingService, component,
        "grantOrganizerByStaff", "false", type = SettingType.BOOLEAN,
        fieldName = "ORGANIZER hozzáférés szervezőknek",
        description = "Ha be van kapcsolva, akkor minden szervező (STAFF+) felhasználó jogosult a szervező rangra"
    )

    val grantOrganizerByAdmin = SettingProxy(componentSettingService, component,
        "grantOrganizerByAdmin", "false", type = SettingType.BOOLEAN,
        fieldName = "ORGANIZER hozzáférés adminoknak",
        description = "Ha be van kapcsolva, akkor minden szervező (ADMIN+) felhasználó jogosult a szervező rangra"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val banListGroup = SettingProxy(componentSettingService, component,
        "banListGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Tiltó lista",
        description = "Ha nincs tiltólistán, akkor a legmagasabb beállított hozzáférést fogja megkapni"
    )

    val bannedGroups = SettingProxy(componentSettingService, component,
        "bannedGroups", "", type = SettingType.TEXT,
        fieldName = "Kitiltott csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva"
    )

    val bannedUsers = SettingProxy(componentSettingService, component,
        "bannedUsers", "", type = SettingType.TEXT,
        fieldName = "Kitiltott felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val ticketGroup = SettingProxy(componentSettingService, component,
        "ticketGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Jegyek",
        description = "A jegyellenőrzés menü beállításai"
    )

    val ticketShowEntryCount = SettingProxy(componentSettingService, component,
        "ticketShowEntryCount", "true", type = SettingType.BOOLEAN,
        fieldName = "Belépések számának mutatása",
        description = "Beolvasáskor mutatja a beolvasónak",
        serverSideOnly = true
    )

    val ticketAllowBmejegy = SettingProxy(componentSettingService, component,
        "ticketAllowBmejegy", "true", type = SettingType.BOOLEAN,
        fieldName = "BME Jegyesek beengedése",
        description = "Csak akkor működik, ha a bmejegy komponens be van kapcsolva",
        serverSideOnly = true
    )

}
