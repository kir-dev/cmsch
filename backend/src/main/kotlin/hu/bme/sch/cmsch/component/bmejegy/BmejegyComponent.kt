package hu.bme.sch.cmsch.component.bmejegy

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
    name = ["bmejegy"],
    havingValue = "true",
    matchIfMissing = false
)
class BmejegyComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "bmejegy",
    "/",
    "BME jegy",
    ControlPermissions.PERMISSION_CONTROL_BMEJEGY,
    listOf(BmejegyRecordEntity::class),
    env
) {

    final override val menuDisplayName = null

    final override var minRole by MinRoleSettingRef(setOf(), minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val logicGroup by SettingGroup(fieldName = "Működés")

    var syncEnabled by BooleanSettingRef(false, fieldName = "Szinkronizáció",
        description = "Ha be van kapcsolva, akkor automatikusan szinkronizál a BME JEGY-ről"
    )

    var syncInterval by NumberSettingRef(10, serverSideOnly = true, strictConversion = false,
        fieldName = "Frissítési idő", description = "Ennyi időnként (perc) frissít az oldalról"
    )

    var bufferSize by NumberSettingRef(524288, serverSideOnly = true, strictConversion = false,
        fieldName = "Buffer méret",
        description = "[ADVANCED] Az API válasz mérete. Alapból 262144, de ez 300 entryig elég csak (kb)."
    )

    var completeByNeptun by BooleanSettingRef(false, serverSideOnly = true, fieldName = "Keresés NEPTUN alapján",
        description = "Neptun alapján keresi a fizetett jegyeket (NINCS IMPLEMENTÁLVA)"
    )

    var completeByEmail by BooleanSettingRef(false, serverSideOnly = true, fieldName = "Keresés EMAIL alapján",
        description = "Email alapján keresi a fizetett jegyeket (NINCS IMPLEMENTÁLVA)"
    )

    var completeByPhotoId by BooleanSettingRef(false, serverSideOnly = true, fieldName = "NEM TÁMOGATOTT | Keresés SZIGSZÁM alapján",
        description = "Szigszám alapján keresi a fizetett jegyeket (ellenőrizni kell, hogy jó-e a formátum)"
    )

    var minTimestamp by NumberSettingRef(1689858001000, serverSideOnly = true,
        fieldName = "NEM TÁMOGATOTT | Ekkortól nézve", description = "Unix timestamp (ms pontossággal)"
    )

    var countToFetch by NumberSettingRef(10000, serverSideOnly = true,
        fieldName = "NEM TÁMOGATOTT | Ennyit töltsön le", description = "Az első ennyi darabot syncelje fel"
    )

    var emailFieldName by StringSettingRef("email", serverSideOnly = true,
        fieldName = "Email mező neve", description = "Hozzárendeléshez használatos (a formban ez a neve)"
    )

    var szigFieldName by StringSettingRef("szig", serverSideOnly = true,
        fieldName = "NEM TÁMOGATOTT | Szig. szám mező neve", description = "Hozzárendeléshez használatos"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val grantGroup1 by SettingGroup(fieldName = "Fizetés utáni művelet #1")

    var forOrder1 by StringSettingRef("", serverSideOnly = true, fieldName = "Termék neve",
        description = "Ezzel a névvel szerepel a BME JEGY oldalon (tartalmazás, üres = ki van kapcsolva)"
    )

    var grantAttendee1 by BooleanSettingRef(false, serverSideOnly = true, fieldName = "Adjon-e ATTENDEE ROLE-t",
        description = "Adjon-e a felhasználónak ATTENDEE ROLE-t? (ha nincs neki magasabb)"
    )

    var grantPrivileged1 by BooleanSettingRef(false, serverSideOnly = true, fieldName = "Adjon-e PRIVILEGED ROLE-t",
        description = "Adjon-e a felhasználónak PRIVILEGED ROLE-t? (ha nincs neki magasabb)"
    )

    var grantGroupName1 by StringSettingRef("", serverSideOnly = true, fieldName = "Csoportba helyezés",
        description = "Csoport tagság állítása (ha üres akkor nem állít)"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val grantGroup2 by SettingGroup(fieldName = "Fizetés utáni művelet #2")

    var forOrder2 by StringSettingRef("", serverSideOnly = true, fieldName = "Termék neve",
        description = "Ezzel a névvel szerepel a BME JEGY oldalon (tartalmazás, üres = ki van kapcsolva)"
    )

    var grantAttendee2 by BooleanSettingRef(false, serverSideOnly = true, fieldName = "Adjon-e ATTENDEE ROLE-t",
        description = "Adjon-e a felhasználónak ATTENDEE ROLE-t? (ha nincs neki magasabb)"
    )

    var grantPrivileged2 by BooleanSettingRef(false, serverSideOnly = true, fieldName = "Adjon-e PRIVILEGED ROLE-t",
        description = "Adjon-e a felhasználónak PRIVILEGED ROLE-t? (ha nincs neki magasabb)"
    )

    var grantGroupName2 by StringSettingRef("", serverSideOnly = true, fieldName = "Csoportba helyezés",
        description = "Csoport tagság állítása (ha üres akkor nem állít)"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val grantGroup3 by SettingGroup(fieldName = "Fizetés utáni művelet #3")

    var forOrder3 by StringSettingRef("", serverSideOnly = true, fieldName = "Termék neve",
        description = "Ezzel a névvel szerepel a BME JEGY oldalon (tartalmazás, üres = ki van kapcsolva)"
    )

    var grantAttendee3 by BooleanSettingRef(false, serverSideOnly = true, fieldName = "Adjon-e ATTENDEE ROLE-t",
        description = "Adjon-e a felhasználónak ATTENDEE ROLE-t? (ha nincs neki magasabb)"
    )

    var grantPrivileged3 by BooleanSettingRef(false, serverSideOnly = true, fieldName = "Adjon-e PRIVILEGED ROLE-t",
        description = "Adjon-e a felhasználónak PRIVILEGED ROLE-t? (ha nincs neki magasabb)"
    )

    var grantGroupName3 by StringSettingRef("", serverSideOnly = true, fieldName = "Csoportba helyezés",
        description = "Csoport tagság állítása (ha üres akkor nem állít)"
    )

}
