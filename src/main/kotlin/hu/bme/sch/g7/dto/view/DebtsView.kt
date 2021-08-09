package hu.bme.sch.g7.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dto.DebtDto
import hu.bme.sch.g7.dto.FullDetails

data class DebtsView(
    @JsonView(FullDetails::class)
    val debts: List<DebtDto>
)