package hu.bme.sch.cmsch.component.bmejegy

data class CheersRequestBody(
    var token: String = "",
    var tickets: List<CheersTicket> = mutableListOf(),
)

data class CheersTicket(
    var id: String = "",
    var status: String? = null,
    var ticketId: String? = null,
    var productId: String? = null,
    var productName: String? = null,
    var price: Int? = null,
    var ticketCode: String? = null,
    var purchasedAt: String? = null,
    var buyerId: String? = null,
    var buyerEmail: String? = null,
    var buyerName: String? = null,
    var linkerCode: String? = null,
)