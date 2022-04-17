package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.dto.scan.CmschIdBuyRequest
import hu.bme.sch.cmsch.dto.scan.NeptunBuyRequest
import hu.bme.sch.cmsch.dto.scan.ResolveRequest
import hu.bme.sch.cmsch.service.UserProfileGeneratorService
import hu.bme.sch.cmsch.service.UserService
import hu.bme.sch.cmsch.util.getUserOrNull
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/admin/sell")
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"], allowCredentials = "false")
@ConditionalOnBean(DebtComponent::class)
class ScannerController(
    val userService: UserService,
    val productService: ProductService,
    val profileService: UserProfileGeneratorService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/{id}")
    fun sell(@PathVariable id: Int, model: Model): String {
        val product = productService.getProductById(id)
        model.addAttribute("itemName", product.map { it.name }.orElse("Hibás termék azonosító"))
        model.addAttribute("itemPrice", "${product.map { it.price }.orElse(0)} JMF")
        model.addAttribute("itemId", id)
        model.addAttribute("prefix", profileService.prefix)
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
            request: HttpServletRequest
    ): SellStatus {
        val user = request.getUserOrNull() ?: return SellStatus.INVALID_PERMISSIONS
        log.info("Selling ${buyRequest.productId} to ${buyRequest.neptun} by ${user.fullName}")
        return productService.sellProductByNeptun(buyRequest.productId, user, buyRequest.neptun.uppercase())
    }

    @ResponseBody
    @PostMapping("/buy-cmschid")
    fun buyNeptun(
        @RequestBody buyRequest: CmschIdBuyRequest,
        request: HttpServletRequest
    ): SellStatus {
        val user = request.getUserOrNull() ?: return SellStatus.INVALID_PERMISSIONS
        log.info("Selling ${buyRequest.productId} to ${buyRequest.cmschId} by ${user.fullName}")
        return productService.sellProductByCmschId(buyRequest.productId, user, buyRequest.cmschId)
    }
}
