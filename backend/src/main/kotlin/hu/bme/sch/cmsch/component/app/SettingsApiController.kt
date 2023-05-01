package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.util.getUser
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/api/settings")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
class SettingsApiController(
    private val adminMenuService: AdminMenuService
) {

    @PostMapping("/favorite")
    fun toggleFavorite(@RequestBody menu: String, auth: Authentication): String {
        adminMenuService.toggleFavoriteMenu(auth.getUser(), menu)
        return "ok"
    }

    @PostMapping("/dismiss-motd")
    fun dismissMotd(@RequestBody motd: String, auth: Authentication): String {
        adminMenuService.dismissMotd(auth.getUser(), motd)
        return "ok"
    }

}