package hu.bme.sch.g7.service

 import hu.bme.sch.g7.dao.LocationRepository
import hu.bme.sch.g7.dao.UserRepository
import hu.bme.sch.g7.dto.LocationDto
import hu.bme.sch.g7.g7mobile.LocationResponse
import hu.bme.sch.g7.model.LocationEntity
import hu.bme.sch.g7.model.RoleType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LocationService(
        private val clock: ClockService,
        private val locationRepository: LocationRepository,
        private val userRepository: UserRepository,
        @Value("\${g7web.profile.qr-prefix:G7_}") val prefix: String
) {

    private val tokenToUserIdMapping = mutableMapOf<String, Int>()

    @Transactional
    fun pushLocation(locationDto: LocationDto): LocationResponse {
        if (!tokenToUserIdMapping.containsKey(locationDto.token)) {
            val user = userRepository.findByG7id(prefix + locationDto.token)
            if (user.isPresent) {
                if (user.get().role.value >= RoleType.STAFF.value) {
                    tokenToUserIdMapping.put(locationDto.token, user.get().id)
                    if (locationRepository.findByUserId(user.get().id).isEmpty) {
                        locationRepository.save(LocationEntity(0, user.get().id, user.get().fullName,  user.get().alias, user.get().groupName))
                    }
                } else {
                    return LocationResponse("jogosulatlan", "n/a")
                }
            } else {
                return LocationResponse("nem jogosult", "n/a")
            }
        }

        val groupName = locationRepository.findByUserId(tokenToUserIdMapping
                .getOrDefault(locationDto.token, 0))
                .map {
                    it.longitude = locationDto.longitude
                    it.latitude = locationDto.latitude
                    it.altitude = locationDto.altitude
                    it.accuracy = locationDto.accuracy
                    it.timestamp = clock.getTimeInSeconds()
                    locationRepository.save(it)
                    it.groupName
                }
                .orElse(null)

        return if (groupName != null)
            LocationResponse("OK", groupName)
        else
            LocationResponse("nem található", "n/a")

    }

    @Transactional(readOnly = true)
    fun findAllLocation(): List<LocationEntity> {
        return locationRepository.findAll()
    }

}