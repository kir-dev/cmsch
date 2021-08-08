package hu.bme.sch.g7.dto

import com.fasterxml.jackson.annotation.JsonView

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

    @JsonView(FullDetails::class)
    var log: String
)