package hu.bme.sch.cmsch.component.bmejegy

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
@ConditionalOnBean(BmejegyComponent::class)
class BmejegyTimer(
    private val bmejegyService: BmejegyService
) {

    @PostConstruct
    fun init() {
//        println("BmejegyTimer init()")
//        bmejegyService.fetchData()
    }

}
