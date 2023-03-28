package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.admin.GenerateInput
import hu.bme.sch.cmsch.admin.INPUT_TYPE_BLOCK_TEXT
import hu.bme.sch.cmsch.model.ManagedEntity
import org.springframework.core.env.Environment

data class ImportDebtsCompleteVirtualEntity(

    @property:GenerateInput(visible = false)
    override var id: Int = 0,

    @property:GenerateInput(label = "Tranzakció ID-k felsorolva", order = 1, type = INPUT_TYPE_BLOCK_TEXT,
            note = "Soronként elválasztva a tranzakciós ID-k")
    var transactionIds: String

) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = null

    override fun toString(): String {
        return "$id|$transactionIds"
    }

}
