package hu.bme.sch.cmsch.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "hu.bme.sch.cmsch.startup")
class StartupPropertyConfig(

)
