package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.race.RaceCategoryEntity
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
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
    env
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

    val riddleGroup = ControlGroup(component, "riddleGroup", fieldName = "Riddleök")

    final val title = StringSettingRef(componentSettingService, component,
        "title", "Riddleök", fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = StringSettingRef(componentSettingService, component,
        "menuDisplayName", "Riddleök", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val shadowBanModerationGroup = ControlGroup(component, "shadowBanModerationGroup",
        fieldName = "Riddle beadások moderálása - Shadow Ban",
        description = "Küldjük el pihenni a \"túl aktív\" játékosokat, de csak titokban. Delikvensenként kezdj új sort, vagy válaszd el őket vesszővel!"
    )

    val userShadowBanList = StringSettingRef(componentSettingService, component,
        "userShadowBanList", "", serverSideOnly = true, type = SettingType.LONG_TEXT,
        fieldName = "Tiltott játékosok listája",
        description = "Ezektől a játékosoktól nem fogadunk el megoldásokat. internalId megadásával lehet egy játékost kitiltani"
    )

    val groupShadowBanList = StringSettingRef(componentSettingService, component,
        "groupShadowBanList", "", serverSideOnly = true, type = SettingType.LONG_TEXT,
        fieldName = "Tiltott csoportok listája",
        description = "Ezektől a csoportoktól és tagjaitól nem fogadunk el megoldásokat"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val banModerationGroup = ControlGroup(component, "banModerationGroup",
        fieldName = "Riddle beadások moderálása - Ban",
        description = "Küldjük el pihenni a \"túl aktív\" játékosokat. Delikvensenként kezdj új sort, vagy válaszd el őket vesszővel!"
    )

    val userBanList = StringSettingRef(componentSettingService, component,
        "userBanList", "", serverSideOnly = true, type = SettingType.LONG_TEXT,
        fieldName = "Tiltott játékosok listája",
        description = "Ezektől a játékosoktól nem fogadunk el megoldásokat. internalId megadásával lehet egy játékost kitiltani"
    )

    val groupBanList = StringSettingRef(componentSettingService, component,
        "groupBanList", "", serverSideOnly = true, type = SettingType.LONG_TEXT,
        fieldName = "Tiltott csoportok listája",
        description = "Ezektől a csoportoktól és tagjaitól nem fogadunk el megoldásokat"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val scoringGroup = ControlGroup(component, "scoringGroup", fieldName = "Pontozás")

    val hintScorePercent = NumberSettingRef(componentSettingService, component,
        "hintScorePercent", 100, serverSideOnly = true, fieldName = "Hint pont érték", strictConversion = false,
        description = "Ennyi százaléka lesz a hinttel megoldott riddle pont értéke a hint nélkül megoldottnak"
    )

    val saveFailedAttempts = BooleanSettingRef(componentSettingService, component,
        "saveFailedAttempts", false, fieldName = "Hibás válaszok számának mentése",
        description = "Jelentős plusz erőforrással jár ennek a használata ha sokan riddleöznek"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val answerGroup = ControlGroup(component, "answerGroup", fieldName = "Válaszok ellenőrzése",
        description = "A transzformációt a beküldött és a riddleben található megoldásra is futtatjuk," +
                " tehát nem kell pl. ékezetek nélkülire átírni a megoldásokat."
    )

    val ignoreCase = BooleanSettingRef(componentSettingService, component,
        "ignoreCase", true, fieldName = "Kis/nagy betű ignorálása",
        description = "A válaszoknál nem számít a kis- és nagybetű"
    )

    val ignoreWhitespace = BooleanSettingRef(componentSettingService, component,
        "ignoreWhitespace", false, fieldName = "Elválasztás ignorálása",
        description = "A válaszoknál nem számít a szavak elválasztása (szóköz, kötőjel, &, +, vessző)"
    )

    val ignoreAccent = BooleanSettingRef(componentSettingService, component,
        "ignoreAccent", false, fieldName = "Ékezetek ignorálása",
        description = "A válaszoknál nem számítanak az ékezetek (áéíóöőúüű)"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val skipGroup = ControlGroup(component, "skipGroup", fieldName = "Átugrás funkció",
        description = "Bizonyos megoldó létszám fölött átugorható a riddle"
    )

    val skipEnabled = BooleanSettingRef(componentSettingService, component,
        "skipEnabled", true, fieldName = "Átugrás bekapcsolva",
        description = "A riddle átugrás gomb elérhető"
    )

    val skipAfterGroupsSolved = NumberSettingRef(componentSettingService, component,
        "skipAfterGroupsSolved", 20, fieldName = "Átugrás ennyi megoldó után", strictConversion = false,
        description = "Ennyi csapat vagy felhasználó megoldása után elérhető a gomb"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val microserviceGroup = ControlGroup(component, "microserviceGroup", fieldName = "Riddle microservice",
        description = "A riddle megoldások kiszervezhetőek egy külön microservicebe"
    )

    val microserviceNodeBaseUrl = StringSettingRef(componentSettingService, component,
        "microserviceNodeBaseUrl", "http://<pod>.<namespace>.svc.cluster.local",
        serverSideOnly = true, type = SettingType.URL,
        fieldName = "Riddle node base URL-je",
        description = "Ezen a címen érhető el clusteren belül a riddle node. Ez a formátum: http://<pod>.<namespace>.svc.cluster.local"
    )

    val microserviceSyncEnabled = BooleanSettingRef(componentSettingService, component,
        "microserviceSyncEnabled", false, fieldName = "Beállítások szinkronizációja",
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
