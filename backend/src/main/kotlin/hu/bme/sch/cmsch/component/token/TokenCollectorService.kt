package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.component.qrfight.QrFightComponent
import hu.bme.sch.cmsch.component.qrfight.QrFightService
import hu.bme.sch.cmsch.component.token.TokenCollectorStatus.ALREADY_SCANNED
import hu.bme.sch.cmsch.component.token.TokenCollectorStatus.SCANNED
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.service.UserService
import org.postgresql.util.PSQLException
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*
import kotlin.jvm.optionals.getOrNull

const val ALL_TOKEN_TYPE = "*"

@Service
@ConditionalOnBean(TokenComponent::class)
open class TokenCollectorService(
    private val tokenRepository: TokenRepository,
    private val tokenPropertyRepository: TokenPropertyRepository,
    private val groupRepository: GroupRepository,
    private val tokenComponent: TokenComponent,
    private val userService: UserService,
    private val clock: TimeService,
    private val qrFightService: Optional<QrFightService>,
    private val qrFightComponent: Optional<QrFightComponent>
) {

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun collectToken(user: CmschUser, token: String): TokenSubmittedView {
        val tokenEntity = tokenRepository.findAllByTokenAndVisibleTrue(token).firstOrNull()
        if (tokenEntity != null) {
            if (!isTokenActive(tokenEntity)) {
                return TokenSubmittedView(TokenCollectorStatus.INACTIVE, null, null, null)
            }
            val userEntity = userService.getById(user.internalId)
            return qrFightService
                .filter { qrFightComponent.map { it.enabled.isValueTrue() }.orElse(false) }
                .map {
                    return@map it.onTokenScanForUser(userEntity, tokenEntity)
                }.orElseGet {
                    if (tokenPropertyRepository.findByToken_TokenAndOwnerUser_Id(token, user.id).isPresent)
                        return@orElseGet TokenSubmittedView(ALREADY_SCANNED, tokenEntity.title, tokenEntity.displayDescription, tokenEntity.displayIconUrl)

                    tokenPropertyRepository.save(TokenPropertyEntity(0, userEntity, null, tokenEntity, clock.getTimeInSeconds()))
                    return@orElseGet TokenSubmittedView(SCANNED, tokenEntity.title, tokenEntity.displayDescription, tokenEntity.displayIconUrl)
                }
        }
        return TokenSubmittedView(TokenCollectorStatus.WRONG, null, null, null)
    }

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun collectTokenForGroup(user: CmschUser, token: String): TokenSubmittedView {
        val groupEntity = user.groupId?.let { groupId -> groupRepository.findById(groupId).getOrNull() }
            ?: return TokenSubmittedView(TokenCollectorStatus.CANNOT_COLLECT, null, null, null)

        val tokenEntity = tokenRepository.findAllByTokenAndVisibleTrue(token).firstOrNull()
        if (tokenEntity != null) {
            if (!isTokenActive(tokenEntity)) {
                return TokenSubmittedView(TokenCollectorStatus.INACTIVE, null, null, null)
            }
            return qrFightService
                .filter { qrFightComponent.map { it.enabled.isValueTrue() }.orElse(false) }
                .map {
                    return@map it.onTokenScanForGroup(user, groupEntity.id, groupEntity.name, tokenEntity)
                }.orElseGet {
                    if (tokenPropertyRepository.findByToken_TokenAndOwnerGroup_Id(token, groupEntity.id).isPresent)
                        return@orElseGet TokenSubmittedView(ALREADY_SCANNED, tokenEntity.title, tokenEntity.displayDescription, tokenEntity.displayIconUrl)

                    tokenPropertyRepository.save(TokenPropertyEntity(0, null, groupEntity, tokenEntity, clock.getTimeInSeconds()))
                    return@orElseGet TokenSubmittedView(SCANNED, tokenEntity.title, tokenEntity.displayDescription, tokenEntity.displayIconUrl)
                }
        }
        return TokenSubmittedView(TokenCollectorStatus.WRONG, null, null, null)
    }

    @Transactional(readOnly = true)
    open fun getTokensForUser(user: CmschUser): List<TokenDto> {
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
    open fun countTokensForUser(user: CmschUser): Int {
        return tokenPropertyRepository.countAllByOwnerUser_Id(user.id)
    }

    @Transactional(readOnly = true)
    open fun getTokensForGroup(group: GroupEntity): List<TokenDto> {
        return tokenPropertyRepository.findAllByOwnerGroup_Id(group.id)
            .map {
                TokenDto(
                    it.token?.title ?: "n/a",
                    it.token?.type ?: "n/a",
                    it.token?.icon ?: "not_found"
                )
            }
    }

    @Transactional(readOnly = true)
    open fun countTokensForGroup(group: GroupEntity): Int {
        return tokenPropertyRepository.countAllByOwnerGroup_Id(group.id)
    }

    @Transactional(readOnly = true)
    open fun getTokensForUserWithCategory(user: CmschUser, category: String): Int {
        return tokenPropertyRepository.countAllByOwnerUser_IdAndToken_Type(user.id, category)
    }

    @Transactional(readOnly = true)
    open fun getTokensForGroupWithCategory(group: GroupEntity, category: String): Int {
        return tokenPropertyRepository.countAllByOwnerGroup_IdAndToken_Type(group.id, category)
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
    open fun getTokenViewForUser(user: CmschUser): TokenView {
        val tokenCategoryToDisplay = tokenComponent.collectRequiredType.getValue()
        return TokenView(
            tokens = getTokensForUser(user),
            collectedTokenCount = fetchCollectedTokenCount(user, tokenCategoryToDisplay),
            totalTokenCount = fetchTotalTokenCount(tokenCategoryToDisplay),
            minTokenToComplete = tokenComponent.collectRequiredTokens.getValue().toIntOrNull() ?: Int.MAX_VALUE
        )
    }

    private fun isTokenActive(tokenEntity: TokenEntity): Boolean =
        clock.inRange(
            tokenEntity.availableFrom ?: 0,
            tokenEntity.availableUntil ?: Long.MAX_VALUE,
            Instant.now().epochSecond,
        )


    private fun fetchTotalTokenCount(tokenCategoryToDisplay: String) =
        if (tokenCategoryToDisplay == ALL_TOKEN_TYPE) {
            getTotalTokenCount()
        } else {
            getTotalTokenCountWithCategory(tokenCategoryToDisplay)
        }

    private fun fetchCollectedTokenCount(user: CmschUser, tokenCategoryToDisplay: String) =
        if (tokenCategoryToDisplay == ALL_TOKEN_TYPE) {
            countTokensForUser(user)
        } else {
            getTokensForUserWithCategory(user, tokenCategoryToDisplay)
        }

}
