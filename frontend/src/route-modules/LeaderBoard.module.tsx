import { Route } from 'react-router-dom'
import { Paths } from '../util/paths'
import LeaderboardPage from '../pages/leader-board/leaderboard.page.tsx'

export function LeaderBoardModule() {
  return <Route path={Paths.LEADER_BOARD + '/*'} element={<LeaderboardPage />} />
}
