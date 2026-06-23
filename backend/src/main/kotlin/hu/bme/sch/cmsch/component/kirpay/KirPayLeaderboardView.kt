package hu.bme.sch.cmsch.component.kirpay

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class KirPayLeaderboardEntry(
    @field:JsonView(FullDetails::class)
    val name: String,

    @field:JsonView(FullDetails::class)
    val itemCount: Long
)

data class KirPayLeaderboardView(
    @field:JsonView(FullDetails::class)
    val entries: List<KirPayLeaderboardEntry> = listOf()
)
