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
    componentSettingService,
    "login",
    "/login",
    "Bejelentkezés",
    ControlPermissions.PERMISSION_CONTROL_APP,
    listOf(UserEntity::class, GroupEntity::class, GroupToUserMappingEntity::class, GuildToUserMappingEntity::class),
    env
) {

    val loginGroup by SettingGroup(fieldName = "Belépés")

    final var title by StringSettingRef("Belépés", fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override var menuDisplayName by StringSettingRef("Belépés", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override var minRole by MinRoleSettingRef(setOf(RoleType.GUEST), minRoleToEdit = RoleType.SUPERUSER,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val authschGroup by SettingGroup(fieldName = "AuthSCH",
        description = "Csak akkor írd át ha tudod mit csinálsz! Ha elrontod nem fog beengedni, szóval óvatosan!"
    )

    var authschScopesRaw by StringSettingRef(        listOf(Scope.BASIC, Scope.SURNAME, Scope.GIVEN_NAME, Scope.EDU_PERSON_ENTILEMENT).joinToString(","),
        serverSideOnly = true, fieldName = "Oauth scopeok",
        description = "Ezek lesznek elkérve a providertől; ezek vannak: " + Scope.entries.joinToString(", ") { it.name }
    )

    val authschScopes = mutableListOf<Scope>()

    override fun onInit() {
        onPersist()
    }

    override fun onPersist() {
        authschScopes.clear()
        val scopes = authschScopesRaw.replace(" ", "").split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { Scope.byNameOrNull(it) }
            .distinct()
        authschScopes.addAll(scopes)
        authschScopesRaw = scopes.joinToString(",") { it.name }
        log.info("Authsch scopes changed to '{}' and saved to the db as: '{}'",
            authschScopes.map { it.name },
            authschScopesRaw)
    }

    var onlyBmeProvider by BooleanSettingRef(false, serverSideOnly = false,
        fieldName = "Címtáron keresztüli belépés", description = "Csak BME címtáron keresztüli belépés jelenik meg"
    )

    var authschPromoted by BooleanSettingRef(true, fieldName = "Authsch opció látszik",
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél látszik az AuthSCH SSO"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val googleSsoGroup by SettingGroup(fieldName = "Google SSO",
        description = "A körtagságok és egyéb körös funkciók ezzel nem működnek automatikusan"
    )

    var googleSsoEnabled by BooleanSettingRef(true, fieldName = "Google opció látszik",
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél látszik az Google SSO"
    )

    var googleAdminAddresses by StringSettingRef("", serverSideOnly = true,
        fieldName = "ADMIN jogú emailcímek", description = "Csak Google auth esetén! Ezeknek a felhasználóknak ADMIN " +
                "joga lesz belépésnél. Az emailcímek vesszővel felsorolva."
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val keycloakGroup by SettingGroup(fieldName = "Keycloak",
        description = "A körtagságok és egyéb körös funkciók ezzel nem működnek automatikusan"
    )

    var keycloakEnabled by BooleanSettingRef(false, fieldName = "Keycloak opció látszik",
        description = "Ha ez be van kapcsolva, akkor a bejelentkezésnél látszik az Keycloak"
    )

    var keycloakAuthName by StringSettingRef("Belső",
        fieldName = "Keycloak gomb felirata", description = "Ezen a néven jelenik meg a bejelentkezési mód."
    )

    var keycloakAdminAddresses by StringSettingRef("", serverSideOnly = true,
        fieldName = "ADMIN jogú emailcímek", description = "Csak Keycloak esetén! Ezeknek a felhasználóknak ADMIN " +
                "joga lesz belépésnél. Az emailcímek vesszővel felsorolva."
    )

    var keycloakSuperuserRole by StringSettingRef("superuser", serverSideOnly = true, fieldName = "SUPERUSER keycloak rule neve",
        description = "Csak Keycloak esetén! Ezeknek a felhasználóknak SYSADMIN joga lesz belépésnél."
    )

    var keycloakAdminRole by StringSettingRef("admin", serverSideOnly = true, fieldName = "ADMIN keycloak rule neve",
        description = "Csak Keycloak esetén! Ezeknek a felhasználóknak ADMIN joga lesz belépésnél."
    )

    var keycloakStaffRole by StringSettingRef("staff", serverSideOnly = true, fieldName = "STAFF keycloak rule neve",
        description = "Csak Keycloak esetén! Ezeknek a felhasználóknak STAFF joga lesz belépésnél."
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val grantRoleGroup by SettingGroup(fieldName = "Automatikus ROLE",
        description = "ROLE = Az oldalhoz való hozzáférés szintje"
    )

    var staffGroups by StringSettingRef("", serverSideOnly = true,
        fieldName = "STAFF jogú Pék csoportok", description = "A pékes decimális id-k felsorolva, pl: 18,106"
    )

    var staffGroupName by StringSettingRef(
        "STAFF",
        serverSideOnly = true,
        fieldName = "Rendező csoport neve",
        description = "Csoport (group) neve amit megkapnak azok akiknek STAFF role is jár. Ha nem létező, akkor nem kapják meg."
    )

    var adminGroups by StringSettingRef("", serverSideOnly = true,
        fieldName = "ADMIN jogú Pék csoportok", description = "A pékes decimális id-k felsorolva, pl: 18,106"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val grantGroupGroup by SettingGroup(fieldName = "Automatikus GROUP",
        description = "GROUP = Csoport az oldalon belül; először a direkt hozzárendelés, aztán a csoport tagság alapján nézi"
    )

    var organizerGroups by StringSettingRef("", serverSideOnly = true,
        fieldName = "Szervező Pék csoportok", description = "A pékes decimális id-k felsorolva, pl: 18,106"
    )

    var organizerGroupName by StringSettingRef(
        "Kiállító",
        serverSideOnly = true,
        fieldName = "Szervező csoport neve",
        description = "Csoport neve amit megkapnak felsorolt pék csoportokból, pl: Kiállító; Ha nem létező, akkor nem kapják meg."
    )

    var fallbackGroupName by StringSettingRef(
        "Vendég",
        serverSideOnly = true,
        fieldName = "Fallback csoport neve",
        description = "Csoport neve, pl: Vendég; Ha nem létező, akkor nem kapják meg."
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val langGroup by SettingGroup(fieldName = "Nyelvi beállítások")

    var topMessage by StringSettingRef("### Válassz belépési módot!", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Felső szöveg", description = "Ha üres akkor nincs ilyen"
    )

    var bottomMessage by StringSettingRef("Mind a két belépési móddal külön felhasználód keletkezik",
        type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Alsó szöveg", description = "Ha üres akkor nincs ilyen"
    )

}
