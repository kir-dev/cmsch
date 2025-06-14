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
    "impressum",
    "/impressum",
    "Impresszum",
    ControlPermissions.PERMISSION_CONTROL_IMPRESSUM,
    listOf(),
    env
) {

    final override val allSettings by lazy {
        listOf(
            impressumGroup,
            title, minRole,

            topMessage,

            developersGroup,
            developerSchamiUrl,
            developerBalintUrl,
            developerLaciUrl,
            developerBeniUrl,
            developerTriszUrl,
            developerSamuUrl,
            developerDaniUrl,
            developerMateUrl,
            developersBottomMessage,

            organizerGroup,
            leadOrganizers,
            leadOrganizersMessage,
            otherOrganizers,
            otherOrganizersMessage
        )
    }

    val impressumGroup = SettingGroup(component, "impressumGroup", fieldName = "Impresszum")

    final val title = StringSettingRef(componentSettingService, component,
        "title", "Impressum", fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", MinRoleSettingRef.ALL_ROLES,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    val topMessage = StringSettingRef(componentSettingService, component,
        "topMessage", "A weblapot a *CMSCH - általános rendezvény webapp* projekt keretein belül a " +
                "[kir-dev](https://kir-dev.sch.bme.hu/) *speed-run munkacsoportja* készítette.",
        type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Oldal tetején megjelenő szöveg", description = "Ha üres akkor nincs ilyen"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val developersGroup = SettingGroup(component, "developersGroup", fieldName = "Fejlesztők",
        description = "A fejlesztők nem szerkeszthetőek a webes felületről, csak a profilképük")

    val developerSchamiUrl = StringSettingRef(componentSettingService, component,
        "developerSchamiUrl", "https://warp.sch.bme.hu/kir-dev/profiles/schami.png", type = SettingType.URL,
        fieldName = "Schámi profilképe"
    )

    val developerBalintUrl = StringSettingRef(componentSettingService, component,
        "developerBalintUrl", "https://warp.sch.bme.hu/kir-dev/profiles/balint.png", type = SettingType.URL,
        fieldName = "Bálint profilképe"
    )

    val developerLaciUrl = StringSettingRef(componentSettingService, component,
        "developerLaciUrl", "https://warp.sch.bme.hu/kir-dev/profiles/laci.png", type = SettingType.URL,
        fieldName = "Laci profilképe"
    )

    val developerBeniUrl = StringSettingRef(componentSettingService, component,
        "developerBeniUrl", "https://warp.sch.bme.hu/kir-dev/profiles/beni.png", type = SettingType.URL,
        fieldName = "Beni profilképe"
    )

    val developerTriszUrl = StringSettingRef(componentSettingService, component,
        "developerTriszUrl", "https://warp.sch.bme.hu/kir-dev/profiles/trisz.png", type = SettingType.URL,
        fieldName = "Trisz profilképe"
    )

    val developerSamuUrl = StringSettingRef(componentSettingService, component,
        "developerSamuUrl", "https://warp.sch.bme.hu/kir-dev/profiles/samu.png", type = SettingType.URL,
        fieldName = "Samu profilképe"
    )

    val developerDaniUrl = StringSettingRef(componentSettingService, component,
        "developerDaniUrl", "https://warp.sch.bme.hu/kir-dev/profiles/dani.png", type = SettingType.URL,
        fieldName = "Dani profilképe"
    )

    val developerMateUrl = StringSettingRef(componentSettingService, component,
        "developerMateUrl", "https://warp.sch.bme.hu/kir-dev/profiles/mate.png", type = SettingType.URL,
        fieldName = "Máté profilképe"
    )

    val developersBottomMessage = StringSettingRef(componentSettingService, component,
        "developersBottomMessage",
        "Felhasznált technológiák: Kotlin, Spring-boot, Typescript és React. " +
                "Mint ahogy az összes többi projektünk, ez is [nyílt forráskódú](https://github.com/kir-dev/cmsch). " +
                "Ha kérdésed van vagy érdekel a többi projektünk is, látogass el az [oldalunkra](https://kir-dev.sch.bme.hu/) " +
                "vagy keress fel minket személyesen!\n\n" +
                "Az alkalmazás a KSZK Kubernetes clusterjében fut, köszönjük az erőforrást és a segítséget nekik ezúton is!",
        type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "A fejlesztők alatt megjelenő szöveg", description = "Ha üres akkor nincs ilyen"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val organizerGroup = SettingGroup(component, "organizerGroup", fieldName = "Rendezők",
        description = "A rendezvény főrendezőit és egyéb rendezőit is meg lehet adni"
    )

    val leadOrganizers = JsonSettingRef(componentSettingService, component,
        "leadOrganizers", listOf(), fieldName = "A főrendezők"
    )

    val leadOrganizersMessage = StringSettingRef(componentSettingService, component,
        "leadOrganizersMessage",
        "A rendezvénnyel kapcsolatos kérdéseket és észrevételeket szívesen fogadjuk a **TODO [@] sch.bme.hu** címen!",
        type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "A főrendezők alatt megjelenő szöveg", description = "Ha üres akkor nincs ilyen"
    )

    val otherOrganizers = JsonSettingRef(componentSettingService, component,
        "otherOrganizers", listOf(), fieldName = "További rendezők"
    )

    val otherOrganizersMessage = StringSettingRef(componentSettingService, component,
        "otherOrganizersMessage", "És még további N rendező!", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "A további rendezők alatt megjelenő szöveg", description = "Ha üres akkor nincs ilyen"
    )
}
