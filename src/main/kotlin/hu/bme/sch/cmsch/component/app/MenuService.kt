package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.extrapage.ExtraPageRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.PostConstruct

@Service
@ConditionalOnBean(ApplicationComponent::class)
class MenuService(
    private val menuRepository: MenuRepository,
    private val components: List<ComponentBase>,
    private val extraPages: Optional<ExtraPageRepository>,
) {

    private val possibleMenus = listOf<MenuItem>()

    @PostConstruct
    fun init() {

    }

}
