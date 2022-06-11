package hu.bme.sch.cmsch.component.debt

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class DebtsView(
    @JsonView(FullDetails::class)
    val debts: List<DebtDto>
)

data class DebtDto(
    @JsonView(FullDetails::class)
    var product: String,

    @JsonView(FullDetails::class)
    var price: Int,

    @JsonView(FullDetails::class)
    var sellerName: String,

    @JsonView(FullDetails::class)
    var representativeName: String,

    @JsonView(FullDetails::class)
    var payed: Boolean,

    @JsonView(FullDetails::class)
    var shipped: Boolean,

    @JsonIgnore
    var log: String,

    @JsonView(FullDetails::class)
    var materialIcon: String
)
