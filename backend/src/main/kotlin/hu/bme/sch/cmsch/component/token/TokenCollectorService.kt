package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.component.qrfight.QrFightComponent
import hu.bme.sch.cmsch.component.qrfight.QrFightService
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.service.UserService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*

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

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun collectToken(user: CmschUser, token: String): Pair<String?, TokenCollectorStatus> {
        val tokenEntity = tokenRepository.findAllByTokenAndVisibleTrue(token).firstOrNull()
        if (tokenEntity != null) {
            if (tokenPropertyRepository.findByToken_TokenAndOwnerUser_Id(token, user.id).isPresent)
                return Pair(tokenEntity.title, TokenCollectorStatus.ALREADY_SCANNED)

            val userEntity = userService.getById(user.internalId)
            return qrFightService
                .filter { qrFightComponent.map { it.enabled.isValueTrue() }.orElse(false) }
                .map {
                    return@map it.onTokenScanForUser(userEntity, tokenEntity)
                }.orElseGet {
                    if (tokenPropertyRepository.findByToken_TokenAndOwnerUser_Id(token, user.id).isPresent)
                        return@orElseGet Pair(tokenEntity.title, TokenCollectorStatus.ALREADY_SCANNED)

                    tokenPropertyRepository.save(TokenPropertyEntity(0, userEntity, null, tokenEntity, clock.getTimeInSeconds()))
                    return@orElseGet Pair(tokenEntity.title, TokenCollectorStatus.SCANNED)
                }
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
            return qrFightService
                .filter { qrFightComponent.map { it.enabled.isValueTrue() }.orElse(false) }
                .map {
                    return@map it.onTokenScanForGroup(userEntity, groupEntity.get(), tokenEntity)
                }.orElseGet {
                    if (tokenPropertyRepository.findByToken_TokenAndOwnerGroup(token, groupEntity.get()).isPresent)
                        return@orElseGet Pair(tokenEntity.title, TokenCollectorStatus.ALREADY_SCANNED)

                    tokenPropertyRepository.save(TokenPropertyEntity(0, null, groupEntity.get(), tokenEntity, clock.getTimeInSeconds()))
                    return@orElseGet Pair(tokenEntity.title, TokenCollectorStatus.SCANNED)
                }
        }
        return Pair(null, TokenCollectorStatus.WRONG)
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
    open fun getTokensForUserWithCategory(user: CmschUser, category: String): Int {
        return tokenPropertyRepository.findAllByOwnerUser_IdAndToken_Type(user.id, category).size
    }

    @Transactional(readOnly = true)
    open fun getTokensForGroupWithCategory(group: GroupEntity, category: String): Int {
        return tokenPropertyRepository.findAllByOwnerGroup_IdAndToken_Type(group.id, category).size
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

    private fun fetchTotalTokenCount(tokenCategoryToDisplay: String) =
        if (tokenCategoryToDisplay == ALL_TOKEN_TYPE) {
            getTotalTokenCount()
        } else {
            getTotalTokenCountWithCategory(tokenCategoryToDisplay)
        }

    private fun fetchCollectedTokenCount(user: CmschUser, tokenCategoryToDisplay: String) =
        if (tokenCategoryToDisplay == ALL_TOKEN_TYPE) {
            getTokensForUser(user).size
        } else {
            getTokensForUserWithCategory(user, tokenCategoryToDisplay)
        }

}
