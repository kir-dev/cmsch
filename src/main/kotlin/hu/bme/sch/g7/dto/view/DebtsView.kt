package hu.bme.sch.g7.dto.view

import hu.bme.sch.g7.model.SoldProductEntity

data class DebtsView(
    val userPreview: UserEntityPreview, // FIXME: ezt mindig le kell küldeni?
    val debts: List<SoldProductEntity>
)