package hu.bme.sch.g7.controller

import hu.bme.sch.g7.admin.OverviewBuilder
import hu.bme.sch.g7.dto.LocationDto
import hu.bme.sch.g7.model.LocationEntity
import hu.bme.sch.g7.model.SoldProductEntity
import hu.bme.sch.g7.service.LocationService
import hu.bme.sch.g7.util.getUser
import hu.bme.sch.g7.util.getUserOrNull
import org.springframework.boot.context.properties.bind.Bindable.listOf
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/api")
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"], allowCredentials = "false")
class TrackLocationController(
        private val locationService: LocationService
) {

    private val overviewDescriptor = OverviewBuilder(LocationEntity::class)

    @ResponseBody
    @GetMapping("/track")
    fun api(request: HttpServletRequest): List<LocationEntity> {
//        return listOf(
//                LocationEntity(0, 1, "Szabó Gergely", "VINYÓ", "LEAD", 19.0539376, 47.4728981, 20f, 183.0, System.currentTimeMillis() / 1000L),
//                LocationEntity(0, 1, "Szabó Gergely", "DOMA", "LEAD", 19.0549376, 47.4738981, 20f, 183.0, System.currentTimeMillis() / 1000L),
//                LocationEntity(0, 1, "Szabó Gergely", "SCHÁMI", "SUPPORT", 19.0529376, 47.4738981, 20f, 183.0, System.currentTimeMillis() / 1000L),
//                LocationEntity(1, 1, "Szabó Gergely 2", "PIROS", "MANAGER", 19.0539376, 47.4748981, 20f, 183.0, System.currentTimeMillis() / 1000L),
//                LocationEntity(2, 3, "Szabó Gergely 3", "", "I22", 19.0529376, 47.4728981, 20f, 183.0, System.currentTimeMillis() / 1000L),
//
//        );

        if (request.getUserOrNull()?.isAdmin()?.not() ?: true) {
            return listOf();
        }
        return locationService.findAllLocation()
    }

    @GetMapping("/tracking")
    fun view(): String {
        return "tracker"
    }

}