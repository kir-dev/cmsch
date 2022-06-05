package hu.bme.sch.cmsch.component.countdown

import hu.bme.sch.cmsch.service.TimeService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component

@ConditionalOnBean(CountdownComponent::class)
@Component
class CountdownFilterConfigurer(
    private val countdown: CountdownComponent,
    private val clock: TimeService
) : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {

    override fun configure(http: HttpSecurity) {
        http.addFilterAfter(CountdownApiFilter(countdown, clock), UsernamePasswordAuthenticationFilter::class.java)
    }

}
