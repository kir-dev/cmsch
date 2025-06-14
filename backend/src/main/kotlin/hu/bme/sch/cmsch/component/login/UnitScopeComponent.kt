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
    "unit-scope",
    "/",
    "Jogviszonyok",
    ControlPermissions.PERMISSION_CONTROL_APP,
    listOf(),
    env
) {

    final override val allSettings by lazy {
        listOf(
            unitScopeGroup,
            minRole,

            unitScopeGrantsEnabled,

            bmeGroup,
            bmeGrantRoleAttendee,
            bmeGrantRolePrivileged,
            bmeGrantGroupName,

            activeGroup,
            activeGrantRoleAttendee,
            activeGrantRolePrivileged,
            activeGrantGroupName,

            newbieGroup,
            newbieGrantRoleAttendee,
            newbieGrantRolePrivileged,
            newbieGrantGroupName,

            vikGroup,
            vikGrantRoleAttendee,
            vikGrantRolePrivileged,
            vikGrantGroupName,

            vikNewbieGroup,
            vikNewbieGrantRoleAttendee,
            vikNewbieGrantRolePrivileged,
            vikNewbieGrantGroupName,

            vbkGroup,
            vbkGrantRoleAttendee,
            vbkGrantRolePrivileged,
            vbkGrantGroupName,

            vbkNewbieGroup,
            vbkNewbieGrantRoleAttendee,
            vbkNewbieGrantRolePrivileged,
            vbkNewbieGrantGroupName,

            )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    val unitScopeGroup = SettingGroup(component, "unitScopeGroup", fieldName = "Jogviszonyok")

    val unitScopeGrantsEnabled = BooleanSettingRef(componentSettingService, component,
        "unitScopeGrantsEnabled", false, serverSideOnly = true, fieldName = "Jogok adása",
        description = "Jogok adása hallgatói státusz alapján, csak akkor, ha ez a kapcsoló aktív és BME_UNIT_SCOPE scope elérhető"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val bmeGroup = SettingGroup(component, "bmeGroup", fieldName = "BME-s felhasználók",
        description = "Érvényesülési sorrend: 1. Csak akkor működik, ha a BME_UNIT_SCOPE aktiválva van és authsch a provider"
    )

    val bmeGrantRoleAttendee = BooleanSettingRef(componentSettingService, component,
        "bmeGrantRoleAttendee", false, serverSideOnly = true, fieldName = ATTENDEE_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél a BME-sek ATTENDEE jogot kapnak (ha a ROLE < STAFF)"
    )


    val bmeGrantRolePrivileged = BooleanSettingRef(componentSettingService, component,
        "bmeGrantRolePrivileged", false, serverSideOnly = true, fieldName = PRIVILEGED_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél a BME-sek PRIVILEGED jogot kapnak (ha a ROLE < STAFF)"
    )


    val bmeGrantGroupName = StringSettingRef(componentSettingService, component,
        "bmeGrantGroupName", "", serverSideOnly = true, fieldName = MOVE_TO_GROUP, description = MOVE_DESCRIPTION
    )


    /// -------------------------------------------------------------------------------------------------------------------

    val activeGroup = SettingGroup(component, "activeGroup", fieldName = "Aktív hallgató felhasználók",
        description = "Érvényesülési sorrend: 2. Csak akkor működik, ha a BME_UNIT_SCOPE aktiválva van és authsch a provider"
    )

    val activeGrantRoleAttendee = BooleanSettingRef(componentSettingService, component,
        "activeGrantRoleAttendee", false, serverSideOnly = true, fieldName = ATTENDEE_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél az aktív hallgatók ATTENDEE jogot kapnak (ha a ROLE < STAFF)"
    )


    val activeGrantRolePrivileged = BooleanSettingRef(componentSettingService, component,
        "activeGrantRolePrivileged", false, serverSideOnly = true, fieldName = PRIVILEGED_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél az aktív hallgatók PRIVILEGED jogot kapnak (ha a ROLE < STAFF)"
    )


    val activeGrantGroupName = StringSettingRef(componentSettingService, component,
        "activeGrantGroupName", "", serverSideOnly = true,
        fieldName = MOVE_TO_GROUP, description = MOVE_DESCRIPTION
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val newbieGroup = SettingGroup(component, "newbieGroup", fieldName = "Első éves felhasználók",
        description = "Érvényesülési sorrend: 3. Csak akkor működik, ha a BME_UNIT_SCOPE aktiválva van és authsch a provider"
    )

    val newbieGrantRoleAttendee = BooleanSettingRef(componentSettingService, component,
        "newbieGrantRoleAttendee", false, serverSideOnly = true, fieldName = ATTENDEE_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél az elsőévesek ATTENDEE jogot kapnak (ha a ROLE < STAFF)"
    )


    val newbieGrantRolePrivileged = BooleanSettingRef(componentSettingService, component,
        "newbieGrantRolePrivileged", false, serverSideOnly = true, fieldName = PRIVILEGED_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél az elsőévesek PRIVILEGED jogot kapnak (ha a ROLE < STAFF)"
    )


    val newbieGrantGroupName = StringSettingRef(componentSettingService, component,
        "newbieGrantGroupName", "", serverSideOnly = true,
        fieldName = MOVE_TO_GROUP, description = MOVE_DESCRIPTION
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val vikGroup = SettingGroup(component, "vikGroup", fieldName = "VIK-es felhasználók",
        description = "Érvényesülési sorrend: 4. Csak akkor működik, ha a BME_UNIT_SCOPE aktiválva van és authsch a provider"
    )

    val vikGrantRoleAttendee = BooleanSettingRef(componentSettingService, component,
        "vikGrantRoleAttendee", false, serverSideOnly = true, fieldName = ATTENDEE_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél a VIK-esek ATTENDEE jogot kapnak (ha a ROLE < STAFF)"
    )


    val vikGrantRolePrivileged = BooleanSettingRef(componentSettingService, component,
        "vikGrantRolePrivileged", false, serverSideOnly = true, fieldName = PRIVILEGED_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél a VIK-esek PRIVILEGED jogot kapnak(ha a ROLE < STAFF)"
    )


    val vikGrantGroupName = StringSettingRef(componentSettingService, component,
        "vikGrantGroupName", "", serverSideOnly = true,
        fieldName = MOVE_TO_GROUP, description = MOVE_DESCRIPTION
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val vikNewbieGroup = SettingGroup(component, "vikNewbieGroup", fieldName = "VIK-es elsőéves felhasználók",
        description = "Érvényesülési sorrend: 5. Csak akkor működik, ha a BME_UNIT_SCOPE aktiválva van és authsch a provider"
    )

    val vikNewbieGrantRoleAttendee = BooleanSettingRef(componentSettingService, component,
        "vikNewbieGrantRoleAttendee", false, serverSideOnly = true, fieldName = ATTENDEE_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél az elsőéves VIK-esek ATTENDEE jogot kapnak (ha a ROLE < STAFF)"
    )


    val vikNewbieGrantRolePrivileged = BooleanSettingRef(componentSettingService, component,
        "vikNewbieGrantRolePrivileged", false, serverSideOnly = true, fieldName = PRIVILEGED_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél az elsőéves VIK-esek PRIVILEGED jogot kapnak(ha a ROLE < STAFF)"
    )


    val vikNewbieGrantGroupName = StringSettingRef(componentSettingService, component,
        "vikNewbieGrantGroupName", "", serverSideOnly = true, fieldName = MOVE_TO_GROUP, description = MOVE_DESCRIPTION
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val vbkGroup = SettingGroup(component, "vbkGroup", fieldName = "VBK-s felhasználók",
        description = "Érvényesülési sorrend: 6. Csak akkor működik, ha a BME_UNIT_SCOPE aktiválva van és authsch a provider"
    )

    val vbkGrantRoleAttendee = BooleanSettingRef(componentSettingService, component,
        "vbkGrantRoleAttendee", false, serverSideOnly = true, fieldName = ATTENDEE_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél a VBK-sok ATTENDEE jogot kapnak (ha a ROLE < STAFF)"
    )


    val vbkGrantRolePrivileged = BooleanSettingRef(componentSettingService, component,
        "vbkGrantRolePrivileged", false, serverSideOnly = true, fieldName = PRIVILEGED_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél a VBK-sok PRIVILEGED jogot kapnak (ha a ROLE < STAFF)"
    )


    val vbkGrantGroupName = StringSettingRef(componentSettingService, component,
        "vbkGrantGroupName", "", serverSideOnly = true,
        fieldName = MOVE_TO_GROUP, description = MOVE_DESCRIPTION
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val vbkNewbieGroup = SettingGroup(component, "vbkNewbieGroup", fieldName = "VBK-s elsőéves felhasználók",
        description = "Érvényesülési sorrend: 7. Csak akkor működik, ha a BME_UNIT_SCOPE aktiválva van és authsch a provider"
    )

    val vbkNewbieGrantRoleAttendee = BooleanSettingRef(componentSettingService, component,
        "vbkNewbieGrantRoleAttendee", false, serverSideOnly = true, fieldName = ATTENDEE_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél az elsőéves VBK-sok ATTENDEE jogot kapnak (ha a ROLE < STAFF)"
    )


    val vbkNewbieGrantRolePrivileged = BooleanSettingRef(componentSettingService, component,
        "vbkNewbieGrantRolePrivileged", false, serverSideOnly = true, fieldName = PRIVILEGED_NAME,
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél az elsőéves VBK-sok PRIVILEGED jogot kapnak (ha a ROLE < STAFF)"
    )


    val vbkNewbieGrantGroupName = StringSettingRef(componentSettingService, component,
        "vbkNewbieGrantGroupName", "", serverSideOnly = true, fieldName = MOVE_TO_GROUP, description = MOVE_DESCRIPTION
    )

}
