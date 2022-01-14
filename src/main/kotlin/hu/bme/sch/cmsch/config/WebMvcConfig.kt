package hu.bme.sch.cmsch.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig : WebMvcConfigurer {

    @Value("\${cmsch.external:/etc/cmsch/external/}")
    private val uploadPath = "/etc/cmsch/external/"

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/cdn/**")
                .addResourceLocations("file:$uploadPath")
    }

}
