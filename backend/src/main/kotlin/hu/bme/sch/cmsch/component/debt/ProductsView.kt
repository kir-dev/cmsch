package hu.bme.sch.cmsch.component.debt

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class ProductsView(

    @field:JsonView(FullDetails::class)
    val products: List<ProductEntity>

)
