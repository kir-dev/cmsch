package hu.bme.sch.g7.config

import hu.bme.sch.g7.model.RoleType
import hu.gerviba.authsch.AuthSchAPI
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.WebSecurity




@EnableWebSecurity
@Configuration
open class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .antMatchers("/", "/cdn/**", "/h2-console/**", "/loggedin", "/login", "/api/**").permitAll()
                .antMatchers("/admin/**").hasAnyRole(RoleType.STAFF.name, RoleType.ADMIN.name, RoleType.SUPERUSER.name)
                .and()
                .formLogin()
                .loginPage("/login")
        http.csrf().ignoringAntMatchers("/api/**")
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.eraseCredentials(true)
    }

    @Bean
    @ConfigurationProperties(prefix = "authsch")
    open fun authSchApi(): AuthSchAPI {
        return AuthSchAPI()
    }
}
