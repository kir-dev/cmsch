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
import hu.bme.sch.cmsch.component.login.keycloak.KeycloakUserInfoResponse
import hu.bme.sch.cmsch.jwt.JwtConfigurer
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.JwtTokenProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher
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
    private val startupPropertyConfig: StartupPropertyConfig,
    @Value("\${custom.keycloak.base-url:http://localhost:8081/auth/realms/master}") private val keycloakBaseUrl: String,
    private val auditLogService: AuditLogService
) {

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

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests {
            it.requestMatchers(
                antMatcher("/"),
                antMatcher("/error"),
                antMatcher("/403"),
                antMatcher("/404"),
                antMatcher("/control/loggedin"),
                antMatcher("/control/login"),
                antMatcher("/control/logged-out"),
                antMatcher("/control/post-login"),
                antMatcher("/style4.css"),
                antMatcher("/flatpickr_custom.css"),
                antMatcher("/tabulator_simple.css"),
                antMatcher("/tabulator_simple.min.css.map"),
                antMatcher("/control/test-user"),
                antMatcher("/images/**"),
                antMatcher("/js/**"),
                antMatcher("/files/**"),
                antMatcher("/admin/logout"),
                antMatcher("/cdn/events/**"),
                antMatcher("/cdn/riddles/**"),
                antMatcher("/countdown"),
                antMatcher("/control/logout"),
                antMatcher("/control/test"),
                antMatcher("/control/open-site"),
                antMatcher("/api/**"),
                antMatcher("/share/**"),
                antMatcher("swagger-ui.html"),
                antMatcher("/v3/api-docs/**"),
                antMatcher("/cdn/manifest/**"),
                antMatcher("/manifest/manifest.json"),
                antMatcher("/cdn/public/**"),
                antMatcher("/cdn/task/**"),
                antMatcher("/cdn/news/**"),
                antMatcher("/cdn/event/**"),
                antMatcher("/cdn/manifest/**"),
                antMatcher("/control/refresh"),
                antMatcher("/oauth2/authorization"),
                antMatcher("/c/**"),
                antMatcher("/ol.js"),
                antMatcher("/ol.css"),
                antMatcher("/tracker.css"),
            ).permitAll()

            it.requestMatchers(
                antMatcher("/control/entrypoint"),
                antMatcher("/control/open-site"),
                antMatcher("/control/stamps"),
                antMatcher("/export-tasks")
            ).hasAnyRole(
                RoleType.BASIC.name,
                RoleType.STAFF.name,
                RoleType.ATTENDEE.name,
                RoleType.PRIVILEGED.name,
                RoleType.ADMIN.name,
                RoleType.SUPERUSER.name
            )

            it.requestMatchers(
                antMatcher("/admin/**"),
                antMatcher("/cdn/**")
            ).hasAnyRole(
                RoleType.STAFF.name,
                RoleType.ADMIN.name,
                RoleType.SUPERUSER.name
            )
        }
        http.formLogin { it.disable() }
        http.exceptionHandling { it.accessDeniedPage("/403") }
        http.apply(JwtConfigurer(jwtTokenProvider))
        http.apply(SessionFilterConfigurer(startupPropertyConfig))
        http.oauth2Login { oauth2 ->
            oauth2.loginPage("/oauth2/authorization")
                .authorizationEndpoint {
                    it.authorizationRequestResolver(
                        CustomAuthorizationRequestResolver(
                            clientRegistrationRepository, "/oauth2/authorization", loginComponent
                        )
                    )
                }.userInfoEndpoint { userInfo ->
                    userInfo
                        .oidcUserService {
                            if (it.clientRegistration.clientId == "google") {
                                resolveGoogleUser(it)
                            } else {
                                resolveKeycloakUser(it)
                            }
                        }
                        .userService { resolveAuthschUser(it) }
                }.defaultSuccessUrl("/control/post-login")
        }
        countdownConfigurer.ifPresent { http.apply(it) }
        http.csrf {
            it.ignoringRequestMatchers(
                antMatcher("/api/**"),
                antMatcher("/admin/api/**"),
                antMatcher("/admin/sell/**"),
                antMatcher("/admin/admission/**"),
                antMatcher("/cdn/**")
            )
        }
        return http.build()
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

        auditLogService.login(userEntity, "authsch user login g:${userEntity.group} r:${userEntity.role}")

        return CmschAuthschUser(
            userEntity.id,
            userEntity.internalId,
            userEntity.role,
            userEntity.permissionsAsList,
            userEntity.fullName,
            mutableListOf(SimpleGrantedAuthority("ROLE_${userEntity.role.name}")),
        )
    }

    private fun resolveKeycloakUser(request: OidcUserRequest): DefaultOidcUser {
        val decodedPayload = String(Base64.getDecoder().decode(request.accessToken.tokenValue.split(".")[1]))
        val profile: KeycloakUserInfoResponse = objectMapper.readerFor(KeycloakUserInfoResponse::class.java)
                .readValue(decodedPayload)
        val userEntity = authschLoginService.fetchKeycloakUserEntity(profile)

        auditLogService.login(userEntity, "keycloak user login g:${userEntity.group} r:${userEntity.role}")

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

        auditLogService.login(userEntity, "google user login g:${userEntity.group} r:${userEntity.role}")

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

}
