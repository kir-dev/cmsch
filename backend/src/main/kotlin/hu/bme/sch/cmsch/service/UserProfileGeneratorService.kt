package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.util.sha256
import org.springframework.stereotype.Service


@Service
class UserProfileGeneratorService(private val startupPropertyConfig: StartupPropertyConfig) {

    fun generateProfileIdForUser(user: UserEntity) {
        user.cmschId = (startupPropertyConfig.profileQrPrefix + (user.internalId + startupPropertyConfig.profileSalt)
            .sha256()
            .substring(startupPropertyConfig.profileQrPrefix.length, 40))
    }

}
