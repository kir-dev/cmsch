package hu.bme.sch.cmsch.component.token

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class TokenSubmittedView(

    @field:JsonView(FullDetails::class)
    var status: TokenCollectorStatus,

    @field:JsonView(FullDetails::class)
    var title: String?,

    @field:JsonView(FullDetails::class)
    var description: String?,

    @field:JsonView(FullDetails::class)
    var iconUrl: String?,

)
