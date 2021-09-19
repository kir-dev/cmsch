package hu.bme.sch.g7.controller

import hu.bme.sch.g7.model.LocationEntity
import hu.bme.sch.g7.service.LocationService
import hu.bme.sch.g7.util.getUser
import hu.bme.sch.g7.util.getUserOrNull
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest

@Controller
//@CrossOrigin(origins = ["*"], allowedHeaders = ["*"], allowCredentials = "true")
class TrackLocationController(
        private val locationService: LocationService
) {

    @ResponseBody
    @GetMapping("/api/track")
    fun api(request: HttpServletRequest): List<LocationEntity> {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantTracker }?.not() ?: true) {
            return listOf()
        }
        return locationService.findAllLocation()
    }

    @ResponseBody
    @GetMapping("/api/track/{groupName}")
    fun apiGroup(@PathVariable groupName: String, request: HttpServletRequest): List<LocationEntity> {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantTracker || it.grantListUsers || it.grantGroupManager }?.not() ?: true) {
            return listOf()
        }
        return locationService.findLocationsOfGroup(groupName)
    }

    @GetMapping("/admin/control/tracking")
    fun view(request: HttpServletRequest, model: Model): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantTracker }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("url", "/api/track")
        return "tracker"
    }

    @GetMapping("/admin/control/tracking/{groupName}")
    fun viewGroup(@PathVariable groupName: String, request: HttpServletRequest, model: Model): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantTracker || it.grantListUsers || it.grantGroupManager }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("url", "/api/track/${groupName}")
        return "tracker"
    }

    @GetMapping("/admin/control/locations/clean")
    fun clean(): String {
        locationService.clean()
        return "redirect:/admin/control/locations"
    }

    @GetMapping("/admin/control/locations/refresh")
    fun refresh(): String {
        locationService.refresh()
        return "redirect:/admin/control/locations"
    }
}