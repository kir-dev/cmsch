package hu.bme.sch.cmsch.dto

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.Preview

data class RiddleCategoryDto(

    @JsonView(Preview::class)
    var categoryId: Int = 0,

    @JsonView(Preview::class)
    var title: String = "",

    @JsonView(Preview::class)
    var nextRiddle: Int? = null,

    @JsonView(Preview::class)
    var completed: Int = 0,

    @JsonView(Preview::class)
    var total: Int = 0

)
