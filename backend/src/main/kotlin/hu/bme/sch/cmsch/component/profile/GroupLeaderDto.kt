package hu.bme.sch.cmsch.component.profile

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class GroupLeaderDto(
    @field:JsonView(FullDetails::class)
    val name: String,

    @field:JsonView(FullDetails::class)
    val facebookUrl: String,

    @field:JsonView(FullDetails::class)
    val mobilePhone: String
) {
    constructor(args: List<String>) : this(
        if (args.isNotEmpty()) args[0].trim() else "",
        if (args.size > 1) args[1].trim() else "",
        if (args.size > 2) args[2].trim() else "",
    )
}
