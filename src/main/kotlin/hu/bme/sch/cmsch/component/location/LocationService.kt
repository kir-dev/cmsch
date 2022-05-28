package hu.bme.sch.cmsch.component.location

import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.ClockService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
@ConditionalOnBean(LocationComponent::class)
class LocationService(
    private val clock: ClockService,
    private val userRepository: UserRepository,
    private val startupPropertyConfig: StartupPropertyConfig
) {

    private val tokenToLocationMapping = ConcurrentHashMap<String, LocationEntity>()

    fun pushLocation(locationDto: LocationDto): LocationResponse {
        if (!tokenToLocationMapping.containsKey(locationDto.token)) {
            val user = userRepository.findByCmschId(startupPropertyConfig.profileQrPrefix + locationDto.token)
            if (user.isPresent) {
                if (user.get().role.value >= RoleType.STAFF.value) {
                    tokenToLocationMapping[locationDto.token] =
                        LocationEntity(0, user.get().id, user.get().fullName, user.get().alias, user.get().groupName)
                } else {
                    return LocationResponse("jogosulatlan", "n/a")
                }
            } else {
                return LocationResponse("nem jogosult", "n/a")
            }
        }

        val groupName = tokenToLocationMapping[locationDto.token]?.let {
            it.longitude = locationDto.longitude
            it.latitude = locationDto.latitude
            it.altitude = locationDto.altitude
            it.accuracy = locationDto.accuracy
            it.timestamp = clock.getTimeInSeconds()
            return@let it.groupName
        }

        return if (groupName != null)
            LocationResponse("OK", groupName)
        else
            LocationResponse("nem található", "n/a")

    }

    fun findAllLocation(): List<LocationEntity> {
        return tokenToLocationMapping.values.toList()
                .sortedBy { it -> it.groupName.length.toString() + it.groupName }
    }

    fun clean() {
        return tokenToLocationMapping.clear()
    }

    fun refresh() {
        return tokenToLocationMapping.keys()
                .asSequence()
                .forEach { token ->
                    tokenToLocationMapping[token]?.let { it ->
                        val user = userRepository.findByCmschId(startupPropertyConfig.profileQrPrefix + token)
                        it.userId = user.get().id
                        it.userName = user.get().fullName
                        it.alias = user.get().alias
                        it.groupName = user.get().groupName
                    }
                }
    }

    fun findLocationsOfGroup(groupName: String): List<LocationEntity> {
        return tokenToLocationMapping.values.filter { it.groupName == groupName }
    }

    fun getRecentLocations(): List<LocationEntity> {
        val range = clock.getTimeInSeconds() + 600
        return tokenToLocationMapping.values.filter { it.timestamp < range }
    }

}
