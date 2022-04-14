package hu.bme.sch.cmsch.component.extrapage

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.component.extrapage.ExtraPageEntity

data class ExtraPageView(

    @JsonView(FullDetails::class)
    val page: ExtraPageEntity?

)
