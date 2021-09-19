package hu.bme.sch.g7.controller

import hu.bme.sch.g7.dao.*
import hu.bme.sch.g7.model.*
import hu.bme.sch.g7.service.ClockService
import hu.bme.sch.g7.service.ImportService
import hu.bme.sch.g7.service.RealtimeConfigService
import hu.bme.sch.g7.service.UserProfileGeneratorService
import hu.bme.sch.g7.util.getUser
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/admin/control/news")
class NewsController(
        repo: NewsRepository,
        importService: ImportService
) : AbstractAdminPanelController<NewsEntity>(
        repo,
        "news", "Hír", "Hírek",
        "A kezdőlapon megjelenő hírek kezelése.",
        NewsEntity::class, ::NewsEntity, importService,
        permissionControl = { it?.isAdmin() ?: false || it?.grantMedia ?: false },
        importable = true
)

@Controller
@RequestMapping("/admin/control/events")
class EventsController(
        repo: EventRepository,
        importService: ImportService
) : AbstractAdminPanelController<EventEntity>(
        repo,
        "events", "Esemény", "Események",
        "A Gólyahét teljes (publikus) esemény listájának kezelse.",
        EventEntity::class, ::EventEntity, importService,
        permissionControl = { it?.isAdmin() ?: false || it?.grantMedia ?: false },
        importable = true
)

@Controller
@RequestMapping("/admin/control/achievements")
class AchievementController(
        repo: AchievementRepository,
        importService: ImportService
) : AbstractAdminPanelController<AchievementEntity>(
        repo,
        "achievements", "Feladat", "Feladatok",
        "Bucketlist feladatok kezelése. A feladatok javítására használd a Javítások menüt!",
        AchievementEntity::class, ::AchievementEntity, importService,
        permissionControl = { it?.isAdmin() ?: false || it?.grantCreateAchievement ?: false },
        importable = true
)

@Controller
@RequestMapping("/admin/control/categories")
class AchievementCategoryController(
        repo: AchievementCategoryRepository,
        importService: ImportService
) : AbstractAdminPanelController<AchievementCategoryEntity>(
        repo,
        "categories", "Feladat kategória", "Feladat kategóriák",
        "Bucketlist feladatok kategóriájának kezelése. A feladatok javítására használd a Javítások menüt!",
        AchievementCategoryEntity::class, ::AchievementCategoryEntity, importService,
        permissionControl = { it?.isAdmin() ?: false || it?.grantCreateAchievement ?: false },
        importable = true
)

@Controller
@RequestMapping("/admin/control/products")
class ProductController(
        repo: ProductRepository,
        importService: ImportService
) : AbstractAdminPanelController<ProductEntity>(
        repo,
        "products", "Termék", "Termékek",
        "Az összes vásárolható termék kezelése. Az eladáshoz külön felület tartozik!",
        ProductEntity::class, ::ProductEntity, importService,
        permissionControl = { it?.isAdmin() ?: false },
        importable = true
)

@Controller
@RequestMapping("/admin/control/debts")
class SoldProductController(
        repo: SoldProductRepository,
        private val clock: ClockService,
        importService: ImportService
) : AbstractAdminPanelController<SoldProductEntity>(
        repo,
        "debts", "Tranzakció", "Tranzakciók",
        "Az összes eladásból származó tranzakciók.",
        SoldProductEntity::class, ::SoldProductEntity, importService,
        controlMode = CONTROL_MODE_EDIT,
        permissionControl = { it?.isAdmin() ?: false || it?.grantFinance ?: false },
        importable = true
) {
    override fun onEntityPreSave(entity: SoldProductEntity, request: HttpServletRequest) {
        val date = clock.getTimeInSeconds()
        val user = request.getUser()
        entity.log = "${entity.log} '${user.fullName}'(${user.id}) changed [shipped: ${entity.shipped}, payed: ${entity.payed}, finsihed: ${entity.finsihed}] at $date;"
    }
}

@Controller
@RequestMapping("/admin/control/groups")
class GroupController(
        repo: GroupRepository,
        importService: ImportService
) : AbstractAdminPanelController<GroupEntity>(
        repo,
        "groups", "Tankör", "Tankörök",
        "Az összes tankör kezelése. A tankörbe való hozzárendelés a felhasználók menüből érhető el!",
        GroupEntity::class, ::GroupEntity, importService,
        mapOf("UserEntity" to { it?.members?.map {
            member -> "${member.fullName} (${member.role.name})"
        }?.toList() ?: listOf("Üres") }),
        permissionControl = { it?.isAdmin() ?: false || it?.grantGroupManager ?: false },
        importable = true
)

@Controller
@RequestMapping("/admin/control/users")
class UserController(
        repo: UserRepository,
        private val profileService: UserProfileGeneratorService,
        private val groups: GroupRepository,
        importService: ImportService
) : AbstractAdminPanelController<UserEntity>(
        repo,
        "users", "Felhasználó", "Felhasználók",
        "Az összes felhasználó (gólyák és seniorok egyaránt) kezelése.",
        UserEntity::class, ::UserEntity, importService,
        mapOf("GroupEntity" to { groups.findAll().map { it.name }.toList() }),
        permissionControl = { it?.isAdmin() ?: false || it?.grantGroupManager ?: false || it?.grantListUsers ?: false },
        importable = true
) {
    override fun onEntityPreSave(entity: UserEntity, request: HttpServletRequest) {
        profileService.generateProfileForUser(entity)
        if (entity.groupName.isNotBlank()) {
            groups.findByName(entity.groupName).ifPresentOrElse({
                entity.group = it
            }, {
                entity.fullName = ""
                entity.group = null
            })
        }
    }
}

@Controller
@RequestMapping("/admin/control/extra-pages")
class ExtraPageController(
        repo: ExtraPageRepository,
        importService: ImportService
) : AbstractAdminPanelController<ExtraPageEntity>(
        repo,
        "extra-pages", "Extra Oldal", "Extra Oldalak",
        "Egyedi oldalak kezelése.",
        ExtraPageEntity::class, ::ExtraPageEntity, importService,
        permissionControl = { it?.isAdmin() ?: false || it?.grantMedia ?: false }
)

@Controller
@RequestMapping("/admin/control/config")
class RealtimeConfigController(
        repo: RealtimeConfigRepository,
        private val config: RealtimeConfigService,
        importService: ImportService
) : AbstractAdminPanelController<RealtimeConfigEntity>(
        repo,
        "config", "Beállítás", "Beállítások",
        "Beállítások szerkesztése. Kérlek ne törölj ki olyat amit nem tudsz, hogy ki szabad törölni!",
        RealtimeConfigEntity::class, ::RealtimeConfigEntity, importService,
        permissionControl = { it?.isAdmin() ?: false }
) {
    override fun onEntityChanged(entity: RealtimeConfigEntity) {
        config.resetCache()
    }
}

@Controller
@RequestMapping("/admin/control/guild-to-user")
class GuildToUserMappingController(
        repo: GuildToUserMappingRepository,
        importService: ImportService
) : AbstractAdminPanelController<GuildToUserMappingEntity>(
        repo,
        "guild-to-user", "Gárda Tagság", "Gárda Tagságok",
        "Felhasználók neptun kód alapján gárdába rendelése. A hozzárendelés minden bejelentkezésnél megtörténik ha van egyezés és még nincs beállítva.",
        GuildToUserMappingEntity::class, ::GuildToUserMappingEntity, importService,
        permissionControl = { it?.isAdmin() ?: false || it?.grantGroupManager ?: false || it?.grantListUsers ?: false },
        importable = true
)

@Controller
@RequestMapping("/admin/control/group-to-user")
class GroupToUserMappingController(
        repo: GroupToUserMappingRepository,
        private val groups: GroupRepository,
        importService: ImportService
) : AbstractAdminPanelController<GroupToUserMappingEntity>(
        repo,
        "group-to-user", "Tankör Tagság", "Tankör Tagságok",
        "Felhasználók neptun kód alapján tankörbe és szakra rendelése. A hozzárendelés minden bejelentkezésnél megtörténik ha van egyezés és még nincs beállítva.",
        GroupToUserMappingEntity::class, ::GroupToUserMappingEntity, importService,
        mapOf("GroupEntity" to { groups.findAll().map { it.name }.toList() }),
        permissionControl = { it?.isAdmin() ?: false || it?.grantGroupManager ?: false || it?.grantListUsers ?: false },
        importable = true
)
