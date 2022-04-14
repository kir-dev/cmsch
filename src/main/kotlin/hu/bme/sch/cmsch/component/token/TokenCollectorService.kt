package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.ClockService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(TokenComponent::class)
open class TokenCollectorService(
    private val tokenRepository: TokenRepository,
    private val tokenPropertyRepository: TokenPropertyRepository,
    private val groupRepository: GroupRepository,
    private val clock: ClockService
) {

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun collectToken(userEntity: UserEntity, token: String): Pair<String?, TokenCollectorStatus> {
        val tokenEntity = tokenRepository.findAllByTokenAndVisibleTrue(token).firstOrNull()
        if (tokenEntity != null) {
            if (tokenPropertyRepository.findByToken_TokenAndOwnerUser(token, userEntity).isPresent)
                return Pair(tokenEntity.title, TokenCollectorStatus.ALREADY_SCANNED)

            tokenPropertyRepository.save(TokenPropertyEntity(0, userEntity, null, tokenEntity, clock.getTimeInSeconds()))
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

            tokenPropertyRepository.save(TokenPropertyEntity(0, null, groupEntity.get(), tokenEntity, clock.getTimeInSeconds()))
            return Pair(tokenEntity.title, TokenCollectorStatus.SCANNED)
        }
        return Pair(null, TokenCollectorStatus.WRONG)
    }

    @Transactional(readOnly = true)
    fun getTokensForUser(user: UserEntity): List<TokenDto> {
        return tokenPropertyRepository.findAllByOwnerUser_Id(user.id)
            .map { TokenDto(it.token?.title ?: "n/a", it.token?.type ?: "n/a") }
    }

    @Transactional(readOnly = true)
    fun getTokensForUserWithCategory(user: UserEntity, category: String): Int {
        return tokenPropertyRepository.findAllByOwnerUser_IdAndToken_Type(user.id, category).size
    }

    @Transactional(readOnly = true)
    fun getTotalTokenCountWithCategory(category: String): Int {
        return tokenRepository.findAllByTypeAndVisibleTrue(category).size
    }

}
