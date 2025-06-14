package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

private const val ATTENDEE_NAME = "ATTENDEE role adása"
private const val PRIVILEGED_NAME = "PRIVILEGED role adása"
private const val MOVE_TO_GROUP = "Csoportba áthelyezés"
private const val MOVE_DESCRIPTION =
    "Ha ez nem üres, akkor a bejelentkezésnél az alábbi csoportba helyez át (ha a csoport leavable)"

@Service
@ConditionalOnBean(LoginComponent::class)
class UnitScopeComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "unit-scope",
    "/",
    "Jogviszonyok",
    ControlPermissions.PERMISSION_CONTROL_APP,
    listOf(),
    env
) {

    final override val menuDisplayName = null

    final override var minRole by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    val unitScopeGroup by SettingGroup(fieldName = "Jogviszonyok")

    var unitScopeGrantsEnabled by BooleanSettingRef(false, serverSideOnly = true, fieldName = "Jogok adása",
        description = "Jogok adása hallgatói státusz alapján, csak akkor, ha ez a kapcsoló aktív és BME_UNIT_SCOPE scope elérhető"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val bmeGroup by SettingGroup(fieldName = "BME-s felhasználók",
        description = "Érvényesülési sorrend: 1. Csak akkor működik, ha a BME_UNIT_SCOPE aktiválva van és authsch a provider"
    )

    var bmeGrantRoleAttendee by BooleanSettingRef(false, serverSideOnly = true, fieldName = ATTENDEE_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél a BME-sek ATTENDEE jogot kapnak (ha a ROLE < STAFF)"
    )


    var bmeGrantRolePrivileged by BooleanSettingRef(false, serverSideOnly = true, fieldName = PRIVILEGED_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél a BME-sek PRIVILEGED jogot kapnak (ha a ROLE < STAFF)"
    )


    var bmeGrantGroupName by StringSettingRef("", serverSideOnly = true, fieldName = MOVE_TO_GROUP, description = MOVE_DESCRIPTION
    )


    /// -------------------------------------------------------------------------------------------------------------------

    val activeGroup by SettingGroup(fieldName = "Aktív hallgató felhasználók",
        description = "Érvényesülési sorrend: 2. Csak akkor működik, ha a BME_UNIT_SCOPE aktiválva van és authsch a provider"
    )

    var activeGrantRoleAttendee by BooleanSettingRef(false, serverSideOnly = true, fieldName = ATTENDEE_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél az aktív hallgatók ATTENDEE jogot kapnak (ha a ROLE < STAFF)"
    )


    var activeGrantRolePrivileged by BooleanSettingRef(false, serverSideOnly = true, fieldName = PRIVILEGED_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél az aktív hallgatók PRIVILEGED jogot kapnak (ha a ROLE < STAFF)"
    )


    var activeGrantGroupName by StringSettingRef("", serverSideOnly = true,
        fieldName = MOVE_TO_GROUP, description = MOVE_DESCRIPTION
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val newbieGroup by SettingGroup(fieldName = "Első éves felhasználók",
        description = "Érvényesülési sorrend: 3. Csak akkor működik, ha a BME_UNIT_SCOPE aktiválva van és authsch a provider"
    )

    var newbieGrantRoleAttendee by BooleanSettingRef(false, serverSideOnly = true, fieldName = ATTENDEE_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél az elsőévesek ATTENDEE jogot kapnak (ha a ROLE < STAFF)"
    )


    var newbieGrantRolePrivileged by BooleanSettingRef(false, serverSideOnly = true, fieldName = PRIVILEGED_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél az elsőévesek PRIVILEGED jogot kapnak (ha a ROLE < STAFF)"
    )


    var newbieGrantGroupName by StringSettingRef("", serverSideOnly = true,
        fieldName = MOVE_TO_GROUP, description = MOVE_DESCRIPTION
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val vikGroup by SettingGroup(fieldName = "VIK-es felhasználók",
        description = "Érvényesülési sorrend: 4. Csak akkor működik, ha a BME_UNIT_SCOPE aktiválva van és authsch a provider"
    )

    var vikGrantRoleAttendee by BooleanSettingRef(false, serverSideOnly = true, fieldName = ATTENDEE_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél a VIK-esek ATTENDEE jogot kapnak (ha a ROLE < STAFF)"
    )


    var vikGrantRolePrivileged by BooleanSettingRef(false, serverSideOnly = true, fieldName = PRIVILEGED_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél a VIK-esek PRIVILEGED jogot kapnak(ha a ROLE < STAFF)"
    )


    var vikGrantGroupName by StringSettingRef("", serverSideOnly = true,
        fieldName = MOVE_TO_GROUP, description = MOVE_DESCRIPTION
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val vikNewbieGroup by SettingGroup(fieldName = "VIK-es elsőéves felhasználók",
        description = "Érvényesülési sorrend: 5. Csak akkor működik, ha a BME_UNIT_SCOPE aktiválva van és authsch a provider"
    )

    var vikNewbieGrantRoleAttendee by BooleanSettingRef(false, serverSideOnly = true, fieldName = ATTENDEE_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél az elsőéves VIK-esek ATTENDEE jogot kapnak (ha a ROLE < STAFF)"
    )


    var vikNewbieGrantRolePrivileged by BooleanSettingRef(false, serverSideOnly = true, fieldName = PRIVILEGED_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél az elsőéves VIK-esek PRIVILEGED jogot kapnak(ha a ROLE < STAFF)"
    )


    var vikNewbieGrantGroupName by StringSettingRef("", serverSideOnly = true, fieldName = MOVE_TO_GROUP, description = MOVE_DESCRIPTION
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val vbkGroup by SettingGroup(fieldName = "VBK-s felhasználók",
        description = "Érvényesülési sorrend: 6. Csak akkor működik, ha a BME_UNIT_SCOPE aktiválva van és authsch a provider"
    )

    var vbkGrantRoleAttendee by BooleanSettingRef(false, serverSideOnly = true, fieldName = ATTENDEE_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél a VBK-sok ATTENDEE jogot kapnak (ha a ROLE < STAFF)"
    )


    var vbkGrantRolePrivileged by BooleanSettingRef(false, serverSideOnly = true, fieldName = PRIVILEGED_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél a VBK-sok PRIVILEGED jogot kapnak (ha a ROLE < STAFF)"
    )


    var vbkGrantGroupName by StringSettingRef("", serverSideOnly = true,
        fieldName = MOVE_TO_GROUP, description = MOVE_DESCRIPTION
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val vbkNewbieGroup by SettingGroup(fieldName = "VBK-s elsőéves felhasználók",
        description = "Érvényesülési sorrend: 7. Csak akkor működik, ha a BME_UNIT_SCOPE aktiválva van és authsch a provider"
    )

    var vbkNewbieGrantRoleAttendee by BooleanSettingRef(false, serverSideOnly = true, fieldName = ATTENDEE_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél az elsőéves VBK-sok ATTENDEE jogot kapnak (ha a ROLE < STAFF)"
    )


    var vbkNewbieGrantRolePrivileged by BooleanSettingRef(false, serverSideOnly = true, fieldName = PRIVILEGED_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél az elsőéves VBK-sok PRIVILEGED jogot kapnak (ha a ROLE < STAFF)"
    )


    var vbkNewbieGrantGroupName by StringSettingRef("", serverSideOnly = true, fieldName = MOVE_TO_GROUP, description = MOVE_DESCRIPTION
    )

}
