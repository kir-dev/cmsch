package hu.bme.sch.cmsch.component.token

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.TokenCollectorStatus

data class TokenSubmittedView(

    @JsonView(FullDetails::class)
    var status: TokenCollectorStatus,

    @JsonView(FullDetails::class)
    var title: String?

)
