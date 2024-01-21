package hu.bme.sch.cmsch.component.proto

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(ProtoComponent::class)
class ProtoService(
    private val protoRepository: ProtoRepository,
) {

    @Transactional(readOnly = true)
    fun getProtoEntityByPath(url: String): ProtoEntity? {
        return protoRepository.findAllByPathAndEnabledTrue(url).firstOrNull()
    }

}