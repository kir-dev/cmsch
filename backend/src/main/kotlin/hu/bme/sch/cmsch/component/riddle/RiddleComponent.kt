package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.component.race.RaceCategoryEntity
import hu.bme.sch.cmsch.service.ControlPermissions
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["riddle"],
    havingValue = "true",
    matchIfMissing = false
)
class RiddleComponent(
    private val riddleModerationService: RiddleModerationService,
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "riddle",
    "/riddle",
    "Riddleök",
    ControlPermissions.PERMISSION_CONTROL_RIDDLE,
    listOf(RiddleEntity::class, RaceCategoryEntity::class, RiddleMappingEntity::class),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            riddleGroup,
            title, menuDisplayName, minRole,

            shadowBanModerationGroup,
            userShadowBanList,
            groupShadowBanList,

            banModerationGroup,
            userBanList,
            groupBanList,

            scoringGroup,
            hintScorePercent,
            saveFailedAttempts,

            answerGroup,
            ignoreCase,
            ignoreWhitespace,
            ignoreAccent,

            skipGroup,
            skipEnabled,
            skipAfterGroupsSolved,

            microserviceGroup,
            microserviceNodeBaseUrl,
            microserviceSyncEnabled,
        )
    }

    val riddleGroup = SettingProxy(componentSettingService, component,
        "riddleGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Riddleök",
        description = ""
    )

    final val title = SettingProxy(componentSettingService, component,
        "title", "Riddleök",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Riddleök", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val shadowBanModerationGroup = SettingProxy(componentSettingService, component,
        "shadowBanModerationGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Riddle beadások moderálása - Shadow Ban",
        description = "Küldjük el pihenni a \"túl aktív\" játékosokat, de csak titokban. Delikvensenként kezdj új sort, vagy válaszd el őket vesszővel!"
    )

    val userShadowBanList = SettingProxy(componentSettingService, component,
        "userShadowBanList", "", serverSideOnly = true, type = SettingType.LONG_TEXT,
        fieldName = "Tiltott játékosok listája",
        description = "Ezektől a játékosoktól nem fogadunk el megoldásokat. internalId megadásával lehet egy játékost kitiltani"
    )

    val groupShadowBanList = SettingProxy(componentSettingService, component,
        "groupShadowBanList", "", serverSideOnly = true, type = SettingType.LONG_TEXT,
        fieldName = "Tiltott csoportok listája", description = "Ezektől a csoportoktól és tagjaitól nem fogadunk el megoldásokat"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val banModerationGroup = SettingProxy(componentSettingService, component,
        "banModerationGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Riddle beadások moderálása - Ban",
        description = "Küldjük el pihenni a \"túl aktív\" játékosokat. Delikvensenként kezdj új sort, vagy válaszd el őket vesszővel!"
    )

    val userBanList = SettingProxy(componentSettingService, component,
        "userBanList", "", serverSideOnly = true, type = SettingType.LONG_TEXT,
        fieldName = "Tiltott játékosok listája",
        description = "Ezektől a játékosoktól nem fogadunk el megoldásokat. internalId megadásával lehet egy játékost kitiltani"
    )

    val groupBanList = SettingProxy(componentSettingService, component,
        "groupBanList", "", serverSideOnly = true, type = SettingType.LONG_TEXT,
        fieldName = "Tiltott csoportok listája", description = "Ezektől a csoportoktól és tagjaitól nem fogadunk el megoldásokat"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val scoringGroup = SettingProxy(componentSettingService, component,
        "scoringGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Pontozás",
        description = ""
    )

    val hintScorePercent = SettingProxy(componentSettingService, component,
        "hintScorePercent", "100", serverSideOnly = true, type = SettingType.NUMBER,
        fieldName = "Hint pont érték", description = "Ennyi százaléka lesz a hinttel megoldott riddle pont " +
                "értéke a hint nélkül megoldottnak"
    )

    val saveFailedAttempts = SettingProxy(componentSettingService, component,
        "saveFailedAttempts", "false", type = SettingType.BOOLEAN,
        fieldName = "Hibás válaszok számának mentése",
        description = "Jelentős plusz erőforrással jár ennek a használata ha sokan riddleöznek"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val answerGroup = SettingProxy(componentSettingService, component,
        "answerGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Válaszok ellenőrzése",
        description = "A transzformációt a beküldött és a riddleben található megoldásra is futtatjuk," +
                " tehát nem kell pl. ékezetek nélkülire átírni a megoldásokat."
    )

    val ignoreCase = SettingProxy(componentSettingService, component,
        "ignoreCase", "true", type = SettingType.BOOLEAN,
        fieldName = "Kis/nagy betű ignorálása",
        description = "A válaszoknál nem számít a kis- és nagybetű"
    )

    val ignoreWhitespace = SettingProxy(componentSettingService, component,
        "ignoreWhitespace", "false", type = SettingType.BOOLEAN,
        fieldName = "Elválasztás ignorálása",
        description = "A válaszoknál nem számít a szavak elválasztása (szóköz, kötőjel, &, +, vessző)"
    )

    val ignoreAccent = SettingProxy(componentSettingService, component,
        "ignoreAccent", "false", type = SettingType.BOOLEAN,
        fieldName = "Ékezetek ignorálása",
        description = "A válaszoknál nem számítanak az ékezetek (áéíóöőúüű)"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val skipGroup = SettingProxy(componentSettingService, component,
        "skipGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Átugrás funkció",
        description = "Bizonyos megoldó létszám fölött átugorható a riddle"
    )

    val skipEnabled = SettingProxy(componentSettingService, component,
        "skipEnabled", "true", type = SettingType.BOOLEAN,
        fieldName = "Átugrás bekapcsolva",
        description = "A riddle átugrás gomb elérhető"
    )

    val skipAfterGroupsSolved = SettingProxy(componentSettingService, component,
        "skipAfterGroupsSolved", "20", type = SettingType.NUMBER,
        fieldName = "Átugrás ennyi megoldó után",
        description = "Ennyi csapat vagy felhasználó megoldása után elérhető a gomb"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val microserviceGroup = SettingProxy(componentSettingService, component,
        "microserviceGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Riddle microservice",
        description = "A riddle megoldások kiszervezhetőek egy külön microservicebe"
    )

    val microserviceNodeBaseUrl = SettingProxy(componentSettingService, component,
        "microserviceNodeBaseUrl", "http://<pod>.<namespace>.svc.cluster.local",
        serverSideOnly = true, type = SettingType.TEXT,
        fieldName = "Riddle node base URL-je",
        description = "Ezen a címen érhető el clusteren belül a riddle node. Ez a formátum: http://<pod>.<namespace>.svc.cluster.local"
    )

    val microserviceSyncEnabled = SettingProxy(componentSettingService, component,
        "microserviceSyncEnabled", "false", type = SettingType.BOOLEAN,
        fieldName = "Beállítások szinkronizációja",
        description = "Ha egy riddle módosul akkor küld például értesítést a nodenak, hogy invalidálja a cachet (nincs implementálva)"
    )

    override fun onPersist() {
        super.onPersist()
        updateBanLists()
    }

    override fun onInit() {
        super.onInit()
        updateBanLists()
    }

    fun updateBanLists() {
        riddleModerationService.setGroupBans(groupBanList.getValue())
        riddleModerationService.setGroupShadowBans(groupShadowBanList.getValue())
        riddleModerationService.setUserBans(userBanList.getValue())
        riddleModerationService.setUserShadowBans(userShadowBanList.getValue())
    }
}
