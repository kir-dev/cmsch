package hu.bme.sch.g7.dto.view

import hu.bme.sch.g7.model.EventEntity

data class SingleEventView(
    val userPreview: UserEntityPreview, // FIXME: ezt mindig le kell küldeni?
    val event: EventEntity?
)