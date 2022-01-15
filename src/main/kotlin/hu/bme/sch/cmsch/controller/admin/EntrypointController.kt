package hu.bme.sch.cmsch.controller.admin

import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.RealtimeConfigService
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletRequest
import kotlin.random.Random

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
        private val config: RealtimeConfigService
) {

    @GetMapping("/control/entrypoint")
    fun entrypoint(model: Model, request: HttpServletRequest): String {
        val user = request.getUserOrNull() ?: return "redirect:/control/logged-out?error=invalid-permissions"
        if (user.role.value < RoleType.STAFF.value)
            return "redirect:${config.getWebsiteUrl()}"

        model.addAttribute("greetings", GREETINGS[Random.nextInt(GREETINGS.size)])
        model.addAttribute("motd", config.getMotd())
        model.addAttribute("website", config.getWebsiteUrl())

        return "entrypoint"
    }

}
