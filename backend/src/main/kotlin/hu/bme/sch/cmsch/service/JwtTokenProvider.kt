package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.component.login.CmschUserPrincipal
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.jwt.InvalidJwtAuthenticationException
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.util.getUserFromDatabase
import io.jsonwebtoken.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.stereotype.Service
import java.util.*
import javax.servlet.http.HttpServletRequest

const val JWT_CLAIM_PERMISSIONS = "permissions"
const val JWT_CLAIM_ROLE = "role"
const val JWT_CLAIM_USERID = "userId"

@Service
class JwtTokenProvider(
    private val startupPropertyConfig: StartupPropertyConfig
){

    private val secretKey = Base64.getEncoder().encodeToString(startupPropertyConfig.secretKey.toByteArray())

    fun createToken(userId: Int, internalId: String, role: RoleType, permissions: List<String>): String {
        val claims: Claims = Jwts.claims().setSubject(internalId)
        claims[JWT_CLAIM_ROLE] = role.name
        claims[JWT_CLAIM_PERMISSIONS] = permissions
        claims[JWT_CLAIM_USERID] = userId.toString()

        val now = Date()
        val validity = Date(now.time + startupPropertyConfig.sessionValidityInMilliseconds)
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

    @Throws(NoSuchElementException::class)
    fun getAuthentication(token: String): Authentication {
        val parsed = parseToken(token)
        val permissions = parsed[JWT_CLAIM_PERMISSIONS]!!
        val role = RoleType.valueOf(parsed[JWT_CLAIM_ROLE]?.toString() ?: RoleType.GUEST.name)
        return UsernamePasswordAuthenticationToken(
            CmschUserPrincipal(
                id = parsed[JWT_CLAIM_USERID]?.toString()?.toInt() ?: 0,
                internalId = parsed.subject,
                role = role,
                permissions = if (permissions is List<*>) (permissions as List<String>) else listOf()
            ),
            "",
            listOf(SimpleGrantedAuthority("ROLE_${role.name}"))
        )
    }

    fun getInternalId(token: String): String {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body.subject
    }

    private fun parseToken(token: String) = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body

    fun resolveToken(req: HttpServletRequest): String? {
        val bearerToken = req.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7, bearerToken.length)
        } else {
            null
        }
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims: Jws<Claims> = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
            !claims.body.expiration.before(Date())
        } catch (e: JwtException) {
            throw InvalidJwtAuthenticationException("Expired or invalid JWT token")
        } catch (e: IllegalArgumentException) {
            throw InvalidJwtAuthenticationException("Expired or invalid JWT token", )
        } catch (e: SignatureException) {
            throw InvalidJwtAuthenticationException("Expired or invalid JWT token")
        }
    }

    fun refreshToken(auth: Authentication): String {
        val user = auth.getUserFromDatabase()
        return createToken(user.id, user.internalId, user.role, user.permissionsAsList)
    }
}
