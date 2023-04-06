package hu.bme.sch.cmsch.config

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.component.countdown.CountdownFilterConfigurer
import hu.bme.sch.cmsch.component.login.LoginComponent
import hu.bme.sch.cmsch.component.login.LoginService
import hu.bme.sch.cmsch.component.login.SessionFilterConfigurer
import hu.bme.sch.cmsch.component.login.authsch.CmschAuthschUser
import hu.bme.sch.cmsch.component.login.authsch.ProfileResponse
import hu.bme.sch.cmsch.component.login.google.CmschGoogleUser
import hu.bme.sch.cmsch.component.login.google.GoogleUserInfoResponse
import hu.bme.sch.cmsch.jwt.JwtConfigurer
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.JwtTokenProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@EnableWebSecurity
@Configuration
@ConditionalOnBean(LoginComponent::class)
open class SecurityConfig(
    private val clientRegistrationRepository: ClientRegistrationRepository,
    private val objectMapper: ObjectMapper,
    private val jwtTokenProvider: JwtTokenProvider,
    private val countdownConfigurer: Optional<CountdownFilterConfigurer>,
    private val authschLoginService: LoginService,
    private val loginComponent: LoginComponent,
    private val startupPropertyConfig: StartupPropertyConfig
) : WebSecurityConfigurerAdapter() {

    var authschUserServiceClient = WebClient.builder()
        .baseUrl("https://auth.sch.bme.hu/api")
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.USER_AGENT, "AuthSchKotlinAPI")
        .build()

    var googleUserServiceClient = WebClient.builder()
        .baseUrl("https://www.googleapis.com/oauth2/v3")
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.USER_AGENT, "AuthSchKotlinAPI")
        .build()

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .antMatchers("/",
                        "/control/loggedin",
                        "/control/login",
                        "/control/logged-out",
                        "/control/post-login",
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
                        "/cdn/news/**",
                        "/cdn/event/**",
                        "/control/refresh",
                        "/oauth2/authorization",
                        "/c/**")
                    .permitAll()

                .antMatchers(
                        "/control/entrypoint",
                        "/control/stamps",
                        "/export-tasks")
                    .hasAnyRole(RoleType.BASIC.name, RoleType.STAFF.name, RoleType.ADMIN.name, RoleType.SUPERUSER.name)

                .antMatchers("/admin/**", "/cdn/**")
                    .hasAnyRole(RoleType.STAFF.name, RoleType.ADMIN.name, RoleType.SUPERUSER.name)

                .and().formLogin().disable()
                .exceptionHandling().accessDeniedPage("/403")
                .and().apply(JwtConfigurer(jwtTokenProvider))
                .and().apply(SessionFilterConfigurer(startupPropertyConfig))
                .and().oauth2Login()
                .loginPage("/oauth2/authorization")
                .authorizationEndpoint()
                .authorizationRequestResolver(
                    CustomAuthorizationRequestResolver(
                        clientRegistrationRepository, "/oauth2/authorization", loginComponent
                    )
                )
                .and()
                .userInfoEndpoint()
                .oidcUserService { resolveGoogleUser(it) }
                .userService { resolveAuthschUser(it) }
                .and()
                .defaultSuccessUrl("/control/post-login")

        countdownConfigurer.ifPresent { http.apply(it) }
        http.csrf().ignoringAntMatchers("/api/**", "/admin/sell/**", "/admin/admission/**", "/cdn/**")
    }

    private fun resolveAuthschUser(request: OAuth2UserRequest): DefaultOAuth2User {
        // The API returns `test/json` which is an invalid mime type
        val authschProfileJson: String? = authschUserServiceClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/profile/")
                    .queryParam("access_token", request.accessToken.tokenValue)
                    .build()
            }
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        val profile = objectMapper.readerFor(ProfileResponse::class.java)
            .readValue<ProfileResponse>(authschProfileJson)!!
        val userEntity = authschLoginService.fetchUserEntity(profile)

        return CmschAuthschUser(
            userEntity.id,
            userEntity.internalId,
            userEntity.role,
            userEntity.permissionsAsList,
            userEntity.fullName,
            mutableListOf(SimpleGrantedAuthority("ROLE_${userEntity.role.name}")),
        )
    }

    private fun resolveGoogleUser(request: OidcUserRequest): DefaultOidcUser {
        val googleProfileJson: String? = googleUserServiceClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/userinfo")
                    .build()
            }
            .header("Authorization", "Bearer " + request.accessToken.tokenValue)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        val profile = objectMapper.readerFor(GoogleUserInfoResponse::class.java)
            .readValue<GoogleUserInfoResponse>(googleProfileJson)!!
        val userEntity = authschLoginService.fetchGoogleUserEntity(profile)

        return CmschGoogleUser(
            userEntity.id,
            userEntity.internalId,
            userEntity.role,
            userEntity.permissionsAsList,
            userEntity.fullName,
            mutableListOf(SimpleGrantedAuthority("ROLE_${userEntity.role.name}")),
            request.idToken
        )
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.eraseCredentials(true)
    }

}
