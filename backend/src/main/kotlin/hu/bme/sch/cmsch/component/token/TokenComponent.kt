package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.service.ControlPermissions
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
) : ComponentBase(
    "token",
    "/token",
    "Tokenek",
    ControlPermissions.PERMISSION_CONTROL_TOKEN,
    listOf(TokenEntity::class, TokenPropertyEntity::class),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            tokenGroup,
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
            defaultTestTokenIcon,

            reportGroup,
            reportTitle,
            reportDescription,
            reportLogo,
            reportFooterText
        )
    }

    val tokenGroup = SettingProxy(componentSettingService, component,
        "tokenGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Tokenek",
        description = ""
    )

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
        description = "Hogy jelenjenek meg a tokenek"
    )

    val showCollector = SettingProxy(componentSettingService, component,
        "showCollector", "false", serverSideOnly = true, type = SettingType.BOOLEAN,
        fieldName = "Megszerző neve látszik", description = "Ha a csoportos gyűjtés be van kapcsolva akkor kiírja-e, " +
                "Látszódjon-e hogy ki szerezte meg a tokent?"
    )

    val defaultTokenIcon = SettingProxy(componentSettingService, component,
        "defaultIcon", "stamp", type = SettingType.TEXT,
        fieldName = "Alapértelmezett ikon", description = "Azoknak a tokeneknek ahova nincs egyedi megadva"
    )

    val defaultTestTokenIcon = SettingProxy(componentSettingService, component,
        "defaultTestIcon", "rocket", type = SettingType.TEXT,
        fieldName = "Alapértelmezett test ikon", description = "Azoknak a teszt tokeneknek ahova nincs egyedi megadva (0-100 egész szám)"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val reportGroup = SettingProxy(componentSettingService, component,
        "styleGroup", "", type = SettingType.COMPONENT_GROUP, persist = false, serverSideOnly = true,
        fieldName = "Jelenléti ív",
        description = "Jelenléti ív beállítások"
    )

    final val reportTitle = SettingProxy(componentSettingService, component,
        "reportTitle", "GÓLYAKÖRTE 2025",
        fieldName = "Jelenléti ív címe", description = "Ez lesz a jelenléti ív címe"
    )

    final val reportDescription = SettingProxy(componentSettingService, component,
        "reportDescription", "A hallgatók a rendezvény alatt ellátogathattak a Schönherz öntevékeny köreinek " +
                "standjaihoz, ahol miután megismerkedtek az adott körrel, beolvashattak egy-egy QR kódot. Ezáltal digitális " +
                "pecséteket szerezhettek.  A rendezvényen a hallgatók megoldhattak logikai és kreatív feladatokat is.",
        fieldName = "Jelenléti ív leírás", description = "Jelenléti ív leírás a cím alatti bekezdésben", type = SettingType.LONG_TEXT
    )

    final val reportLogo = SettingProxy(componentSettingService, component,
        "reportLogo", "/cdn/manifest/report-logo.png",
        fieldName = "Jelenléti ív logója", description = "Ez lesz a jelenléti íven megjelenített logó (csak PNG és JPG jó)",
        type = SettingType.IMAGE,
    )

    final val reportFooterText = SettingProxy(componentSettingService, component,
        "reportFooterText", "Az exportot a Kir-Dev generálta a résztvevők hozzájárulásával!",
        fieldName = "Jelenléti footer szöveg", description = "Ez lesz a jelenléti ív footerében"
    )

}
