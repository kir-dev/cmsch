package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.dto.TokenCollectorStatus
import hu.bme.sch.cmsch.model.UserEntity
import org.springframework.stereotype.Service

@Service
open class TokenCollectorService {

    fun collectToken(userEntity: UserEntity, token: String): Pair<String?, TokenCollectorStatus> {
        println("token: " + token)
        return Pair(null, TokenCollectorStatus.WRONG)
    }

}
