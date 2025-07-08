package hu.bme.sch.cmsch.setting

import hu.bme.sch.cmsch.component.app.ComponentSettingEntity
import hu.bme.sch.cmsch.component.app.ComponentSettingRepository
import hu.bme.sch.cmsch.setting.SettingProviderConfiguration.Companion.DATABASE_SETTING_CACHE
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class ComponentSettingService(
    private val env: Environment,
    private val componentSettingRepository: ComponentSettingRepository,
    @param:Qualifier(DATABASE_SETTING_CACHE) private val settingCache: Optional<SettingCache>
) {

    fun <T : Any> getBaseValue(setting: SettingRef<T>, defaultValue: T): T =
        env.getProperty("hu.bme.sch.cmsch.${setting.component}.${setting.property}")?.let {
            // Always be strict with env vars to spot configuration errors
            setting.serializer.deserialize(it, defaultValue, true)
        } ?: defaultValue

    fun <T : Any> getValue(setting: SettingRef<T>): String? {
        if (!setting.persist) return null
        if (!setting.cache || settingCache.isEmpty) {
            val property = componentSettingRepository
                .findByComponentAndProperty(setting.component, setting.property).map { it.value }
            if (property.isEmpty) commitDefaultValue(setting)
            return null
        }
        val cached: Optional<String>? = settingCache.get().getValue(setting)
        if (cached != null) return cached.getOrNull()

        loadComponentSettingsIntoCache(setting, settingCache.get())
        return settingCache.get().getValue(setting)?.getOrNull()
    }

    fun setValue(setting: SettingRef<*>, value: String?) {
        if (!setting.persist) return

        val entity = componentSettingRepository
            .findByComponentAndProperty(setting.component, setting.property)
            .orElseGet {
                ComponentSettingEntity(
                    component = setting.component,
                    property = setting.property,
                )
            }
        entity.value = value ?: ""
        componentSettingRepository.save(entity)

        // remove it instead of adding it, because if the transaction gets rolled back,
        // we will have inconsistent data in the cache
        settingCache.ifPresent { it.removeValue(setting) }
    }

    private fun <T : Any> loadComponentSettingsIntoCache(setting: SettingRef<T>, cache: SettingCache) {
        // Usually we use more than one setting from a component in a single request, fetch all properties in a batch
        val allSettings = componentSettingRepository.findAllByComponent(setting.component)
        val properties = allSettings.map { it.property to Optional.ofNullable(it.value) }

        // If the property that triggered the read is not present in the db, then create a record for it
        if (allSettings.none { it.property == setting.property }) {
            commitDefaultValue(setting)
            cache.setValue(setting, Optional.of(serializeSettingDefaultValue(setting)))
        }

        cache.setValuesForComponent(setting.component, properties)
    }

    private fun <T : Any> serializeSettingDefaultValue(setting: SettingRef<T>): String {
        return setting.serializer.serialize(setting.defaultValue, setting.strictConversion)
    }

    private fun <T : Any> commitDefaultValue(setting: SettingRef<T>) {
        componentSettingRepository.save(
            ComponentSettingEntity(
                component = setting.component,
                property = setting.property,
                value = serializeSettingDefaultValue(setting)
            )
        )
    }

}
