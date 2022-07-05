package hu.bme.sch.cmsch.component.opengraph

import hu.bme.sch.cmsch.component.app.ApplicationComponent
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ShareController(
    private val openGraphService: OpenGraphService,
    private val applicationComponent: ApplicationComponent
) {

    @GetMapping("/share/page/{url}")
    fun sharePage(@PathVariable url: String, model: Model): String {
        return openGraphService.findExtraPage(url)
            .map {
                fillModelWithCommon(model, it)
                model.addAttribute("redirectUrl", "${applicationComponent.siteUrl.getValue()}page/${url}")
                return@map "openGraph"
            }
            .orElse("redirect:/404")
    }

    @GetMapping("/share/event/{url}")
    fun shareEvent(@PathVariable url: String, model: Model): String {
        return openGraphService.findEvent(url)
            .map {
                fillModelWithCommon(model, it)
                model.addAttribute("redirectUrl", "${applicationComponent.siteUrl.getValue()}event/${url}")
                return@map "openGraph"
            }
            .orElse("redirect:/404")
    }

    @GetMapping("/share/news/{url}")
    fun shareNews(@PathVariable url: String, model: Model): String {
        return openGraphService.findNews(url)
            .map {
                fillModelWithCommon(model, it)
                model.addAttribute("redirectUrl", "${applicationComponent.siteUrl.getValue()}news/${url}")
                return@map "openGraph"
            }
            .orElse("redirect:/404")
    }

    private fun fillModelWithCommon(model: Model, it: OpenGraphResource) {
        model.addAttribute("ogTitle", it.ogTitle)
        model.addAttribute("ogImage", it.ogImage)
        model.addAttribute("ogDescription", it.ogDescription)
    }

}
