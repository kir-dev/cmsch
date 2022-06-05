package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.extrapage.ExtraPageRepository
import hu.bme.sch.cmsch.model.RoleType
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.annotation.PostConstruct

@Service
@ConditionalOnBean(ApplicationComponent::class)
open class MenuService(
    private val menuRepository: MenuRepository,
    private val extraMenuRepository: ExtraMenuRepository,
    private val components: List<ComponentBase>,
    private val extraPages: Optional<ExtraPageRepository>,
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

        extraPages.ifPresent { pages ->
            possibleMenus.addAll(pages.findAll()
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

        possibleMenus.addAll(extraMenuRepository.findAll()
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
        val storedMenus = menuRepository.findAllByRole(role)
        possibleMenus.forEach { menu ->
            storedMenus.firstOrNull { it.menuId == menu.id }?.let {
                menu.order = it.order
                menu.subMenu = it.subMenu
                menu.visible = it.visible
            }
        }
    }

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

}
