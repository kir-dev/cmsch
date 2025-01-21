package hu.bme.sch.cmsch.setting

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SettingProviderConfiguration {

    companion object {
        const val DATABASE_SETTING_PROVIDER_CACHE = "databaseSettingProviderCache"
    }

    @Bean
    @Qualifier(DATABASE_SETTING_PROVIDER_CACHE)
    @ConditionalOnProperty(
        prefix = "hu.bme.sch.cmsch.startup",
        name = ["distributed-mode"],
        havingValue = "false",
        matchIfMissing = true
    )
    fun singletonDatabaseSettingCache(): SettingCache = SettingCache()

}
