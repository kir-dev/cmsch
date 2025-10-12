package hu.bme.sch.cmsch.statistics

import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
@ConditionalOnBooleanProperty(value = ["hu.bme.sch.cmsch.component.load.stats"])
class StatisticsFilterConfig(
    private var userActivityFilter: Optional<UserActivityFilter>
) {

    @Bean
    fun userActivityFilterRegistration(): FilterRegistrationBean<UserActivityFilter> {
        val registration = FilterRegistrationBean<UserActivityFilter>(userActivityFilter.orElseThrow())
        registration.addUrlPatterns("/api/*")
        return registration
    }
}
