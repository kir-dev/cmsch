package hu.bme.sch.cmsch.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.ProductEntity

data class ProductsView(

    @JsonView(FullDetails::class)
    val products: List<ProductEntity>

)
