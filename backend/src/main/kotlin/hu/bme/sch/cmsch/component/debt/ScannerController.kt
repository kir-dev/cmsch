package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.dto.CmschIdBuyRequest
import hu.bme.sch.cmsch.dto.NeptunBuyRequest
import hu.bme.sch.cmsch.dto.ResolveRequest
import hu.bme.sch.cmsch.service.UserService
import hu.bme.sch.cmsch.util.getUserOrNull
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/sell")
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"], allowCredentials = "false")
@ConditionalOnBean(DebtComponent::class)
class ScannerController(
    private val userService: UserService,
    private val productService: ProductService,
    private val startupPropertyConfig: StartupPropertyConfig
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/{id}")
    fun sell(@PathVariable id: Int, model: Model): String {
        val product = productService.getProductById(id)
        model.addAttribute("itemName", product.map { it.name }.orElse("Hibás termék azonosító"))
        model.addAttribute("itemPrice", "${product.map { it.price }.orElse(0)} JMF")
        model.addAttribute("itemId", id)
        model.addAttribute("prefix", startupPropertyConfig.profileQrPrefix)
        return "scanner"
    }

    @ResponseBody
    @PostMapping("/resolve")
    fun resolve(@RequestBody resolve: ResolveRequest): String {
        log.info("Scanning ${resolve.cmschId}")
        return userService.searchByCmschId(resolve.cmschId)
                .map { it.fullName }
                .orElse("Not Found")
    }

    @ResponseBody
    @PostMapping("/buy-neptun")
    fun buyNeptun(
        @RequestBody buyRequest: NeptunBuyRequest,
        auth: Authentication
    ): SellStatus {
        val user = auth.getUserOrNull() ?: return SellStatus.INVALID_PERMISSIONS
        log.info("Selling ${buyRequest.productId} to ${buyRequest.neptun} by ${user.userName}")
        return productService.sellProductByNeptun(buyRequest.productId, user, buyRequest.neptun.uppercase())
    }

    @ResponseBody
    @PostMapping("/buy-cmschid")
    fun buyNeptun(
        @RequestBody buyRequest: CmschIdBuyRequest,
        auth: Authentication
    ): SellStatus {
        val user = auth.getUserOrNull() ?: return SellStatus.INVALID_PERMISSIONS
        log.info("Selling ${buyRequest.productId} to ${buyRequest.cmschId} by ${user.userName}")
        return productService.sellProductByCmschId(buyRequest.productId, user, buyRequest.cmschId)
    }
}
