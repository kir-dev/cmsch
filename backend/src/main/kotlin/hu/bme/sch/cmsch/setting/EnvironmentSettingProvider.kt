package hu.bme.sch.cmsch.setting

import org.springframework.core.annotation.Order
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.util.*

@Order(100)
@Component
class EnvironmentSettingProvider(private val env: Environment) : SettingProvider {
    // env property lookup might be expensive
    private val cache = SettingCache()

    override fun getValue(setting: SettingProxy): Optional<String> {
        val property = cache.getValue(setting)
        if (property == null) {
            val v = Optional.ofNullable(env.getProperty("hu.bme.sch.cmsch.${setting.component}.${setting.property}"))
            cache.setValue(setting, v)
            return v
        }

        return property
    }

    override fun setValue(setting: SettingProxy, value: String) {
        //  env is read-only
    }
}
