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
    componentSettingService,
    "accessKeys",
    "/access-key",
    "Hozzáférési kulcsok",
    ControlPermissions.PERMISSION_CONTROL_ACCESS_KEYS,
    listOf(AccessKeyEntity::class),
    env
) {

    val menuGroup by SettingGroup(fieldName = "Menü megjelenése")

    final var title by StringSettingRef("Azonosítás",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában")

    final override var menuDisplayName by StringSettingRef("Azonosítás", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek")

    final override var minRole by MinRoleSettingRef(setOf(), fieldName = "Jogosultságok",
        description = "Melyik roleokkal nyitható meg az oldal")

    /// -------------------------------------------------------------------------------------------------------------------

    val appearanceGroup by SettingGroup(fieldName = "Megjelenés")

    var disabledErrorMessage by StringSettingRef("Jelenleg nem lehet kódot beváltani",
        fieldName = "Kikapcsolt hibaüzenet", description = "Ez jelenik meg ha ki van kapcsolva", serverSideOnly = true)

    var invalidCodeErrorMessage by StringSettingRef("Nem megfelelő kód", fieldName = "Hibás kód hibaüzenet",
        description = "Ez jelenik meg ha hibás kódot írnak be", serverSideOnly = true)

    var alreadyUsedErrorMessage by StringSettingRef("Ez a kód már be lett váltva",
        fieldName = "Kód be lett váltva hibaüzenet", description = "Ez jelenik meg ha ez a kód már be lett váltva",
        serverSideOnly = true)

    var mustLogInErrorMessage by StringSettingRef("Nem vagy bejelentkezve",
        fieldName = "Nem lett bejelentkezve hibaüzenet", description = "Ez jelenik meg ha ez a nincs bejelentkezve",
        serverSideOnly = true)

    var youUsedErrorMessage by StringSettingRef("Te már használtál fel kódot",
        fieldName = "Te már használtál fel hibaüzenet",
        description = "Ez jelenik meg ha a beváltó már használt fel kódot", serverSideOnly = true)

    var topMessage by StringSettingRef(type = SettingType.LONG_TEXT_MARKDOWN, serverSideOnly = true,
        fieldName = "Lapon megjelenő szöveg", description = "A kezdőlapon megjelenő szöveg. Ha üres akkor nincs ilyen.")

    var fieldName by StringSettingRef("Kód", fieldName = "Mező neve",
        description = "Ez jelenik meg a beviteli mező felett", serverSideOnly = true)

    /// -------------------------------------------------------------------------------------------------------------------

    val logicGroup by SettingGroup(fieldName = "Működés")

    var enabled by BooleanSettingRef(serverSideOnly = true, fieldName = "Lehet beváltani",
        description = "Ha ez be van kapcsolva, akkor lehetséges csak beváltani kulcsokat"
    )

    var canOneUserUseMultiple by BooleanSettingRef(fieldName = "Egy felhasználó többet is beválthat",
        serverSideOnly = true,
        description = "Ha ez be van kapcsolva, akkor több kódot is beválthat egy felhasználó, de a szabályok felülírhatják az előző hatását.")

}
