package hu.bme.sch.cmsch.component.proto

import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("test")
@ConditionalOnBean(ProtoComponent::class)
class ProtoSeedConfig(
    private val protoRepository: ProtoRepository
) {

    @PostConstruct
    fun seedData() {
        addProto()
    }

    private fun addProto() {
        protoRepository.saveAll(
            listOf(
                ProtoEntity(
                    id = 1,
                    path = "/test",
                    responseValue = """{"status": "ok"}""",
                    mimeType = "application/json",
                    statusCode = 200,
                )
            )
        )
    }

}