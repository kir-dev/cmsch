package hu.bme.sch.cmsch.config

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("test")
@Configuration
class TestConfigStarter(
    private val testConfig: TestConfig
) {

    @PostConstruct
    fun init() {
        testConfig.init()
    }

}