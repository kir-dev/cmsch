package hu.bme.sch.cmsch.config

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import javax.annotation.PostConstruct

@ConstructorBinding
@ConfigurationProperties("hu.bme.sch.cmsch.component.load")
data class ComponentLoadConfig(

    var admission: Boolean = false,
    var app: Boolean = false,
    var challenge: Boolean = false,
    var countdown: Boolean = false,
    var debt: Boolean = false,
    var event: Boolean = false,
    var extraPage: Boolean = false,
    var groupselection: Boolean = false,
    var home: Boolean = false,
    var impressum: Boolean = false,
    var leaderboard: Boolean = false,
    var location: Boolean = false,
    var login: Boolean = false,
    var news: Boolean = false,
    var profile: Boolean = false,
    var qrFight: Boolean = false,
    var race: Boolean = false,
    var riddle: Boolean = false,
    var signup: Boolean = false,
    var task: Boolean = false,
    var team: Boolean = false,
    var token: Boolean = false,

) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun onInit() {
        log.info("ComponentLoadConfig settings: {}", this.toString())
    }

}
