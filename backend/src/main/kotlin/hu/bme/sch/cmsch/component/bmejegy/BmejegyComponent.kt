package hu.bme.sch.cmsch.component.bmejegy

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["bmejegy"],
    havingValue = "true",
    matchIfMissing = false
)
class BmejegyComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "bmejegy",
    "/",
    "BME jegy",
    ControlPermissions.PERMISSION_CONTROL_BMEJEGY,
    listOf(BmejegyRecordEntity::class),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            minRole,

            logicGroup,
            syncEnabled,
            syncInterval,
            completeByNeptun,
            completeByEmail,
            completeByPhotoId,
            bufferSize,
            minTimestamp,
            countToFetch,
            szigFieldName,

            grantGroup1,
            forOrder1,
            grantAttendee1,
            grantPrivileged1,
            grantGroupName1,

            grantGroup2,
            forOrder2,
            grantAttendee2,
            grantPrivileged2,
            grantGroupName2,

            grantGroup3,
            forOrder3,
            grantAttendee3,
            grantPrivileged3,
            grantGroupName3,
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "", minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val logicGroup = SettingProxy(componentSettingService, component,
        "logicGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Működés",
        description = ""
    )

    val syncEnabled = SettingProxy(componentSettingService, component,
        "syncEnabled", "false", type = SettingType.BOOLEAN,
        fieldName = "Szinkronizáció", description = "Ha be van kapcsolva, akkor automatikusan szinkronizál a BME JEGY-ről"
    )

    val syncInterval = SettingProxy(componentSettingService, component,
        "syncInterval", "10", type = SettingType.NUMBER, serverSideOnly = true,
        fieldName = "Frissítési idő", description = "Ennyi időnként (perc) frissít az oldalról"
    )

    val bufferSize = SettingProxy(componentSettingService, component,
        "bufferSize", "524288", type = SettingType.NUMBER, serverSideOnly = true,
        fieldName = "Buffer méret", description = "[ADVANCED] Az API válasz mérete. Alapból 262144, de ez 300 entryig elég csak (kb)."
    )

    val completeByNeptun = SettingProxy(componentSettingService, component,
        "completeByNeptun", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Keresés NEPTUN alapján", description = "Neptun alapján keresi a fizetett jegyeket (NINCS IMPLEMENTÁLVA)"
    )

    val completeByEmail = SettingProxy(componentSettingService, component,
        "completeByEmail", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Keresés EMAIL alapján", description = "Email alapján keresi a fizetett jegyeket (NINCS IMPLEMENTÁLVA)"
    )

    val completeByPhotoId = SettingProxy(componentSettingService, component,
        "completeByPhotoId", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Keresés SZIGSZÁM alapján", description = "Szigszám alapján keresi a fizetett jegyeket (ellenőrizni kell, hogy jó-e a formátum)"
    )

    val minTimestamp = SettingProxy(componentSettingService, component,
        "minTimestamp", "1689858001000", type = SettingType.NUMBER, serverSideOnly = true,
        fieldName = "Ekkortól nézve", description = "Unix timestamp (ms pontossággal)"
    )

    val countToFetch = SettingProxy(componentSettingService, component,
        "countToFetch", "10000", type = SettingType.NUMBER, serverSideOnly = true,
        fieldName = "Ennyit töltsön le", description = "Az első ennyi darabot syncelje fel"
    )

    val szigFieldName = SettingProxy(componentSettingService, component,
        "szigFieldName", "szig", serverSideOnly = true,
        fieldName = "Szig. szám mező neve", description = "Hozzárendeléshez használatos"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val grantGroup1 = SettingProxy(componentSettingService, component,
        "grantGroup1", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Fizetés utáni művelet #1",
        description = ""
    )

    val forOrder1 = SettingProxy(componentSettingService, component,
        "forOrder1", "", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Termék neve", description = "Ezzel a névvel szerepel a BME JEGY oldalon (tartalmazás, üres = ki van kapcsolva)"
    )

    val grantAttendee1 = SettingProxy(componentSettingService, component,
        "grantAttendee1", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Adjon-e ATTENDEE ROLE-t", description = "Adjon-e a felhasználónak ATTENDEE ROLE-t? (ha nincs neki magasabb)"
    )

    val grantPrivileged1 = SettingProxy(componentSettingService, component,
        "grantPrivileged1", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Adjon-e PRIVILEGED ROLE-t", description = "Adjon-e a felhasználónak PRIVILEGED ROLE-t? (ha nincs neki magasabb)"
    )

    val grantGroupName1 = SettingProxy(componentSettingService, component,
        "grantGroupName1", "", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Csoportba helyezés", description = "Csoport tagság állítása (ha üres akkor nem állít)"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val grantGroup2 = SettingProxy(componentSettingService, component,
        "grantGroup2", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Fizetés utáni művelet #2",
        description = ""
    )

    val forOrder2 = SettingProxy(componentSettingService, component,
        "forOrder2", "", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Termék neve", description = "Ezzel a névvel szerepel a BME JEGY oldalon (tartalmazás, üres = ki van kapcsolva)"
    )

    val grantAttendee2 = SettingProxy(componentSettingService, component,
        "grantAttendee2", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Adjon-e ATTENDEE ROLE-t", description = "Adjon-e a felhasználónak ATTENDEE ROLE-t? (ha nincs neki magasabb)"
    )

    val grantPrivileged2 = SettingProxy(componentSettingService, component,
        "grantPrivileged2", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Adjon-e PRIVILEGED ROLE-t", description = "Adjon-e a felhasználónak PRIVILEGED ROLE-t? (ha nincs neki magasabb)"
    )

    val grantGroupName2 = SettingProxy(componentSettingService, component,
        "grantGroupName2", "", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Csoportba helyezés", description = "Csoport tagság állítása (ha üres akkor nem állít)"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val grantGroup3 = SettingProxy(componentSettingService, component,
        "grantGroup3", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Fizetés utáni művelet #3",
        description = ""
    )

    val forOrder3 = SettingProxy(componentSettingService, component,
        "forOrder3", "", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Termék neve", description = "Ezzel a névvel szerepel a BME JEGY oldalon (tartalmazás, üres = ki van kapcsolva)"
    )

    val grantAttendee3 = SettingProxy(componentSettingService, component,
        "grantAttendee3", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Adjon-e ATTENDEE ROLE-t", description = "Adjon-e a felhasználónak ATTENDEE ROLE-t? (ha nincs neki magasabb)"
    )

    val grantPrivileged3 = SettingProxy(componentSettingService, component,
        "grantPrivileged3", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Adjon-e PRIVILEGED ROLE-t", description = "Adjon-e a felhasználónak PRIVILEGED ROLE-t? (ha nincs neki magasabb)"
    )

    val grantGroupName3 = SettingProxy(componentSettingService, component,
        "grantGroupName3", "", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Csoportba helyezés", description = "Csoport tagság állítása (ha üres akkor nem állít)"
    )

}
