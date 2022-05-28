package hu.bme.sch.cmsch.component.debt

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(DebtComponent::class)
class DebtApiController(
    private val debtsRepository: SoldProductRepository
) {

    @JsonView(FullDetails::class)
    @GetMapping("/debts")
    fun debts(auth: Authentication): DebtsView {
        return DebtsView(
            debts = debtsRepository.findAllByOwnerId(auth.getUser().id)
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
        )
    }

}
