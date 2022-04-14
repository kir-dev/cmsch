package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.component.*
import hu.gerviba.authsch.struct.Scope
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["login"],
    havingValue = "true",
    matchIfMissing = false
)
class LoginComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase("login", "/profile", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            minRole,

            authschGroup,
            authschScopesRaw,
            loginBaseUrl,

            grantRoleGroup,
            staffGroups,
            adminGroups,

            grantGroupGroup,
            organizerGroups,
            organizerGroupName,
            fallbackGroupName,
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val authschGroup = SettingProxy(componentSettingService, component,
        "authschGroup", "", type = SettingType.COMPONENT_GROUP, persist = false, serverSideOnly = true,
        fieldName = "AuthSCH",
        description = "Csak akkor írd át ha tudod mit csinálsz! Ha elrontod nem fog beengedni, szóval óvatosan!"
    )

    val authschScopesRaw = SettingProxy(componentSettingService, component,
        "authschScopes",
        listOf(Scope.BASIC, Scope.SURNAME, Scope.GIVEN_NAME, Scope.EDU_PERSON_ENTILEMENT).joinToString(","),
        type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Oauth scopeok",
        description = "Ezek lesznek elkérve a providertől; ezek vannak: "
                + Scope.values().joinToString(",") { it.name }
    )

    val authschScopes = mutableListOf<Scope>()

    override fun onValuesUpdated() {
        authschScopes.clear()
        val scopes = authschScopesRaw.getValue().split(", ")
            .filter { it.isNotBlank() }
            .mapNotNull { Scope.byScopeOrNull(it) }
            .distinct()
        authschScopes.addAll(scopes)
        authschScopesRaw.setAndPersistValue(scopes.joinToString(",") { it.name })
    }

    val loginBaseUrl = SettingProxy(componentSettingService, component,
        "loginBaseUrl", "https://auth.sch.bme.hu/site/login", serverSideOnly = true,
        fieldName = "Belépés base url", description = ""
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val grantRoleGroup = SettingProxy(componentSettingService, component,
        "grantRoleGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Automatikus ROLE",
        description = "ROLE = Az oldalhoz való hozzáférés szintje"
    )

    val staffGroups = SettingProxy(componentSettingService, component,
        "staffGroups", "18,106", serverSideOnly = true,
        fieldName = "STAFF jogú Pék coportok", description = "A pékes decimális id-k felsorolva, pl: 18,106"
    )

    val staffGroupName = SettingProxy(componentSettingService, component,
        "staffGroupName", "STAFF", serverSideOnly = true,
        fieldName = "Rendező csoport neve", description = "Csoport (group) neve amit megkapnak azok akiknek STAFF role is jár. Ha nem létező, akkor nem kapják meg."
    )

    val adminGroups = SettingProxy(componentSettingService, component,
        "adminGroups", "18,106", serverSideOnly = true,
        fieldName = "ADMIN jogú Pék coportok", description = "A pékes decimális id-k felsorolva, pl: 18,106"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val grantGroupGroup = SettingProxy(componentSettingService, component,
        "grantGroupGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Automatikus GROUP",
        description = "GROUP = Csoport az oldalon belül; először a dirket hozzárendelés, aztán a csoport tagság alapján nézi"
    )

    val organizerGroups = SettingProxy(componentSettingService, component,
        "organizerGroups", "1,2", serverSideOnly = true,
        fieldName = "Szervező Pék csoportok", description = "A pékes decimális id-k felsorolva, pl: 18,106"
    )

    val organizerGroupName = SettingProxy(componentSettingService, component,
        "organizerGroupName", "Kiállító", serverSideOnly = true,
        fieldName = "Szervező csoport neve", description = "Csoport neve amit megkapnak felsorolt pék csoportokból, pl: Kiállító; Ha nem létező, akkor nem kapják meg."
    )

    val fallbackGroupName = SettingProxy(componentSettingService, component,
        "fallbackGroupName", "Vendég", serverSideOnly = true,
        fieldName = "Alap csoport neve", description = "Csoport neve, pl: Vendég; Ha nem létező, akkor nem kapják meg."
    )

}
