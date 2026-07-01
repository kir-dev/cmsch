package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.race.RaceCategoryEntity
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnBooleanProperty(value = ["hu.bme.sch.cmsch.component.load.riddle"])
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

    final var title by StringSettingRef("Riddleök", fieldName = "Lap címe",
        description = "Ez jelenik meg a böngésző címsorában")

    final override var menuDisplayName by StringSettingRef("Riddleök", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek")

    final override var minRole by MinRoleSettingRef(setOf(),
        fieldName = "Jogosultságok", description = "Mely szerepkörökkel nyitható meg az oldal")

    var visibleRiddlesPerCategory by NumberSettingRef(1, type = SettingType.NUMBER, fieldName = "Egyidőben mutatott riddle-ök száma",
        description = "Kategóriánként ennyi riddle jelenik meg egyszerre, amelyek közül a felhasználó választhat")

    /// -------------------------------------------------------------------------------------------------------------------

    val shadowBanModerationGroup by SettingGroup(fieldName = "Riddle beadások moderálása - Shadow Ban",
        description = "A megadott felhasználók vagy csoportok megoldásait a rendszer nem fogadja el, de erről nem kapnak értesítést. Felhasználónként vagy csoportonként kezdjen új sort, vagy használjon vesszőt elválasztóként.")

    var userShadowBanList by StringSettingRef(fieldName = "Tiltott játékosok listája",
        type = SettingType.LONG_TEXT, serverSideOnly = true,
        description = "A tiltott játékosok CMSCH ID-jai. Ezen játékosoktól a rendszer nem fogad el megoldásokat.")

    var groupShadowBanList by StringSettingRef(fieldName = "Tiltott csoportok listája", type = SettingType.LONG_TEXT,
        serverSideOnly = true, description = "A tiltott csoportok nevei. Ezen csoportok tagjaitól a rendszer nem fogad el megoldásokat.")

    /// -------------------------------------------------------------------------------------------------------------------

    val banModerationGroup by SettingGroup(fieldName = "Riddle beadások moderálása - Ban",
        description = "A megadott felhasználók vagy csoportok megoldásait a rendszer nem fogadja el. Felhasználónként vagy csoportonként kezdjen új sort, vagy használjon vesszőt elválasztóként.")

    var userBanList by StringSettingRef(serverSideOnly = true, fieldName = "Tiltott játékosok listája",
        type = SettingType.LONG_TEXT,
        description = "A tiltott játékosok CMSCH ID-jai. Ezen játékosoktól a rendszer nem fogad el megoldásokat.")

    var groupBanList by StringSettingRef(fieldName = "Tiltott csoportok listája", type = SettingType.LONG_TEXT,
        serverSideOnly = true, description = "A tiltott csoportok nevei. Ezen csoportok tagjaitól a rendszer nem fogad el megoldásokat.")

    /// -------------------------------------------------------------------------------------------------------------------

    val scoringGroup by SettingGroup(fieldName = "Pontozás")

    var hintScorePercent by NumberSettingRef(100,
        serverSideOnly = true, fieldName = "Hint pont érték (%)", strictConversion = false,
        description = "A hinttel megoldott riddle-ért járó pontszám a teljes pontszám százalékában")

    var saveFailedAttempts by BooleanSettingRef(false, fieldName = "Hibás válaszok számának mentése",
        description = "Bekapcsolt állapotban a rendszer menti a hibás próbálkozásokat (jelentős erőforrás-többlettel járhat)")

    /// -------------------------------------------------------------------------------------------------------------------

    val answerGroup by SettingGroup(fieldName = "Válaszok ellenőrzése",
        description = "A transzformációkat a beküldött válaszra és a tárolt megoldásra is lefuttatjuk.")

    var ignoreCase by BooleanSettingRef(true, fieldName = "Kis- és nagybetű figyelmen kívül hagyása",
        description = "A válaszok ellenőrzésekor nem számít a kis- és nagybetűk közötti különbség")

    var ignoreWhitespace by BooleanSettingRef(false, fieldName = "Szóközök és elválasztók figyelmen kívül hagyása",
        description = "A válaszok ellenőrzésekor nem számítanak a szóközök, kötőjelek és egyéb elválasztó karakterek")

    var ignoreAccent by BooleanSettingRef(false, fieldName = "Ékezetek figyelmen kívül hagyása",
        description = "A válaszok ellenőrzésekor nem számítanak az ékezetek (pl. á helyett a is elfogadható)")

    /// -------------------------------------------------------------------------------------------------------------------

    val skipGroup by SettingGroup(fieldName = "Átugrás funkció",
        description = "Bizonyos számú megoldó után a riddle átugorhatóvá válik")

    var skipEnabled by BooleanSettingRef(true, fieldName = "Átugrás engedélyezve",
        description = "A riddle átugrása gomb elérhető a felhasználók számára")

    var skipAfterGroupsSolved by NumberSettingRef(20, fieldName = "Átugrás ennyi megoldó után",
        strictConversion = false, description = "Ennyi csapat vagy felhasználó megoldása után válik elérhetővé az átugrás")

    /// -------------------------------------------------------------------------------------------------------------------

    val microserviceGroup by SettingGroup(fieldName = "Riddle microservice",
        description = "A riddle megoldások ellenőrzése kiszervezhető egy külső szolgáltatásba")

    var microserviceNodeBaseUrl by StringSettingRef("http://<pod>.<namespace>.svc.cluster.local",
        serverSideOnly = true, type = SettingType.URL, fieldName = "Riddle node belső URL-je",
        description = "A riddle node elérhetősége a belső hálózaton (pl. Kubernetes clusteren belül)")

    var microserviceSyncEnabled by BooleanSettingRef(false, fieldName = "Beállítások szinkronizálása",
        description = "Bekapcsolt állapotban a rendszer értesíti a node-ot a riddle-ök módosításáról a cache invalidálásához (nincs implementálva)")

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
