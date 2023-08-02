package hu.bme.sch.cmsch.component.news

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.Preview

data class NewsView(

    @field:JsonView(Preview::class)
    val news: List<NewsEntity> = listOf()

)
