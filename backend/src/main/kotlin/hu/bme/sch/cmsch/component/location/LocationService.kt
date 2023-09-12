package hu.bme.sch.cmsch.component.location

import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.repository.EntityPageDataSource
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.TimeService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
@ConditionalOnBean(LocationComponent::class)
class LocationService(
    private val clock: TimeService,
    private val userRepository: UserRepository,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val waypointRepository: WaypointRepository,
    private val locationComponent: LocationComponent
) : EntityPageDataSource<LocationEntity, Int> {

    private val tokenToLocationMapping = ConcurrentHashMap<String, LocationEntity>()

    fun pushLocation(locationDto: LocationDto): LocationResponse {
        if (!tokenToLocationMapping.containsKey(locationDto.token)) {
            val user = userRepository.findByCmschId(startupPropertyConfig.profileQrPrefix + locationDto.token)
            if (user.isPresent) {
                if (user.get().role.value >= RoleType.STAFF.value) {
                    tokenToLocationMapping[locationDto.token] =
                        LocationEntity(
                            id = 0,
                            userId = user.get().id,
                            userName = user.get().fullName,
                            alias = user.get().alias,
                            groupName = user.get().groupName,
                            markerColor = resolveColor(user.get().groupName)
                        )
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
            it.speed = locationDto.speed
            it.altitudeAccuracy = locationDto.altitudeAccuracy
            it.heading = locationDto.heading
            it.timestamp = clock.getTimeInSeconds()
            return@let it.groupName
        }

        return if (groupName != null) {
            LocationResponse("OK", groupName)
        } else {
            LocationResponse("nem található", "n/a")
        }
    }

    private fun resolveColor(groupName: String): String {
        return when (groupName) {
            locationComponent.blackGroupName.getValue()  -> { "#000000" }
            locationComponent.blueGroupName.getValue()   -> { "#5fa8d3" }
            locationComponent.cyanGroupName.getValue()   -> { "#70d6ff" }
            locationComponent.pinkGroupName.getValue()   -> { "#ff70a6" }
            locationComponent.orangeGroupName.getValue() -> { "#f8961e" }
            locationComponent.greenGroupName.getValue()  -> { "#a7c957" }
            locationComponent.redGroupName.getValue()    -> { "#ef233c" }
            locationComponent.whiteGroupName.getValue()  -> { "#ffffff" }
            locationComponent.yellowGroupName.getValue() -> { "#fee440" }
            locationComponent.purpleGroupName.getValue() -> { "#9d4edd" }
            locationComponent.grayGroupName.getValue()   -> { "#c0c0c0" }
            else -> { locationComponent.defaultGroupColor.getValue() }
        }
    }

    fun findAllLocation(): List<LocationEntity> {
        return tokenToLocationMapping.values.toList()
                .sortedBy { it.groupName }
    }

    fun clean() {
        return tokenToLocationMapping.clear()
    }

    fun refresh() {
        return tokenToLocationMapping.keys()
                .asSequence()
                .forEach { token ->
                    tokenToLocationMapping[token]?.let {
                        val user = userRepository.findByCmschId(startupPropertyConfig.profileQrPrefix + token)
                        it.userId = user.get().id
                        it.userName = user.get().fullName
                        it.alias = user.get().alias
                        it.groupName = user.get().groupName
                    }
                }
    }

    fun findLocationsOfGroup(groupId: Int): List<LocationEntity> {
        return tokenToLocationMapping.values.filter { it.id == groupId }
    }

    fun findLocationsOfGroupName(group: String): List<MapMarker> {
        val locations = mutableListOf<MapMarker>()
        locations.addAll(tokenToLocationMapping.values
            .filter { it.groupName == group }
            .map { MapMarker(
                displayName = mapDisplayName(it),
                longitude = it.longitude,
                latitude = it.latitude,
                altitude = it.altitude,
                accuracy = it.accuracy,
                altitudeAccuracy = it.altitudeAccuracy,
                heading = it.heading,
                speed = it.speed,
                timestamp = it.timestamp,
                markerShape = it.markerShape,
                markerColor = it.markerColor,
                description = it.description,
            ) }
        )
        locations.addAll(waypointRepository.findAll().map { it.toMapMarker() })
        return locations
    }

    private fun mapDisplayName(location: LocationEntity): String {
        return if (locationComponent.showGroupName.isValueTrue() && location.groupName.isNotBlank()) {
            if (locationComponent.showUserName.isValueTrue()) {
                if (locationComponent.showAlias.isValueTrue() && location.alias.isNotBlank()) {
                    "${location.userName} (${location.alias}) [${location.groupName}]"
                } else {
                    "${location.userName} [${location.groupName}]"
                }
            } else if (locationComponent.showAlias.isValueTrue()) {
                "${location.alias} [${location.groupName}]"
            } else {
                location.groupName
            }
        } else {
            if (locationComponent.showUserName.isValueTrue()) {
                if (locationComponent.showAlias.isValueTrue() && location.alias.isNotBlank()) {
                    "${location.userName} (${location.alias})"
                } else {
                    location.userName
                }
            } else if (locationComponent.showAlias.isValueTrue()) {
                location.alias
            } else {
                ""
            }
        }
    }

    fun getRecentLocations(): List<LocationEntity> {
        val range = clock.getTimeInSeconds() + 600
        return tokenToLocationMapping.values.filter { it.timestamp < range }
    }

    override fun findAll() = findAllLocation()

    override fun count() = findAllLocation().size.toLong()

    override fun deleteAll() = clean()

    override fun <S : LocationEntity?> saveAll(entities: Iterable<S>): Iterable<S> {
        return entities.filterNotNull().map { save(it) }
    }

    override fun <S : LocationEntity> save(entity: S): S {
        val keyToUpdate = tokenToLocationMapping.entries.find { it.value.id == entity.id }?.key
        if (keyToUpdate != null)
            tokenToLocationMapping[keyToUpdate] = entity
        return entity
    }

    override fun delete(entity: LocationEntity) {
        val keyToRemove = tokenToLocationMapping.entries.find { it.value.id == entity.id }?.key
        if (keyToRemove != null)
            tokenToLocationMapping.remove(keyToRemove)
    }

    override fun findById(id: Int): Optional<LocationEntity> {
        return Optional.ofNullable(tokenToLocationMapping.entries
            .filter { it.value.id == id }
            .map { it.value }
            .firstOrNull())
    }

}
