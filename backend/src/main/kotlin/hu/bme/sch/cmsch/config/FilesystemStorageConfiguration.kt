package hu.bme.sch.cmsch.config

import hu.bme.sch.cmsch.service.FilesystemStorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.Duration

@Configuration
@ConditionalOnBean(FilesystemStorageService::class)
class FilesystemStorageConfiguration(
    private val startupPropertyConfig: StartupPropertyConfig,
    private val storageService: FilesystemStorageService
) : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val handler = registry.addResourceHandler("/${storageService.objectServePath}/**")
            .addResourceLocations("file:${storageService.getFileStoragePath()}")
        if (startupPropertyConfig.cdnCacheMaxAge > 0) {
            handler.setCacheControl(CacheControl.maxAge(Duration.ofSeconds(startupPropertyConfig.cdnCacheMaxAge)))
        }
    }

}
