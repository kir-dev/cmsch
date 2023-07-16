package hu.bme.sch.cmsch.config

import hu.bme.sch.cmsch.component.login.LoginComponent
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest

const val GOOGLE = "google"
const val AUTHSCH = "authsch"
const val KEYCLOAK = "keycloak"

class CustomAuthorizationRequestResolver(
    repo: ClientRegistrationRepository,
    authorizationRequestBaseUri: String,
    private val loginComponent: LoginComponent,
) : OAuth2AuthorizationRequestResolver {

    private var defaultResolver: OAuth2AuthorizationRequestResolver? = null

    init {
        defaultResolver = DefaultOAuth2AuthorizationRequestResolver(repo, authorizationRequestBaseUri)
    }

    override fun resolve(request: HttpServletRequest): OAuth2AuthorizationRequest? {
        return when (request.servletPath) {
            "/oauth2/authorization/google" -> {
                var req: OAuth2AuthorizationRequest? = defaultResolver?.resolve(request)
                if (req != null)
                    req = customizeAuthorizationRequest(req, GOOGLE)
                req
            }
            "/oauth2/authorization/authsch" -> {
                var req: OAuth2AuthorizationRequest? = defaultResolver?.resolve(request)
                if (req != null)
                    req = customizeAuthorizationRequest(req, AUTHSCH)
                req
            }
            else -> {
                var req: OAuth2AuthorizationRequest? = defaultResolver?.resolve(request)
                if (req != null)
                    req = customizeAuthorizationRequest(req, KEYCLOAK)
                req
            }
        }
    }

    override fun resolve(request: HttpServletRequest, clientRegistrationId: String): OAuth2AuthorizationRequest? {
        var req: OAuth2AuthorizationRequest? = defaultResolver?.resolve(request, clientRegistrationId)
        if (req != null)
            req = customizeAuthorizationRequest(req, clientRegistrationId)
        return req
    }

    private fun customizeAuthorizationRequest(request: OAuth2AuthorizationRequest, clientRegistrationId: String): OAuth2AuthorizationRequest? {
        return when(clientRegistrationId) {
            AUTHSCH -> {
                if (loginComponent.onlyBmeProvider.isValueTrue()) {
                    val target = OAuth2AuthorizationRequest
                        .from(request)
                        .scopes(loginComponent.authschScopes.map { it.scope }.toSet())
                        .build()
                    OAuth2AuthorizationRequest.from(request)
                        .authorizationUri("https://auth.sch.bme.hu/Shibboleth.sso/Login")
                        .parameters {
                            it["target"] = target.authorizationRequestUri
                        }
                        .scopes(loginComponent.authschScopes.map { it.scope }.toSet())
                        .build()
                } else {
                    OAuth2AuthorizationRequest
                        .from(request)
                        .scopes(loginComponent.authschScopes.map { it.scope }.toSet())
                        .build()
                }
            }
            GOOGLE -> OAuth2AuthorizationRequest
                .from(request)
                .scopes(setOf("profile", "email", "openid"))
                .build()
            KEYCLOAK -> OAuth2AuthorizationRequest
                .from(request)
                .build()
            else -> OAuth2AuthorizationRequest
                .from(request)
                .build()
        }
    }

}
