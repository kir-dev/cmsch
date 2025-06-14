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
    componentSettingService,
    "token",
    "/token",
    "Tokenek",
    ControlPermissions.PERMISSION_CONTROL_TOKEN,
    listOf(TokenEntity::class, TokenPropertyEntity::class),
    env
) {

    val tokenGroup by SettingGroup(fieldName = "Tokenek")

    final var title by StringSettingRef("QR kódok", fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override var menuDisplayName by StringSettingRef("QR kódok", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override var minRole by MinRoleSettingRef(setOf(),
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    var qrFrontendBaseUrl by StringSettingRef("https://todo.sch.bme.hu/token/scan?token=", serverSideOnly = false,
        fieldName = "QR frontend url", description = "Ez lesz a tokenek elé generálva", type = SettingType.URL,
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val collectTokensGroup by SettingGroup(fieldName = "Pecsét gyűjtés",
        description = "Csak akkor írd át ha tudod mit csinálsz! Ha elrontod nem fog beengedni, szóval óvatosan!"
    )

    var collectFeatureEnabled by BooleanSettingRef(false, serverSideOnly = false, fieldName = "Pecsét gyűjtés aktív",
        description = "Bizonyos mennyiségű pecsét összegyűjtésének sikeressége külön kijelezve"
    )

    var collectRequiredTokens by NumberSettingRef(20, fieldName = "Szükséges pecsét", strictConversion = false,
        description = "Ha min. ennyi pecséttel rendelkezik akkor megvan a státusz"
    )

    var collectRequiredType by StringSettingRef("*", serverSideOnly = false, fieldName = "Pecsét token típusa",
        description = "Ebből a fajtából kell összegyűjteni az n darabot; Ha '*' akkor bármilyen típust elfogad."
    )

    var minTokenNotEnoughMessage by StringSettingRef("Még {} darab kell a tanköri jelenléthez", type = SettingType.LONG_TEXT,
        fieldName = "'Nincs elég' üzenet",
        description = "Az üzenet ha még nincs elérve a cél, {} = a szám amennyi kell még"
    )

    var minTokenDoneMessage by StringSettingRef("Megvan a tanköri jelenlét", type = SettingType.LONG_TEXT,
        fieldName = "'Már van elég' üzenet", description = "Ha üres, nem látszik"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val styleGroup by SettingGroup(fieldName = "Stílus",
        description = "Hogy jelenjenek meg a tokenek"
    )

    var showCollector by BooleanSettingRef(false, serverSideOnly = true, fieldName = "Megszerző neve látszik",
        description = "Ha a csoportos gyűjtés be van kapcsolva akkor kiírja-e, Látszódjon-e hogy ki szerezte meg a tokent?"
    )

    var defaultIcon by StringSettingRef("stamp", fieldName = "Alapértelmezett ikon",
        description = "Azoknak a tokeneknek ahova nincs egyedi megadva"
    )

    var defaultTestIcon by StringSettingRef("rocket", fieldName = "Alapértelmezett test ikon",
        description = "Azoknak a teszt tokeneknek ahova nincs egyedi megadva (0-100 egész szám)"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val reportGroup = SettingGroup(fieldName = "Jelenléti ív", description = "Jelenléti ív beállítások")

    final var reportTitle by StringSettingRef("GÓLYAKÖRTE 2025", fieldName = "Jelenléti ív címe", description = "Ez lesz a jelenléti ív címe"
    )

    final var reportSummaryTableColumns by StringSettingRef(
        "stamp,attendance,riddle,achievement",
        fieldName = "Riport összefoglaló táblázat oszlopai",
        description = "Ezek az oszlopok fognak megjelenni az összefoglaló táblázatban" +
                ", vesszővel elválasztva kell őket megadni, lehetséges értékek: stamp,attendance,riddle,achievement,time-between-scans"
    )

    final var reportDescription by StringSettingRef(
        "A hallgatók a rendezvény alatt ellátogathattak a Schönherz öntevékeny köreinek " +
                "standjaihoz, ahol miután megismerkedtek az adott körrel, beolvashattak egy-egy QR kódot. Ezáltal digitális " +
                "pecséteket szerezhettek.  A rendezvényen a hallgatók megoldhattak logikai és kreatív feladatokat is.",
        fieldName = "Jelenléti ív leírás",
        description = "Jelenléti ív leírás a cím alatti bekezdésben",
        type = SettingType.LONG_TEXT
    )

    final var reportLogo by StringSettingRef(
        "manifest/report-logo.png",
        fieldName = "Jelenléti ív logója",
        description = "Ez lesz a jelenléti íven megjelenített logó (csak PNG és JPG jó)",
        type = SettingType.IMAGE,
    )

    final var reportFooterText by StringSettingRef("Az exportot a Kir-Dev generálta a résztvevők hozzájárulásával!",
        fieldName = "Jelenléti footer szöveg", description = "Ez lesz a jelenléti ív footerében"
    )

}
