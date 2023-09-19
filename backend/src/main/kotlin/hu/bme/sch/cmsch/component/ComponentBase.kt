package hu.bme.sch.cmsch.component

import hu.bme.sch.cmsch.admin.GenerateInput
import hu.bme.sch.cmsch.component.app.MenuSettingItem
import hu.bme.sch.cmsch.dto.SearchableResource
import hu.bme.sch.cmsch.dto.SearchableResourceType
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.PermissionValidator
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

abstract class ComponentBase(
    val component: String,
    val menuUrl: String,
    private val componentName: String,
    val showPermission: PermissionValidator,
    val entities: List<KClass<out ManagedEntity>>,
    private val componentSettingService: ComponentSettingService,
    private val env: Environment,
) {

    internal val log = LoggerFactory.getLogger(javaClass)

    open val menuDisplayName: SettingProxy? = null

    abstract val minRole: MinRoleSettingProxy

    internal abstract val allSettings: List<SettingProxy>

    val menuPriority: Int
        get() = env.getProperty("hu.bme.sch.cmsch.${component}.priority")?.toIntOrNull() ?: 0

    @PostConstruct
    fun init() {
        log.info("Loading component: {}", component)

        validateNoDuplication()
        validateComponentName()
        validateAllSettingsAdded()

        loadFromSettings()
        updateFromDatabase()

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
                log.error("Invalid component name {} in component: {}, property: {}",
                    setting.component, component, setting.property)
            }
        }
    }

    private fun validateAllSettingsAdded() {
        val settingsFieldsCount = javaClass.declaredFields.count { it.type is SettingProxy && it.get(this) != null }
        val settingListSize = allSettings.distinct().count()

        if (settingsFieldsCount != settingListSize) {
            log.error("Some of the settings are missing, {} out of {} added to the list",
                settingListSize, settingsFieldsCount)
        }
    }

    private fun loadFromSettings() {
        allSettings.forEach { setting ->
            env.getProperty("hu.bme.sch.cmsch.${setting.component}.${setting.property}")?.let {
                if (it.isNotBlank())
                    setting.rawValue = it
            }
            log.info(" - Setting prepared from config: {}.{} = '{}'", setting.component, setting.property, setting.rawValue)
        }
    }

    fun updateFromDatabase() {
        log.info("Loading {} component settings from database", component)
        componentSettingService.loadDefaultSettings(allSettings)
        allSettings.forEach {
            log.info(" - Setting loaded from db: {}.{} = '{}'", it.component, it.property, it.getValue())
        }
    }

    fun attachConstants(): Map<String, Any> {
        componentSettingService.refreshCachedSettings(allSettings)
        return allSettings
            .filter { it.constant && !it.isServerSideOnly }
            .associate { it.property to it.getMappedValue() }
    }

    fun persistChanges() {
        componentSettingService.persistSettings(allSettings)
    }

    open fun onPersist() {
        // Empty implementation, override it when its needed
    }

    fun onFirePersistEvent() {
        componentSettingService.onPersisted()
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
