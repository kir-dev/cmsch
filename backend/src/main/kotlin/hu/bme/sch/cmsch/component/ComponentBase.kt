package hu.bme.sch.cmsch.component

import hu.bme.sch.cmsch.admin.GenerateInput
import hu.bme.sch.cmsch.component.app.MenuSettingItem
import hu.bme.sch.cmsch.dto.SearchableResource
import hu.bme.sch.cmsch.dto.SearchableResourceType
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.PermissionValidator
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.setting.MutableSetting
import hu.bme.sch.cmsch.setting.Setting
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

abstract class ComponentBase(
    val componentSettingService: ComponentSettingService,
    val component: String,
    val menuUrl: String,
    private val componentName: String,
    val showPermission: PermissionValidator,
    val entities: List<KClass<out ManagedEntity>>,
    private val env: Environment,
) {

    internal val log = LoggerFactory.getLogger(javaClass)

    open val menuDisplayName: String? = null

    abstract var minRole: Set<RoleType>

    val allSettings: MutableList<Setting<*>> = mutableListOf()

    val menuPriority: Int
        get() = env.getProperty("hu.bme.sch.cmsch.${component}.priority")?.toIntOrNull() ?: 0

    @PostConstruct
    fun init() {
        log.info("Loading component: {}", component)

        validateNoDuplication()
        validateComponentName()
        validateAllSettingsAdded()
        touchDefinedProperties()

        onInit()
    }

    private fun validateNoDuplication() {
        allSettings.forEach { setting ->
            if (allSettings.count { it.property == setting.property } != 1) {
                log.error("Duplicate property found: {}.{}", setting.component, setting.property)
            }
        }
    }

    private fun validateComponentName() {
        allSettings.forEach { setting ->
            if (setting.component != component) {
                log.error(
                    "Invalid component name {} in component: {}, property: {}",
                    setting.component, component, setting.property
                )
            }
        }
    }

    // This saves the default value if not present and loads the property into the cache if enabled
    private fun touchDefinedProperties() {
        allSettings.filterIsInstance<MutableSetting<*>>().forEach { it.getValue() }
    }

    private fun validateAllSettingsAdded() {
        val settingFields: List<String> = javaClass.declaredFields
            .filter { MutableSetting::class.java.isAssignableFrom(it.type) }
            .map {
                val accessible = it.canAccess(this)
                it.isAccessible = true
                val property = (it[this] as Setting<*>).property
                it.isAccessible = accessible
                return@map property
            }.distinct()
        val providedSettings = allSettings
            .filter { MutableSetting::class.java.isAssignableFrom(it.javaClass) }
            .map { it.property }
            .distinct()

        if (settingFields.count() != providedSettings.count()) {
            log.error(
                "Some of the settings are missing, {} out of {} added to the list, {}",
                providedSettings.count(),
                settingFields.count(),
                settingFields.toMutableSet().also { it.removeAll(providedSettings) }
            )
        }
    }

    fun registerSetting(setting: Setting<*>) = allSettings.add(setting)

    fun attachConstants(): Map<String, Any> {
        return allSettings
            .filter { !it.isServerSideOnly }
            .associate { it.property to it.getValue() }
    }

    open fun onPersist() {
        // Empty implementation, override it when its needed
    }

    open fun onInit() {
        // Empty implementation, override it when its needed
    }

    open fun getAdditionalMenus(role: RoleType): List<MenuSettingItem> {
        return listOf()
    }

    fun getPropertyResources(): List<SearchableResource> {
        return allSettings.map { setting ->
            SearchableResource(
                name = setting.fieldName,
                description = ": $componentName",
                type = SearchableResourceType.PROPERTY,
                permission = showPermission,
                target = "/admin/control/component/${component}/settings#_${setting.property}"
            )
        }
    }

    fun getEntityResources(): List<SearchableResource> {
        return entities.flatMap { entityClass ->
            val config = entityClass.createInstance().getEntityConfig(env)
                ?: return@flatMap listOf()

            entityClass.memberProperties
                .mapNotNull { it.findAnnotation<GenerateInput>() }
                .filter { it.label.isNotEmpty() }
                .map {
                    SearchableResource(
                        name = it.label,
                        description = ": ${config.name}",
                        type = SearchableResourceType.ENTITY,
                        permission = config.showPermission,
                        target = "/admin/control/entity/${config.name}#_${it.order}"
                    )
                }
        }
    }

}
