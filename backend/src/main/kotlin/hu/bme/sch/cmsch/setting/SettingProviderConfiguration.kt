package hu.bme.sch.cmsch.setting

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SettingProviderConfiguration {

    companion object {
        const val DATABASE_SETTING_CACHE = "databaseSettingCache"
    }

    @Bean
    @Qualifier(DATABASE_SETTING_CACHE)
    @ConditionalOnBooleanProperty(
        value = ["hu.bme.sch.cmsch.startup.distributed-mode"],
        havingValue = false,
        matchIfMissing = true,
    )
    fun singletonDatabaseSettingCache(): SettingCache = SettingCache()

}
