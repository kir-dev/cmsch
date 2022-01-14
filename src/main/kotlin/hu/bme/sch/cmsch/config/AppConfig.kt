package hu.bme.sch.cmsch.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
class AppConfig(
        objectMapper: ObjectMapper
) {

    init {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    @PostConstruct
    fun onLoad() {
    }

}
