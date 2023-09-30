package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.CMSCH_VERSION
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.dto.*
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.statistics.UserActivityFilter
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import jakarta.annotation.PostConstruct

private fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    val hashBytes = md.digest(this.toByteArray())
    val sb = StringBuilder()
    for (b in hashBytes) {
        sb.append(String.format("%02x", b))
    }
    return sb.toString()
}

private const val CACHE_TIMEOUT = 1000 * 60 * 30

@Service
class AdminMenuService(
    private val applicationComponent: ApplicationComponent,
    private val environment: Environment,
    private val userService: UserService,
    private val clock: TimeService,
    private val stats: Optional<UserActivityFilter>
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val entries: MutableMap<String, MutableList<AdminMenuEntry>> = ConcurrentHashMap()
    private val categories: MutableMap<String, AdminMenuCategory> = ConcurrentHashMap()
    private val searchableResources: MutableList<SearchableResource> = mutableListOf()
    private val userContexts: MutableMap<String, UserSiteContext> = ConcurrentHashMap()

    private var siteContext: SiteContext = SiteContext(
        "CMSCH 4 APP",
        "dev",
        "Context is still loading...",
        "",
        "",
        Runtime.version().toString(),
        0,
        0,
        applicationComponent.adminBrandColor.getValue(),
        darkenHexColor(applicationComponent.adminBrandColor.getValue(), 90),
        darkenHexColor(applicationComponent.adminBrandColor.getValue(), 80)
    )

    @PostConstruct
    fun invalidateSiteContext() {
        siteContext = SiteContext(
            applicationComponent.siteName.getValue()
                    + (if (applicationComponent.adminPanelName.getValue().isNotBlank())
                        " - ${applicationComponent.adminPanelName.getValue()}" else ""),
            if (environment.activeProfiles.contains("internal")
                    || environment.activeProfiles.contains("test")
                    || environment.activeProfiles.contains("dev")) {
                "dev"
            } else if (applicationComponent.isLive.isValueTrue()) {
                "live"
            } else {
                "staging"
            },
            applicationComponent.motd.getValue(),
            applicationComponent.adminSiteUrl.getValue(),
            applicationComponent.siteUrl.getValue(),
            CMSCH_VERSION,
            stats.map { it.rpm }.orElse(0),
            stats.map { it.usersIn5Minutes }.orElse(0),
            applicationComponent.adminBrandColor.getValue(),
            darkenHexColor(applicationComponent.adminBrandColor.getValue(), 90),
            darkenHexColor(applicationComponent.adminBrandColor.getValue(), 80)
        )
    }

    fun invalidateUser(internalId: String) {
        userContexts.remove(internalId)
    }

    @Scheduled(fixedRate = 1000 * 60 * 10)
    fun runCacheControl() {
        log.info("Running AdminMenuService cache control")
        val now = clock.getTime()
        val toBeRemoved = userContexts
            .filter { it.value.cacheCreated + CACHE_TIMEOUT < now }
            .map { it.key }
            .toList()

        toBeRemoved.forEach { userContexts.remove(it) }
        log.info("Cleaned: {} still cached {}", toBeRemoved.size, userContexts.size)
    }

    @Scheduled(fixedDelay = 5000)
    fun postInit() {
        Collections.sort(searchableResources, Comparator.comparing { it.name.replace("'", "") })
    }

    fun registerEntry(component: String, entry: AdminMenuEntry) {
        entries.computeIfAbsent(component) { mutableListOf() }.add(entry)
        searchableResources.add(SearchableResource(
            name = entry.title,
            type = SearchableResourceType.MENU,
            description = ": Menü",
            target = entry.target,
            permission = entry.showPermission
        ))
    }

    fun registerCategory(component: String, category: AdminMenuCategory) {
        categories[component] = category
    }

    fun registerResource(resource: SearchableResource) {
        searchableResources.add(resource)
    }

    private fun getMenusOfCategory(category: String, user: CmschUser): List<AdminMenuEntry> {
        return entries.getOrDefault(category, listOf())
            .filter { it.showPermission.validate(user) }
            .sortedBy { it.priority }
    }

    fun addPartsForMenu(user: CmschUser, model: Model) {
        model.addAttribute("menu", categories.entries
            .associateWith { getMenusOfCategory(it.key, user) }
            .entries
            .toList()
            .sortedBy { it.key.value.priority })

        model.addAttribute("context", siteContext)
        model.addAttribute("userContext", getContextForUser(user))
        model.addAttribute("showStats", ControlPermissions.PERMISSION_SHOW_LIVE_STATS.validate(user))
    }

    private fun getContextForUser(user: CmschUser): UserSiteContext {
        val context = userContexts[user.internalId]
        if (context != null)
            return context

        val userEntity = userService.getById(user.internalId)
        val config = userService.resolveConfig(userEntity.config)
        val result = UserSiteContext(
            userName = user.userName,
            email = userEntity.email,
            emailHash = if (userEntity.email.isEmpty()) userEntity.fullName.md5() else userEntity.email.md5(),
            profilePicture = userEntity.profilePicture,
            role = user.role,
            group = userEntity.groupName,
            favoriteMenus = config.favoriteMenus,
            dismissedMotd = config.dismissedMotd,
            permissions = user.permissionsAsList,
            resources = searchableResources.filter { it.permission.validate(user) },
            cacheCreated = clock.getTime()
        )

        if (userEntity.role.value >= RoleType.STAFF.value)
            userContexts[user.internalId] = result

        return result
    }

    fun toggleFavoriteMenu(user: CmschUser, menu: String) {
        val context = getContextForUser(user)
        if (context.favoriteMenus.contains(menu)) {
            context.favoriteMenus.remove(menu)
        } else {
            context.favoriteMenus.add(menu)
        }
        saveContextConfig(user, context)
    }

    fun dismissMotd(user: CmschUser, motd: String) {
        val context = getContextForUser(user)
        context.dismissedMotd = motd
        saveContextConfig(user, context)
    }

    private fun saveContextConfig(user: CmschUser, context: UserSiteContext) {
        userService.saveUserConfig(user, UserConfig(context.favoriteMenus, context.dismissedMotd))
    }

    @Scheduled(cron = "5 * * * * *")
    fun onEveryMinute() {
        siteContext.rpm = stats.map { it.rpm }.orElse(0)
        siteContext.activeUsers = stats.map { it.usersIn5Minutes }.orElse(0)
    }

}

data class AdminMenuCategory(val title: String, val priority: Int)

data class AdminMenuEntry(
    val title: String,
    val icon: String,
    val target: String,
    val priority: Int,
    val showPermission: PermissionValidator
)

fun darkenHexColor(hexColor: String, percentage: Int): String {
    val color = if (hexColor.length < 7) "#DEDEDE" else hexColor
    require(percentage in 0..100) { "Percentage should be between 0 and 100" }

    val r = color.substring(1, 3).toInt(16)
    val g = color.substring(3, 5).toInt(16)
    val b = color.substring(5, 7).toInt(16)

    val factor = percentage / 100.0

    val newR = (r * factor).toInt().coerceIn(0, 255)
    val newG = (g * factor).toInt().coerceIn(0, 255)
    val newB = (b * factor).toInt().coerceIn(0, 255)

    return String.format("#%02X%02X%02X", newR, newG, newB)
}