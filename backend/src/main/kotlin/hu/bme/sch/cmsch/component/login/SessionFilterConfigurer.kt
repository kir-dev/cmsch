package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.config.StartupPropertyConfig
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class SessionFilterConfigurer(
    private val startupPropertyConfig: StartupPropertyConfig
) : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {

    override fun configure(http: HttpSecurity) {
        http.addFilterAfter(
            SessionIncreaseFilter(startupPropertyConfig),
            UsernamePasswordAuthenticationFilter::class.java
        )
    }

}

