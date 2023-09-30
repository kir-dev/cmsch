package hu.bme.sch.cmsch.dto

import com.fasterxml.jackson.annotation.JsonProperty
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.PermissionValidator

data class SiteContext(
    val siteName: String,
    val production: String,
    val motd: String,
    val siteUrl: String,
    val frontendUrl: String,
    val version: String,
    var rpm: Int,
    var activeUsers: Int,
    var brandColor: String,
    var brandColorDarker: String,
    var brandColorDarker2: String
)

data class UserSiteContext(
    val userName: String,
    val email: String,
    val emailHash: String,
    val profilePicture: String,
    val role: RoleType,
    val group: String,
    val favoriteMenus: MutableList<String>,
    var dismissedMotd: String,
    val permissions: List<String>,
    val resources: List<SearchableResource>,
    val cacheCreated: Long
)

enum class SearchableResourceType(
    val icon: String,
    val cssClass: String
) {
    MENU("menu", "tag-menu"),
    PROPERTY("sell", "tag-property"),
    ENTITY("layers", "tag-entity"),
    ANCHOR("anchor", "tag-anchor")
}

data class SearchableResource(
    val name: String,
    val type: SearchableResourceType,
    val description: String,
    val permission: PermissionValidator,
    val target: String,
)

data class UserConfig(
    @JsonProperty(required = false)
    var favoriteMenus: MutableList<String> = mutableListOf(),

    @JsonProperty(required = false)
    var dismissedMotd: String = ""
)