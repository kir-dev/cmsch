package hu.bme.sch.cmsch.component.bmejegy

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/cheers")
@ConditionalOnBean(BmejegyComponent::class)
class CheersController(
    @Value("\${hu.bme.sch.cmsch.component.bmejegy.cheers.token:}") private val token: String,
    private val cheersBmejegyService: CheersBmejegyService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    
    @PostMapping
    fun updateTickets(@RequestBody cheers: CheersRequestBody, request: HttpServletRequest) {
        if (cheers.token != token || token.isEmpty()) {
            log.warn("Invalid cheers API request with token: $token (ip:${request.remoteAddr})")
            return
        }
        log.info("Updating ticket DB (size:${cheers.tickets.size}, ip:${request.remoteAddr})")
        cheersBmejegyService.updateUserStatuses(cheers.tickets)
        cheersBmejegyService.updateUserStatuses()
    }
    
}