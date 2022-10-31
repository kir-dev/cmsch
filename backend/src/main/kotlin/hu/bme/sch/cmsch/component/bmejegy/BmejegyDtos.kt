package hu.bme.sch.cmsch.component.bmejegy

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty


@JsonIgnoreProperties(ignoreUnknown = true)
data class BmeJegyResponse(
    var page: String? = null,
    var total: Int? = 0,
    var records: String? = null,
    var rows: List<BmejegyRow>? = null
)

data class BmejegyRow(
    var cell: BmejegyItemDto? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BmejegyItemDto(
    var order_item_id: String? = null,
    @set:JsonProperty("ID")
    var id: String? = null,
    var post_date: String? = null,
    var post_status: String? = null,
    var order_item_name: String? = null,
    var line_total: String? = null,
    var voucher_code: String? = null,
    var order_key: String? = null,
    var first_name: String? = null,
    var last_name: String? = null,
    var full_name: String? = null,
    var email: String? = null,
    var ic_0: String? = null,
    var ic_1: String? = null,
    var ic_2: String? = null,
    var ic_3: String? = null,
    var ic_4: String? = null,
    var ic_5: String? = null,
)
