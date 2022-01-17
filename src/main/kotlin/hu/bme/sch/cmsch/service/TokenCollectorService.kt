package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.dao.GroupRepository
import hu.bme.sch.cmsch.dao.TokenPropertyRepository
import hu.bme.sch.cmsch.dao.TokenRepository
import hu.bme.sch.cmsch.dto.TokenCollectorStatus
import hu.bme.sch.cmsch.model.TokenPropertyEntity
import hu.bme.sch.cmsch.model.UserEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
open class TokenCollectorService(
    private val tokenRepository: TokenRepository,
    private val tokenPropertyRepository: TokenPropertyRepository,
    private val groupRepository: GroupRepository
) {

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun collectToken(userEntity: UserEntity, token: String): Pair<String?, TokenCollectorStatus> {
        val tokenEntity = tokenRepository.findAllByTokenAndVisibleTrue(token).firstOrNull()
        if (tokenEntity != null) {
            if (tokenPropertyRepository.findByToken_TokenAndOwnerUser(token, userEntity).isPresent)
                return Pair(tokenEntity.title, TokenCollectorStatus.ALREADY_SCANNED)

            tokenPropertyRepository.save(TokenPropertyEntity(0, userEntity, null, tokenEntity))
            return Pair(tokenEntity.title, TokenCollectorStatus.SCANNED)
        }
        return Pair(null, TokenCollectorStatus.WRONG)
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun collectTokenForGroup(userEntity: UserEntity, token: String): Pair<String?, TokenCollectorStatus> {
        val groupEntity = groupRepository.findByName(userEntity.groupName)
        if (groupEntity.isEmpty)
            return Pair(null, TokenCollectorStatus.CANNOT_COLLECT)

        val tokenEntity = tokenRepository.findAllByTokenAndVisibleTrue(token).firstOrNull()
        if (tokenEntity != null) {
            if (tokenPropertyRepository.findByToken_TokenAndOwnerGroup(token, groupEntity.get()).isPresent)
                return Pair(tokenEntity.title, TokenCollectorStatus.ALREADY_SCANNED)

            tokenPropertyRepository.save(TokenPropertyEntity(0, null, groupEntity.get(), tokenEntity))
            return Pair(tokenEntity.title, TokenCollectorStatus.SCANNED)
        }
        return Pair(null, TokenCollectorStatus.WRONG)
    }

}
