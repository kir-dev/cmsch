package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.component.login.CmschUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(DebtComponent::class)
class DebtService(
    private val debtsRepository: SoldProductRepository,
) {

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    fun getDebtsForUser(user: CmschUser): List<DebtDto> {
        return debtsRepository.findAllByOwnerId(user.id)
            .map { DebtDto(
                it.product,
                it.price,
                it.sellerName,
                it.responsibleName,
                it.payed,
                it.shipped,
                it.log,
                it.materialIcon
            ) }
    }

}
