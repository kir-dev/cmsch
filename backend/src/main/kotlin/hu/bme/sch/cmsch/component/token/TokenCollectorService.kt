package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.component.login.CmschUserPrincipal
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.service.UserService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

const val ALL_TOKEN_TYPE = "*"

@Service
@ConditionalOnBean(TokenComponent::class)
open class TokenCollectorService(
    private val tokenRepository: TokenRepository,
    private val tokenPropertyRepository: TokenPropertyRepository,
    private val groupRepository: GroupRepository,
    private val tokenComponent: TokenComponent,
    private val userService: UserService,
    private val clock: TimeService
) {

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun collectToken(user: CmschUser, token: String): Pair<String?, TokenCollectorStatus> {
        val tokenEntity = tokenRepository.findAllByTokenAndVisibleTrue(token).firstOrNull()
        if (tokenEntity != null) {
            if (tokenPropertyRepository.findByToken_TokenAndOwnerUser_Id(token, user.id).isPresent)
                return Pair(tokenEntity.title, TokenCollectorStatus.ALREADY_SCANNED)

            val userEntity = userService.getById(user.internalId)
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
    open fun getTokensForUser(user: CmschUserPrincipal): List<TokenDto> {
        return tokenPropertyRepository.findAllByOwnerUser_Id(user.id)
            .map {
                TokenDto(
                    it.token?.title ?: "n/a",
                    it.token?.type ?: "n/a",
                    it.token?.icon ?: "not_found"
                )
            }
    }

    @Transactional(readOnly = true)
    open fun getTokensForUserWithCategory(user: CmschUserPrincipal, category: String): Int {
        return tokenPropertyRepository.findAllByOwnerUser_IdAndToken_Type(user.id, category).size
    }

    @Transactional(readOnly = true)
    open fun getTotalTokenCount(): Int {
        return tokenRepository.countAllByVisibleTrue().toInt()
    }

    @Transactional(readOnly = true)
    open fun getTotalTokenCountWithCategory(category: String): Int {
        return tokenRepository.countAllByTypeAndVisibleTrue(category).toInt()
    }

    @Transactional(readOnly = true)
    open fun getTokenViewForUser(user: CmschUserPrincipal): TokenView {
        val tokenCategoryToDisplay = tokenComponent.collectRequiredType.getValue()
        return TokenView(
            tokens = getTokensForUser(user),
            collectedTokenCount = fetchCollectedTokenCount(user, tokenCategoryToDisplay),
            totalTokenCount = fetchTotalTokenCount(tokenCategoryToDisplay),
            minTokenToComplete = tokenComponent.collectRequiredTokens.getValue().toIntOrNull() ?: Int.MAX_VALUE
        )
    }

    private fun fetchTotalTokenCount(tokenCategoryToDisplay: String) =
        if (tokenCategoryToDisplay == ALL_TOKEN_TYPE) {
            getTotalTokenCount()
        } else {
            getTotalTokenCountWithCategory(tokenCategoryToDisplay)
        }

    private fun fetchCollectedTokenCount(user: CmschUserPrincipal, tokenCategoryToDisplay: String) =
        if (tokenCategoryToDisplay == ALL_TOKEN_TYPE) {
            getTokensForUser(user).size
        } else {
            getTokensForUserWithCategory(user, tokenCategoryToDisplay)
        }

}
