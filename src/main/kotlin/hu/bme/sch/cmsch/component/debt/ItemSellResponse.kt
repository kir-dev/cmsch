package hu.bme.sch.cmsch.component.debt

enum class SellStatus {
    SOLD,
    NOT_IN_GROUP,
    INVALID_PERMISSIONS,
    PRODUCT_NOT_FOUND,
    BUYER_NOT_FOUND,
    ITEM_NOT_AVAILABLE
}

data class ItemSellResponse(
    val status: SellStatus
)
