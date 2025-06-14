package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
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
    env
) {

    final override val allSettings by lazy {
        listOf(
            tokenGroup,
            title, menuDisplayName, minRole,
            qrFrontendBaseUrl,

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
            reportSummaryTableColumns,
            reportDescription,
            reportLogo,
            reportFooterText
        )
    }

    val tokenGroup = ControlGroup(component, "tokenGroup", fieldName = "Tokenek")

    final val title = StringSettingRef(componentSettingService, component,
        "title", "QR kódok", fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = StringSettingRef(componentSettingService, component,
        "menuDisplayName", "QR kódok", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    val qrFrontendBaseUrl = StringSettingRef(
        componentSettingService, component,
        "qrFrontendBaseUrl", "https://todo.sch.bme.hu/token/scan?token=", serverSideOnly = false,
        fieldName = "QR frontend url", description = "Ez lesz a tokenek elé generálva", type = SettingType.URL,
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val collectTokensGroup = ControlGroup(component, "collectTokensGroup", fieldName = "Pecsét gyűjtés",
        description = "Csak akkor írd át ha tudod mit csinálsz! Ha elrontod nem fog beengedni, szóval óvatosan!"
    )

    val collectFeatureEnabled = BooleanSettingRef(componentSettingService, component,
        "collectFeature", false, serverSideOnly = false, fieldName = "Pecsét gyűjtés aktív",
        description = "Bizonyos mennyiségű pecsét összegyűjtésének sikeressége külön kijelezve"
    )

    val collectRequiredTokens = NumberSettingRef(componentSettingService, component,
        "collectRequired", 20, fieldName = "Szükséges pecsét", strictConversion = false,
        description = "Ha min. ennyi pecséttel rendelkezik akkor megvan a státusz"
    )

    val collectRequiredType = StringSettingRef(componentSettingService,
        component,
        "collectType",
        "*",
        serverSideOnly = false,
        type = SettingType.TEXT,
        fieldName = "Pecsét token típusa",
        description = "Ebből a fajtából kell összegyűjteni az n darabot; Ha '*' akkor bármilyen típust elfogad."
    )

    val minTokenNotEnoughMessage = StringSettingRef(componentSettingService,
        component,
        "minTokenMsg",
        "Még {} darab kell a tanköri jelenléthez",
        type = SettingType.LONG_TEXT,
        fieldName = "'Nincs elég' üzenet",
        description = "Az üzenet ha még nincs elérve a cél, {} = a szám amennyi kell még"
    )

    val minTokenDoneMessage = StringSettingRef(componentSettingService, component,
        "minTokenAchievedMsg", "Megvan a tanköri jelenlét", type = SettingType.LONG_TEXT,
        fieldName = "'Már van elég' üzenet", description = "Ha üres, nem látszik"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val styleGroup = ControlGroup(component, "styleGroup", fieldName = "Stílus",
        description = "Hogy jelenjenek meg a tokenek"
    )

    val showCollector = BooleanSettingRef(componentSettingService, component,
        "showCollector", false, serverSideOnly = true, fieldName = "Megszerző neve látszik",
        description = "Ha a csoportos gyűjtés be van kapcsolva akkor kiírja-e, Látszódjon-e hogy ki szerezte meg a tokent?"
    )

    val defaultTokenIcon = StringSettingRef(componentSettingService, component,
        "defaultIcon", "stamp", type = SettingType.TEXT,
        fieldName = "Alapértelmezett ikon", description = "Azoknak a tokeneknek ahova nincs egyedi megadva"
    )

    val defaultTestTokenIcon = StringSettingRef(componentSettingService, component,
        "defaultTestIcon", "rocket", type = SettingType.TEXT, fieldName = "Alapértelmezett test ikon",
        description = "Azoknak a teszt tokeneknek ahova nincs egyedi megadva (0-100 egész szám)"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val reportGroup =
        ControlGroup(component, "reportGroup", fieldName = "Jelenléti ív", description = "Jelenléti ív beállítások")

    final val reportTitle = StringSettingRef(componentSettingService, component,
        "reportTitle", "GÓLYAKÖRTE 2025", fieldName = "Jelenléti ív címe", description = "Ez lesz a jelenléti ív címe"
    )

    final val reportSummaryTableColumns = StringSettingRef(componentSettingService,
        component,
        "reportSummaryTableColumns",
        "stamp,attendance,riddle,achievement",
        fieldName = "Riport összefoglaló táblázat oszlopai",
        description = "Ezek az oszlopok fognak megjelenni az összefoglaló táblázatban" +
                ", vesszővel elválasztva kell őket megadni, lehetséges értékek: stamp,attendance,riddle,achievement,time-between-scans"
    )

    final val reportDescription = StringSettingRef(componentSettingService,
        component,
        "reportDescription",
        "A hallgatók a rendezvény alatt ellátogathattak a Schönherz öntevékeny köreinek " +
                "standjaihoz, ahol miután megismerkedtek az adott körrel, beolvashattak egy-egy QR kódot. Ezáltal digitális " +
                "pecséteket szerezhettek.  A rendezvényen a hallgatók megoldhattak logikai és kreatív feladatokat is.",
        fieldName = "Jelenléti ív leírás",
        description = "Jelenléti ív leírás a cím alatti bekezdésben",
        type = SettingType.LONG_TEXT
    )

    final val reportLogo = StringSettingRef(
        componentSettingService,
        component,
        "reportLogo",
        "manifest/report-logo.png",
        fieldName = "Jelenléti ív logója",
        description = "Ez lesz a jelenléti íven megjelenített logó (csak PNG és JPG jó)",
        type = SettingType.IMAGE,
    )

    final val reportFooterText = StringSettingRef(componentSettingService, component,
        "reportFooterText", "Az exportot a Kir-Dev generálta a résztvevők hozzájárulásával!",
        fieldName = "Jelenléti footer szöveg", description = "Ez lesz a jelenléti ív footerében"
    )

}
