package hu.bme.sch.cmsch.component.tournament

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.Optional
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class StageResultDto(
    val teamId: Int,
    val teamName: String,
    val stageId: Int = 0,
    var highlighted: Boolean = false,
    var initialSeed: Int = 0,
    var detailedStats: Optional<GroupStageResults> = Optional.empty()
): Comparable<StageResultDto> {
    override fun compareTo(other: StageResultDto): Int {
        return compareValuesBy(this, other,
            { it.detailedStats.getOrElse({ GroupStageResults() })}, {it.initialSeed}, {it.highlighted})
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
            { it.position.inv() },
            { it.points },
            { it.won },
            { it.goalDifference },
            { it.goalsFor })
    }
}
