package hu.bme.sch.cmsch

import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.config.ComponentLoadConfig
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

const val CMSCH_VERSION = "4.8.1"

@SpringBootApplication
@EnableConfigurationProperties(value = [ComponentLoadConfig::class, StartupPropertyConfig::class])
@EntityScan(basePackages = ["hu.bme.sch.cmsch.model"], basePackageClasses = [ApplicationComponent::class])
class CMSchApplication

fun main(args: Array<String>) {
    runApplication<CMSchApplication>(*args)
}
