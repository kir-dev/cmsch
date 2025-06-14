package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.component.login.CmschUserPrincipal
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.jwt.InvalidJwtAuthenticationException
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.util.getUserEntityFromDatabase
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.stereotype.Service
import java.util.*

const val JWT_CLAIM_PERMISSIONS = "permissions"
const val JWT_CLAIM_ROLE = "role"
const val JWT_CLAIM_USERID = "userId"
const val JWT_CLAIM_USERNAME = "userName"
const val JWT_CLAIM_GROUP_ID = "groupId"
const val JWT_CLAIM_GROUP_NAME = "groupName"

private const val EXPIRED_OR_INVALID_TOKEN = "Expired or invalid JWT token"

@Service
class JwtTokenProvider(
    private val startupPropertyConfig: StartupPropertyConfig
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val secretKey = Keys.hmacShaKeyFor(startupPropertyConfig.secretKey.toByteArray())
    private val parser = Jwts.parser().verifyWith(secretKey).build()

    fun createToken(cmschUser: CmschUser): String {
        return createToken(
            userId = cmschUser.id,
            internalId = cmschUser.internalId,
            role = cmschUser.role,
            permissions = cmschUser.permissionsAsList,
            fullName = cmschUser.userName,
            groupId = cmschUser.groupId,
            groupName = cmschUser.groupName
        )
    }

    fun createToken(
        userId: Int,
        internalId: String,
        role: RoleType,
        permissions: List<String>,
        fullName: String,
        groupId: Int?,
        groupName: String
    ): String {
        val claims = Jwts.claims().subject(internalId)
        claims.add(JWT_CLAIM_ROLE, role.name)
        claims.add(JWT_CLAIM_PERMISSIONS, permissions)
        claims.add(JWT_CLAIM_USERID, userId.toString())
        claims.add(JWT_CLAIM_USERNAME, fullName)
        claims.add(JWT_CLAIM_GROUP_ID, groupId)
        claims.add(JWT_CLAIM_GROUP_NAME, groupName)

        val now = Date()
        val validity = Date(now.time + startupPropertyConfig.sessionValiditySeconds * 1000)
        return Jwts.builder()
            .claims(claims.build())
            .issuedAt(now)
            .expiration(validity)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    @Throws(NoSuchElementException::class)
    fun getAuthentication(token: String): Authentication {
        val parsed = parseToken(token)
        val role = RoleType.valueOf(parsed[JWT_CLAIM_ROLE]?.toString() ?: RoleType.GUEST.name)
        return UsernamePasswordAuthenticationToken(
            CmschUserPrincipal(
                id = parsed[JWT_CLAIM_USERID]?.toString()?.toInt() ?: 0,
                internalId = parsed.subject,
                role = role,
                permissionsAsList = getPermissionsFromClaims(parsed),
                userName = parsed[JWT_CLAIM_USERNAME]?.toString() ?: "unnamed",
                groupId = parsed[JWT_CLAIM_GROUP_ID]?.toString()?.toIntOrNull(),
                groupName = parsed[JWT_CLAIM_GROUP_NAME]?.toString() ?: ""
            ),
            "",
            listOf(SimpleGrantedAuthority("ROLE_${role.name}"))
        )
    }

    private fun getPermissionsFromClaims(claims: Claims): List<String> {
        val permissionClaim = claims[JWT_CLAIM_PERMISSIONS]
        if (permissionClaim !is List<*>) {
            log.error("Invalid JWT format! Permission claim is not a list of strings: {}", permissionClaim)
            return listOf()
        }

        val permissions = permissionClaim.filterIsInstance<String>()
        if (permissions.size != permissionClaim.size) {
            log.error("Invalid JWT format! Permission claim is not a list of strings: {}", permissionClaim)
        }
        return permissions
    }

    fun getInternalId(token: String): String {
        return parser.parseSignedClaims(token).payload.subject
    }

    private fun parseToken(token: String) = parser.parseSignedClaims(token).payload

    fun resolveToken(req: HttpServletRequest): String? {
        val jwtCookie = req.cookies?.find { it.name == "jwt" }?.value
        return jwtCookie
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims: Jws<Claims> = parser.parseSignedClaims(token)
            !claims.payload.expiration.before(Date())
        } catch (e: JwtException) {
            throw InvalidJwtAuthenticationException(EXPIRED_OR_INVALID_TOKEN)
        } catch (e: IllegalArgumentException) {
            throw InvalidJwtAuthenticationException(EXPIRED_OR_INVALID_TOKEN)
        } catch (e: io.jsonwebtoken.security.SignatureException) {
            throw InvalidJwtAuthenticationException(EXPIRED_OR_INVALID_TOKEN)
        }
    }

    fun refreshToken(auth: Authentication): String {
        val user = auth.getUserEntityFromDatabase()
        return createToken(
            userId = user.id,
            internalId = user.internalId,
            role = user.role,
            permissions = user.permissionsAsList,
            fullName = user.fullName,
            groupId = user.groupId,
            groupName = user.groupName
        )
    }
}
