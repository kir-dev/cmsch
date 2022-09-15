export type LeaderBoardView = {
  userScore?: number
  userBoard?: Array<LeaderBoardItemView>
  groupScore?: number
  groupBoard?: Array<LeaderBoardItemView>
}

export type LeaderBoardItemView = {
  name: string
  groupName: string
  score?: number
}

export type DetailedLeaderBoardView = {
  userScore?: number
  userBoard?: Array<DetailedLeaderBoardItemView>
  groupScore?: number
  groupBoard?: Array<DetailedLeaderBoardItemView>
}

export type DetailedLeaderBoardItemView = {
  id: number
  name: string
  items: Map<string, number>
  total: number
}
