package hu.bme.sch.cmsch.component.admission

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnBooleanProperty(value = ["hu.bme.sch.cmsch.component.load.admission"])
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

    var onlyAcceptApprovedForms by BooleanSettingRef(fieldName = "Csak az elfogadott formok számítanak",
        description = "Ha be van kapcsolva, akkor csak az elfogadott és nem elutasított formok számítanak. Csak akkor működik, ha a forms komponens be van kapcsolva.")

    var saveEntryLog by BooleanSettingRef(defaultValue = true, fieldName = "Beléptetések mentése",
        description = "Ha be van kapcsolva, akkor minden beengedés logolva van")

    /// -------------------------------------------------------------------------------------------------------------------

    val groupAccessGroup by SettingGroup(fieldName = "Csoportok hozzáférése",
        description = "Ha nincs tiltólistán, akkor a legmagasabb beállított hozzáférést fogja megkapni")

    var userGroups by StringSettingRef(fieldName = "USER hozzáférésű csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva")

    var vipGroups by StringSettingRef(fieldName = "VIP hozzáférésű csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva")

    var performerGroups by StringSettingRef(fieldName = "PERFORMER hozzáférésű csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva")

    var organizerGroups by StringSettingRef(fieldName = "ORGANIZER hozzáférésű csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva")

    var leadOrganizerGroups by StringSettingRef(fieldName = "LEAD_ORGANIZER hozzáférésű csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva")

    /// -------------------------------------------------------------------------------------------------------------------

    val userAccessGroup by SettingGroup(fieldName = "Felhasználók hozzáférése",
        description = "Ha nincs tiltólistán, akkor a legmagasabb beállított hozzáférést fogja megkapni")

    var vipUsers by StringSettingRef(fieldName = "USER hozzáférésű felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva")

    var performerUsers by StringSettingRef(fieldName = "USER hozzáférésű felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva")

    var organizerUsers by StringSettingRef(fieldName = "USER hozzáférésű felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva")

    var leadOrganizerUsers by StringSettingRef(fieldName = "USER hozzáférésű felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva")

    var userUsers by StringSettingRef(fieldName = "USER hozzáférésű felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva")

    /// -------------------------------------------------------------------------------------------------------------------

    val roleGroup by SettingGroup(fieldName = "Szerepek hozzáférése",
        description = "Ha nincs tiltólistán, akkor a legmagasabb beállított hozzáférést fogja megkapni")

    var grantUserTo by EnumSettingRef(RoleType.SUPERUSER, fieldName = "USER hozzáférés",
        description = "A kiválasztott rangtól és felette mindenki USER hozzáférést kap")

    var grantOrganizerTo by EnumSettingRef(RoleType.SUPERUSER, fieldName = "ORGANIZER hozzáférés",
        description = "A kiválasztott rangtól és felette mindenki ORGANIZER hozzáférést kap")

    /// -------------------------------------------------------------------------------------------------------------------

    val banListGroup by SettingGroup(fieldName = "Tiltó lista",
        description = "Ha nincs tiltólistán, akkor a legmagasabb beállított hozzáférést fogja megkapni")

    var bannedGroups by StringSettingRef(fieldName = "Kitiltott csoportok",
        description = "A csoportok nevei felsorolva és vesszővel (,) elválasztva")

    var bannedUsers by StringSettingRef(fieldName = "Kitiltott felhasználók",
        description = "A felhasználók CMSCH-ID-jei felsorolva és vesszővel (,) elválasztva")

    /// -------------------------------------------------------------------------------------------------------------------

    val ticketGroup by SettingGroup(fieldName = "Jegyek", description = "A jegyellenőrzés menü beállításai")

    var ticketShowEntryCount by BooleanSettingRef(defaultValue = true, fieldName = "Belépések számának mutatása",
        description = "Beolvasáskor mutatja a beolvasónak", serverSideOnly = true)

    var ticketAllowBmejegy by BooleanSettingRef(defaultValue = true, fieldName = "BME Jegyesek beengedése",
        description = "Csak akkor működik, ha a bmejegy komponens be van kapcsolva", serverSideOnly = true)

}
