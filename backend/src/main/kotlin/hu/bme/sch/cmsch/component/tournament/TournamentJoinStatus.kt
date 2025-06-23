package hu.bme.sch.cmsch.component.tournament

enum class TournamentJoinStatus {
    OK,
    JOINING_DISABLED,
    ALREADY_JOINED,
    TOURNAMENT_NOT_FOUND,
    NOT_JOINABLE,
    INSUFFICIENT_PERMISSIONS,
    ERROR
}