package hu.bme.sch.cmsch.component.tournament

data class ParticipantDto(
    var teamId: Int = 0,
    var teamName: String = "",
)

data class SeededParticipantDto(
    var teamId: Int = 0,
    var teamName: String = "",
    var seed: Int = 0,
)