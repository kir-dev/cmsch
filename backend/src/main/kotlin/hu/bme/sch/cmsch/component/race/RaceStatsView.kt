package hu.bme.sch.cmsch.component.race

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails


data class RaceStatsView(
    // The best time in seconds
    @field:JsonView(FullDetails::class)
    val bestTime: Float,

    // The placement on the uncategorized leaderboard
    @field:JsonView(FullDetails::class)
    val placement: Int,

    // The amount of submissions made
    @field:JsonView(FullDetails::class)
    val timesParticipated: Int,

    // Average of times
    @field:JsonView(FullDetails::class)
    val averageTime: Float,

    // Deviation of times
    @field:JsonView(FullDetails::class)
    val deviation: Float,

    // kcal consumed per second, using the best time as reference
    @field:JsonView(FullDetails::class)
    val kCaloriesPerSecond: Float,
)
