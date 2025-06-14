package hu.bme.sch.cmsch.component.bmejegy

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.BooleanSettingRef
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.setting.ControlGroup
import hu.bme.sch.cmsch.setting.MinRoleSettingRef
import hu.bme.sch.cmsch.setting.NumberSettingRef
import hu.bme.sch.cmsch.setting.SettingRef
import hu.bme.sch.cmsch.setting.SettingType
import hu.bme.sch.cmsch.setting.StringSettingRef
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
    env
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
            emailFieldName,
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

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", "", minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val logicGroup = ControlGroup(component, "logicGroup", fieldName = "Működés")

    val syncEnabled = BooleanSettingRef(componentSettingService,
        component, "syncEnabled", false, fieldName = "Szinkronizáció",
        description = "Ha be van kapcsolva, akkor automatikusan szinkronizál a BME JEGY-ről"
    )

    val syncInterval = NumberSettingRef(componentSettingService, component,
        "syncInterval", 10, serverSideOnly = true, strictConversion = false,
        fieldName = "Frissítési idő", description = "Ennyi időnként (perc) frissít az oldalról"
    )

    val bufferSize = NumberSettingRef(componentSettingService, component,
        "bufferSize", 524288, serverSideOnly = true, strictConversion = false,
        fieldName = "Buffer méret", description = "[ADVANCED] Az API válasz mérete. Alapból 262144, de ez 300 entryig elég csak (kb)."
    )

    val completeByNeptun = BooleanSettingRef(componentSettingService, component,
        "completeByNeptun", false, serverSideOnly = true,
        fieldName = "Keresés NEPTUN alapján", description = "Neptun alapján keresi a fizetett jegyeket (NINCS IMPLEMENTÁLVA)"
    )

    val completeByEmail = BooleanSettingRef(componentSettingService, component,
        "completeByEmail", false, serverSideOnly = true,
        fieldName = "Keresés EMAIL alapján", description = "Email alapján keresi a fizetett jegyeket (NINCS IMPLEMENTÁLVA)"
    )

    val completeByPhotoId = BooleanSettingRef(componentSettingService, component,
        "completeByPhotoId", false, serverSideOnly = true,
        fieldName = "NEM TÁMOGATOTT | Keresés SZIGSZÁM alapján", description = "Szigszám alapján keresi a fizetett jegyeket (ellenőrizni kell, hogy jó-e a formátum)"
    )

    val minTimestamp = NumberSettingRef(componentSettingService, component,
        "minTimestamp", 1689858001000, serverSideOnly = true,
        fieldName = "NEM TÁMOGATOTT | Ekkortól nézve", description = "Unix timestamp (ms pontossággal)"
    )

    val countToFetch = NumberSettingRef(componentSettingService, component,
        "countToFetch", 10000, serverSideOnly = true,
        fieldName = "NEM TÁMOGATOTT | Ennyit töltsön le", description = "Az első ennyi darabot syncelje fel"
    )

    val emailFieldName = StringSettingRef(componentSettingService, component,
        "emailFieldName", "email", serverSideOnly = true,
        fieldName = "Email mező neve", description = "Hozzárendeléshez használatos (a formban ez a neve)"
    )

    val szigFieldName = StringSettingRef(componentSettingService, component,
        "szigFieldName", "szig", serverSideOnly = true,
        fieldName = "NEM TÁMOGATOTT | Szig. szám mező neve", description = "Hozzárendeléshez használatos"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val grantGroup1 = ControlGroup(component, "grantGroup1", fieldName = "Fizetés utáni művelet #1")

    val forOrder1 = StringSettingRef(componentSettingService, component,
        "forOrder1", "", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Termék neve", description = "Ezzel a névvel szerepel a BME JEGY oldalon (tartalmazás, üres = ki van kapcsolva)"
    )

    val grantAttendee1 = BooleanSettingRef(componentSettingService, component,
        "grantAttendee1", false, serverSideOnly = true,
        fieldName = "Adjon-e ATTENDEE ROLE-t", description = "Adjon-e a felhasználónak ATTENDEE ROLE-t? (ha nincs neki magasabb)"
    )

    val grantPrivileged1 = BooleanSettingRef(componentSettingService, component,
        "grantPrivileged1", false, serverSideOnly = true,
        fieldName = "Adjon-e PRIVILEGED ROLE-t", description = "Adjon-e a felhasználónak PRIVILEGED ROLE-t? (ha nincs neki magasabb)"
    )

    val grantGroupName1 = StringSettingRef(componentSettingService, component,
        "grantGroupName1", "", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Csoportba helyezés", description = "Csoport tagság állítása (ha üres akkor nem állít)"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val grantGroup2 = ControlGroup(component, "grantGroup2", fieldName = "Fizetés utáni művelet #2")

    val forOrder2 = StringSettingRef(componentSettingService, component,
        "forOrder2", "", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Termék neve", description = "Ezzel a névvel szerepel a BME JEGY oldalon (tartalmazás, üres = ki van kapcsolva)"
    )

    val grantAttendee2 = BooleanSettingRef(componentSettingService, component,
        "grantAttendee2", false, serverSideOnly = true,
        fieldName = "Adjon-e ATTENDEE ROLE-t", description = "Adjon-e a felhasználónak ATTENDEE ROLE-t? (ha nincs neki magasabb)"
    )

    val grantPrivileged2 = BooleanSettingRef(componentSettingService, component,
        "grantPrivileged2", false, serverSideOnly = true,
        fieldName = "Adjon-e PRIVILEGED ROLE-t", description = "Adjon-e a felhasználónak PRIVILEGED ROLE-t? (ha nincs neki magasabb)"
    )

    val grantGroupName2 = StringSettingRef(componentSettingService, component,
        "grantGroupName2", "", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Csoportba helyezés", description = "Csoport tagság állítása (ha üres akkor nem állít)"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val grantGroup3 = ControlGroup(component, "grantGroup3", fieldName = "Fizetés utáni művelet #3")

    val forOrder3 = StringSettingRef(componentSettingService, component,
        "forOrder3", "", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Termék neve", description = "Ezzel a névvel szerepel a BME JEGY oldalon (tartalmazás, üres = ki van kapcsolva)"
    )

    val grantAttendee3 = BooleanSettingRef(componentSettingService, component,
        "grantAttendee3", false, serverSideOnly = true,
        fieldName = "Adjon-e ATTENDEE ROLE-t", description = "Adjon-e a felhasználónak ATTENDEE ROLE-t? (ha nincs neki magasabb)"
    )

    val grantPrivileged3 = BooleanSettingRef(componentSettingService, component,
        "grantPrivileged3", false, serverSideOnly = true,
        fieldName = "Adjon-e PRIVILEGED ROLE-t", description = "Adjon-e a felhasználónak PRIVILEGED ROLE-t? (ha nincs neki magasabb)"
    )

    val grantGroupName3 = StringSettingRef(componentSettingService, component,
        "grantGroupName3", "", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Csoportba helyezés", description = "Csoport tagság állítása (ha üres akkor nem állít)"
    )

}
