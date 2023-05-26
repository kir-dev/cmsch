package hu.bme.sch.cmsch.statistics

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["stats"],
    havingValue = "true",
    matchIfMissing = false
)
class StatisticsFilterConfig(
    private var userActivityFilter: Optional<UserActivityFilter>
) {

    @Bean
    fun userActivityFilterRegistration(): FilterRegistrationBean<UserActivityFilter> {
        val registration = FilterRegistrationBean<UserActivityFilter>()
        registration.filter = userActivityFilter.orElseThrow()
        registration.addUrlPatterns("/api/*")
        return registration
    }
}