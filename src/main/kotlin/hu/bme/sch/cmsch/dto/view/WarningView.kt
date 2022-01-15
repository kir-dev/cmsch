package hu.bme.sch.cmsch.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.Preview

data class WarningView(

    @JsonView(Preview::class)
    var message: String = "",

    @JsonView(Preview::class)
    var type: String = "warning"

)
