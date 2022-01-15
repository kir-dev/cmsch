package hu.bme.sch.cmsch.controller.api

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dao.SoldProductRepository
import hu.bme.sch.cmsch.dto.DebtDto
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.GroupEntityDto
import hu.bme.sch.cmsch.dto.GroupMemberLocationDto
import hu.bme.sch.cmsch.dto.view.ProfileView
import hu.bme.sch.cmsch.service.ClockService
import hu.bme.sch.cmsch.service.LocationService
import hu.bme.sch.cmsch.service.RealtimeConfigService
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
class ProfileApiController(
    private val config: RealtimeConfigService,
    private val debtsRepository: SoldProductRepository,
    private val locationService: LocationService,
    private val clock: ClockService
) {

    @JsonView(FullDetails::class)
    @GetMapping("/profile")
    fun profile(request: HttpServletRequest): ProfileView {
        val user = request.getUserOrNull() ?: return ProfileView(false, UNKNOWN_USER, group = null)
        if (config.isSiteLowProfile())
            return ProfileView(false, UNKNOWN_USER, group = null)

        val group = user.group?.let { GroupEntityDto(it) }
        return ProfileView(
            loggedin = true,
            user = user,
            group = group,
            locations = locationService.findLocationsOfGroup(group?.name ?: "")
                .filter { it.timestamp + 600 > clock.getTimeInSeconds() }
                .map { GroupMemberLocationDto(
                    it.alias.ifBlank { it.userName },
                    it.longitude,
                    it.latitude,
                    it.accuracy,
                    it.timestamp
                ) },
            debts = debtsRepository.findAllByOwnerId(user.id)
                .map { DebtDto(
                    it.product,
                    it.price,
                    it.sellerName,
                    it.responsibleName,
                    it.payed,
                    it.shipped,
                    it.log,
                    it.materialIcon
                ) }
        )
    }

}
