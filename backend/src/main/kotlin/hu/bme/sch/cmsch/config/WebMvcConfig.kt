package hu.bme.sch.cmsch.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.Duration

@Configuration
class WebMvcConfig(
    private val startupPropertyConfig: StartupPropertyConfig
) : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val handler = registry.addResourceHandler("/cdn/**")
                .addResourceLocations("file:${startupPropertyConfig.external}")
        if (startupPropertyConfig.cdnCacheMaxAge > 0) {
            handler.setCacheControl(CacheControl.maxAge(Duration.ofSeconds(startupPropertyConfig.cdnCacheMaxAge)))
        }
    }

}
