package hu.bme.sch.g7.controller

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dao.SoldProductRepository
import hu.bme.sch.g7.dto.DebtDto
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.service.RealtimeConfigService
import hu.bme.sch.g7.util.getUser
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletRequest

@Controller
class AddonPaymentController(
        private val config: RealtimeConfigService,
        private val debtsRepository: SoldProductRepository,
) {

    @JsonView(FullDetails::class)
    @GetMapping("/pay")
    fun profile(request: HttpServletRequest, model: Model): String {
        val user = request.getUser()
        if (config.isSiteLowProfile())
            return "403"

        model.addAttribute("user", user)
        val debts = debtsRepository.findAllByOwnerId(user.id)
                .map {
                    DebtDto(
                            it.product,
                            it.price,
                            it.sellerName,
                            it.responsibleName,
                            it.payed,
                            it.shipped,
                            it.log,
                            it.materialIcon
                    )
                }
        model.addAttribute("debts", debts)
        model.addAttribute("sumDebts", debts
                .filter { !it.payed }
                .sumOf { it.price })
        return "addonPayments"
    }

}
