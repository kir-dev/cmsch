import { LeaderBoardItemView } from './leaderBoardView'

export type RaceView = {
  categoryName: string
  description: string
  place?: number
  bestTime?: number
  board: LeaderBoardItemView[]
}
