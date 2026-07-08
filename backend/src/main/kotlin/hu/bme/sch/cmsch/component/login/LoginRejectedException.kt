package hu.bme.sch.cmsch.component.login

import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2Error

class LoginRejectedException(val userMessage: String) :
    OAuth2AuthenticationException(OAuth2Error("login_rejected", userMessage, null), userMessage)
