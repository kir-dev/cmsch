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
  label?: string
  items?: LeaderBoardDetail[]
  total?: number
  tokenRarities?: { [key: string]: number }
}

export type LeaderBoardDetail = {
  name: string
  value: number
}
