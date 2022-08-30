package hu.bme.sch.cmsch.config

import hu.bme.sch.cmsch.component.countdown.CountdownFilterConfigurer
import hu.bme.sch.cmsch.jwt.JwtConfigurer
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.JwtTokenProvider
import hu.gerviba.authsch.AuthSchAPI
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import java.util.*


@EnableWebSecurity
@Configuration
open class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
    private val countdownConfigurer: Optional<CountdownFilterConfigurer>
) : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .antMatchers("/",
                        "/control/loggedin",
                        "/control/login",
                        "/control/logged-out",
                        "/style.css",
                        "/control/test-user",
                        "/images/**",
                        "/js/**",
                        "/files/**",
                        "/admin/logout",
                        "/cdn/events/**",
                        "/cdn/riddles/**",
                        "/countdown",
                        "/control/logout",
                        "/control/test",
                        "/control/open-site",
                        "/api/**",
                        "/share/**",
                        "swagger-ui.html", "/v3/api-docs/**",
                        "/cdn/manifest/**", "/manifest/manifest.json",
                        "/cdn/public/**",
                        "/cdn/task/**",
                        "/control/refresh")
                    .permitAll()

                .antMatchers(
                        "/control/entrypoint",
                        "/control/stamps")
                    .hasAnyRole(RoleType.BASIC.name, RoleType.STAFF.name, RoleType.ADMIN.name, RoleType.SUPERUSER.name)

                .antMatchers("/admin/**", "/cdn/**")
                    .hasAnyRole(RoleType.STAFF.name, RoleType.ADMIN.name, RoleType.SUPERUSER.name)

                .and().formLogin().disable()
                .exceptionHandling().accessDeniedPage("/403")
                .and().apply(JwtConfigurer(jwtTokenProvider))

        countdownConfigurer.ifPresent { http.apply(it) }
        http.csrf().ignoringAntMatchers("/api/**", "/admin/sell/**", "/admin/admission/**")
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.eraseCredentials(true)
    }

    @Bean
    @ConfigurationProperties(prefix = "authsch")
    fun authSchApi(): AuthSchAPI {
        return AuthSchAPI()
    }
}
