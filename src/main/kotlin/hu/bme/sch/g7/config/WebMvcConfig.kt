package hu.bme.sch.g7.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig : WebMvcConfigurer {

    @Value("\${g7web.external:/etc/g7web/external/}")
    private val uploadPath = "/etc/g7web/external/"

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/cdn/**")
                .addResourceLocations("file:$uploadPath")
    }

}