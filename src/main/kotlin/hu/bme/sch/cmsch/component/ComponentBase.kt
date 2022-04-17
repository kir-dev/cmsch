package hu.bme.sch.cmsch.component

import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import javax.annotation.PostConstruct

abstract class ComponentBase(
    val component: String,
    val menuUrl: String,
    private val componentSettingService: ComponentSettingService,
    private val env: Environment
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

        onValuesUpdated()
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
            log.info("Setting prepared from config: {}.{} = '{}'", setting.component, setting.property, setting.rawValue)
        }
    }

    private fun updateFromDatabase() {
        log.info("Loading {} component settings from database", component)
        componentSettingService.loadDefaultSettings(allSettings)
        allSettings.forEach {
            log.info("Setting loaded from db: {}.{} = '{}'", it.component, it.property, it.getValue())
        }
    }

    fun attachConstants(): Map<String, String> {
        componentSettingService.refreshCachedSettings(allSettings)
        return allSettings
            .filter { it.constant && !it.serverSideOnly }
            .associate { it.property to it.getValue() }
    }

    open fun onValuesUpdated() {
        // Empty implementation, override it when its needed
    }

}
