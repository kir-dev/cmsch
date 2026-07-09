package hu.bme.sch.cmsch.component.tournament

enum class TournamentCancelStatus {
    OK,
    NOT_PLAYING,
    TOURNAMENT_NOT_FOUND,
    NOT_CANCELABLE,
    INSUFFICIENT_PERMISSIONS,
    ERROR
}