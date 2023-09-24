package hu.bme.sch.cmsch.component.key

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.service.ControlPermissions
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
    componentSettingService, env
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

    val menuGroup = SettingProxy(componentSettingService, component,
        "menuGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Menü megjelenése",
        description = ""
    )

    final val title = SettingProxy(componentSettingService, component,
        "title", "Azonosítás",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Azonosítás", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val appearanceGroup = SettingProxy(componentSettingService, component,
        "appearanceGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Megjelenés",
        description = ""
    )

    val disabledErrorMessage = SettingProxy(componentSettingService, component,
        "disabledErrorMessage", "Jelenleg nem lehet kódot beváltani",
        fieldName = "Kikapcsolt hibaüzenet", description = "Ez jelenik meg ha ki van kapcsolva",
        serverSideOnly = true,
    )

    val invalidCodeErrorMessage = SettingProxy(componentSettingService, component,
        "invalidCodeErrorMessage", "Nem megfelelő kód",
        fieldName = "Hibás kód hibaüzenet", description = "Ez jelenik meg ha hibás kódot írnak be",
        serverSideOnly = true,
    )

    val alreadyUsedErrorMessage = SettingProxy(componentSettingService, component,
        "alreadyUsedErrorMessage", "Ez a kód már be lett váltva",
        fieldName = "Kód be lett váltva hibaüzenet",
        description = "Ez jelenik meg ha ez a kód már be lett váltva",
        serverSideOnly = true,
    )

    val mustLogInErrorMessage = SettingProxy(componentSettingService, component,
        "mustLogInErrorMessage", "Nem vagy bejelentkezve",
        fieldName = "Nem lett bejelentkezve hibaüzenet",
        description = "Ez jelenik meg ha ez a nincs bejelentkezve",
        serverSideOnly = true,
    )

    val youUsedErrorMessage = SettingProxy(componentSettingService, component,
        "youUsedErrorMessage", "Te már használtál fel kódot",
        fieldName = "Te már használtál fel hibaüzenet",
        description = "Ez jelenik meg ha a beváltó már használt fel kódot",
        serverSideOnly = true,
    )

    val topMessage = SettingProxy(componentSettingService, component,
        "topMessage", "",
        type = SettingType.LONG_TEXT_MARKDOWN, serverSideOnly = true,
        fieldName = "Lapon megjelenő szöveg", description = "A kezdőlapon megjelenő szöveg. Ha üres akkor nincs ilyen."
    )

    val fieldName = SettingProxy(componentSettingService, component,
        "fieldName", "Kód",
        fieldName = "Mező neve", description = "Ez jelenik meg a beviteli mező felett",
        serverSideOnly = true,
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val logicGroup = SettingProxy(componentSettingService, component,
        "logicGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Működés",
        description = ""
    )

    val enabled = SettingProxy(componentSettingService, component,
        "enabled", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Lehet beváltani",
        description = "Ha ez be van kapcsolva, akkor lehetséges csak beváltani kulcsokat"
    )

    val canOneUserUseMultiple = SettingProxy(componentSettingService, component,
        "canOneUserUseMultiple", "false", type = SettingType.BOOLEAN,
        fieldName = "Egy felhasználó többet is beválthat", serverSideOnly = true,
        description = "Ha ez be van kapcsolva, akkor több kódot is beválthat egy felhasználó," +
                " de a szabályok felülírhatják az előző hatását."
    )

}