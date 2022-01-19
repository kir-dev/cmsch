package hu.bme.sch.cmsch.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.dto.RiddleCategoryDto

data class RiddleCategoryView(

    @JsonView(Preview::class)
    var riddles: List<RiddleCategoryDto> = listOf()

)
