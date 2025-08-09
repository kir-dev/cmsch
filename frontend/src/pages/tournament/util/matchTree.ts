import { MatchView } from '../../../util/views/tournament.view.ts'

export type MatchTree = {
  root: MatchView
  lowerTree: MatchTree | null
  upperTree: MatchTree | null
}
