package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/manifest")
@ConditionalOnBean(ApplicationComponent::class)
class ManifestApiController(
    private val manifestComponent: ManifestComponent,
    private val storageService: StorageService,
) {

    @GetMapping("/manifest.json")
    fun manifestJson(): ManifestJsonView {
        return ManifestJsonView(
            theme_color = manifestComponent.themeColor,
            background_color = manifestComponent.backgroundColor,
            display = manifestComponent.display,
            scope = manifestComponent.applicationScope,
            start_url = manifestComponent.startUrl,
            name = manifestComponent.name,
            short_name = manifestComponent.shortName,
            description = manifestComponent.description,
            icons = listOf(
                ManifestIconView(
                    src = storageService.getObjectUrl("manifest/icon-192x192.png").orElse(""),
                    sizes = "192x192",
                    type = MediaType.IMAGE_PNG_VALUE
                ),
                ManifestIconView(
                    src = storageService.getObjectUrl("manifest/icon-256x256.png").orElse(""),
                    sizes = "256x256",
                    type = MediaType.IMAGE_PNG_VALUE
                ),
                ManifestIconView(
                    src = storageService.getObjectUrl("manifest/icon-384x384.png").orElse(""),
                    sizes = "384x384",
                    type = MediaType.IMAGE_PNG_VALUE
                ),
                ManifestIconView(
                    src = storageService.getObjectUrl("manifest/icon-512x512.png").orElse(""),
                    sizes = "512x512",
                    type = MediaType.IMAGE_PNG_VALUE
                ),
            )
        )
    }
}

data class ManifestJsonView(
    val theme_color: String,
    val background_color: String,
    val display: String,
    val scope: String,
    val start_url: String,
    val name: String,
    val short_name: String,
    val description: String,
    val icons: List<ManifestIconView>
)

data class ManifestIconView(
    val src: String, val sizes: String, val type: String
)
