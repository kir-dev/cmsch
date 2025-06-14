package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.login.authsch.Scope
import hu.bme.sch.cmsch.model.*
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
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
) : ComponentBase(
    "login",
    "/login",
    "Bejelentkezés",
    ControlPermissions.PERMISSION_CONTROL_APP,
    listOf(UserEntity::class, GroupEntity::class, GroupToUserMappingEntity::class, GuildToUserMappingEntity::class),
    env
) {

    final override val allSettings by lazy {
        listOf(
            loginGroup,
            title, menuDisplayName, minRole,

            authschGroup,
            authschScopesRaw,
            onlyBmeProvider,
            authschPromoted,

            googleSsoGroup,
            googleSsoEnabled,
            googleAdminAddresses,

            keycloakGroup,
            keycloakEnabled,
            keycloakAuthName,
            keycloakAdminAddresses,
            keycloakSuperuserRole,
            keycloakAdminRole,
            keycloakStaffRole,

            grantRoleGroup,
            staffGroups,
            staffGroupName,
            adminGroups,

            grantGroupGroup,
            organizerGroups,
            organizerGroupName,
            fallbackGroupName,

            langGroup,
            topMessage,
            bottomMessage,
        )
    }

    val loginGroup = ControlGroup(component, "loginGroup", fieldName = "Belépés")

    final val title = StringSettingRef(componentSettingService, component,
        "title", "Belépés", fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = StringSettingRef(componentSettingService, component,
        "menuDisplayName", "Belépés", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", RoleType.GUEST.name, minRoleToEdit = RoleType.SUPERUSER,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val authschGroup = ControlGroup(component, "authschGroup", fieldName = "AuthSCH",
        description = "Csak akkor írd át ha tudod mit csinálsz! Ha elrontod nem fog beengedni, szóval óvatosan!"
    )

    val authschScopesRaw = StringSettingRef(componentSettingService, component,
        "authschScopes",
        listOf(Scope.BASIC, Scope.SURNAME, Scope.GIVEN_NAME, Scope.EDU_PERSON_ENTILEMENT).joinToString(","),
        type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Oauth scopeok",
        description = "Ezek lesznek elkérve a providertől; ezek vannak: " + Scope.entries.joinToString(", ") { it.name }
    )

    val authschScopes = mutableListOf<Scope>()

    override fun onInit() {
        onPersist()
    }

    override fun onPersist() {
        authschScopes.clear()
        val scopes = authschScopesRaw.getValue().replace(" ", "").split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { Scope.byNameOrNull(it) }
            .distinct()
        authschScopes.addAll(scopes)
        authschScopesRaw.setValue(scopes.joinToString(",") { it.name })
        log.info("Authsch scopes changed to '{}' and saved to the db as: '{}'",
            authschScopes.map { it.name },
            authschScopesRaw.rawValue)
    }

    val onlyBmeProvider = BooleanSettingRef(componentSettingService, component,
        "onlyBmeProvider", false, serverSideOnly = false,
        fieldName = "Címtáron keresztüli belépés", description = "Csak BME címtáron keresztüli belépés jelenik meg"
    )

    val authschPromoted = BooleanSettingRef(componentSettingService, component,
        "authschPromoted", true, fieldName = "Authsch opció látszik",
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél látszik az AuthSCH SSO"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val googleSsoGroup = ControlGroup(component, "googleSsoGroup", fieldName = "Google SSO",
        description = "A körtagságok és egyéb körös funkciók ezzel nem működnek automatikusan"
    )

    val googleSsoEnabled = BooleanSettingRef(componentSettingService, component,
        "googleSsoEnabled", true, fieldName = "Google opció látszik",
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél látszik az Google SSO"
    )

    val googleAdminAddresses = StringSettingRef(componentSettingService, component,
        "googleAdminAddresses", "", serverSideOnly = true,
        fieldName = "ADMIN jogú emailcímek", description = "Csak Google auth esetén! Ezeknek a felhasználóknak ADMIN " +
                "joga lesz belépésnél. Az emailcímek vesszővel felsorolva."
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val keycloakGroup = ControlGroup(component, "keycloakGroup", fieldName = "Keycloak",
        description = "A körtagságok és egyéb körös funkciók ezzel nem működnek automatikusan"
    )

    val keycloakEnabled = BooleanSettingRef(componentSettingService, component,
        "keycloakEnabled", false, fieldName = "Keycloak opció látszik",
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél látszik az Keycloak"
    )

    val keycloakAuthName = StringSettingRef(componentSettingService, component,
        "keycloakAuthName", "Belső",
        fieldName = "Keycloak gomb felirata", description = "Ezen a néven jelenik meg a bejelentkezési mód."
    )

    val keycloakAdminAddresses = StringSettingRef(componentSettingService, component,
        "keycloakAdminAddresses", "", serverSideOnly = true,
        fieldName = "ADMIN jogú emailcímek", description = "Csak Keycloak esetén! Ezeknek a felhasználóknak ADMIN " +
                "joga lesz belépésnél. Az emailcímek vesszővel felsorolva."
    )

    val keycloakSuperuserRole = StringSettingRef(componentSettingService, component,
        "keycloakSuperuserRole", "superuser", serverSideOnly = true, fieldName = "SUPERUSER keycloak rule neve",
        description = "Csak Keycloak esetén! Ezeknek a felhasználóknak SYSADMIN joga lesz belépésnél."
    )

    val keycloakAdminRole = StringSettingRef(componentSettingService, component,
        "keycloakAdminRole", "admin", serverSideOnly = true, fieldName = "ADMIN keycloak rule neve",
        description = "Csak Keycloak esetén! Ezeknek a felhasználóknak ADMIN joga lesz belépésnél."
    )

    val keycloakStaffRole = StringSettingRef(componentSettingService, component,
        "keycloakStaffRole", "staff", serverSideOnly = true, fieldName = "STAFF keycloak rule neve",
        description = "Csak Keycloak esetén! Ezeknek a felhasználóknak STAFF joga lesz belépésnél."
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val grantRoleGroup = ControlGroup(component, "grantRoleGroup", fieldName = "Automatikus ROLE",
        description = "ROLE = Az oldalhoz való hozzáférés szintje"
    )

    val staffGroups = StringSettingRef(componentSettingService, component,
        "staffGroups", "", serverSideOnly = true,
        fieldName = "STAFF jogú Pék csoportok", description = "A pékes decimális id-k felsorolva, pl: 18,106"
    )

    val staffGroupName = StringSettingRef(componentSettingService,
        component,
        "staffGroupName",
        "STAFF",
        serverSideOnly = true,
        fieldName = "Rendező csoport neve",
        description = "Csoport (group) neve amit megkapnak azok akiknek STAFF role is jár. Ha nem létező, akkor nem kapják meg."
    )

    val adminGroups = StringSettingRef(componentSettingService, component,
        "adminGroups", "", serverSideOnly = true,
        fieldName = "ADMIN jogú Pék csoportok", description = "A pékes decimális id-k felsorolva, pl: 18,106"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val grantGroupGroup = ControlGroup(component, "grantGroupGroup", fieldName = "Automatikus GROUP",
        description = "GROUP = Csoport az oldalon belül; először a direkt hozzárendelés, aztán a csoport tagság alapján nézi"
    )

    val organizerGroups = StringSettingRef(componentSettingService, component,
        "organizerGroups", "", serverSideOnly = true,
        fieldName = "Szervező Pék csoportok", description = "A pékes decimális id-k felsorolva, pl: 18,106"
    )

    val organizerGroupName = StringSettingRef(componentSettingService,
        component,
        "organizerGroupName",
        "Kiállító",
        serverSideOnly = true,
        fieldName = "Szervező csoport neve",
        description = "Csoport neve amit megkapnak felsorolt pék csoportokból, pl: Kiállító; Ha nem létező, akkor nem kapják meg."
    )

    val fallbackGroupName = StringSettingRef(componentSettingService,
        component,
        "fallbackGroupName",
        "Vendég",
        serverSideOnly = true,
        fieldName = "Fallback csoport neve",
        description = "Csoport neve, pl: Vendég; Ha nem létező, akkor nem kapják meg."
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val langGroup = ControlGroup(component, "langGroup", fieldName = "Nyelvi beállítások")

    val topMessage = StringSettingRef(componentSettingService, component,
        "topMessage", "### Válassz belépési módot!", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Felső szöveg", description = "Ha üres akkor nincs ilyen"
    )

    val bottomMessage = StringSettingRef(componentSettingService, component,
        "bottomMessage", "Mind a két belépési móddal külön felhasználód keletkezik",
        type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Alsó szöveg", description = "Ha üres akkor nincs ilyen"
    )

}
