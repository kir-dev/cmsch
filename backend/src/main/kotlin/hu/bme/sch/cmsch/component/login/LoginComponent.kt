package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.component.login.authsch.Scope
import hu.bme.sch.cmsch.model.RoleType
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
) : ComponentBase("login", "/login", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            title, menuDisplayName, minRole,

            authschGroup,
            authschScopesRaw,
            onlyBmeProvider,
            authschPromoted,

            googleSsoGroup,
            googleSsoEnabled,
            googleAdminAddresses,

            grantRoleGroup,
            staffGroups,
            adminGroups,

            grantGroupGroup,
            organizerGroups,
            organizerGroupName,
            fallbackGroupName,

            langGroup,
            bottomMessage
        )
    }

    final val title = SettingProxy(componentSettingService, component,
        "title", "Belépés",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Belépés", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
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
        type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Oauth scopeok",
        description = "Ezek lesznek elkérve a providertől; ezek vannak: "
                + Scope.values().joinToString(", ") { it.name }
    )

    val authschScopes = mutableListOf<Scope>()

    override fun onInit() {
        onPersis()
    }

    override fun onPersis() {
        authschScopes.clear()
        val scopes = authschScopesRaw.getValue().replace(" ", "").split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { Scope.byNameOrNull(it) }
            .distinct()
        authschScopes.addAll(scopes)
        authschScopesRaw.setAndPersistValue(scopes.joinToString(",") { it.name })
        log.info("Authsch scopes changed to '{}' and saved to the db as: '{}'", authschScopes.map { it.name }, authschScopesRaw.rawValue)
    }

    val onlyBmeProvider = SettingProxy(componentSettingService, component,
        "onlyBmeProvider", "false", type = SettingType.BOOLEAN, serverSideOnly = false,
        fieldName = "Címtáron keresztüli belépés", description = "Csak BME címtáron keresztüli belépés jelenik meg"
    )

    val authschPromoted = SettingProxy(componentSettingService, component,
        "authschPromoted", "true", type = SettingType.BOOLEAN,
        fieldName = "Authsch opció látszik", description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél látszik az AuthSCH SSO"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val googleSsoGroup = SettingProxy(componentSettingService, component,
        "googleSsoGroup", "", type = SettingType.COMPONENT_GROUP, persist = false, serverSideOnly = true,
        fieldName = "Google SSO",
        description = "A körtagságok és egyéb körös funkciók ezzel nem működnek automatikusan"
    )

    val googleSsoEnabled = SettingProxy(componentSettingService, component,
        "googleSsoEnabled", "false", type = SettingType.BOOLEAN,
        fieldName = "Google opció látszik", description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél látszik az Google SSO"
    )

    val googleAdminAddresses = SettingProxy(componentSettingService, component,
        "googleAdminAddresses", "", serverSideOnly = true,
        fieldName = "ADMIN jogú emailcímek", description = "Google auth esetén csak! Ezeknek a felhasználóknak ADMIN " +
                "joga lesz belépésnél. Az emailcímek vesszővel felsorolva."
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val grantRoleGroup = SettingProxy(componentSettingService, component,
        "grantRoleGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Automatikus ROLE",
        description = "ROLE = Az oldalhoz való hozzáférés szintje"
    )

    val staffGroups = SettingProxy(componentSettingService, component,
        "staffGroups", "", serverSideOnly = true,
        fieldName = "STAFF jogú Pék coportok", description = "A pékes decimális id-k felsorolva, pl: 18,106"
    )

    val staffGroupName = SettingProxy(componentSettingService, component,
        "staffGroupName", "STAFF", serverSideOnly = true,
        fieldName = "Rendező csoport neve", description = "Csoport (group) neve amit megkapnak azok akiknek STAFF role is jár. Ha nem létező, akkor nem kapják meg."
    )

    val adminGroups = SettingProxy(componentSettingService, component,
        "adminGroups", "", serverSideOnly = true,
        fieldName = "ADMIN jogú Pék coportok", description = "A pékes decimális id-k felsorolva, pl: 18,106"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val grantGroupGroup = SettingProxy(componentSettingService, component,
        "grantGroupGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Automatikus GROUP",
        description = "GROUP = Csoport az oldalon belül; először a dirket hozzárendelés, aztán a csoport tagság alapján nézi"
    )

    val organizerGroups = SettingProxy(componentSettingService, component,
        "organizerGroups", "", serverSideOnly = true,
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

    /// -------------------------------------------------------------------------------------------------------------------

    val langGroup = SettingProxy(componentSettingService, component,
        "langGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Nyelvi bellítások",
        description = ""
    )

    val bottomMessage = SettingProxy(componentSettingService, component,
        "bottomMessage", "Mind a két belépési móddal külön felhasználód keletkezik",
        type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Alsó szöveg", description = "Ha üres akkor nincs ilyen"
    )


}
