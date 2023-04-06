package hu.bme.sch.cmsch.component.leaderboard

interface LeaderBoardEntry {

    var name: String

    var taskScore: Int

    var riddleScore: Int

    var challengeScore: Int

    var tokenScore: Int

    var totalScore: Int

}
