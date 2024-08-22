package hu.bme.sch.cmsch.component.debt

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@ConditionalOnBean(DebtComponent::class)
class DebtApiController(
    private val debtService: DebtService
) {

    @JsonView(FullDetails::class)
    @GetMapping("/debts")
    fun debts(auth: Authentication): DebtsView {
        return DebtsView(
            debts = debtService.getDebtsForUser(auth.getUser())
        )
    }

}
