package hu.bme.sch.cmsch.controller.storage

import hu.bme.sch.cmsch.service.S3StorageService
import hu.bme.sch.cmsch.service.StorageService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.HandlerMapping
import java.net.URI

private const val view = "cdn"

@RestController
@RequestMapping("/$view")
@ConditionalOnBean(S3StorageService::class)
class StorageRedirectController(private val storageService: StorageService) {

    @GetMapping("/**")
    fun redirectToObject(request: HttpServletRequest): ResponseEntity<Any> {
        val requestPath = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString()
        val objectPath = requestPath.split("/$view/", limit = 2)[1]

        return storageService.getObjectUrl(objectPath).map {
            val headers = HttpHeaders()
            headers.location = URI.create(it)
            val redirectStatus = HttpStatusCode.valueOf(303)
            return@map ResponseEntity
                .status(redirectStatus)
                .headers(headers)
                .build<Any>()
        }.orElseGet {
            ResponseEntity.notFound().build()
        }
    }

}
