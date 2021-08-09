package hu.bme.sch.g7.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dto.Preview
import hu.bme.sch.g7.model.NewsEntity

data class NewsView(
        @JsonView(Preview::class)
        val warningMessage: String = "",

        @JsonView(Preview::class)
        val news: List<NewsEntity> = listOf()
)