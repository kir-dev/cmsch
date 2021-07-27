package hu.bme.sch.g7.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dto.Preview
import hu.bme.sch.g7.model.NewsEntity

data class NewsView(
        @JsonView(Preview::class)
        val userPreview: UserEntityPreview, // FIXME: ezt mindig le kell küldeni?

        @JsonView(Preview::class)
        val title: String,

        @JsonView(Preview::class)
        val interval: String,

        @JsonView(Preview::class)
        val warningMessage: String = "",

        @JsonView(Preview::class)
        val startsAt: Long,

        @JsonView(Preview::class)
        val news: List<NewsEntity> = listOf()
)