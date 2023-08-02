package hu.bme.sch.cmsch.component.app

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.Preview

data class WarningView(

    @field:JsonView(Preview::class)
    var message: String = "",

    @field:JsonView(Preview::class)
    var type: String = "warning"

)
