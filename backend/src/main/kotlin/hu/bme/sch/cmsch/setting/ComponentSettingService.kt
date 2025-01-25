package hu.bme.sch.cmsch.setting

import org.springframework.stereotype.Service

@Service
open class ComponentSettingService(private val settingProviders: List<SettingProvider>) {

    fun setSettingValue(setting: SettingProxy, value: String) {
        settingProviders.forEach { it.setValue(setting, value) }
    }

    fun getSettingValue(setting: SettingProxy): String {
        // The order of the setting providers matter, because of this loop
        for (provider in settingProviders) {
            val setting = provider.getValue(setting)
            if (setting.isPresent) return setting.get()
        }

        return setting.defaultValue
    }

}
