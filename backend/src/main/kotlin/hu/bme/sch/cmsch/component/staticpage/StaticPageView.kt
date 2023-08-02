package hu.bme.sch.cmsch.component.staticpage

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class StaticPageView(

    @field:JsonView(FullDetails::class)
    val page: StaticPageEntity?

)
