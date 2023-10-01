export type LeaderBoardView = {
  userScore?: number
  userBoard?: Array<LeaderBoardItemView>
  groupScore?: number
  groupBoard?: Array<LeaderBoardItemView>
}

export type LeaderBoardItemView = {
  id?: number
  name: string
  groupName: string
  groupId?: number
  description?: string
  score?: number
  items?: LeaderBoardDetail[]
  total?: number
}

export type LeaderBoardDetail = {
  name: string
  value: number
}
