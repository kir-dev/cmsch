import { LeaderBoardItemView } from './leaderBoardView'

export type RaceView = {
  place?: number
  bestTime?: number
  board: LeaderBoardItemView[]
}
