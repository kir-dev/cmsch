package hu.bme.sch.cmsch.component.leaderboard

data class DetailedCategoryLeaderBoardView(

    val userId: Int? = 0,

    val userBoard: List<LeaderBoardService.TopListByCategoryDetails>? = null,

    val groupId: Int? = null,

    val groupBoard: List<LeaderBoardService.TopListByCategoryDetails>? = null

)
