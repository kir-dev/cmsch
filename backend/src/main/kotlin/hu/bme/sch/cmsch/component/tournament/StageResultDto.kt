package hu.bme.sch.cmsch.component.tournament

data class StageResultDto(
    var stageId: Int,
    var stageName: String,
    var teamId: Int,
    var teamName: String,
    var seed: Int = 0,
    var position: Int = 0,
    var points: Int = 0,
    var won: Int = 0,
    var drawn: Int = 0,
    var lost: Int = 0,
    var goalsFor: Int = 0,
    var goalsAgainst: Int = 0,
    var goalDifference: Int = 0
)
