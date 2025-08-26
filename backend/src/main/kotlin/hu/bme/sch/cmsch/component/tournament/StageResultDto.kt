package hu.bme.sch.cmsch.component.tournament

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class StageResultDto(
    val teamId: Int,
    val teamName: String,
    val stageId: Int = 0,
    var highlighted: Boolean = false,
    var initialSeed: Int = 0,
    var highestSeed: Int = 0,
    var detailedStats: GroupStageResults? = null,
): Comparable<StageResultDto> {
    override fun compareTo(other: StageResultDto): Int {
        if (this.stageId != other.stageId) {
            return this.stageId.compareTo(other.stageId)
        }
        if (this.detailedStats == null && other.detailedStats == null) {
            return -this.initialSeed.compareTo(other.highestSeed)
        }
        if (this.detailedStats == null) {
            return -1 // null detailed stats are considered better (knockout stages are later)
        }
        return compareValuesBy(this, other,
            { it.detailedStats?:GroupStageResults()}, {it.initialSeed}, {it.highlighted})
    }
}
data class GroupStageResults(
    var group: String = "",
    var position: UShort = UShort.MAX_VALUE,
    var points: UInt = 0u,
    var won: UShort = 0u,
    var drawn: UShort = 0u,
    var lost: UShort = 0u,
    var goalsFor: UInt = 0u,
    var goalsAgainst: UInt = 0u,
    var goalDifference: Int = 0
): Comparable<GroupStageResults> {
    override fun compareTo(other: GroupStageResults): Int {
        return compareValuesBy(this, other,
            { -it.position.toInt() },
            { it.points },
            { it.won },
            { it.goalDifference },
            { it.goalsFor })
    }
}
