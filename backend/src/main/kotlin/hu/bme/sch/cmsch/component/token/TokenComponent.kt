package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.component.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["token"],
    havingValue = "true",
    matchIfMissing = false
)
class TokenComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase("token", "/token", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            title, menuDisplayName, minRole,

            collectTokensGroup,
            collectFeatureEnabled,
            collectRequiredTokens,
            collectRequiredType,
            minTokenNotEnoughMessage,
            minTokenDoneMessage,

            styleGroup,
            showCollector,
            defaultTokenIcon,
            defaultTestTokenIcon
        )
    }

    final val title = SettingProxy(componentSettingService, component,
        "title", "QR kódok",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "QR kódok", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val collectTokensGroup = SettingProxy(componentSettingService, component,
        "collectTokensGroup", "", persist = false, serverSideOnly = true, type = SettingType.COMPONENT_GROUP,
        fieldName = "Pecsét gyűjtés",
        description = "Csak akkor írd át ha tudod mit csinálsz! Ha elrontod nem fog beengedni, szóval óvatosan!"
    )

    val collectFeatureEnabled = SettingProxy(componentSettingService, component,
        "collectFeature", "false", serverSideOnly = false, type = SettingType.BOOLEAN,
        fieldName = "Pecsét gyűjtés aktív", description = "Bizonyos mennyiségű pecsét összegyűjtésének " +
                "sikeressége külön kijelezve"
    )

    val collectRequiredTokens = SettingProxy(componentSettingService, component,
        "collectRequired", "20", type = SettingType.NUMBER,
        fieldName = "Szükséges pecsét", description = "Ha min. ennyi pecséttel rendelkezik akkor megvan a státusz"
    )

    val collectRequiredType = SettingProxy(componentSettingService, component,
        "collectType", "default", serverSideOnly = false, type = SettingType.TEXT,
        fieldName = "Pecsét token típusa", description = "Ebből a fajából kell összegyűjteni az n darabot; Ha '*' akkor bármilyen típust elfogad."
    )

    val minTokenNotEnoughMessage = SettingProxy(componentSettingService, component,
        "minTokenMsg", "Még {} darab kell a tanköri jelenléthez", type = SettingType.LONG_TEXT,
        fieldName = "'Nincs elég' üzenet", description = "Az üzenet ha még nincs elérve a cél, {} = a szám amennyi kell még"
    )

    val minTokenDoneMessage = SettingProxy(componentSettingService, component,
        "minTokenAchievedMsg", "Megvan a tanköri jelenlét", type = SettingType.LONG_TEXT,
        fieldName = "'Már van elég' üzenet", description = "Ha üres, nem látszik"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val styleGroup = SettingProxy(componentSettingService, component,
        "styleGroup", "", type = SettingType.COMPONENT_GROUP, persist = false, serverSideOnly = true,
        fieldName = "Stílus",
        description = "Hogy jelenlegenek meg a tokenek"
    )

    val showCollector = SettingProxy(componentSettingService, component,
        "showCollector", "false", serverSideOnly = true, type = SettingType.BOOLEAN,
        fieldName = "Megszerző neve látszik", description = "Ha a csoportos gyűjtés be van kapcsolva akkor kiírja-e, " +
                "Látszódjön-e hogy ki szerezte meg a tokent?"
    )

    val defaultTokenIcon = SettingProxy(componentSettingService, component,
        "defaultIcon", "stamp", type = SettingType.TEXT,
        fieldName = "Alapértelmezett ikon", description = "Azoknak a tokeneknek ahova nincs egyedi megadva"
    )

    val defaultTestTokenIcon = SettingProxy(componentSettingService, component,
        "defaultTestIcon", "rocket", type = SettingType.TEXT,
        fieldName = "Alapértelmezett test ikon", description = "Azoknak a teszt tokeneknek ahova nincs egyedi megadva (0-100 egész szám)"
    )

}
