package hu.bme.sch.cmsch.config

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import javax.annotation.PostConstruct

@ConstructorBinding
@ConfigurationProperties("hu.bme.sch.cmsch.component.load")
data class ComponentLoadConfig(

    val app: Boolean = false,
    val login: Boolean = false,
    val achievement: Boolean = false,
    val profile: Boolean = false,
    val riddle: Boolean = false,
    val token: Boolean = false,
    val news: Boolean = false,
    val event: Boolean = false,
    val extraPage: Boolean = false,
    val debt: Boolean = false,
    val location: Boolean = false,
    val leaderboard: Boolean = false,
    val contdown: Boolean = false,
    val home: Boolean = false,
    val groupselection: Boolean = false,

) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun onInit() {
        log.info("ComponentLoadConfig settings: {}", this.toString())
    }

}
