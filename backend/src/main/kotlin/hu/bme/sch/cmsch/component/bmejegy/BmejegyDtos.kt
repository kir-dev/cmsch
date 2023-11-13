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
    var cell: Map<String, String>? = null
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
    var ic_6: String? = null,
    var ic_7: String? = null,
    var ic_8: String? = null,
    var ic_9: String? = null,
    var ic_10: String? = null,
    var ic_11: String? = null,
    var ic_12: String? = null,
    var ic_13: String? = null,
    var ic_14: String? = null,
    var ic_15: String? = null,
    var ic_16: String? = null,
    var ic_17: String? = null,
    var ic_18: String? = null,
    var ic_19: String? = null,
    var ic_20: String? = null,
    var ic_21: String? = null,
    var ic_22: String? = null,
)
