import { lazy } from 'react'
import { Route } from 'react-router-dom'
import { Paths } from '../util/paths'

const LeaderBoardPage = lazy(() => import('../pages/leader-board/leaderBoard.page'))
const LeaderBoardCategoryPage = lazy(() => import('../pages/leader-board/leaderBoardCategory.page'))

export function LeaderBoardModule() {
  return (
    <Route path={Paths.LEADER_BOARD}>
      <Route path="category" element={<LeaderBoardCategoryPage />} />
      <Route index element={<LeaderBoardPage />} />
    </Route>
  )
}
