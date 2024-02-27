package hu.bme.sch.cmsch.component.proto

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/proto")
class ProtoApiController(
    private val protoService: ProtoService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/**")
    fun getProtoEntity(request: HttpServletRequest): ResponseEntity<String> {
        val dynamicPath = extractDynamicPath(request)
            ?: return ResponseEntity.notFound().build()

        val protoEntity = protoService.getProtoEntityByPath(dynamicPath)
        return if (protoEntity != null) {
            log.info(
                "Proto found with path {} response type {} code {}",
                dynamicPath, protoEntity.mimeType, protoEntity.statusCode
            )

            val headers = HttpHeaders()
            if (protoEntity.mimeType.isNotBlank()) {
                headers.set("Content-Type", protoEntity.mimeType)
            }
            ResponseEntity.status(protoEntity.statusCode).headers(headers).body(protoEntity.responseValue)
        } else {
            log.info("No proto found with path {}, returning NOT_FOUND", dynamicPath)
            ResponseEntity.notFound().build()
        }
    }

    private fun extractDynamicPath(request: HttpServletRequest): String? {
        return request.requestURI?.substring(request.contextPath.length + "/api/proto".length)
    }

}