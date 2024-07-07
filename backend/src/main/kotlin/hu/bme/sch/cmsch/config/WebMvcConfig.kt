package hu.bme.sch.cmsch.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl
import org.springframework.http.HttpMethod
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.Duration

@Configuration
class WebMvcConfig(
    private val startupPropertyConfig: StartupPropertyConfig,
    @Value("\${cmsch.frontend.production-url:*}") private val productionUrl: String,
    @Value("\${cmsch.backend.allowed-origin-patterns:*}") private val allowedOrigins: List<String>
) : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val handler = registry.addResourceHandler("/cdn/**")
            .addResourceLocations("file:${startupPropertyConfig.external}")
        if (startupPropertyConfig.cdnCacheMaxAge > 0) {
            handler.setCacheControl(CacheControl.maxAge(Duration.ofSeconds(startupPropertyConfig.cdnCacheMaxAge)))
        }
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        arrayOf("/api/**", "/manifest/**", "/cdn/**").forEach {
            registry.addMapping(it)
                .allowedOrigins(productionUrl)
                .allowedOriginPatterns(*allowedOrigins.toTypedArray())
                .allowedMethods(*HttpMethod.values().map(HttpMethod::name).toTypedArray())
        }
    }
}
