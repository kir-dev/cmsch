package hu.bme.sch.cmsch.component.app

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.staticpage.StaticPageRepository
import hu.bme.sch.cmsch.component.race.RaceCategoryRepository
import hu.bme.sch.cmsch.component.form.FormRepository
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.util.transaction
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*
import jakarta.annotation.PostConstruct
import org.postgresql.util.PSQLException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.transaction.PlatformTransactionManager

@Service
@ConditionalOnBean(ApplicationComponent::class)
open class MenuService(
    private val menuRepository: MenuRepository,
    private val extraMenuRepository: ExtraMenuRepository,
    private val components: List<ComponentBase>,
    private val extraPages: Optional<StaticPageRepository>,
    private val forms: Optional<FormRepository>,
    private val races: Optional<RaceCategoryRepository>,
    private val auditLogService: AuditLogService,
    private val transactionManager: PlatformTransactionManager
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val menusForRoles = EnumMap<RoleType, MutableList<MenuItem>>(RoleType::class.java)

    @PostConstruct
    fun init() {
        RoleType.values().forEach { role ->
            menusForRoles[role] = mutableListOf()
        }
    }

    @EventListener
    fun onStarted(event: ContextRefreshedEvent) {
        log.info("Refreshing menu from database")
        RoleType.values().forEach { role ->
            regenerateMenuCache(role)
        }
    }

    fun getMenusForRole(role: RoleType): List<MenuSettingItem> {
        val possibleMenus = mutableListOf<MenuSettingItem>()

        possibleMenus.addAll(components
            .filter { it.menuDisplayName != null }
            .filter { it.minRole.isAvailableForRole(role) || role.isAdmin }
            .map {
                MenuSettingItem(
                    it.javaClass.simpleName,
                    it.menuDisplayName!!.getValue(), it.menuUrl, 0,
                    visible = false, subMenu = false, external = false
                )
            })

        possibleMenus.addAll(components.flatMap { it.getAdditionalMenus(role) })

        extraPages.ifPresent { pages ->
            possibleMenus.addAll(transactionManager.transaction(readOnly = true) { pages.findAll() }
                .filter { it.showAsMenu }
                .filter { it.minRole.value <= role.value }
                .map {
                    MenuSettingItem(
                        it.javaClass.simpleName + "@" + it.id,
                        it.menuTitle, "/page/${it.url}", 0, it.showAsMenu,
                        subMenu = false, external = false
                    )
                })
        }

        forms.ifPresent { pages ->
            possibleMenus.addAll(transactionManager.transaction(readOnly = true) { pages.findAll() }
                .filter { (it.minRole.value <= role.value && it.maxRole.value >= role.value) || role.isAdmin }
                .map {
                    MenuSettingItem(
                        it.javaClass.simpleName + "@" + it.id,
                        it.menuName, "/form/${it.url}", 0, false,
                        subMenu = false, external = false
                    )
                })
        }

        races.ifPresent { pages ->
            possibleMenus.addAll(transactionManager.transaction(readOnly = true) { pages.findAllByVisibleTrue() }
                .map {
                    MenuSettingItem(
                        it.javaClass.simpleName + "@" + it.id,
                        it.name, "/race/${it.slug}", 0, false,
                        subMenu = false, external = false
                    )
                })
        }

        possibleMenus.addAll(transactionManager.transaction(readOnly = true) { extraMenuRepository.findAll() }
            .map {
                MenuSettingItem(
                    it.javaClass.simpleName + "@" + it.id,
                    it.name, it.url, 0,
                    visible = true, subMenu = false, external = it.external
                )
            })

        fillMenuDetails(possibleMenus, role)

        return possibleMenus
    }

    private fun fillMenuDetails(possibleMenus: MutableList<MenuSettingItem>, role: RoleType) {
        val storedMenus = transactionManager.transaction(readOnly = true) { menuRepository.findAllByRole(role) }
        possibleMenus.forEach { menu ->
            storedMenus.firstOrNull { it.menuId == menu.id }?.let {
                menu.order = it.order
                menu.subMenu = it.subMenu
                menu.visible = it.visible
            }
        }
    }

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun persistSettings(menus: List<MenuSettingItem>, role: RoleType) {
        menuRepository.deleteAllByRole(role)
        val menusToStore = menus.map {
            MenuEntity(
                menuId = it.id,
                role = role,
                order = it.order,
                visible = it.visible,
                subMenu = it.subMenu
            )
        }
        menuRepository.saveAll(menusToStore)
        regenerateMenuCache(role)
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    open fun regenerateMenuCache(role: RoleType) {
        menusForRoles[role]!!.clear()
        val storedMenus = getMenusForRole(role)
            .filter { it.visible }
            .sortedBy { it.order }

        val result = mutableListOf<MenuItem>()

        var lastTopMenu: MenuItem? = null
        for (storedMenu in storedMenus) {
            if (!storedMenu.subMenu) {
                val menu = storedMenu.toMenuItem()
                lastTopMenu = menu
                result.add(menu)
            } else if (lastTopMenu != null) {
                lastTopMenu.children.add(storedMenu.toMenuItem())
            } else {
                result.add(storedMenu.toMenuItem())
            }
        }

        menusForRoles[role]!!.addAll(result)
    }

    fun getCachedMenuForRole(role: RoleType): List<MenuItem> {
        return menusForRoles[role]!!
    }

    fun getComponentNames() = components.map { it.component }

    @JsonPropertyOrder("role", "name", "order", "visible", "subMenu", "external")
    data class MenuImportEntry(
        var role: RoleType = RoleType.GUEST,
        var name: String = "",
        var order: Int = 0,
        var visible: Boolean = false,
        var subMenu: Boolean = false,
        var external: Boolean = false,
    )

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun importMenu(entries: List<MenuImportEntry>, rolesToInclude: List<RoleType>): Pair<Int, Int> {
        var imported = 0
        var notAffected = 0

        entries.groupBy { it.role }
            .filter { it.key in rolesToInclude }
            .forEach { (role, menus) ->
                val initialMenus = getMenusForRole(role)
                initialMenus.forEach { initialMenu ->
                    menus.firstOrNull { it.name == initialMenu.name }
                        ?.also {
                            initialMenu.order = it.order
                            initialMenu.visible = it.visible
                            initialMenu.subMenu = it.subMenu
                            initialMenu.external = it.external
                            ++imported
                        } ?: {
                            initialMenu.order = -1
                            initialMenu.visible = false
                            initialMenu.subMenu = false
                            ++notAffected
                        }
                }
                persistSettings(initialMenus, role)
            }
        return Pair(imported, notAffected)
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    fun exportMenu(): List<MenuImportEntry> {
        return RoleType.values().flatMap { role ->
            getMenusForRole(role)
                .map { MenuImportEntry(role, it.name, it.order, it.visible, it.subMenu, it.external) }
        }
    }

}
