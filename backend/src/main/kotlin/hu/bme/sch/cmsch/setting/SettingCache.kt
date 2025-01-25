package hu.bme.sch.cmsch.setting

import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class SettingCache {
    private val backingStorage: MutableMap<String, MutableMap<String, Optional<String>>> = ConcurrentHashMap()

    /// Optional.EMPTY means that there is no value
    fun getValue(setting: SettingProxy): Optional<String>? =
        backingStorage[setting.component]?.get(setting.property)


    fun setValue(setting: SettingProxy, value: Optional<String>) {
        checkIfComponentStorageIsCreated(setting.component)
        backingStorage[setting.component]?.put(setting.property, value)
    }

    fun setValuesForComponent(component: String, properties: List<Pair<String, Optional<String>>>) {
        checkIfComponentStorageIsCreated(component)
        backingStorage[component]?.putAll(properties)
    }

    fun removeValue(setting: SettingProxy) {
        backingStorage[setting.component]?.remove(setting.property)
    }

    fun checkIfComponentStorageIsCreated(component: String) {
        if (!backingStorage.containsKey(component)) {
            backingStorage[component] = ConcurrentHashMap()
        }
    }
}
