package hu.bme.sch.cmsch.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "hu.bme.sch.cmsch.component.load")
class ComponentLoadConfig(

    val app: Boolean = true,

    val achievement: Boolean = false,

    val riddle: Boolean = false,

    val token: Boolean = false,

    val profile: Boolean = false,

)
