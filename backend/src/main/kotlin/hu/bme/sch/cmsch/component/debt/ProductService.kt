package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.TimeService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Suppress("RedundantModalityModifier") // Spring transactional proxy requires it not to be final
@Service
@ConditionalOnBean(DebtComponent::class)
open class ProductService(
    private val productRepository: ProductRepository,
    private val soldProductRepository: SoldProductRepository,
    private val userRepository: UserRepository,
    private val clock: TimeService
) {

    @Transactional(readOnly = true)
    open fun getAllFoods() = productRepository.findAllByType(ProductType.FOOD)

    @Transactional(readOnly = true)
    open fun getAllMerch() = productRepository.findAllByType(ProductType.MERCH)

    @Transactional(readOnly = true)
    open fun getAllProducts() = productRepository.findAll()

    @Transactional
    open fun sellProductByCmschId(productId: Int, seller: UserEntity, cmschId: String): SellStatus {
        val user = userRepository.findByCmschId(cmschId).orElse(null) ?: return SellStatus.BUYER_NOT_FOUND
        return sellProduct(productId, seller, user)
    }

    @Transactional
    open fun sellProductByNeptun(productId: Int, seller: UserEntity, neptun: String): SellStatus {
        val user = userRepository.findByNeptun(neptun).orElse(null) ?: return SellStatus.BUYER_NOT_FOUND
        return sellProduct(productId, seller, user)
    }

    private fun sellProduct(productId: Int, seller: UserEntity, buyer: UserEntity): SellStatus {
        val product = productRepository.findById(productId).orElse(null) ?: return SellStatus.PRODUCT_NOT_FOUND
        if (product.available.not())
            return SellStatus.ITEM_NOT_AVAILABLE
        val groupId = buyer.group?.id ?: return SellStatus.NOT_IN_GROUP
        val date = clock.getTimeInSeconds()

        soldProductRepository.save(
            SoldProductEntity(
                0,
                product.name, product.price,
                seller.id, seller.fullName,
                buyer.id, buyer.fullName,
                groupId, null, "",
                true, date,
                false, 0,
                false,
                " '${seller.fullName}'(${seller.id}) sold '${product.name}'(${product.price} JMF) to '${buyer.fullName}'(${buyer.id}) at $date;",
                product.materialIcon
            )
        )
        return SellStatus.SOLD
    }

    @Transactional(readOnly = true)
    open fun findTransactionById(id: Int) = soldProductRepository.findById(id)

    @Transactional
    open fun setTransactionPayed(id: Int, responsibleUser: UserEntity) {
        soldProductRepository.findById(id).ifPresent {
            if (it.payed)
                return@ifPresent

            val date = clock.getTimeInSeconds()
            it.payed = true
            it.payedAt = date
            it.responsibleId = responsibleUser.id
            it.responsibleName = responsibleUser.fullName
            it.log += " '${responsibleUser.fullName}'(${responsibleUser.id}) recieved the money at $date;"
            soldProductRepository.save(it)
        }
    }

    @Transactional(readOnly = true)
    open fun getAllDebtsByGroup(user: UserEntity): List<SoldProductEntity> {
        val groupId = user.group?.id ?: return listOf()
        return soldProductRepository.findAllByResponsibleGroupId(groupId)
    }

    @Transactional(readOnly = true)
    open fun getAllDebtsByUser(user: UserEntity): List<SoldProductEntity> {
        return soldProductRepository.findAllByOwnerId(user.id)
    }

    @Transactional(readOnly = true)
    open fun getProductById(id: Int) = productRepository.findById(id)

}
