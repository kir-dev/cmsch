package hu.bme.sch.cmsch.jwt

import org.springframework.security.core.AuthenticationException

class InvalidJwtAuthenticationException(e: String) : AuthenticationException(e)
