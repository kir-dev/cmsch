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
import hu.bme.sch.cmsch.component.serviceaccount.ServiceAccountFilterConfigurer
import hu.bme.sch.cmsch.jwt.JwtConfigurer
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.JwtTokenProvider
import hu.bme.sch.cmsch.service.StorageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.retry.annotation.EnableRetry
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@EnableWebSecurity
@Configuration
@EnableRetry(order = Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnBean(LoginComponent::class)
class SecurityConfig(
    private val clientRegistrationRepository: ClientRegistrationRepository,
    private val objectMapper: ObjectMapper,
    private val jwtTokenProvider: JwtTokenProvider,
    private val countdownConfigurer: Optional<CountdownFilterConfigurer>,
    private val serviceAccountFilterConfigurer: Optional<ServiceAccountFilterConfigurer>,
    private val authschLoginService: LoginService,
    private val loginComponent: LoginComponent,
    private val startupPropertyConfig: StartupPropertyConfig,
    @Value("\${custom.keycloak.base-url:http://localhost:8081/auth/realms/master}") private val keycloakBaseUrl: String,
    private val auditLogService: AuditLogService
) {

    private val log = LoggerFactory.getLogger(javaClass)

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
                "/",
                "/error",
                "/403",
                "/404",
                "/control/loggedin",
                "/control/login",
                "/control/logged-out",
                "/control/post-login",
                "/style4.css",
                "/flatpickr_custom.css",
                "/tabulator_simple.css",
                "/tabulator_simple.min.css.map",
                "/control/test-user",
                "/images/**",
                "/js/**",
                "/docs-icons/**",
                "/files/**",
                "/admin/logout",
                "/countdown",
                "/control/logout",
                "/control/test",
                "/control/open-site",
                "/api/**",
                "/remote-api/**",
                "/share/**",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/${StorageService.OBJECT_SERVE_PATH}/**",
                "/manifest/manifest.json",
                "/control/refresh",
                "/oauth2/authorization",
                "/c/**",
                "/ol.js",
                "/ol.css",
                "/tracker.css",
                "/scanner.css",
                "/coloris.min.css",
                "/coloris.min.js",
                "/redirect/beacon",
                "/actuator/prometheus",
                "/actuator/health/liveness",
                "/actuator/health/readiness",
            ).permitAll()

            it.requestMatchers(
                "/control/open-site",
            ).hasAnyRole(
                RoleType.BASIC.name,
                RoleType.STAFF.name,
                RoleType.ATTENDEE.name,
                RoleType.PRIVILEGED.name,
                RoleType.ADMIN.name,
                RoleType.SUPERUSER.name
            )

            it.requestMatchers(
                "/admin/**",
                "/${StorageService.OBJECT_SERVE_PATH}/**",
            ).hasAnyRole(
                RoleType.STAFF.name,
                RoleType.ADMIN.name,
                RoleType.SUPERUSER.name
            )
        }
        http.formLogin { it.disable() }
        http.exceptionHandling { it.accessDeniedPage("/403") }
        http.with(JwtConfigurer(jwtTokenProvider), Customizer.withDefaults())
        http.with(SessionFilterConfigurer(startupPropertyConfig), Customizer.withDefaults())
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
                            if (it.clientRegistration.clientId.contains("google")) {
                                resolveGoogleUser(it)
                            } else {
                                resolveKeycloakUser(it)
                            }
                        }
                        .userService { resolveAuthschUser(it) }
                }.defaultSuccessUrl("/control/post-login")
        }
        serviceAccountFilterConfigurer.ifPresent { http.with(it, Customizer.withDefaults()) }
        countdownConfigurer.ifPresent { http.with(it, Customizer.withDefaults()) }
        http.csrf {
            it.ignoringRequestMatchers(
                "/api/**",
                "/remote-api/**",
                "/admin/api/**",
                "/admin/sell/**",
                "/admin/admission/**",
                "/${StorageService.OBJECT_SERVE_PATH}/**",
            )
        }.cors(Customizer.withDefaults())
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
            id = userEntity.id,
            internalId = userEntity.internalId,
            role = userEntity.role,
            permissionsAsList = userEntity.permissionsAsList,
            userName = userEntity.fullName,
            authorities = mutableListOf(SimpleGrantedAuthority("ROLE_${userEntity.role.name}")),
            groupId = userEntity.groupId,
            groupName = userEntity.groupName
        )
    }

    private fun resolveKeycloakUser(request: OidcUserRequest): DefaultOidcUser {
        val decodedPayload = String(Base64.getDecoder().decode(request.accessToken.tokenValue.split(".")[1]))
        val profile: KeycloakUserInfoResponse = objectMapper.readerFor(KeycloakUserInfoResponse::class.java)
            .readValue(decodedPayload)
        val userEntity = authschLoginService.fetchKeycloakUserEntity(profile)

        auditLogService.login(userEntity, "keycloak user login g:${userEntity.group} r:${userEntity.role}")

        return CmschGoogleUser(
            id = userEntity.id,
            internalId = userEntity.internalId,
            role = userEntity.role,
            permissionsAsList = userEntity.permissionsAsList,
            userName = userEntity.fullName,
            authorities = mutableListOf(SimpleGrantedAuthority("ROLE_${userEntity.role.name}")),
            idToken = request.idToken,
            groupId = userEntity.groupId,
            groupName = userEntity.groupName
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
        log.info("google profile response = $profile")
        val userEntity = authschLoginService.fetchGoogleUserEntity(profile)

        auditLogService.login(userEntity, "google user login g:${userEntity.group} r:${userEntity.role}")

        return CmschGoogleUser(
            id = userEntity.id,
            internalId = userEntity.internalId,
            role = userEntity.role,
            permissionsAsList = userEntity.permissionsAsList,
            userName = userEntity.fullName,
            authorities = mutableListOf(SimpleGrantedAuthority("ROLE_${userEntity.role.name}")),
            idToken = request.idToken,
            groupId = userEntity.groupId,
            groupName = userEntity.groupName
        )
    }

}
