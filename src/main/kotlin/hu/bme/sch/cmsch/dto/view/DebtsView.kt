package hu.bme.sch.cmsch.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.DebtDto
import hu.bme.sch.cmsch.dto.FullDetails

data class DebtsView(

    @JsonView(FullDetails::class)
    val debts: List<DebtDto>

)
