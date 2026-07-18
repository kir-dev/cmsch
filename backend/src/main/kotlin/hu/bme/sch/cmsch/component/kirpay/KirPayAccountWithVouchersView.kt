package hu.bme.sch.cmsch.component.kirpay

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class KirPayAccountView(
    @field:JsonView(FullDetails::class)
    val balance: Long,
)

data class KirPayVoucherWithItemNameView(
    @field:JsonView(FullDetails::class)
    val itemName: String,

    @field:JsonView(FullDetails::class)
    val count: Int
)

data class KirPayAccountWithVouchersView(
    @field:JsonView(FullDetails::class)
    val account: KirPayAccountView,

    @field:JsonView(FullDetails::class)
    val vouchers: List<KirPayVoucherWithItemNameView>
)
