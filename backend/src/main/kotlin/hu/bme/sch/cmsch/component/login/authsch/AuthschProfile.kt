package hu.bme.sch.cmsch.component.login.authsch

import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.core.oidc.user.OidcUser

class AuthschProfile private constructor(
    val internalId: String,
    val email: String?,
    val secondaryEmail: String?,
    val fullName: String,
    val neptun: String?,
    val activeMemberships: List<ActiveMembership>,
    val unitScopes: List<BMEUnitScope>?,
) {

    companion object {
        private val log = LoggerFactory.getLogger(AuthschProfile::class.java)

        const val SAM_ACCOUNT_NAME = "directory.sch.bme.hu:sAMAccountName"
        const val EMAIL = "email"
        const val NEPTUN = "bme.hu:niifPersonOrgID"
        const val UNIT_SCOPE = "meta.bme.hu:unitScope/v1"
        const val ACTIVE_MEMBERSHIPS = "pek.sch.bme.hu:activeMemberships/v1"

        fun from(oidcUser: OidcUser): AuthschProfile {
            val internalId = oidcUser.subject
                ?: throw IllegalStateException("The AuthSCH userinfo response contains no 'sub' claim")

            val fullName = listOfNotNull(
                oidcUser.getClaim<Any>("family_name") as? String,
                oidcUser.getClaim<Any>("given_name") as? String
            ).joinToString(" ").ifBlank { oidcUser.getClaim<Any>("name") as? String ?: "" }

            val (email, secondaryEmail) = readEmails(oidcUser)

            return AuthschProfile(
                internalId = internalId,
                email = email,
                secondaryEmail = secondaryEmail,
                fullName = fullName,
                neptun = oidcUser.getClaim<Any>(NEPTUN) as? String,
                activeMemberships = parseActiveMemberships(oidcUser),
                unitScopes = parseUnitScopes(oidcUser)
            )
        }


        private fun readEmails(oidcUser: OidcUser): Pair<String?, String?> {
            val samAccountName = (oidcUser.getClaim<Any>(SAM_ACCOUNT_NAME) as? String)?.takeIf { it.isNotBlank() }
            val fallback = (oidcUser.getClaim<Any>(EMAIL) as? String)?.takeIf { it.isNotBlank() }

            if (samAccountName != null)
                return "$samAccountName@sch.bme.hu" to fallback

            if (fallback == null) {
                log.warn("Neither the {} nor the {} claim is present for user {}, so the user is saved without an email", SAM_ACCOUNT_NAME, EMAIL, oidcUser.subject)
                return null to null
            }
            log.info("The {} claim is missing for user {}, saving the {} claim as the secondary email address", SAM_ACCOUNT_NAME, oidcUser.subject, EMAIL)
            return null to fallback
        }

        private fun parseActiveMemberships(oidcUser: OidcUser): List<ActiveMembership> {
            val raw = oidcUser.getClaim<Any>(ACTIVE_MEMBERSHIPS)
            if (raw == null) {
                log.warn("The {} claim is missing, so no staff/admin/organizer membership can be read", ACTIVE_MEMBERSHIPS)
                return listOf()
            }
            val memberships = (raw as? List<*>)?.filterIsInstance<Map<*, *>>() ?: run {
                log.warn("The {} claim is a {} instead of a list, ignoring it", ACTIVE_MEMBERSHIPS, raw::class.simpleName)
                return listOf()
            }
            val parsed = memberships.mapNotNull { entry ->
                runCatching {
                    ActiveMembership(
                        id = (entry["id"] as? Number)?.toLong() ?: entry["id"]?.toString()?.toLongOrNull() ?: 0L,
                        name = entry["name"]?.toString() ?: "",
                        title = (entry["title"] as? List<*>).orEmpty().map { title -> title.toString() }
                    )
                }.onFailure { error ->
                    log.warn("Could not parse the activeMemberships entry {}: {}", entry, error.toString())
                }.getOrNull()
            }
            if (parsed.size != memberships.size)
                log.warn("Skipped {} malformed activeMemberships entries", memberships.size - parsed.size)
            return parsed
        }

        private fun parseUnitScopes(oidcUser: OidcUser): List<BMEUnitScope>? {
            val raw = oidcUser.getClaim<Any>(UNIT_SCOPE) ?: return null
            val names = when (raw) {
                is String -> raw.split(Regex("[\\s,]+")).filter { it.isNotBlank() }
                is Collection<*> -> raw.mapNotNull { it?.toString() }
                else -> {
                    log.warn("Unexpected type {} for the {} claim, ignoring it", raw::class.simpleName, UNIT_SCOPE)

                    // Don't erase the old claim from DB if we can't parse the new one
                    return null
                }
            }
            return names.mapNotNull { name ->
                BMEUnitScope.byNameOrNull(name).also {
                    if (it == null) log.warn("Unknown BME unit scope '{}', ignoring it", name)
                }
            }
        }
    }
}
