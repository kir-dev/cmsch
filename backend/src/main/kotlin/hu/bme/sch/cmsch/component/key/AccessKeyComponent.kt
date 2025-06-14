package hu.bme.sch.cmsch.component.key

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["accessKeys"],
    havingValue = "true",
    matchIfMissing = false
)
class AccessKeyComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "accessKeys",
    "/access-key",
    "Hozzáférési kulcsok",
    ControlPermissions.PERMISSION_CONTROL_ACCESS_KEYS,
    listOf(AccessKeyEntity::class),
    env
) {

    final override val allSettings by lazy {
        listOf(
            menuGroup,
            title, menuDisplayName, minRole,

            appearanceGroup,
            disabledErrorMessage,
            invalidCodeErrorMessage,
            alreadyUsedErrorMessage,
            mustLogInErrorMessage,
            youUsedErrorMessage,
            topMessage,
            fieldName,

            logicGroup,
            enabled,
            canOneUserUseMultiple,
        )
    }

    val menuGroup = SettingGroup(component, "menuGroup", fieldName = "Menü megjelenése")

    final val title = StringSettingRef(componentSettingService, component,
        "title", "Azonosítás", fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = StringSettingRef(componentSettingService, component,
        "menuDisplayName", "Azonosítás", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val appearanceGroup = SettingGroup(component, "appearanceGroup", fieldName = "Megjelenés")

    val disabledErrorMessage = StringSettingRef(
        componentSettingService, component,
        "disabledErrorMessage", "Jelenleg nem lehet kódot beváltani",
        fieldName = "Kikapcsolt hibaüzenet", description = "Ez jelenik meg ha ki van kapcsolva",
        serverSideOnly = true,
    )

    val invalidCodeErrorMessage = StringSettingRef(
        componentSettingService, component,
        "invalidCodeErrorMessage", "Nem megfelelő kód",
        fieldName = "Hibás kód hibaüzenet", description = "Ez jelenik meg ha hibás kódot írnak be",
        serverSideOnly = true,
    )

    val alreadyUsedErrorMessage = StringSettingRef(
        componentSettingService, component,
        "alreadyUsedErrorMessage", "Ez a kód már be lett váltva",
        fieldName = "Kód be lett váltva hibaüzenet",
        description = "Ez jelenik meg ha ez a kód már be lett váltva",
        serverSideOnly = true,
    )

    val mustLogInErrorMessage = StringSettingRef(
        componentSettingService, component,
        "mustLogInErrorMessage", "Nem vagy bejelentkezve",
        fieldName = "Nem lett bejelentkezve hibaüzenet",
        description = "Ez jelenik meg ha ez a nincs bejelentkezve",
        serverSideOnly = true,
    )

    val youUsedErrorMessage = StringSettingRef(
        componentSettingService, component,
        "youUsedErrorMessage", "Te már használtál fel kódot",
        fieldName = "Te már használtál fel hibaüzenet",
        description = "Ez jelenik meg ha a beváltó már használt fel kódot",
        serverSideOnly = true,
    )

    val topMessage = StringSettingRef(componentSettingService, component,
        "topMessage", "",
        type = SettingType.LONG_TEXT_MARKDOWN, serverSideOnly = true,
        fieldName = "Lapon megjelenő szöveg", description = "A kezdőlapon megjelenő szöveg. Ha üres akkor nincs ilyen."
    )

    val fieldName = StringSettingRef(
        componentSettingService, component,
        "fieldName", "Kód",
        fieldName = "Mező neve", description = "Ez jelenik meg a beviteli mező felett",
        serverSideOnly = true,
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val logicGroup = SettingGroup(component, "logicGroup", fieldName = "Működés")

    val enabled = BooleanSettingRef(componentSettingService, component,
        "enabled", false, serverSideOnly = true, fieldName = "Lehet beváltani",
        description = "Ha ez be van kapcsolva, akkor lehetséges csak beváltani kulcsokat"
    )

    val canOneUserUseMultiple = BooleanSettingRef(componentSettingService, component,
        "canOneUserUseMultiple", false, fieldName = "Egy felhasználó többet is beválthat", serverSideOnly = true,
        description = "Ha ez be van kapcsolva, akkor több kódot is beválthat egy felhasználó, de a szabályok felülírhatják az előző hatását."
    )

}
