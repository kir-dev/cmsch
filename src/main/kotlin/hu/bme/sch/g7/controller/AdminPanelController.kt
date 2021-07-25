package hu.bme.sch.g7.controller

import hu.bme.sch.g7.dao.*
import hu.bme.sch.g7.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/news")
class NewsController(@Autowired repo: NewsRepository) : AbstractAdminPanelController<NewsEntity>(
        repo,
        "news", "Hír", "Hírek",
        "A kezdőlapon megjelenő hírek kezelése.",
        NewsEntity::class, ::NewsEntity
)

@Controller
@RequestMapping("/admin/control/events")
class EventsController(@Autowired repo: EventRepository) : AbstractAdminPanelController<EventEntity>(
        repo,
        "events", "Esemény", "Események",
        "A Gólyahét teljes (publikus) esemény listájának kezelse.",
        EventEntity::class, ::EventEntity
)

@Controller
@RequestMapping("/admin/control/achievements")
class AchievementController(@Autowired repo: AchievementRepository) : AbstractAdminPanelController<AchievementEntity>(
        repo,
        "achievements", "Feladat", "Feladatok",
        "Bucketlist feladatok kezelése. A feladatok javítására használd a Javítások menüt!",
        AchievementEntity::class, ::AchievementEntity
)

@Controller
@RequestMapping("/admin/control/products")
class ProductController(@Autowired repo: ProductRepository) : AbstractAdminPanelController<ProductEntity>(
        repo,
        "products", "Termék", "Termékek",
        "Az összes vásárolható termék kezelése. Az eladáshoz külön felület tartozik!",
        ProductEntity::class, ::ProductEntity
)

@Controller
@RequestMapping("/admin/control/groups")
class GroupController(@Autowired repo: GroupRepository) : AbstractAdminPanelController<GroupEntity>(
        repo,
        "groups", "Tankör", "Tankörök",
        "Az összes tankör kezelése. A tankörbe való hozzárendelés a felhasználók menüből érhető el!",
        GroupEntity::class, ::GroupEntity
)

@Controller
@RequestMapping("/admin/control/users")
class UserController(@Autowired repo: UserRepository) : AbstractAdminPanelController<UserEntity>(
        repo,
        "users", "Felhasználó", "Felhasználók",
        "Az összes felhasznéló (gólyák és seniorok egyaránt) kezelése.",
        UserEntity::class, ::UserEntity
)

@Controller
@RequestMapping("/admin/control/extra-pages")
class ExtraPageController(@Autowired repo: ExtraPageRepository) : AbstractAdminPanelController<ExtraPageEntity>(
        repo,
        "extra-pages", "Extra Oldal", "Extra Oldalak",
        "Egyedi oldalak kezelése.",
        ExtraPageEntity::class, ::ExtraPageEntity
)