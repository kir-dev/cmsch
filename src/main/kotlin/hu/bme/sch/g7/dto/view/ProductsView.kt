package hu.bme.sch.g7.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.model.ProductEntity

data class ProductsView(
        @JsonView(FullDetails::class)
        val products: List<ProductEntity>
)