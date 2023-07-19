package hu.bme.sch.cmsch.config

import liquibase.integration.spring.SpringLiquibase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource


@Configuration
class LiquibaseConfig {
//    @Bean
//    fun liquibase(dataSource: DataSource): SpringLiquibase {
//        val liquibase = SpringLiquibase()
//        liquibase.dataSource = dataSource
//        liquibase.changeLog = "classpath:db/changelog/changelog-core-master.yaml"
//        return liquibase
//    }
}