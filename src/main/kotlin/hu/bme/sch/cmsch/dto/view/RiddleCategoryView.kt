package hu.bme.sch.cmsch.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.RiddleCategoryEntity

data class RiddleCategoryView(

    @JsonView(Preview::class)
    var riddles: List<RiddleCategoryEntity> = listOf()

)
