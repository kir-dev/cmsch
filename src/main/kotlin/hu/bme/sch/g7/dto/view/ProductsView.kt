package hu.bme.sch.g7.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.model.ProductEntity

@JsonView(FullDetails::class)
data class ProductsView(
    val userPreview: UserEntityPreview, // FIXME: ezt mindig le kell küldeni?
    val products: List<ProductEntity>
)