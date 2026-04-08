package hu.bme.sch.cmsch.component.location

import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.repository.EntityPageDataSource
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.StaffPermissions
import hu.bme.sch.cmsch.service.TimeService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
@ConditionalOnBean(LocationComponent::class)
class LocationService(
    private val clock: TimeService,
    private val userRepository: UserRepository,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val waypointRepository: WaypointRepository,
    private val locationRepository: LocationRepository,
    private val locationComponent: LocationComponent,
    private val transactionManager: PlatformTransactionManager
) : EntityPageDataSource<LocationEntity, Int> {

    private val log = LoggerFactory.getLogger(javaClass)

    fun pushLocation(locationDto: LocationDto): LocationResponse {
        val user = userRepository.findByCmschId(startupPropertyConfig.profileQrPrefix + locationDto.token)
        if (user.isEmpty) {
            return LocationResponse("nem jogosult", "n/a")
        }

        val userEntity = user.get()
        if (userEntity.role.value < RoleType.STAFF.value) {
            return LocationResponse("jogosulatlan", "n/a")
        }

        val existingByUserId = locationRepository.findByUserId(userEntity.id)
        val existingByToken = locationRepository.findByToken(locationDto.token)
        val entity = if (existingByUserId.isPresent) {
            existingByUserId.get()
        } else if (existingByToken.isPresent) {
            existingByToken.get()
        } else {
            LocationEntity(
                id = 0,
                token = locationDto.token,
                userId = userEntity.id,
                userName = userEntity.fullName,
                alias = userEntity.alias,
                groupName = userEntity.groupName,
                markerColor = resolveColor(userEntity.groupName),
                broadcast = locationDto.broadcastEnabled
                        && userEntity.hasPermission(StaffPermissions.PERMISSION_BROADCAST_LOCATION.permissionString)
            )
        }

        entity.longitude = locationDto.longitude
        entity.latitude = locationDto.latitude
        entity.altitude = locationDto.altitude
        entity.accuracy = locationDto.accuracy
        entity.speed = locationDto.speed
        entity.altitudeAccuracy = locationDto.altitudeAccuracy
        entity.heading = locationDto.heading
        entity.timestamp = clock.getTimeInSeconds()
        entity.broadcast = locationDto.broadcastEnabled && shareLocationAllowed(userEntity)

        locationRepository.save(entity)
        return LocationResponse(if (entity.broadcast) "OK" else "BROADCAST", entity.groupName)
    }

    private fun shareLocationAllowed(token: String): Boolean {
        val user = userRepository.findByCmschId(startupPropertyConfig.profileQrPrefix + token)
        return user.getOrNull()?.hasPermission(StaffPermissions.PERMISSION_BROADCAST_LOCATION.permissionString) ?: false
    }

    private fun shareLocationAllowed(user: UserEntity): Boolean {
        return user.hasPermission(StaffPermissions.PERMISSION_BROADCAST_LOCATION.permissionString)
    }

    private fun resolveColor(groupName: String): String {
        return when (groupName) {
            locationComponent.blackGroupName  -> { "#000000" }
            locationComponent.blueGroupName   -> { "#5fa8d3" }
            locationComponent.cyanGroupName   -> { "#70d6ff" }
            locationComponent.pinkGroupName   -> { "#ff70a6" }
            locationComponent.orangeGroupName -> { "#f8961e" }
            locationComponent.greenGroupName  -> { "#a7c957" }
            locationComponent.redGroupName    -> { "#ef233c" }
            locationComponent.whiteGroupName  -> { "#ffffff" }
            locationComponent.yellowGroupName -> { "#fee440" }
            locationComponent.purpleGroupName -> { "#9d4edd" }
            locationComponent.grayGroupName   -> { "#c0c0c0" }
            else -> { locationComponent.defaultGroupColor }
        }
    }

    fun findAllLocation(): MutableList<LocationEntity> {
        return locationRepository.findAll()
            .toList()
            .sortedBy { it.groupName }
            .toMutableList()
    }

    fun clean() {
        locationRepository.deleteAll()
    }

    fun refresh() {
        locationRepository.findAll().forEach { location ->
            val user = userRepository.findByCmschId(startupPropertyConfig.profileQrPrefix + location.token)
            user.ifPresent { userEntity ->
                location.userId = userEntity.id
                location.userName = userEntity.fullName
                location.alias = userEntity.alias
                location.groupName = userEntity.groupName
                location.markerColor = resolveColor(userEntity.groupName)
                locationRepository.save(location)
            }
        }
    }

    fun findLocationsOfGroup(groupId: Int): List<LocationEntity> {
        return locationRepository.findAll()
            .filter { it.userId == groupId }
    }

    fun findLocationsOfGroupName(group: String): List<MapMarker> {
        val locations = mutableListOf<MapMarker>()
        val visibilityDuration = locationComponent.visibleDuration
        val currentTime = clock.getTimeInSeconds()

        locations.addAll(locationRepository.findAll()
            .filter { it.groupName == group || it.broadcast }
            .filter { it.timestamp + visibilityDuration > currentTime }
            .map {
                MapMarker(
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
                )
            }
        )
        locations.addAll(waypointRepository.findAll().map { it.toMapMarker() })
        return locations
    }

    private fun mapDisplayName(location: LocationEntity): String {
        return if (locationComponent.showGroupName && location.groupName.isNotBlank()) {
            if (locationComponent.showUserName) {
                if (locationComponent.showAlias && location.alias.isNotBlank()) {
                    "${location.userName} (${location.alias}) [${location.groupName}]"
                } else {
                    "${location.userName} [${location.groupName}]"
                }
            } else if (locationComponent.showAlias) {
                "${location.alias} [${location.groupName}]"
            } else {
                location.groupName
            }
        } else {
            if (locationComponent.showUserName) {
                if (locationComponent.showAlias && location.alias.isNotBlank()) {
                    "${location.userName} (${location.alias})"
                } else {
                    location.userName
                }
            } else if (locationComponent.showAlias) {
                location.alias
            } else {
                ""
            }
        }
    }

    fun getRecentLocations(): List<LocationEntity> {
        val range = clock.getTimeInSeconds() - 600
        return locationRepository.findAllByTimestampGreaterThan(range)
    }

    @Scheduled(fixedRate = 60000)
    fun cleanupStaleLocations() {
        val visibilityDuration = locationComponent.visibleDuration
        val cutoff = clock.getTimeInSeconds() - visibilityDuration
        val staleLocations = locationRepository.findAllByTimestampLessThan(cutoff)

        if (staleLocations.isNotEmpty()) {
            log.info("Cleaning up {} stale locations", staleLocations.size)
            locationRepository.deleteAll(staleLocations)
        }
    }

    override fun findAll() = findAllLocation()

    override fun count() = locationRepository.count()

    override fun deleteAll() = clean()

    override fun <S : LocationEntity> saveAll(entities: MutableIterable<S>): MutableIterable<S> {
        return locationRepository.saveAll(entities)
    }

    override fun <S : LocationEntity> save(entity: S): S {
        return locationRepository.save(entity)
    }

    override fun delete(entity: LocationEntity) {
        locationRepository.delete(entity)
    }

    override fun findById(id: Int): Optional<LocationEntity> {
        return locationRepository.findById(id)
    }

}