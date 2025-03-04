package hu.bme.sch.cmsch.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(
    @Value("\${cmsch.frontend.production-url:*}") private val productionUrl: String,
    @Value("\${cmsch.backend.allowed-origin-patterns:*}") private val allowedOrigins: List<String>
) : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowCredentials(true)
            .allowedOriginPatterns(*allowedOrigins.toTypedArray(), productionUrl)
            .allowedMethods(*HttpMethod.values().map(HttpMethod::name).toTypedArray())
    }
}
