package hu.bme.sch.cmsch.component.impressum

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["impressum"],
    havingValue = "true",
    matchIfMissing = false
)
class ImpressumComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "impressum",
    "/impressum",
    "Impresszum",
    ControlPermissions.PERMISSION_CONTROL_IMPRESSUM,
    listOf(),
    env
) {

    val impressumGroup by SettingGroup(fieldName = "Impresszum")

    final var title by StringSettingRef("Impressum",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában")

    final override val menuDisplayName = null

    final override var minRole by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal")

    var topMessage by StringSettingRef("A weblapot a *CMSCH - általános rendezvény webapp* projekt keretein belül a " +
            "[kir-dev](https://kir-dev.sch.bme.hu/) *speed-run munkacsoportja* készítette.",
        type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Oldal tetején megjelenő szöveg", description = "Ha üres akkor nincs ilyen")

    /// -------------------------------------------------------------------------------------------------------------------

    val developersGroup by SettingGroup(fieldName = "Fejlesztők",
        description = "A fejlesztők nem szerkeszthetőek a webes felületről, csak a profilképük")

    var developerSchamiUrl by StringSettingRef("", type = SettingType.IMAGE_URL, fieldName = "Schámi profilképe")

    var developerBalintUrl by StringSettingRef("", type = SettingType.IMAGE_URL, fieldName = "Bálint profilképe")

    var developerLaciUrl by StringSettingRef("", type = SettingType.IMAGE_URL, fieldName = "Laci profilképe")

    var developerBeniUrl by StringSettingRef("", type = SettingType.IMAGE_URL, fieldName = "Beni profilképe")

    var developerTriszUrl by StringSettingRef("", type = SettingType.IMAGE_URL, fieldName = "Trisz profilképe")

    var developerSamuUrl by StringSettingRef("", type = SettingType.IMAGE_URL, fieldName = "Samu profilképe")

    var developerDaniUrl by StringSettingRef("", type = SettingType.IMAGE_URL, fieldName = "Dani profilképe")

    var developerMateUrl by StringSettingRef("", type = SettingType.IMAGE_URL, fieldName = "Máté profilképe")

    var developersBottomMessage by StringSettingRef("Felhasznált technológiák: Kotlin, Spring-boot, Typescript és React. " +
            "Mint ahogy az összes többi projektünk, ez is [nyílt forráskódú](https://github.com/kir-dev/cmsch). " +
            "Ha kérdésed van vagy érdekel a többi projektünk is, látogass el az [oldalunkra](https://kir-dev.sch.bme.hu/) " +
            "vagy keress fel minket személyesen!\n\n" +
            "Az alkalmazás a KSZK Kubernetes clusterjében fut, köszönjük az erőforrást és a segítséget nekik ezúton is!",
        type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "A fejlesztők alatt megjelenő szöveg", description = "Ha üres akkor nincs ilyen")

    /// -------------------------------------------------------------------------------------------------------------------

    val organizerGroup by SettingGroup(fieldName = "Rendezők",
        description = "A rendezvény főrendezőit és egyéb rendezőit is meg lehet adni")

    var leadOrganizers by JsonSettingRef<List<OrganizerDto>>(listOf(),
        fieldName = "A főrendezők", type = SettingType.MULTIPLE_PEOPLE)

    var leadOrganizersMessage by StringSettingRef("A rendezvénnyel kapcsolatos kérdéseket és észrevételeket szívesen fogadjuk a **TODO [@] sch.bme.hu** címen!",
        type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "A főrendezők alatt megjelenő szöveg", description = "Ha üres akkor nincs ilyen")

    var otherOrganizers by JsonSettingRef<List<OrganizerDto>>(listOf(),
        fieldName = "További rendezők", type = SettingType.MULTIPLE_PEOPLE)

    var otherOrganizersMessage by StringSettingRef("És még további N rendező!", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "A további rendezők alatt megjelenő szöveg", description = "Ha üres akkor nincs ilyen")

}
