package hu.bme.sch.cmsch.component.leaderboard

data class DetailedLeaderBoardView(

    val userId: Int? = 0,

    val userBoard: List<LeaderBoardService.TopListDetails>? = null,

    val groupId: Int? = null,

    val groupBoard: List<LeaderBoardService.TopListDetails>? = null

)
