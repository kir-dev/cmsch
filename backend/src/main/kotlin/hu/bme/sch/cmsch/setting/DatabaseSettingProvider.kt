package hu.bme.sch.cmsch.setting

import hu.bme.sch.cmsch.component.app.ComponentSettingEntity
import hu.bme.sch.cmsch.component.app.ComponentSettingRepository
import hu.bme.sch.cmsch.setting.SettingProviderConfiguration.Companion.DATABASE_SETTING_PROVIDER_CACHE
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

@Order(50)
@Component
class DatabaseSettingProvider(
    private val componentSettingRepository: ComponentSettingRepository,
    @Qualifier(DATABASE_SETTING_PROVIDER_CACHE) private val settingCache: Optional<SettingCache>
) : SettingProvider {

    override fun getValue(setting: SettingProxy): Optional<String> {
        if (!setting.persist) return Optional.empty()
        if (!setting.cache || settingCache.isEmpty) {
            val property = componentSettingRepository
                .findByComponentAndProperty(setting.component, setting.property).map { it.value }
            if (property.isEmpty) commitDefaultValue(setting)
            return Optional.of(setting.defaultValue)
        }
        val cached = settingCache.get().getValue(setting)
        if (cached != null) return cached

        loadComponentSettingsIntoCache(setting, settingCache.get())
        return settingCache.get().getValue(setting)!!
    }

    override fun setValue(setting: SettingProxy, value: String) {
        if (!setting.persist) return

        val entity = componentSettingRepository
            .findByComponentAndProperty(setting.component, setting.property)
            .orElseGet {
                ComponentSettingEntity(
                    component = setting.component,
                    property = setting.property,
                )
            }
        entity.value = value
        componentSettingRepository.save(entity)

        // remove it instead of adding it, because if the transaction gets rolled back,
        // we will have inconsistent data in the cache
        settingCache.ifPresent { it.removeValue(setting) }
    }

    private fun loadComponentSettingsIntoCache(setting: SettingProxy, cache: SettingCache) {
        // Usually we use more than one setting from a component in a single request, fetch all properties in a batch
        val allSettings = componentSettingRepository.findAllByComponent(setting.component)
        val properties = allSettings.map { it.property to Optional.ofNullable(it.value) }

        // If the property that triggered the read is not present in the db, then create a record for it
        if (allSettings.none { it.property == setting.property }) {
            commitDefaultValue(setting)
            cache.setValue(setting, Optional.of(setting.defaultValue))
        }

        cache.setValuesForComponent(setting.component, properties)
    }

    private fun commitDefaultValue(setting: SettingProxy) {
        componentSettingRepository.save(
            ComponentSettingEntity(
                component = setting.component,
                property = setting.property,
                value = setting.defaultValue
            )
        )
    }

}
