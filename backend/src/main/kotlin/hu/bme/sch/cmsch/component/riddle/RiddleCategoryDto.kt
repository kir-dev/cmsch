package hu.bme.sch.cmsch.component.riddle

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.Preview

data class RiddleCategoryDto(

    @field:JsonView(Preview::class)
    var categoryId: Int = 0,

    @field:JsonView(Preview::class)
    var title: String = "",

    @field:JsonView(Preview::class)
    var nextRiddles: List<RiddleView> = listOf(),

    @field:JsonView(Preview::class)
    var completed: Int = 0,

    @field:JsonView(Preview::class)
    var total: Int = 0

)
