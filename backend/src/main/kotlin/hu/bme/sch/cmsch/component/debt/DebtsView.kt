package hu.bme.sch.cmsch.component.debt

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class DebtsView(
    @field:JsonView(FullDetails::class)
    val debts: List<DebtDto>
)

data class DebtDto(
    @field:JsonView(FullDetails::class)
    var product: String,

    @field:JsonView(FullDetails::class)
    var price: Int,

    @field:JsonView(FullDetails::class)
    var sellerName: String,

    @field:JsonView(FullDetails::class)
    var representativeName: String,

    @field:JsonView(FullDetails::class)
    var payed: Boolean,

    @field:JsonView(FullDetails::class)
    var shipped: Boolean,

    @field:JsonIgnore
    var log: String,

    @field:JsonView(FullDetails::class)
    var materialIcon: String
)
