package hu.bme.sch.g7.controller

import hu.bme.sch.g7.dao.*
import hu.bme.sch.g7.model.*
import hu.bme.sch.g7.service.RealtimeConfigService
import hu.bme.sch.g7.service.UserProfileGeneratorService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/news")
class NewsController(repo: NewsRepository) : AbstractAdminPanelController<NewsEntity>(
        repo,
        "news", "Hír", "Hírek",
        "A kezdőlapon megjelenő hírek kezelése.",
        NewsEntity::class, ::NewsEntity
)

@Controller
@RequestMapping("/admin/control/events")
class EventsController(repo: EventRepository) : AbstractAdminPanelController<EventEntity>(
        repo,
        "events", "Esemény", "Események",
        "A Gólyahét teljes (publikus) esemény listájának kezelse.",
        EventEntity::class, ::EventEntity
)

@Controller
@RequestMapping("/admin/control/achievements")
class AchievementController(repo: AchievementRepository) : AbstractAdminPanelController<AchievementEntity>(
        repo,
        "achievements", "Feladat", "Feladatok",
        "Bucketlist feladatok kezelése. A feladatok javítására használd a Javítások menüt!",
        AchievementEntity::class, ::AchievementEntity
)

@Controller
@RequestMapping("/admin/control/products")
class ProductController(repo: ProductRepository) : AbstractAdminPanelController<ProductEntity>(
        repo,
        "products", "Termék", "Termékek",
        "Az összes vásárolható termék kezelése. Az eladáshoz külön felület tartozik!",
        ProductEntity::class, ::ProductEntity
)

@Controller
@RequestMapping("/admin/control/groups")
class GroupController(
        repo: GroupRepository
) : AbstractAdminPanelController<GroupEntity>(
        repo,
        "groups", "Tankör", "Tankörök",
        "Az összes tankör kezelése. A tankörbe való hozzárendelés a felhasználók menüből érhető el!",
        GroupEntity::class, ::GroupEntity,
        mapOf("UserEntity" to { it?.members?.map {
            member -> "${member.fullName} (${member.role.name})"
        }?.toList() ?: listOf("Üres") })
)

@Controller
@RequestMapping("/admin/control/users")
class UserController(
        repo: UserRepository,
        val profileService: UserProfileGeneratorService,
        val groups: GroupRepository,
) : AbstractAdminPanelController<UserEntity>(
        repo,
        "users", "Felhasználó", "Felhasználók",
        "Az összes felhasználó (gólyák és seniorok egyaránt) kezelése.",
        UserEntity::class, ::UserEntity,
        mapOf("GroupEntity" to { groups.findAll().map { it.name }.toList() })
) {
    override fun onEntityPreSave(entity: UserEntity) {
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
class ExtraPageController(repo: ExtraPageRepository) : AbstractAdminPanelController<ExtraPageEntity>(
        repo,
        "extra-pages", "Extra Oldal", "Extra Oldalak",
        "Egyedi oldalak kezelése.",
        ExtraPageEntity::class, ::ExtraPageEntity
)

@Controller
@RequestMapping("/admin/control/config")
class RealtimeConfigController(
        repo: RealtimeConfigRepository,
        val config: RealtimeConfigService
) : AbstractAdminPanelController<RealtimeConfigEntity>(
        repo,
        "config", "Beállítás", "Beállítások",
        "Beállítások szerkesztése. Kérlek ne törölj ki olyat amit nem tudsz, hogy ki szabad törölni!",
        RealtimeConfigEntity::class, ::RealtimeConfigEntity
) {
    override fun onEntityChanged(entity: RealtimeConfigEntity) {
        config.resetCache()
    }
}

@Controller
@RequestMapping("/admin/control/guild-to-user")
class GuildToUserMappingController(
        repo: GuildToUserMappingRepository,
) : AbstractAdminPanelController<GuildToUserMappingEntity>(
        repo,
        "guild-to-user", "Gárda Tagság", "Gárda Tagságok",
        "Felhasználók neptun kód alapján gárdába rendelése. A hozzárendelés minden bejelentkezésnél megtörténik ha van egyezés és még nincs beállítva.",
        GuildToUserMappingEntity::class, ::GuildToUserMappingEntity
)

@Controller
@RequestMapping("/admin/control/group-to-user")
class GroupToUserMappingController(
        repo: GroupToUserMappingRepository,
        val groups: GroupRepository,
) : AbstractAdminPanelController<GroupToUserMappingEntity>(
        repo,
        "group-to-user", "Tankör Tagság", "Tankör Tagságok",
        "Felhasználók neptun kód alapján tankörbe és szakra rendelése. A hozzárendelés minden bejelentkezésnél megtörténik ha van egyezés és még nincs beállítva.",
        GroupToUserMappingEntity::class, ::GroupToUserMappingEntity,
        mapOf("GroupEntity" to { groups.findAll().map { it.name }.toList() })
)
