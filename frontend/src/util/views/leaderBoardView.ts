export type LeaderBoardView = {
  userScore?: number
  userBoard?: Array<LeaderBoardItemView>
  groupScore?: number
  groupBoard?: Array<LeaderBoardItemView>
}

export type LeaderBoardItemView = {
  name: string
  score?: number
}
