package hu.bme.sch.g7.dto.view

import hu.bme.sch.g7.model.ExtraPageEntity

data class ExtraPageView(
        val userPreview: UserEntityPreview, // FIXME: ezt mindig le kell küldeni?
        val page: ExtraPageEntity
)
