package hu.bme.sch.cmsch.controller.admin

import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.login.LoginComponent
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus


val GREETINGS = listOf("Csuma-luma!", "Csumm gecc!", "Na' csá!",
        "Szevasz Tavasz!", "Szia-mia!", "Hellóka", "Vattan-csummgecc!",
        "Hali!", "Vilkommen!", "Haló!", "Szervusz!", "Puszó! <3", ":3",
        "Csummgecc!", "Csumm!", "Csummantás!", "Csumázom önt gecc!", "Helló-belló!",
        "Helló!", "Puszika!", "Halika!", "Ayo!", "Ayo-mate!", "Csippantanék'",
        "Van egy szál cigid?", "Forgalmit, jogosítványt!", "Hogy ityeg a fityeg?",
        "Na mizu? mizu?! mizu?!!", "POV: SENIOR VAGY!", "Legjobb tankör!"
)

@Controller
class EntrypointController(
        private val applicationComponent: ApplicationComponent,
        private val loginComponent: LoginComponent
) {

    @GetMapping("/")
    @ResponseBody
    fun index(auth: Authentication?): String {
        return if (auth == null) "hey!" else "hoo!"
    }

    @RequestMapping(value = [ "/oauth2/authorization", "/login" ])
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun authorize(model: Model, @RequestParam(defaultValue = "") error: String): String {
        model.addAttribute("siteName", applicationComponent.siteName.getValue())
        model.addAttribute("error", error)
        model.addAttribute("googleEnabled", loginComponent.googleSsoEnabled.isValueTrue())
        model.addAttribute("keycloakEnabled", loginComponent.keycloakEnabled.isValueTrue())
        return "authSelection"
    }

    @GetMapping("/c/e")
    fun controlEntrypoint() = "redirect:/admin/control/basics"

    @GetMapping("/c/a")
    fun controlAuthAuthsch() = "redirect:/oauth2/authorization/authsch"

    @GetMapping("/c/g")
    fun controlAuthGoogle() = "redirect:/oauth2/authorization/google"

    @GetMapping("/c/k")
    fun controlAuthKeycloak() = "redirect:/oauth2/authorization/keycloak"

}
