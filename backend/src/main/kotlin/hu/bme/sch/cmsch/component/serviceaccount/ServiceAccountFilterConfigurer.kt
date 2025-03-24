package hu.bme.sch.cmsch.component.serviceaccount

import hu.bme.sch.cmsch.service.TimeService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component

@Component
@ConditionalOnBean(ServiceAccountComponent::class)
class ServiceAccountFilterConfigurer(
    private val serviceAccountKeyRepository: ServiceAccountKeyRepository,
    private val clock: TimeService,
) : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {

    override fun configure(http: HttpSecurity) {
        val serviceAccountFilter = ServiceAccountFilter(serviceAccountKeyRepository, clock)
        http.addFilterBefore(serviceAccountFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

}
