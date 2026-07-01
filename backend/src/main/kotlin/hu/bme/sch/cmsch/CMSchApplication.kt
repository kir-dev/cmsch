package hu.bme.sch.cmsch

import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.config.ComponentLoadConfig
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(value = [ComponentLoadConfig::class, StartupPropertyConfig::class])
@EntityScan(basePackages = ["hu.bme.sch.cmsch.model"], basePackageClasses = [ApplicationComponent::class])
class CMSchApplication

fun main(args: Array<String>) {
    runApplication<CMSchApplication>(*args)
}
