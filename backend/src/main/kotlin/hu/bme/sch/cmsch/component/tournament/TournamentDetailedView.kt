package hu.bme.sch.cmsch.component.tournament

data class TournamentWithParticipants(
    val id: Int,
    val title: String,
    val description: String,
    val location: String,
    val participantCount: Int,
    val participants: List<ParticipantDto>,
    val status: Int,
)

data class MatchDto(
    val id: Int,
    val gameId: Int,
    val home: ParticipantDto?,
    val away: ParticipantDto?,
    val homeScore: Int?,
    val awayScore: Int?,
    val status: MatchStatus
)

data class KnockoutStageDetailedView(
    val id: Int,
    val name: String,
    val level: Int,
    val participantCount: Int,
    val nextRound: Int,
    val status: StageStatus,
    val matches: List<MatchDto>,
)


data class TournamentDetailedView(
    val tournament: TournamentWithParticipants,
    val stages: List<KnockoutStageDetailedView>,
)