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
    componentSettingService,
    "riddle",
    "/riddle",
    "Riddleök",
    ControlPermissions.PERMISSION_CONTROL_RIDDLE,
    listOf(RiddleEntity::class, RaceCategoryEntity::class, RiddleMappingEntity::class),
    env
) {

    val riddleGroup by SettingGroup(fieldName = "Riddleök")

    final var title by StringSettingRef("Riddleök",
        fieldName = "Lap címe",
        description = "Ez jelenik meg a böngésző címsorában"
    )

    final override var menuDisplayName by StringSettingRef("Riddleök", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override var minRole by MinRoleSettingRef(setOf(),
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val shadowBanModerationGroup by SettingGroup(fieldName = "Riddle beadások moderálása - Shadow Ban",
        description = "Küldjük el pihenni a \"túl aktív\" játékosokat, de csak titokban. Delikvensenként kezdj új sort, vagy válaszd el őket vesszővel!"
    )

    var userShadowBanList by StringSettingRef("", serverSideOnly = true, type = SettingType.LONG_TEXT,
        fieldName = "Tiltott játékosok listája",
        description = "Ezektől a játékosoktól nem fogadunk el megoldásokat. internalId megadásával lehet egy játékost kitiltani"
    )

    var groupShadowBanList by StringSettingRef("", serverSideOnly = true, type = SettingType.LONG_TEXT,
        fieldName = "Tiltott csoportok listája",
        description = "Ezektől a csoportoktól és tagjaitól nem fogadunk el megoldásokat"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val banModerationGroup by SettingGroup(fieldName = "Riddle beadások moderálása - Ban",
        description = "Küldjük el pihenni a \"túl aktív\" játékosokat. Delikvensenként kezdj új sort, vagy válaszd el őket vesszővel!"
    )

    var userBanList by StringSettingRef("", serverSideOnly = true, type = SettingType.LONG_TEXT,
        fieldName = "Tiltott játékosok listája",
        description = "Ezektől a játékosoktól nem fogadunk el megoldásokat. internalId megadásával lehet egy játékost kitiltani"
    )

    var groupBanList by StringSettingRef("", serverSideOnly = true, type = SettingType.LONG_TEXT,
        fieldName = "Tiltott csoportok listája",
        description = "Ezektől a csoportoktól és tagjaitól nem fogadunk el megoldásokat"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val scoringGroup by SettingGroup(fieldName = "Pontozás")

    var hintScorePercent by NumberSettingRef(100,
        serverSideOnly = true,
        fieldName = "Hint pont érték",
        strictConversion = false,
        description = "Ennyi százaléka lesz a hinttel megoldott riddle pont értéke a hint nélkül megoldottnak"
    )

    var saveFailedAttempts by BooleanSettingRef(false, fieldName = "Hibás válaszok számának mentése",
        description = "Jelentős plusz erőforrással jár ennek a használata ha sokan riddleöznek"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val answerGroup by SettingGroup(fieldName = "Válaszok ellenőrzése",
        description = "A transzformációt a beküldött és a riddleben található megoldásra is futtatjuk," +
                " tehát nem kell pl. ékezetek nélkülire átírni a megoldásokat."
    )

    var ignoreCase by BooleanSettingRef(true, fieldName = "Kis/nagy betű ignorálása",
        description = "A válaszoknál nem számít a kis- és nagybetű"
    )

    var ignoreWhitespace by BooleanSettingRef(false, fieldName = "Elválasztás ignorálása",
        description = "A válaszoknál nem számít a szavak elválasztása (szóköz, kötőjel, &, +, vessző)"
    )

    var ignoreAccent by BooleanSettingRef(false, fieldName = "Ékezetek ignorálása",
        description = "A válaszoknál nem számítanak az ékezetek (áéíóöőúüű)"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val skipGroup by SettingGroup(fieldName = "Átugrás funkció",
        description = "Bizonyos megoldó létszám fölött átugorható a riddle"
    )

    var skipEnabled by BooleanSettingRef(true, fieldName = "Átugrás bekapcsolva",
        description = "A riddle átugrás gomb elérhető"
    )

    var skipAfterGroupsSolved by NumberSettingRef(20,
        fieldName = "Átugrás ennyi megoldó után",
        strictConversion = false,
        description = "Ennyi csapat vagy felhasználó megoldása után elérhető a gomb"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val microserviceGroup by SettingGroup(fieldName = "Riddle microservice",
        description = "A riddle megoldások kiszervezhetőek egy külön microservicebe"
    )

    var microserviceNodeBaseUrl by StringSettingRef("http://<pod>.<namespace>.svc.cluster.local",
        serverSideOnly = true, type = SettingType.URL,
        fieldName = "Riddle node base URL-je",
        description = "Ezen a címen érhető el clusteren belül a riddle node. Ez a formátum: http://<pod>.<namespace>.svc.cluster.local"
    )

    var microserviceSyncEnabled by BooleanSettingRef(false, fieldName = "Beállítások szinkronizációja",
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
        riddleModerationService.setGroupBans(groupBanList)
        riddleModerationService.setGroupShadowBans(groupShadowBanList)
        riddleModerationService.setUserBans(userBanList)
        riddleModerationService.setUserShadowBans(userShadowBanList)
    }
}
