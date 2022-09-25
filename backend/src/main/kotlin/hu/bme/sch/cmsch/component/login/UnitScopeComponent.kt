package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
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
) : ComponentBase("unit-scope", "/", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            minRole,

            unitScopeGrantsEnabled,

            bmeGroup,
            bmeGrantRoleAttendee,
            bmeGrantRolePrivileged,
            bmeGrantGroupName,

            vikGroup,
            vikGrantRoleAttendee,
            vikGrantRolePrivileged,
            vikGrantGroupName,

            vbkGroup,
            vbkGrantRoleAttendee,
            vbkGrantRolePrivileged,
            vbkGrantGroupName,

            activeGroup,
            activeGrantRoleAttendee,
            activeGrantRolePrivileged,
            activeGrantGroupName,

            newbieGroup,
            newbieGrantRoleAttendee,
            newbieGrantRolePrivileged,
            newbieGrantGroupName,
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    val unitScopeGrantsEnabled = SettingProxy(componentSettingService, component,
        "unitScopeGrantsEnabled", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Jogok adása", description = "Jogok adása hallgatói státusz alapján, csak akkor, " +
                "ha ez a kapcsoló aktív és BME_UNIT_SCOPE scope elérhető"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val bmeGroup = SettingProxy(componentSettingService, component,
        "bmeGroup", "", type = SettingType.COMPONENT_GROUP, persist = false, serverSideOnly = true,
        fieldName = "BME-s felhasználók",
        description = "Érvényesülési sorrend: 1. " +
                "Csak akkor működik, ha a BME_UNIT_SCOPE aktiválva van és authsch a provider"
    )

    val bmeGrantRoleAttendee = SettingProxy(componentSettingService, component,
        "bmeGrantRoleAttendee", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = ATTENDEE_NAME, description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél a BME-sek " +
                "ATTENDEE jogot kapnak (ha a ROLE < STAFF)"
    )

    val bmeGrantRolePrivileged = SettingProxy(componentSettingService, component,
        "bmeGrantRolePrivileged", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = PRIVILEGED_NAME, description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél a BME-sek" +
                " PRIVILEGED jogot kapnak (ha a ROLE < STAFF)"
    )

    val bmeGrantGroupName = SettingProxy(componentSettingService, component,
        "bmeGrantGroupName", "", serverSideOnly = true,
        fieldName = MOVE_TO_GROUP, description = MOVE_DESCRIPTION
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val vikGroup = SettingProxy(componentSettingService, component,
        "vikGroup", "", type = SettingType.COMPONENT_GROUP, persist = false, serverSideOnly = true,
        fieldName = "VIK-es felhasználók",
        description = "Érvényesülési sorrend: 2. " +
                "Csak akkor működik, ha a BME_UNIT_SCOPE aktiválva van és authsch a provider"
    )

    val vikGrantRoleAttendee = SettingProxy(componentSettingService, component,
        "vikGrantRoleAttendee", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = ATTENDEE_NAME, description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél a VIK-esek " +
                "ATTENDEE jogot kapnak (ha a ROLE < STAFF)"
    )

    val vikGrantRolePrivileged = SettingProxy(componentSettingService, component,
        "vikGrantRolePrivileged", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = PRIVILEGED_NAME, description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél a VIK-esek " +
                "PRIVILEGED jogot kapnak(ha a ROLE < STAFF)"
    )

    val vikGrantGroupName = SettingProxy(componentSettingService, component,
        "vikGrantGroupName", "", serverSideOnly = true,
        fieldName = MOVE_TO_GROUP, description = MOVE_DESCRIPTION
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val vbkGroup = SettingProxy(componentSettingService, component,
        "vbkGroup", "", type = SettingType.COMPONENT_GROUP, persist = false, serverSideOnly = true,
        fieldName = "VBK-s felhasználók",
        description = "Érvényesülési sorrend: 3. " +
                "Csak akkor működik, ha a BME_UNIT_SCOPE aktiválva van és authsch a provider"
    )

    val vbkGrantRoleAttendee = SettingProxy(componentSettingService, component,
        "vbkGrantRoleAttendee", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = ATTENDEE_NAME, description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél a VBK-sok " +
                "ATTENDEE jogot kapnak (ha a ROLE < STAFF)"
    )

    val vbkGrantRolePrivileged = SettingProxy(componentSettingService, component,
        "vbkGrantRolePrivileged", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = PRIVILEGED_NAME, description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél a VIK-sok " +
                "PRIVILEGED jogot kapnak (ha a ROLE < STAFF)"
    )

    val vbkGrantGroupName = SettingProxy(componentSettingService, component,
        "vbkGrantGroupName", "", serverSideOnly = true,
        fieldName = MOVE_TO_GROUP, description = MOVE_DESCRIPTION
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val activeGroup = SettingProxy(componentSettingService, component,
        "activeGroup", "", type = SettingType.COMPONENT_GROUP, persist = false, serverSideOnly = true,
        fieldName = "Aktív hallgató felhasználók",
        description = "Érvényesülési sorrend: 4. " +
                "Csak akkor működik, ha a BME_UNIT_SCOPE aktiválva van és authsch a provider"
    )

    val activeGrantRoleAttendee = SettingProxy(componentSettingService, component,
        "activeGrantRoleAttendee", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = ATTENDEE_NAME, description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél az aktív hallgatók " +
                "ATTENDEE jogot kapnak (ha a ROLE < STAFF)"
    )

    val activeGrantRolePrivileged = SettingProxy(componentSettingService, component,
        "activeGrantRolePrivileged", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = PRIVILEGED_NAME, description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél az aktív hallgatók " +
                "PRIVILEGED jogot kapnak (ha a ROLE < STAFF)"
    )

    val activeGrantGroupName = SettingProxy(componentSettingService, component,
        "activeGrantGroupName", "", serverSideOnly = true,
        fieldName = MOVE_TO_GROUP, description = MOVE_DESCRIPTION
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val newbieGroup = SettingProxy(componentSettingService, component,
        "newbieGroup", "", type = SettingType.COMPONENT_GROUP, persist = false, serverSideOnly = true,
        fieldName = "Első éves felhasználók",
        description = "Érvényesülési sorrend: 5. " +
                "Csak akkor működik, ha a BME_UNIT_SCOPE aktiválva van és authsch a provider"
    )

    val newbieGrantRoleAttendee = SettingProxy(componentSettingService, component,
        "newbieGrantRoleAttendee", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = ATTENDEE_NAME, description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél az elsőévesek " +
                "ATTENDEE jogot kapnak (ha a ROLE < STAFF)"
    )

    val newbieGrantRolePrivileged = SettingProxy(componentSettingService, component,
        "newbieGrantRolePrivileged", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = PRIVILEGED_NAME, description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél az elsőévesek " +
                "PRIVILEGED jogot kapnak (ha a ROLE < STAFF)"
    )

    val newbieGrantGroupName = SettingProxy(componentSettingService, component,
        "newbieGrantGroupName", "", serverSideOnly = true,
        fieldName = MOVE_TO_GROUP, description = MOVE_DESCRIPTION
    )

}
