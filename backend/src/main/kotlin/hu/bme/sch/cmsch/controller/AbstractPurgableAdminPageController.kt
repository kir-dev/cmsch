package hu.bme.sch.cmsch.controller

import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_PURGE
import hu.bme.sch.cmsch.util.getUser
import org.slf4j.LoggerFactory
import org.springframework.data.repository.CrudRepository
import org.springframework.security.core.Authentication
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

abstract class AbstractPurgeAdminPageController<T : ManagedEntity>(
    private val repo: CrudRepository<*, Int>?,
    private val adminMenuService: AdminMenuService,
    private val titlePlural: String,
    private val view: String,
    private val allowedToPurge: Boolean
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/purge")
    fun purge(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (!allowedToPurge || PERMISSION_PURGE.validate(user).not()) {
            model.addAttribute("permission", PERMISSION_PURGE.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("view", view)
        model.addAttribute("user", user)
        return "purge"
    }

    @PostMapping("/purge")
    fun purgeConfirmed(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        if (!allowedToPurge || PERMISSION_PURGE.validate(user).not()) {
            log.info("User '{}'#{} wanted to purge view '{}'", user.userName, user.id, view)
            throw IllegalStateException("Insufficient permissions")
        }

        if (repo != null) {
            log.info("Purging view '{}' by user '{}'#{}", view, user.userName, user.id)
            val before = repo.count()
            try {
                repo.deleteAll()
            } catch (e : Exception) {
                log.error("Purging failed on view '{}'", view, e)
            }
            val after = repo.count()
            model.addAttribute("purgedCount", before - after)
            log.info("Purged {} on view '{}'", before - after, view)
        } else {
            log.info("Purging is disabled for virtual entities (on view: {})", view)
            model.addAttribute("purgedCount", 0)
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("view", view)
        model.addAttribute("user", user)
        adminMenuService.addPartsForMenu(user, model)
        return "purge"
    }

}
