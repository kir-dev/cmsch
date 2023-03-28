package hu.bme.sch.cmsch.model

import hu.bme.sch.cmsch.component.EntityConfig
import org.springframework.core.env.Environment
import java.io.Serializable

interface ManagedEntity : Serializable {

    var id: Int

    fun getEntityConfig(env: Environment): EntityConfig?

}
