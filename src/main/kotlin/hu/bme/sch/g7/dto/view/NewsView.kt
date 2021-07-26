package hu.bme.sch.g7.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dto.Preview
import hu.bme.sch.g7.model.NewsEntity

@JsonView(Preview::class)
data class NewsView(
        val userPreview: UserEntityPreview, // FIXME: ezt mindig le kell küldeni?
        val title: String,
        val interval: String,
        val warningMessage: String = "",
        val startsAt: Long,
        val news: List<NewsEntity> = listOf()
)