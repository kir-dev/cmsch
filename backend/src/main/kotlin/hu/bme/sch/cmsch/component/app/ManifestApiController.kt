package hu.bme.sch.cmsch.component.app

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

const val IMAGE_PNG = "image/png"

@RestController
@RequestMapping("/manifest")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(ApplicationComponent::class)
class ManifestApiController(
    private val manifestComponent: ManifestComponent,
    private val applicationComponent: ApplicationComponent
) {

    @GetMapping("/manifest.json")
    fun manifestJson(): ManifestJsonView {
        val baseUrl = applicationComponent.adminSiteUrl.getValue()
        return ManifestJsonView(
            theme_color = manifestComponent.themeColor.getValue(),
            background_color = manifestComponent.backgroundColor.getValue(),
            display = manifestComponent.display.getValue(),
            scope = manifestComponent.applicationScope.getValue(),
            start_url = manifestComponent.startUrl.getValue(),
            name = manifestComponent.name.getValue(),
            short_name = manifestComponent.shortName.getValue(),
            description = manifestComponent.description.getValue(),
            icons = listOf(
                ManifestIconView(
                    src = "${baseUrl}cdn/manifest/icon-192x192.png",
                    sizes = "192x192",
                    type = IMAGE_PNG),
                ManifestIconView(
                    src = "${baseUrl}cdn/manifest/icon-256x256.png",
                    sizes = "256x256",
                    type = IMAGE_PNG),
                ManifestIconView(
                    src = "${baseUrl}cdn/manifest/icon-384x384.png",
                    sizes = "384x384",
                    type = IMAGE_PNG),
                ManifestIconView(
                    src = "${baseUrl}cdn/manifest/icon-512x512.png",
                    sizes = "512x512",
                    type = IMAGE_PNG),
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
    val src: String,
    val sizes: String,
    val type: String
)
