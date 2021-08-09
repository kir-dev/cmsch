package hu.bme.sch.g7.controller

import hu.bme.sch.g7.dto.scan.G7idBuyRequest
import hu.bme.sch.g7.dto.scan.NeptunBuyRequest
import hu.bme.sch.g7.dto.scan.ResolveRequest
import hu.bme.sch.g7.dto.view.SellStatus
import hu.bme.sch.g7.service.ProductService
import hu.bme.sch.g7.service.RealtimeConfigService
import hu.bme.sch.g7.service.UserProfileGeneratorService
import hu.bme.sch.g7.service.UserService
import hu.bme.sch.g7.util.getUser
import hu.bme.sch.g7.util.getUserOrNull
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/admin/sell")
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"], allowCredentials = "false")
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
        log.info("Scanning ${resolve.g7id}")
        return userService.searchByG7Id(resolve.g7id)
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
    @PostMapping("/buy-g7id")
    fun buyNeptun(
            @RequestBody buyRequest: G7idBuyRequest,
            request: HttpServletRequest
    ): SellStatus {
        val user = request.getUserOrNull() ?: return SellStatus.INVALID_PERMISSIONS
        log.info("Selling ${buyRequest.productId} to ${buyRequest.g7id} by ${user.fullName}")
        return productService.sellProductByG7Id(buyRequest.productId, user, buyRequest.g7id)
    }
}